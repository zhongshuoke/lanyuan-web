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
import com.lanyuan.entity.TypePic;
import com.lanyuan.entity.WxAccType;
import com.lanyuan.entity.WxArticle;
import com.lanyuan.service.AdvertisementService;
import com.lanyuan.service.WxAccTypeService;
import com.lanyuan.service.WxArticleService;

@Controller
@RequestMapping("/querydata")
public class WxCommArticleListController extends BaseCommonController{
    
	@Inject
	private WxArticleService wxArticleService;
	@Inject
	private AdvertisementService advertisementService;
	@Inject
	private WxAccTypeService wxAccTypeService;
	
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
	 * @param class_id
	 * @param limit
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
	
}
