package com.lanyuan.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import com.lanyuan.controller.common.BaseCommonController;
import com.lanyuan.entity.Advertisement;
import com.lanyuan.entity.City;
import com.lanyuan.entity.OperateTypeEnum;
import com.lanyuan.entity.TypePic;
import com.lanyuan.entity.WxAccType;
import com.lanyuan.entity.WxArticle;
import com.lanyuan.entity.WxUser;
import com.lanyuan.entity.WxUserOperation;
import com.lanyuan.service.AdvertisementService;
import com.lanyuan.service.CityService;
import com.lanyuan.service.WxAccTypeService;
import com.lanyuan.service.WxArticleService;
import com.lanyuan.service.WxUserOperationService;
import com.lanyuan.service.WxUserService;
import com.lanyuan.util.Common;
import com.lanyuan.util.MD5;

@Controller
@RequestMapping("/querydata")
public class WxCommArticleListController extends BaseCommonController{
	
	//token，WxUser.id
	public static final Map<String, Integer> LOGIN_MAP = new HashMap<String, Integer>();
    
	@Inject
	private WxArticleService wxArticleService;
	@Inject
	private AdvertisementService advertisementService;
	@Inject
	private WxAccTypeService wxAccTypeService;
	@Inject
	private CityService cityService;
	@Inject
	private WxUserOperationService wxUserOperationService;
	@Inject
	private WxUserService wxUserService;
	
	static {
		Map<String, String> map = getAllProperties("jdbc");
		System.out.println(map);
		Connection conn = null;
		try {
			Class.forName(map.get("jdbc.driverClassName"));// 动态加载mysql驱动
			String username = map.get("jdbc.username");
			String password = map.get("jdbc.password");
			String url = map.get("jdbc.url")+"&user="+username+"&password="+password;
			conn = DriverManager.getConnection(url);
			Statement stmt = conn.createStatement();
			String sql = "SELECT id,token FROM wxuser";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				int userId = rs.getInt(1);
				String token = rs.getString(2);
				System.out.println(userId+token);
				if(StringUtils.isNotBlank(token)) {
					LOGIN_MAP.put(token, userId);	
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 返回文章列表
	 * @param data_type 数据类型：more，更多文章；其他，最新文章
	 * @param class_id 栏目类别id：不同栏目id用英文逗号分隔，不传值表示所有栏目
	 * @param max_article_id 用户查询最新文章时当时数据库中的最大文章id，之后查询更多文章时以这个id为约束条件
	 * @param keyword 搜索关键字
	 * @param page 第几页
	 * @param limit 每次查询的文章数
	 */
	@RequestMapping("/getArticleList")
	@ResponseBody
	//, @RequestParam(value ="class_id") String class_id
	public Map<String, Object> getArticleList(String data_type, String class_id, String max_article_id, String keyword, String page, String limit) {
		LOG.info("class_id="+class_id);
		LOG.info("max_article_id="+max_article_id);
		LOG.info("data_type="+data_type);
		LOG.info("keyword="+keyword);
		LOG.info("page="+page);
		LOG.info("limit="+limit);
		//只有在“获取更多文章”时，需要利用客户端回传的他开始查询时的最大文章id
		Long i_max_article_id = null;
		if("more".equals(data_type)) {	//更多文章
			if(!StringUtils.isBlank(max_article_id)) {
				try{
					i_max_article_id = Long.valueOf(max_article_id);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		//class_id 等号分隔的数字
		//
		int i_limit=20;
		if(!StringUtils.isBlank(limit)) {
			try{
				i_limit = Integer.valueOf(limit);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		//
		int i_page=1;
		if(!StringUtils.isBlank(page)) {
			try{
				i_page = Integer.valueOf(page);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		int i_offset = (i_page-1)*i_limit;
		
		List<WxArticle> commArticleList = null;
		if("more".equals(data_type)) {	//更多文章
			commArticleList = wxArticleService.queryMoreWxArticle(i_max_article_id, i_offset, i_limit, class_id, keyword);
		}else{	//查询最新文章
			commArticleList = wxArticleService.queryLastestWxArticle(class_id, keyword, i_limit);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		if("more".equals(data_type)) {	//更多文章
			if(i_max_article_id==null) {
				if(commArticleList!=null && commArticleList.size()>0) {
					i_max_article_id = Long.valueOf(commArticleList.get(0).getId());
				}else {
					i_max_article_id = Long.valueOf(Integer.MAX_VALUE);
				}	
			}
		}else {
			if(commArticleList!=null && commArticleList.size()>0) {
				i_max_article_id = Long.valueOf(commArticleList.get(0).getId());
			}else {
				i_max_article_id = Long.valueOf(Integer.MAX_VALUE);
			}	
		}
		map.put("max_article_id", i_max_article_id);
		map.put("commArticleList",commArticleList);
		map.put("errorCode",1);
		map.put("message","");
		return map;
	}
	
	/**
	 * 返回通用栏文章列表（带图片的文章）
	 * @param class_id
	 * @param limit
	 */
	@RequestMapping("/getCommArticleList")
	@ResponseBody
	//, @RequestParam(value ="class_id") String class_id
	public Map<String, Object> getCommArticleList(String class_id, String limit) {
		LOG.info("class_id="+class_id);
		LOG.info("limit="+limit);
		if(StringUtils.isBlank(limit)) {
			limit = "4";
		}
		//List<TypePic> typePicList = new ArrayList<TypePic>();	//写个查询啊 TODO class_id limit
		List<TypePic> piclist = new ArrayList<TypePic>();
		for(int index = 0; index<4; index++) {
			TypePic tp = new TypePic();
			tp.setId(index+1);
			tp.setWxAccTypeId(index+1L);
			tp.setWxArticleId(index+1L);
			tp.setTypePicUrl("y:/localpic/typepic/1.jpg");
			tp.setWxArticTitle("wxArticTitle_"+(index+1));
			piclist.add(tp);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("typePicList",piclist);
		map.put("errorCode",1);
		map.put("message","");
		return map;
	}
	
	/**
	 * 返回当前栏目
	 */
	@RequestMapping("/getClassList")
	@ResponseBody
	public Map<String, Object> getClassList() {
		List<WxAccType> wxAccTypeList = wxAccTypeService.queryAll(null);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("wxAccTypeList",wxAccTypeList);
		map.put("errorCode",1);
		map.put("message","");
		return map;
	}
	
	/**
	 * 返回城市列表
	 */
	@RequestMapping("/getCityList")
	@ResponseBody
	public Map<String, Object> getCityList(String page, String limit) {
		int i_page = 1;
		int i_limit = 5;
		if(!StringUtils.isBlank(page)) {
			i_page = Integer.valueOf(page);
		}
		if(!StringUtils.isBlank(limit)) {
			i_limit = Integer.valueOf(limit);
		}
		int i_offset = (i_page-1)*i_limit;
		List<City> wxCityList = cityService.queryCityList(i_offset, i_limit);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("wxCityList",wxCityList);
		map.put("errorCode",1);
		map.put("message","");
		return map;
	}
	
	/**
	 * 返回单篇文章内容Content
	 * @param wxArticleId 文章id
	 */
	@RequestMapping("/queryArticleContent")
	@ResponseBody
	public Map<String, Object> queryArticleContent(String wxArticleId) {
		Map<String, Object> map = new HashMap<String, Object>();
		WxArticle wa = wxArticleService.getById(wxArticleId);
		//map.put("content",HtmlUtils.htmlEscape(wa.getContent()));
		map.put("content",wa.getContent());
		map.put("errorCode",1);
		map.put("message","");
		return map;
	}
	
	/**
	 * 返回文章wxArticleId中的广告
	 * @param wxArticleId 文章id
	 */
	@RequestMapping("/queryAdvertisement")
	@ResponseBody
	public Map<String, Object> queryAdvertisement(String wxArticleId) {
		Map<String, Object> map = new HashMap<String, Object>();
////		int advertisementCount = advertisementService.count(new Advertisement());
////		int offset = (int)(Math.random() * advertisementCount);
//		Advertisement advertisement = null;
//		List<Advertisement> advertisementList = advertisementService.queryAll(new Advertisement());
//		if(advertisementList!=null && advertisementList.size()>0) {
//			int offset = (int)(Math.random() * advertisementList.size());
//			advertisement = advertisementList.get(offset);
//		}
		//APP端请求参数wxArticleId,取该值各位数值之和的个位数以实现随机的概念
		int param = Integer.valueOf(wxArticleId);
		int randomint = Common.sumDig(param);
		Advertisement advertisement = advertisementService.getById(String.valueOf(randomint));
		if (null==advertisement){
			advertisement = advertisementService.getById(String.valueOf(1));
		}
		//返回接口的图片地址必须是加了前缀的
		advertisement.setPicurl("http://121.40.69.199:8000/media/"+advertisement.getPicurl());
		map.put("advertisement", advertisement);
		map.put("errorCode",1);
		map.put("message","");
		return map;
	}
	
	/**
	 * 返回文章wxArticleId的相关文章列表，最多3篇。
	 * 相关文章：同一微信号下的最新文章
	 * @param wxArticleId 文章id
	 */
	@RequestMapping("/queryRelatedArticle")
	@ResponseBody
	public Map<String, Object> queryRelatedArticle(String wxArticleId) {
		Map<String, Object> map = new HashMap<String, Object>();
		WxArticle wa = wxArticleService.getById(wxArticleId);
		WxArticle wxArticle = new WxArticle();
		wxArticle.setWxAccountNo(wa.getWxAccountNo());
		List<WxArticle> wxArticleList = wxArticleService.queryAll(wxArticle);
		if(wxArticleList.size()>3) {//删除自己，以后再说 TODO
			map.put("relatedArticleList",wxArticleList.subList(0, 3));
		}else {
			map.put("relatedArticleList",wxArticleList);
		}
		map.put("errorCode",1);
		map.put("message","");
		return map;
	}
	
	/**
	 * 返回文章wxArticleId的相关文章列表，最多3篇。
	 * 相关文章：同一栏目下的最新文章   TODO
	 * @param wxArticleId 文章id
	 */
	@RequestMapping("/queryMoreRecommendArticleList")
	@ResponseBody
	public Map<String, Object> queryMoreRecommendArticleList(String wxArticleId) {
		Map<String, Object> map = new HashMap<String, Object>();
		WxArticle wa = wxArticleService.getById(wxArticleId);
		WxArticle wxArticle = new WxArticle();
		wxArticle.setWxAccountNo(wa.getWxAccountNo());	//TODO
		List<WxArticle> moreRecommendArticleList = wxArticleService.queryAll(wxArticle);
		if(moreRecommendArticleList.size()>3) {//删除自己，以后再说 TODO
			map.put("moreRecommendArticleList",moreRecommendArticleList.subList(0, 3));
		}else {
			map.put("moreRecommendArticleList",moreRecommendArticleList);
		}
		map.put("errorCode",1);
		map.put("message","");
		return map;
	}
	
	/**
	 * 返回今日推荐文章
	 * @param max_article_id 用户查询最新文章时当时数据库中的最大文章id，之后查询更多文章时以这个id为约束条件
	 * @param page 第几页
	 * @param limit 每次查询的文章数
	 */
	@RequestMapping("/queryRecommendArticleList")
	@ResponseBody
	public Map<String, Object> queryRecommendArticleList(String max_article_id, String page, String limit) { 
		LOG.info("max_article_id="+max_article_id);
		LOG.info("page="+page);
		LOG.info("limit="+limit);
		//
		Long i_max_article_id = null;
		if(!StringUtils.isBlank(max_article_id)) {
			try{
				i_max_article_id = Long.valueOf(max_article_id);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		//
		int i_limit=20;
		if(!StringUtils.isBlank(limit)) {
			try{
				i_limit = Integer.valueOf(limit);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		//
		int i_page=1;
		if(!StringUtils.isBlank(page)) {
			try{
				i_page = Integer.valueOf(page);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		int i_offset = (i_page-1)*i_limit;
		//
		List<WxArticle> recommendArticleList = wxArticleService.queryMoreWxArticle(i_max_article_id, i_offset, i_limit, null, null);	//TODO 查询接口换一下
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("max_article_id", i_max_article_id);
		map.put("recommendArticleList",recommendArticleList);
		map.put("errorCode",1);
		map.put("message","");
		return map;
	}
	
	/**
	 * 用户注册
	 * @param phone 手机号
	 * @param username 用户名
	 * @param password 密码
	 * @param verifyCode 手机验证码
	 */
	@RequestMapping("/registerUser")
	@ResponseBody
	public Map<String, Object> registerUser(String phone, String username, String password, String verifyCode) {
		Map<String, Object> map = new HashMap<String, Object>();
		if(StringUtils.isBlank(phone)) {
			map.put("errorCode",0);
			map.put("message","手机号不能为空");
			return map;
		}
		if(StringUtils.isBlank(username)) {
			map.put("errorCode",0);
			map.put("message","用户名不能为空");
			return map;
		}
		if(StringUtils.isBlank(password)) {
			map.put("errorCode",0);
			map.put("message","密码不能为空");
			return map;
		}
//		if(StringUtils.isBlank(verifyCode)) {
//			map.put("errorCode",0);
//			map.put("message","验证码错误");
//			return map;
//		}
		List<WxUser> wxUserList = null;
		WxUser wxUser = new WxUser();
		wxUser.setCellPhoneNo(phone);
		wxUserList = wxUserService.queryAll(wxUser);
		if(wxUserList.size()>0) {
			map.put("errorCode",0);
			map.put("message","电话号码已被注册");
			return map;
		}
		wxUser = new WxUser();
		wxUser.setUsername(username);
		if(wxUserList.size()>0) {
			map.put("errorCode",0);
			map.put("message","用户名已被注册");
			return map;
		}
		
		try{
			String token = MD5.md5(username+":"+password);
			WxUser registerWxUser = new WxUser();
			registerWxUser.setCellPhoneNo(phone);
			registerWxUser.setUsername(username);
			registerWxUser.setPassword(password);
			registerWxUser.setToken(token);
			registerWxUser.setRegisterDate(new Date());
			wxUserService.add(registerWxUser);
			//
			map.put("token", token);
			map.put("errorCode",1);
			map.put("message","注册成功");
			LOGIN_MAP.put(token, registerWxUser.getId());
			return map;
		} catch(Exception e) {
			LOG.error(e);
		}
		map.put("errorCode",0);
		map.put("message","注册失败");
		return map;
	}
	
	/**
	 * 用户登录
	 * @param phone 手机号
	 * @param username 用户名
	 * @param password 密码
	 */
	@RequestMapping("/login")
	@ResponseBody
	public Map<String, Object> login(String phone, String username, String password) {
		Map<String, Object> map = new HashMap<String, Object>();
		if(StringUtils.isBlank(password)) {
			map.put("errorCode",0);
			map.put("message","密码不能为空");
			return map;
		}
		if(StringUtils.isBlank(phone) && StringUtils.isBlank(username)) {//如果输入的是phone要转换成username，统一做MD5的token
			map.put("errorCode",0);
			map.put("message","手机号和用户名不能都为空");
			return map;
		}

		//
		List<WxUser> wxUserList = null;
		WxUser wxUser = null;
		if(!StringUtils.isBlank(phone)) {
			wxUser = new WxUser();
			wxUser.setCellPhoneNo(phone);
			wxUserList = wxUserService.queryAll(wxUser);
		}
		if(wxUserList.isEmpty() && !StringUtils.isBlank(username)) {
			wxUser = new WxUser();
			wxUser.setUsername(username);
			wxUserList = wxUserService.queryAll(wxUser);
		}
		if(wxUserList.isEmpty()) {
			map.put("errorCode",0);
			map.put("message","电话号码或用户名错误");
			return map;
		}
		WxUser loginWxUser = wxUserList.get(0);
		if(!password.equals(loginWxUser.getPassword())) {
			map.put("errorCode",0);
			map.put("message","密码错误");
			return map;
		}
		String token = MD5.md5(loginWxUser.getUsername()+":"+loginWxUser.getPassword());
		try{
			WxUser updateWxUser = new WxUser();
			updateWxUser.setId(loginWxUser.getId());
			updateWxUser.setToken(token);
			wxUserService.update(updateWxUser);
		}catch(Exception e) {
			LOG.error(e);
		}
		map.put("token", token);
		map.put("errorCode",1);
		map.put("message","登陆成功");
		LOGIN_MAP.put(token, loginWxUser.getId());
		return map;
	}
	
	
	/**
	 * 用户登录
	 * @param phone 手机号
	 * @param username 用户名
	 * @param verifyCode 验证码
	 */
	@RequestMapping("/findPassword")
	@ResponseBody
	public Map<String, Object> findPassword(String phone, String username, String verifyCode) {
		Map<String, Object> map = new HashMap<String, Object>();
		if(StringUtils.isBlank(phone) && StringUtils.isBlank(username)) {//如果输入的是phone要转换成username，统一做MD5的token
			map.put("errorCode",0);
			map.put("message","手机号和用户名不能都为空");
			return map;
		}
//		if(StringUtils.isBlank(verifyCode)) {
//			map.put("errorCode",0);
//			map.put("message","验证码不能为空");
//			return map;
//		}
//		if(verifyCode.equals(verifyCode)) {	//TODO 数据库数据
//			
//		}
		//
		List<WxUser> wxUserList = null;
		WxUser wxUser = null;
		if(!StringUtils.isBlank(phone)) {
			wxUser = new WxUser();
			wxUser.setCellPhoneNo(phone);
			wxUserList = wxUserService.queryAll(wxUser);
		}
		if(wxUserList.isEmpty() && !StringUtils.isBlank(username)) {
			wxUser = new WxUser();
			wxUser.setUsername(username);
			wxUserList = wxUserService.queryAll(wxUser);
		}
		if(wxUserList.isEmpty()) {
			map.put("errorCode",0);
			map.put("message","电话号码或用户名错误");
			return map;
		}
		String password = wxUserList.get(0).getPassword();
		map.put("password", password);
		map.put("errorCode",1);
		map.put("message","获取密码成功，请重新登录！");
		return map;
	}
	
	/**
	 * 文章投稿
	 * @param content 投稿内容
	 * @param token 登陆token
	 */
	@RequestMapping("/articleApply")
	@ResponseBody
	public Map<String, Object> articleApply(String content, String token) {
		Map<String, Object> map = new HashMap<String, Object>();
		if(StringUtils.isBlank(content)) {
			map.put("errorCode",0);
			map.put("message","投稿内容不能为空");
			return map;
		}
		if(!StringUtils.isBlank(token)) {
			if(LOGIN_MAP.containsKey(token)) {
				//TODO 保存数据库
				map.put("errorCode",1);
				map.put("message","投稿成功");
				return map;
			}else {
				map.put("errorCode",0);
				map.put("message","token已失效，请重新登录");
				return map;
			}
		}else {
			//TODO 保存数据库
			map.put("errorCode",1);
			map.put("message","投稿成功");
			return map;
		}
	}
	
	private List<WxUserOperation> getWxUserOperationList(int id, int userId, String operateType, int operateId, String description) {
		WxUserOperation t = new WxUserOperation();
		if(id>0) {
			t.setId(id);
		}
		if(userId>0) {
			t.setUserId(userId);	
		}
		if(operateId>0) {
			t.setOperateId(operateId);
		}
		if(StringUtils.isNotBlank(operateType)) {
			t.setOperateType(operateType);
		}
		if(StringUtils.isNotBlank(description)) {
			t.setDescription(description);
		}
		List<WxUserOperation> wxUserOperationList = wxUserOperationService.queryAll(t);
		if(wxUserOperationList==null) {
			wxUserOperationList = new ArrayList<WxUserOperation>();
		}
		return wxUserOperationList;
	}
	
	/**
	 * 栏目订阅
	 * @param class_id 栏目id
	 * @param token 登陆token
	 */
	@RequestMapping("/subscribeClass")
	@ResponseBody
	public Map<String, Object> subscribeClass(String class_id, String token) {
		Map<String, Object> map = new HashMap<String, Object>();
		WxAccType wxAccType = null;
		if(StringUtils.isBlank(class_id)) {
			map.put("errorCode",0);
			map.put("message","栏目不能为空");
			return map;
		}else {
			wxAccType = wxAccTypeService.getById(class_id);
			if(wxAccType==null) {
				map.put("errorCode",0);
				map.put("message","该栏目不存在");
				return map;
			}
		}
		if(!StringUtils.isBlank(token)) {
			if(LOGIN_MAP.containsKey(token)) {
				//保存数据库
				int userId = LOGIN_MAP.get(token);
				List<WxUserOperation> wxUserOperationList = getWxUserOperationList(0, userId, OperateTypeEnum.subscribeClass.name(), wxAccType.getId(), null);
				if(wxUserOperationList.size()>0) {
					map.put("errorCode",1);
					map.put("message","订阅成功");
					return map;
				}else {
					WxUserOperation wxUserOperation = new WxUserOperation();
					wxUserOperation.setUserId(userId);
					wxUserOperation.setOperateType(OperateTypeEnum.subscribeClass.name());
					wxUserOperation.setOperateId(wxAccType.getId());
					wxUserOperation.setDescription(null);
					wxUserOperation.setCreateDate(new Date());
					try{
						wxUserOperationService.add(wxUserOperation);
					}catch(Exception e) {
						LOG.error(e);
					}
					map.put("errorCode",1);
					map.put("message","订阅成功");
					return map;
				}
			}else {
				map.put("errorCode",0);
				map.put("message","token已失效，请重新登录");
				return map;
			}
		}else {
			map.put("errorCode",0);
			map.put("message","登陆后才能订阅，请登录");
			return map;
		}
	}
	
	/**
	 * 关键字订阅
	 * @param keyword 订阅的关键字
	 * @param token 登陆token
	 */
	@RequestMapping("/subscribeKeyword")
	@ResponseBody
	public Map<String, Object> subscribeKeyword(String keyword, String token) {
		Map<String, Object> map = new HashMap<String, Object>();
		if(StringUtils.isBlank(keyword)) {
			map.put("errorCode",0);
			map.put("message","关键字不能为空");
			return map;
		}
		if(!StringUtils.isBlank(token)) {
			if(LOGIN_MAP.containsKey(token)) {
				//保存数据库
				int userId = LOGIN_MAP.get(token);
				List<WxUserOperation> wxUserOperationList = getWxUserOperationList(0, userId, OperateTypeEnum.subscribeKeyword.name(), 0, keyword);
				if(wxUserOperationList.size()>0) {
					map.put("errorCode",1);
					map.put("message","订阅成功");
					return map;
				}else {
					WxUserOperation wxUserOperation = new WxUserOperation();
					wxUserOperation.setUserId(userId);
					wxUserOperation.setOperateType(OperateTypeEnum.subscribeKeyword.name());
					wxUserOperation.setOperateId(0);
					wxUserOperation.setDescription(keyword);
					wxUserOperation.setCreateDate(new Date());
					try{
						wxUserOperationService.add(wxUserOperation);
					}catch(Exception e) {
						LOG.error(e);
					}
					map.put("errorCode",1);
					map.put("message","订阅成功");
					return map;
				}
			}else {
				map.put("errorCode",0);
				map.put("message","token已失效，请重新登录");
				return map;
			}
		}else {
			map.put("errorCode",0);
			map.put("message","登陆后才能订阅，请登录");
			return map;
		}
	}
	
	/**
	 * 给文章点赞
	 * @param wxArticleId 文章id
	 * @param token 登陆token
	 */
	@RequestMapping("/likeButton")
	@ResponseBody
	public Map<String, Object> likeButton(String wxArticleId, String token) {
		Map<String, Object> map = new HashMap<String, Object>();
		WxArticle wxArticle = null;
		if(StringUtils.isBlank(wxArticleId)) {
			map.put("errorCode",0);
			map.put("message","文章不能为空");
			return map;
		}else {
			wxArticle = wxArticleService.getById(wxArticleId);
			if(wxArticle==null) {
				map.put("errorCode",0);
				map.put("message","该文章不存在");
				return map;
			}
		}
		if(!StringUtils.isBlank(token)) {
			if(LOGIN_MAP.containsKey(token)) {
				//保存数据库
				int userId = LOGIN_MAP.get(token);
				List<WxUserOperation> wxUserOperationList = getWxUserOperationList(0, userId, OperateTypeEnum.likeButton.name(), Integer.valueOf(wxArticleId), null);
				if(wxUserOperationList.size()>0) {
					map.put("errorCode",1);
					map.put("message","点赞成功");
					return map;
				}else {
					WxUserOperation wxUserOperation = new WxUserOperation();
					wxUserOperation.setUserId(userId);
					wxUserOperation.setOperateType(OperateTypeEnum.likeButton.name());
					wxUserOperation.setOperateId(Integer.valueOf(wxArticleId));
					wxUserOperation.setDescription(null);
					wxUserOperation.setCreateDate(new Date());
					try{
						wxUserOperationService.add(wxUserOperation);
					}catch(Exception e) {
						LOG.error(e);
					}
					map.put("errorCode",1);
					map.put("message","点赞成功");
					return map;
				}
			}else {
				map.put("errorCode",0);
				map.put("message","token已失效，请重新登录");
				return map;
			}
		}else {
			map.put("errorCode",0);
			map.put("message","登陆后才能点赞，请登录");
			return map;
		}
	}
	
	
	/**
	 * 收藏文章
	 * @param wxArticleId 文章id
	 * @param token 登陆token
	 */
	@RequestMapping("/collectionArticle")
	@ResponseBody
	public Map<String, Object> collectionArticle(String wxArticleId, String token) {
		Map<String, Object> map = new HashMap<String, Object>();
		WxArticle wxArticle = null;
		if(StringUtils.isBlank(wxArticleId)) {
			map.put("errorCode",0);
			map.put("message","文章不能为空");
			return map;
		}else {
			wxArticle = wxArticleService.getById(wxArticleId);
			if(wxArticle==null) {
				map.put("errorCode",0);
				map.put("message","该文章不存在");
				return map;
			}
		}
		if(!StringUtils.isBlank(token)) {
			if(LOGIN_MAP.containsKey(token)) {
				//保存数据库
				int userId = LOGIN_MAP.get(token);
				List<WxUserOperation> wxUserOperationList = getWxUserOperationList(0, userId, OperateTypeEnum.collectionArticle.name(), Integer.valueOf(wxArticleId), null);
				if(wxUserOperationList.size()>0) {
					map.put("errorCode",1);
					map.put("message","收藏成功");
					return map;
				}else {
					WxUserOperation wxUserOperation = new WxUserOperation();
					wxUserOperation.setUserId(userId);
					wxUserOperation.setOperateType(OperateTypeEnum.collectionArticle.name());
					wxUserOperation.setOperateId(Integer.valueOf(wxArticleId));
					wxUserOperation.setDescription(null);
					wxUserOperation.setCreateDate(new Date());
					try{
						wxUserOperationService.add(wxUserOperation);
					}catch(Exception e) {
						LOG.error(e);
					}
					map.put("errorCode",1);
					map.put("message","收藏成功");
					return map;
				}
			}else {
				map.put("errorCode",0);
				map.put("message","token已失效，请重新登录");
				return map;
			}
		}else {
			map.put("errorCode",0);
			map.put("message","登陆后才能收藏，请登录");
			return map;
		}
	}
	
	/**
	 * 文章阅读数加一
	 * @param wxArticleId 文章id
	 */
	@RequestMapping("/increaseReadNum")
	@ResponseBody
	public Map<String, Object> increaseReadNum(String wxArticleId) {
		Map<String, Object> map = new HashMap<String, Object>();
		WxArticle wxArticle = null;
		if(StringUtils.isBlank(wxArticleId)) {
			map.put("errorCode",0);
			map.put("message","文章不能为空");
			return map;
		}else {
			wxArticle = wxArticleService.getById(wxArticleId);
			if(wxArticle==null) {
				map.put("errorCode",0);
				map.put("message","该文章不存在");
				return map;
			}
		}
		//保存数据库
		WxArticle update_wxArticle = new WxArticle();
		update_wxArticle.setId(wxArticle.getId());
		update_wxArticle.setReadNum(wxArticle.getReadNum()+1);
		try {
			wxArticleService.update(update_wxArticle);
		} catch (Exception e) {
			LOG.error(e);
		}
		map.put("errorCode",1);
		map.put("message","阅读数加一成功");
		return map;
	}

	/** 
	 * 获取指定配置文件中所以的数据 
	 * @param propertyName 
	 *        调用方式： 
	 *            1.配置文件放在resource源包下，不用加后缀 
	 *              PropertiesUtil.getAllMessage("message"); 
	 *            2.放在包里面的 
	 *              PropertiesUtil.getAllMessage("com.test.message"); 
	 * @return 
	 */  
	public static Map<String, String> getAllProperties(String propertyName) {  
	    // 获得资源包  
	    ResourceBundle rb = ResourceBundle.getBundle(propertyName.trim());  
	    // 通过资源包拿到所有的key  
	    Enumeration<String> allKey = rb.getKeys();  
	    // 遍历key 得到 value  
	    Map<String, String> map = new HashMap<String, String>();  
	    while (allKey.hasMoreElements()) {  
	        String key = allKey.nextElement();  
	        String value = (String) rb.getString(key);  
	        map.put(key, value);
	    }  
	    return map;  
	}  
}
