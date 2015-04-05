package com.lanyuan.controller;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lanyuan.entity.City;
import com.lanyuan.entity.WxAccType;
import com.lanyuan.entity.WxAccount;
import com.lanyuan.entity.Resources;
import com.lanyuan.entity.WxArticle;
import com.lanyuan.pulgin.mybatis.plugin.PageView;
import com.lanyuan.service.CityService;
import com.lanyuan.service.WxAccTypeService;
import com.lanyuan.service.WxAccountService;
import com.lanyuan.util.Common;
import com.lanyuan.util.Md5Tool;
import com.lanyuan.util.POIUtils;

/**
 * 
 * @author caokun
 * 2015-04-05
 */
@Controller
@RequestMapping("/background/city/")
public class CityController extends BaseController{
	@Inject
	private CityService cityService;
	
	@RequestMapping("list")
	public String list(Model model, Resources menu, String pageNow) {
		return Common.BACKGROUND_PATH+"/city/list";
	}
	/**
	 * @param model
	 * 存放返回界面的model
	 * @return
	 */
	@ResponseBody
	@RequestMapping("query")
	public PageView query(City city,String pageNow,String pagesize) {
		pageView = cityService.query(getPageView(pageNow,pagesize), city);
		return pageView;
	}
	
	@RequestMapping("exportExcel")
	public void exportExcel(HttpServletResponse response,City city) {
		 List<City> cs =cityService.queryAll(city);
		POIUtils.exportToExcel(response, "城市列表", cs, City.class, "城市", cs.size());
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
	public Map<String, Object> add(City city) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			//account.setPassword(Md5Tool.getMd5(account.getPassword()));
			cityService.add(city);
			map.put("flag", "true");
		} catch (Exception e) {
			map.put("flag", "false");
		}
		return map;
	}

	
	/**
	 * 跳转到新增界面
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("addUI")
	public String addUI(Model model) {
		List<City> cityList = cityService.queryAll(new City());
		model.addAttribute("cityList", cityList);
		return Common.BACKGROUND_PATH+"/city/add";
	}
	
	/**
	 * 跑到编辑界面
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("editUI")
	public String editUI(Model model,String cityId) {
		City city = cityService.getById(cityId);
		model.addAttribute("city", city);
		return Common.BACKGROUND_PATH+"/city/edit";
	}
	
	/**
	 * 验证城市ID是否存在
	 * @author caokun
	 * @param id
	 * @return
	 */
	@RequestMapping("isExist")
	@ResponseBody
	public boolean isExist(int id){
		City city = cityService.isExist(id);
		if(city == null){
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
					cityService.delete(string);
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
	public Map<String, Object> update(Model model, City city) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			cityService.update(city);
			map.put("flag", "true");
		} catch (Exception e) {
			map.put("flag", "false");
		}
		return map;
	}
}