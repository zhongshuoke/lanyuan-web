package com.lanyuan.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import com.lanyuan.controller.common.BaseCommonController;
import com.lanyuan.entity.TypePic;
import com.lanyuan.entity.WxArticle;
import com.lanyuan.service.WxArticleService;
import com.lanyuan.util.POIUtils;

@Controller
@RequestMapping("/querydata")
public class WxCommArticleListController extends BaseCommonController{
    
	@Inject
	private WxArticleService wxArticleService;
	
	/**
	 * 返回通用栏文章列表
	 * @param response
	 * @param wxArticle
	 */
	@RequestMapping("/getCommArticleList")
	@ResponseBody
	//, @RequestParam(value ="class_id") String class_id
	public Map<String, Object> getCommArticleList(HttpServletRequest request, HttpServletResponse response, String data_type, String class_id, String max_article_id, String keyword, String page, String limit) {
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
	 * 返回单篇文章Id和Content
	 * @param response
	 * @param wxArticle
	 */
	@RequestMapping("/querySingleCommArticle")
	@ResponseBody
	public Map<String, Object> querySingleCommArticle(HttpServletResponse response,WxArticle wxArticle) {
		Map<String, Object> map = new HashMap<String, Object>();
		WxArticle wa = wxArticleService.querySingleArticle("考研解放去哪里!");
		wa.setContent(HtmlUtils.htmlEscape(wa.getContent()));
		map.put("singleArticle",wa );
		map.put("errorCode",1);
		map.put("result",null);
		return map;
	}
}
