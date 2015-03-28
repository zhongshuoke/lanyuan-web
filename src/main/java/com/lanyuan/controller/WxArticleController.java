package com.lanyuan.controller;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lanyuan.entity.WxArticle;
import com.lanyuan.entity.Resources;
import com.lanyuan.pulgin.mybatis.plugin.PageView;
import com.lanyuan.sensitivew.SensitivewordFilter;
import com.lanyuan.service.WxArticleService;
import com.lanyuan.util.Common;
import com.lanyuan.util.Md5Tool;
import com.lanyuan.util.POIUtils;

/**
 * 
 * @author caokun
 * 2015-01-25
 * @version 1.0v
 */
@Controller
@RequestMapping("/background/wxarticle/")
public class WxArticleController extends BaseController{
	@Inject
	private WxArticleService wxArticleService;
	
	@RequestMapping("list")
	public String list(Model model, Resources menu, String pageNow) {
		return Common.BACKGROUND_PATH+"/wxarticle/list";
	}
	/**
	 * @param model
	 * 存放返回界面的model
	 * @return
	 */
	@ResponseBody
	@RequestMapping("query")
	public PageView query(WxArticle wxArticle,String pageNow,String pagesize) {
		pageView = wxArticleService.query(getPageView(pageNow,pagesize), wxArticle);
		return pageView;
	}
	
	@RequestMapping("exportExcel")
	public void exportExcel(HttpServletResponse response,WxArticle wxArticle) {
		 List<WxArticle> acs =wxArticleService.queryAll(wxArticle);
		POIUtils.exportToExcel(response, "微信公众号报表", acs, WxArticle.class, "账号", acs.size());
	}
	/**
	 * 保存数据
	 * 
	 * @param model
	 * @param videoType
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("add")
	@ResponseBody
	public Map<String, Object> add(WxArticle wxArticle) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			//wxArticle.setPassword(Md5Tool.getMd5(wxArticle.getPassword()));
			wxArticleService.add(wxArticle);
			map.put("flag", "true");
		} catch (Exception e) {
			map.put("flag", "false");
		}
		return map;
	}

	
	/**
	 * 跑到新增界面
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("addUI")
	public String addUI(Model model,String articleId) {
		WxArticle wxArticle = wxArticleService.getById(articleId);
		model.addAttribute("wxArticle", wxArticle);
		return Common.BACKGROUND_PATH+"/wxarticle/add";
	}
	
	/**
	 * 跑到编辑界面
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("editUI")
	public String editUI(Model model,String articleId) {
		WxArticle wxArticle = wxArticleService.getById(articleId);
		model.addAttribute("wxArticle", wxArticle);
		return Common.BACKGROUND_PATH+"/wxarticle/edit";
	}
	
	@RequestMapping("queryhtml")
	@ResponseBody
	public Map<String, Object> queryHtmlById(String id) {
		WxArticle wxArticle = wxArticleService.getById(id);
		Map<String, Object> map = new HashMap<String, Object>();
		if (wxArticle!=null){
			map.put("flag", "true");
			map.put("content", wxArticle.getContent());
		} else {
			map.put("flag", "false");
		}
		return map;
	}
	
	@RequestMapping("savehtml")
	@ResponseBody
	public Map<String, Object> saveHtmlById(String id, String content) {
		SensitivewordFilter filter = new SensitivewordFilter();
		System.out.println("敏感词的数量：" + filter.getSensitiveWordMap().size());
		System.out.println("待检测语句字数：" + content.length());  
        long beginTime = System.currentTimeMillis();  
        Set<String> set = filter.getSensitiveWord(content, 1);  
        long endTime = System.currentTimeMillis();
        String senwchkresult = "语句中包含敏感词的个数为：" + set.size() + "。包含：" + set;
        System.out.println("语句中包含敏感词的个数为：" + set.size() + "。包含：" + set);  
        System.out.println("总共消耗时间为：" + (endTime - beginTime)); 
		WxArticle wxArticle = new WxArticle();
		wxArticle.setId(Integer.parseInt(id));
		wxArticle.setContent(content);
		Map<String, Object> map = new HashMap<String, Object>();
		if (0==set.size()){
			try {
				wxArticleService.update(wxArticle);
				map.put("flag", "true");
				
			} catch (Exception e) {
				map.put("flag", "false");
				e.printStackTrace();
			}
		}
		else{
			map.put("senwchkresult", senwchkresult);
			map.put("flag", "false");
		}
		return map;
	}	
	
	/**
	 * 验证账号是否存在
	 * @author lanyuan
	 * Email：mmm333zzz520@163.com
	 * date：2014-2-19
	 * @param name
	 * @return
	 */
	@RequestMapping("isExist")
	@ResponseBody
	public boolean isExist(String name){
		WxArticle wxArticle = wxArticleService.isExist(name);
		if(wxArticle == null){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 删除
	 * 
	 * @param model
	 * @param videoTypeId
	 * @return
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping("deleteById")
	public Map<String, Object> deleteById(Model model, String ids) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			String id[] = ids.split(",");
			for (String string : id) {
				if(!Common.isEmpty(string)){
				wxArticleService.delete(string);
				}
			}
			map.put("flag", "true");
		} catch (Exception e) {
			map.put("flag", "false");
		}
		return map;
	}
	/**
	 * 删除
	 * 
	 * @param model
	 * @param videoTypeId
	 * @return
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping("updateState")
	public Map<String, Object> updateState(Model model, String ids,String state) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			String id[] = ids.split(",");
			for (String string : id) {
				if(!Common.isEmpty(string)){
					WxArticle wxArticle = new WxArticle();
					wxArticle.setId(Integer.parseInt(string));
					//wxArticle.setState(state);
					wxArticleService.update(wxArticle);
				}
			}
			map.put("flag", "true");
		} catch (Exception e) {
			map.put("flag", "false");
		}
		return map;
	}
	/**
	 * 更新类型
	 * 
	 * @param model
	 * @return
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping("update")
	public Map<String, Object> update(Model model, WxArticle wxArticle) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
	
			//wxArticle.setPassword(Md5Tool.getMd5(wxArticle.getPassword()));
			wxArticleService.update(wxArticle);
			map.put("flag", "true");
		} catch (Exception e) {
			map.put("flag", "false");
		}
		return map;
	}
}