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
 * @author lanyuan
 * 2013-11-19
 * @Email: mmm333zzz520@163.com
 * @version 1.0v
 */
@Controller
@RequestMapping("/background/wxaccount/")
public class WxAccountController extends BaseController{
	@Inject
	private WxAccountService wxAccountService;
	
	@Inject
	private WxAccTypeService wxAccTypeService;
	
	@Inject
	private CityService cityService;
	
	@RequestMapping("list")
	public String list(Model model, Resources menu, String pageNow) {
		return Common.BACKGROUND_PATH+"/wxaccount/list";
	}
	/**
	 * @param model
	 * 存放返回界面的model
	 * @return
	 */
	@ResponseBody
	@RequestMapping("query")
	public PageView query(WxAccount account,String pageNow,String pagesize) {
		pageView = wxAccountService.query(getPageView(pageNow,pagesize), account);
		return pageView;
	}
	@RequestMapping("exportExcel")
	public void exportExcel(HttpServletResponse response,WxAccount account) {
		 List<WxAccount> acs =wxAccountService.queryAll(account);
		POIUtils.exportToExcel(response, "微信公众号报表", acs, WxAccount.class, "账号", acs.size());
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
	public Map<String, Object> add(WxAccount account) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			//account.setPassword(Md5Tool.getMd5(account.getPassword()));
			wxAccountService.add(account);
			wxAccountService.addwxinfo(account);
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
		List<WxAccType> wxAccTypeList = wxAccTypeService.queryAll(new WxAccType());
		model.addAttribute("wxAccTypeList", wxAccTypeList);
		List<City> cityList = cityService.queryAll(new City());
		model.addAttribute("cityList", cityList);
		return Common.BACKGROUND_PATH+"/wxaccount/add";
	}
	
	/**
	 * 账号角色页面
	 * 微信公众号不存在该method
	 * @param model
	 * @return
	 */
	@RequestMapping("accRole")
	public String accRole(Model model,String wxAccountName,String roleName) {

		try {
			wxAccountName=java.net.URLDecoder.decode(wxAccountName,"UTF-8");  
			roleName= java.net.URLDecoder.decode(roleName,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			
		} 
		model.addAttribute("wxAccountName", wxAccountName);
		model.addAttribute("roleName", roleName);
		
		return Common.BACKGROUND_PATH+"/wxaccount/acc_role";
	}
	
	/**
	 * 跑到新增界面
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("editUI")
	public String editUI(Model model,String accountId) {
		WxAccount account = wxAccountService.getById(accountId);
		model.addAttribute("wxAccount", account);
		return Common.BACKGROUND_PATH+"/wxaccount/edit";
	}
	
	
	@RequestMapping("executecapture")
	@ResponseBody
	public Map<String, Object> executecapture(Model model, String ids) {
		Map<String, Object> map = new HashMap<String, Object>();
		WxAccount wxAccount = new WxAccount();
		String wxaccountStr = "";
		try {
			String id[] = ids.split(",");
			for (String string : id) {
				if(!Common.isEmpty(string)){
					wxAccount = wxAccountService.getById(string);
					wxaccountStr += wxAccount.getWxAccountNo() + ","; 
				}
			}
			String surl = "http://localhost:8000/gather/remote_scan_article/"+wxaccountStr;
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(surl);
			HttpResponse resp = httpClient.execute(httpget);
			System.out.println("请求: " + httpget.getRequestLine()); 
			System.out.println("response value is: "+ resp.toString());
			map.put("flag", "true");
			
		} catch (Exception e) {
			map.put("flag", "false");
			e.printStackTrace();
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
		WxAccount account = wxAccountService.isExist(name);
		if(account == null){
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
				wxAccountService.delete(string);
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
					WxAccount account = new WxAccount();
					account.setId(Integer.parseInt(string));
					account.setState(state);
					wxAccountService.update(account);
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
	public Map<String, Object> update(Model model, WxAccount wxAccount) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
	
			//account.setPassword(Md5Tool.getMd5(account.getPassword()));
			wxAccountService.update(wxAccount);
			map.put("flag", "true");
		} catch (Exception e) {
			map.put("flag", "false");
		}
		return map;
	}
}