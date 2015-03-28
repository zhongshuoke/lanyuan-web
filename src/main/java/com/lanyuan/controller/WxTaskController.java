package com.lanyuan.controller;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lanyuan.entity.Resources;
import com.lanyuan.entity.WxAccType;
import com.lanyuan.entity.WxSchedule;
import com.lanyuan.entity.WxTask;
import com.lanyuan.pulgin.mybatis.plugin.PageView;
import com.lanyuan.service.WxScheduleService;
import com.lanyuan.service.WxTaskService;
import com.lanyuan.util.Common;
import com.lanyuan.util.Md5Tool;
import com.lanyuan.util.POIUtils;

@Controller
@RequestMapping("/background/wxtask/")
public class WxTaskController extends BaseController{
	@Inject
	private WxTaskService wxTaskService;
	
	@Inject
	private WxScheduleService wxScheduleService;
	
	@RequestMapping("list")
	public String list(Model model, Resources menu, String pageNow) {
		return Common.BACKGROUND_PATH+"/wxtask/list";
	}
	/**
	 * @param model
	 * 存放返回界面的model
	 * @return
	 */
	@ResponseBody
	@RequestMapping("query")
	public PageView query(WxTask wxTask,String pageNow,String pagesize) {
		pageView = wxTaskService.query(getPageView(pageNow,pagesize), wxTask);
		return pageView;
	}
	@RequestMapping("exportExcel")
	public void exportExcel(HttpServletResponse response,WxTask wxTask) {
		 List<WxTask> acs =wxTaskService.queryAll(wxTask);
		POIUtils.exportToExcel(response, "微信抓取任务报表", acs, WxTask.class, "账号", acs.size());
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
	public Map<String, Object> add(WxTask wxTask) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			//wxAccType.setPassword(Md5Tool.getMd5(wxAccType.getPassword()));
			wxTaskService.add(wxTask);
			wxTaskService.updateTasks();
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
		List<WxSchedule> wxScheduleList = wxScheduleService.queryAll(new WxSchedule());
		model.addAttribute("wxScheduleList", wxScheduleList);
		return Common.BACKGROUND_PATH+"/wxtask/add";
	}
	
	/**
	 * 账号角色页面
	 * 微信公众号类别不存在该method
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
		
		return Common.BACKGROUND_PATH+"/wxacctype/acc_role";
	}
	
	/**
	 * 跑到新增界面
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("editUI")
	public String editUI(Model model,String accountId) {
		WxTask wxTask = wxTaskService.getById(accountId);
		model.addAttribute("wxTask", wxTask);
		List<WxSchedule> wxScheduleList = wxScheduleService.queryAll(new WxSchedule());
		model.addAttribute("wxScheduleList", wxScheduleList);
		return Common.BACKGROUND_PATH+"/wxtask/edit";
	}
	

	@RequestMapping("isExist")
	@ResponseBody
	public boolean isExist(int crontab_id){
		WxTask wxTask = wxTaskService.isExist(crontab_id);
		if(wxTask == null){
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
					wxTaskService.delete(string);
				}
			}
			map.put("flag", "true");
		} catch (Exception e) {
			map.put("flag", "false");
		}
		return map;
	}
	/**
	 * 更新
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
					WxTask wxTask = new WxTask();
					wxTask.setId(Integer.parseInt(string));
					wxTaskService.update(wxTask);
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
	public Map<String, Object> update(Model model, WxTask wxTask) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
	
			//wxAccType.setPassword(Md5Tool.getMd5(wxAccType.getPassword()));
			wxTaskService.update(wxTask);
			map.put("flag", "true");
		} catch (Exception e) {
			map.put("flag", "false");
		}
		return map;
	}
}