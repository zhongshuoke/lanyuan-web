package com.lanyuan.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.lanyuan.entity.Resources;
import com.lanyuan.entity.WxRecommendToday;
import com.lanyuan.pulgin.mybatis.plugin.PageView;
import com.lanyuan.sensitivew.SensitivewordFilter;
import com.lanyuan.service.WxRecommendTodayService;
import com.lanyuan.uploadfilepath.UploadFilePathVO;
import com.lanyuan.util.Common;
import com.lanyuan.util.POIUtils;
import com.lanyuan.util.PropertiesUtils;
import com.lanyuan.util.RandomUtil;

/**
 * 
 * @author caokun
 * 2015-01-25
 * @version 1.0v
 */
@Controller
@RequestMapping("/background/wxrecommarticle/")
public class WxRecommArticleController extends BaseController{
	@Inject
	private WxRecommendTodayService wxRecommendTodayService;
	
	@RequestMapping("list")
	public String list(Model model, Resources menu, String pageNow) {
		return Common.BACKGROUND_PATH+"/wxrecommarticle/list";
	}
	/**
	 * @param model
	 * 存放返回界面的model
	 * @return
	 */
	@ResponseBody
	@RequestMapping("query")
	public PageView query(WxRecommendToday wxRecommendToday,String pageNow,String pagesize) {
		pageView = wxRecommendTodayService.query(getPageView(pageNow,pagesize), wxRecommendToday);
		return pageView;
	}
	
	@RequestMapping("exportExcel")
	public void exportExcel(HttpServletResponse response,WxRecommendToday wxRecommendToday) {
		 List<WxRecommendToday> acs =wxRecommendTodayService.queryAll(wxRecommendToday);
		POIUtils.exportToExcel(response, "微信今日推荐报表", acs, WxRecommendToday.class, "账号", acs.size());
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
	public Map<String, Object> add(WxRecommendToday wxRecommendToday) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			//wxArticle.setPassword(Md5Tool.getMd5(wxArticle.getPassword()));
			wxRecommendTodayService.add(wxRecommendToday);
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
	public String addUI(Model model,String recommarticleId) {
		WxRecommendToday wxRecommendToday = wxRecommendTodayService.getById(recommarticleId);
		model.addAttribute("wxRecommendToday", wxRecommendToday);
		return Common.BACKGROUND_PATH+"/wxrecommarticle/add";
	}
	
	/**
	 * 跳转到编辑界面
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("editUI")
	public String editUI(Model model,String recommarticleId) {
		WxRecommendToday wxRecommendToday = wxRecommendTodayService.getById(recommarticleId);
		model.addAttribute("wxRecommendToday", wxRecommendToday);
		return Common.BACKGROUND_PATH+"/wxrecommarticle/edit";
	}
	
	/**
	 * 跳转到编辑摘要界面
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("editAbstractUI")
	public String editAbstractUI(Model model,String recommarticleId) {
		WxRecommendToday wxRecommendToday = wxRecommendTodayService.getById(recommarticleId);
		model.addAttribute("wxRecommendToday", wxRecommendToday);
		return Common.BACKGROUND_PATH+"/wxrecommarticle/editabstract";
	}	
	
	/**
	 * 跳转到上传图片界面
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("uploadPicUI")
	public String uploadPicUI(Model model,String recommarticleId) {
		WxRecommendToday wxRecommendToday = wxRecommendTodayService.getById(recommarticleId);
		model.addAttribute("wxRecommendToday", wxRecommendToday);
		return Common.BACKGROUND_PATH+"/wxrecommarticle/uploadpic";
	}
	
	/**
	 * 将图片保存至服务器
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value="saveOrUpdatePic", method=RequestMethod.POST)
	@ResponseBody
	public Object saveOrUpdatePic(HttpServletRequest request,String recommarticleId) throws Exception{
		 Integer userID = 0;
	        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
	        MultipartFile multipartFile = multipartRequest.getFile("Filedata");
	        /** 写文件前先读出图片原始高宽 **/
	        byte[] bytes = multipartFile.getBytes();
	        InputStream is = new ByteArrayInputStream(bytes);
	        int width = 0; // 原始图片宽
	        int height = 0; // 原始图片高
	        String rePicWidth = PropertiesUtils.findPropertiesKey("rePicWidth");
	        String rePicHeight = PropertiesUtils.findPropertiesKey("rePicHeight");
	        try {
	            BufferedImage bufimg = ImageIO.read(is);
	            // 只有图片才获取高宽
	            if (bufimg != null) {
	                width = bufimg.getWidth();
	                height = bufimg.getHeight();
	                
	                if ( !StringUtils.equals(rePicWidth, String.valueOf(width))|| !StringUtils.equals(rePicHeight, String.valueOf(height))) {
	                	Map<String, Object> map = new HashMap<String, Object>();
	                	map.put("successs", false);
	                	map.put("errorMsg", "您上传的推荐文章图片失败,失败原因是:尺寸不正确，正确的应该是"+rePicWidth+" x "+rePicHeight);
	                	return map;
	                }
	            }
	            is.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	            is.close();
	            throw new Exception("uploadify上传图片读取图片高宽时发生异常!");
	        }

	        /** 拼成完整的文件保存路径加文件 **/
	        String originalFilename = multipartFile.getOriginalFilename(); // 文件全名
	        String suffix = StringUtils.substringAfter(originalFilename, "."); // 后缀
	        // 文件名转码
	        // fileName = Base64.StringToBase64(fileName);
	        // fileName = StringUtil.join(fileName, suffix);
	        UploadFilePathVO uploadFile = this.initFileUpload(userID, "pic", suffix, width, height);
	        File file = new File(uploadFile.getRealPath());
	        System.out.println("上传文件的路径为： "+ uploadFile.getRealPath());
	        WxRecommendToday wxRecommendToday = wxRecommendTodayService.getById(recommarticleId);
	        wxRecommendToday.setPicUrl(uploadFile.getRealPath());
	        wxRecommendTodayService.update(wxRecommendToday);
	        multipartFile.transferTo(file);

	    return uploadFile;
	}
	
	/**
     * 获取图片上传路径(处理高宽)
     *
     * @return
     */
    public static UploadFilePathVO initFileUpload(Integer userID, String imageType, String suffix, int width, int height) {
        String randomKey = RandomUtil.getRandomString(6);
        
        Date date = new Date();
        String dateStr = new SimpleDateFormat("yyyyMMdd").format(date);
        String timeStr = new SimpleDateFormat("HHmmssSSS").format(date);
        int hashcode = Math.abs(userID.hashCode() % 256);

        String picStorePath = PropertiesUtils.findPropertiesKey("picStorePath");
        String relativePath = StringUtils.join(imageType, "/", hashcode, "/", userID, "/", dateStr, "/");
        String realPath = StringUtils.join(picStorePath, "/", relativePath);

        File logoSaveFile = new File(realPath);
        if (!logoSaveFile.exists()) {
            logoSaveFile.mkdirs();
        }
        // 图片文件名: 时间戳 + 随机串 + 高宽
        String fileName = StringUtils.join(timeStr, randomKey, '_', height, '_', width, '.', suffix);
        UploadFilePathVO uploadFile = new UploadFilePathVO();
        uploadFile.setImgHeight(height);
        uploadFile.setImgWidth(width);
        uploadFile.setRelativePath(StringUtils.join(relativePath, fileName));
        uploadFile.setRealPath(StringUtils.join(realPath, fileName));
        uploadFile.setSuccess(true);
        uploadFile.setErrorMsg("上传图片成功！");
        return uploadFile;
    }
	
	@RequestMapping("queryhtml")
	@ResponseBody
	public Map<String, Object> queryHtmlById(String id) {
		WxRecommendToday wxRecommendToday = wxRecommendTodayService.getById(id);
		Map<String, Object> map = new HashMap<String, Object>();
		if (wxRecommendToday!=null){
			map.put("flag", "true");
			map.put("content", wxRecommendToday.getContent());
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
        WxRecommendToday wxRecommendToday = new WxRecommendToday();
        wxRecommendToday.setId(Integer.parseInt(id));
        wxRecommendToday.setContent(content);
		Map<String, Object> map = new HashMap<String, Object>();
		if (0==set.size()){
			try {
				wxRecommendTodayService.update(wxRecommendToday);
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
	public boolean isExist(int id){
		WxRecommendToday wxRecommendToday = wxRecommendTodayService.isExist(id);
		if(wxRecommendToday == null){
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
					wxRecommendTodayService.delete(string);
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
					WxRecommendToday wxRecommendToday = new WxRecommendToday();
					wxRecommendToday.setId(Integer.parseInt(string));
					//wxArticle.setState(state);
					wxRecommendTodayService.update(wxRecommendToday);
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
	public Map<String, Object> update(Model model, WxRecommendToday wxRecommendToday) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
	
			//wxArticle.setPassword(Md5Tool.getMd5(wxArticle.getPassword()));
			wxRecommendTodayService.update(wxRecommendToday);
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
	@RequestMapping("updateabstract")
	public Map<String, Object> updateabstract(Model model, WxRecommendToday wxRecommendToday) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
	
			//wxArticle.setPassword(Md5Tool.getMd5(wxArticle.getPassword()));
			wxRecommendTodayService.update(wxRecommendToday);
			map.put("flag", "true");
		} catch (Exception e) {
			map.put("flag", "false");
		}
		return map;
	}	
	
}