package com.lanyuan.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.lanyuan.entity.TypePic;
import com.lanyuan.entity.WxAccType;
import com.lanyuan.entity.WxArticle;
import com.lanyuan.service.AdvertisementService;
import com.lanyuan.service.CityService;
import com.lanyuan.service.WxAccTypeService;
import com.lanyuan.service.WxArticleService;
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
		map.put("content",HtmlUtils.htmlEscape(wa.getContent()));
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
		
		Advertisement advertisement = new Advertisement();
		advertisement.setId(1);
		advertisement.setTitle("KFC广告");
		advertisement.setUrl("http://zhidao.baidu.com/daily/view?id=4071");
		advertisement.setCreateTime(new Date());
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
	 * 文章阅读数加一
	 * @param wxArticleId 文章id
	 */
	@RequestMapping("/increaseReadNum")
	@ResponseBody
	public Map<String, Object> increaseReadNum(String wxArticleId) {
		Map<String, Object> map = new HashMap<String, Object>();
		if(StringUtils.isBlank(wxArticleId)) {
			map.put("errorCode",0);
			map.put("message","文章不能为空");
			return map;
		}
		//TODO 保存数据库
		map.put("errorCode",1);
		map.put("message","阅读数加一成功");
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
//		Map<String, Object> map = new HashMap<String, Object>();
//		WxArticle wa = wxArticleService.getById(wxArticleId);
//		WxArticle wxArticle = new WxArticle();
//		wxArticle.setWxAccountNo(wa.getWxAccountNo());	//TODO
//		List<WxArticle> moreRecommendArticleList = wxArticleService.queryAll(wxArticle);
//		if(moreRecommendArticleList.size()>3) {//删除自己，以后再说 TODO
//			map.put("moreRecommendArticleList",moreRecommendArticleList.subList(0, 3));
//		}else {
//			map.put("moreRecommendArticleList",moreRecommendArticleList);
//		}
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
		String token = MD5.md5(username+password+new Date().getTime());
		map.put("token", token);
		map.put("errorCode",1);
		map.put("message","注册成功");
		LOGIN_MAP.put(token, (int)(Math.random() * 10000));	//用户id写sql查 TODO
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
		if(StringUtils.isBlank(phone) && StringUtils.isBlank(username)) {//如果输入的是phone要转换成username，统一做MD5的token
			map.put("errorCode",0);
			map.put("message","手机号和用户名不能都为空");
			return map;
		}
		if(StringUtils.isBlank(password)) {
			map.put("errorCode",0);
			map.put("message","密码不能为空");
			return map;
		}
		String token = MD5.md5(username+password+new Date().getTime());
		map.put("token", token);
		map.put("errorCode",1);
		map.put("message","登陆成功");
		LOGIN_MAP.put(token, (int)(Math.random() * 10000));	//用户id写sql查 TODO
		return map;
	}
	
	
	/**
	 * 用户登录
	 * @param phone 手机号
	 * @param username 用户名
	 * @param verifyCode 验证码
	 */
	@RequestMapping("/login")
	@ResponseBody
	public Map<String, Object> findPassword(String phone, String username, String verifyCode) {
		Map<String, Object> map = new HashMap<String, Object>();
		if(StringUtils.isBlank(phone) && StringUtils.isBlank(username)) {//如果输入的是phone要转换成username，统一做MD5的token
			map.put("errorCode",0);
			map.put("message","手机号和用户名不能都为空");
			return map;
		}
		if(StringUtils.isBlank(verifyCode)) {
			map.put("errorCode",0);
			map.put("message","验证码不能为空");
			return map;
		}
		//
		if(verifyCode.equals(verifyCode)) {	//TODO 数据库数据
			
		}
		String password = "testPWD";
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
	
	/**
	 * 栏目订阅
	 * @param class_id 栏目id
	 * @param token 登陆token
	 */
	@RequestMapping("/subscribeClass")
	@ResponseBody
	public Map<String, Object> subscribeClass(String class_id, String token) {
		Map<String, Object> map = new HashMap<String, Object>();
		if(StringUtils.isBlank(class_id)) {
			map.put("errorCode",0);
			map.put("message","请选择栏目");
			return map;
		}
		if(!StringUtils.isBlank(token)) {
			if(LOGIN_MAP.containsKey(token)) {
				//TODO 保存数据库
				map.put("errorCode",1);
				map.put("message","订阅成功");
				return map;
			}else {
				map.put("errorCode",0);
				map.put("message","token已失效，请重新登录");
				return map;
			}
		}else {
			//TODO 保存数据库
			map.put("errorCode",1);
			map.put("message","订阅成功");
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
				//TODO 保存数据库
				map.put("errorCode",1);
				map.put("message","订阅成功");
				return map;
			}else {
				map.put("errorCode",0);
				map.put("message","token已失效，请重新登录");
				return map;
			}
		}else {
			//TODO 保存数据库
			map.put("errorCode",1);
			map.put("message","订阅成功");
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
		if(StringUtils.isBlank(wxArticleId)) {
			map.put("errorCode",0);
			map.put("message","文章不能为空");
			return map;
		}
		if(!StringUtils.isBlank(token)) {
			if(LOGIN_MAP.containsKey(token)) {
				//TODO 保存数据库
				map.put("errorCode",1);
				map.put("message","点赞成功");
				return map;
			}else {
				map.put("errorCode",0);
				map.put("message","token已失效，请重新登录");
				return map;
			}
		}else {
			//TODO 保存数据库
			map.put("errorCode",1);
			map.put("message","点赞成功");
			return map;
		}
	}
	
	
	/**
	 * 给文章点赞
	 * @param wxArticleId 文章id
	 * @param token 登陆token
	 */
	@RequestMapping("/collectionArticle")
	@ResponseBody
	public Map<String, Object> collectionArticle(String wxArticleId, String token) {
		Map<String, Object> map = new HashMap<String, Object>();
		if(StringUtils.isBlank(wxArticleId)) {
			map.put("errorCode",0);
			map.put("message","文章不能为空");
			return map;
		}
		if(!StringUtils.isBlank(token)) {
			if(LOGIN_MAP.containsKey(token)) {
				//TODO 保存数据库
				map.put("errorCode",1);
				map.put("message","收藏成功");
				return map;
			}else {
				map.put("errorCode",0);
				map.put("message","token已失效，请重新登录");
				return map;
			}
		}else {
			//TODO 保存数据库
			map.put("errorCode",1);
			map.put("message","收藏成功");
			return map;
		}
	}

}
