package com.lanyuan.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.lanyuan.entity.Advertisement;
import com.lanyuan.entity.Resources;
import com.lanyuan.pulgin.mybatis.plugin.PageView;
import com.lanyuan.service.AdvertisementService;
import com.lanyuan.uploadfilepath.UploadFilePathVO;
import com.lanyuan.util.Common;
import com.lanyuan.util.POIUtils;
import com.lanyuan.util.RandomUtil;

@Controller
@RequestMapping("/background/advertisement/")
public class AdvertisementController extends BaseController{
	@Inject
	private AdvertisementService advertisementService;
	
	@RequestMapping("list")
	public String list(Model model, Resources menu, String pageNow) {
		return Common.BACKGROUND_PATH+"/advertisement/list";
	}
	/**
	 * @param model
	 * 存放返回界面的model
	 * @return
	 */
	@ResponseBody
	@RequestMapping("query")
	public PageView query(Advertisement advertisement,String pageNow,String pagesize) {
		pageView = advertisementService.query(getPageView(pageNow,pagesize), advertisement);
		return pageView;
	}
	@RequestMapping("exportExcel")
	public void exportExcel(HttpServletResponse response,Advertisement advertisement) {
		 List<Advertisement> acs =advertisementService.queryAll(advertisement);
		POIUtils.exportToExcel(response, "广告信息报表", acs, Advertisement.class, "账号", acs.size());
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
	public Map<String, Object> add(Advertisement advertisement) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			//wxAccType.setPassword(Md5Tool.getMd5(wxAccType.getPassword()));
			advertisementService.add(advertisement);
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
	public String addUI() {
		return Common.BACKGROUND_PATH+"/advertisement/add";
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
	public String editUI(Model model,String Id) {
		Advertisement advertisement = advertisementService.getById(Id);
		model.addAttribute("advertisement", advertisement);
		return Common.BACKGROUND_PATH+"/advertisement/edit";
	}
	
	/**
	 * 跳转到上传图片界面
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("uploadPicUI")
	public String uploadPicUI(Model model,String Id) {
		Advertisement advertisement = advertisementService.getById(Id);
		model.addAttribute("advertisement", advertisement);
		return Common.BACKGROUND_PATH+"/advertisement/uploadpic";
	}
	
	/**
	 * 将图片保存至服务器
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value="saveOrUpdatePic", method=RequestMethod.POST)
	@ResponseBody
	public Object saveOrUpdatePic(HttpServletRequest request,String Id) throws Exception{
		 Integer userID = 0;
	        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
	        MultipartFile multipartFile = multipartRequest.getFile("Filedata");
	        /** 写文件前先读出图片原始高宽 **/
	        byte[] bytes = multipartFile.getBytes();
	        InputStream is = new ByteArrayInputStream(bytes);
	        int width = 0; // 原始图片宽
	        int height = 0; // 原始图片高
	        try {
	            BufferedImage bufimg = ImageIO.read(is);
	            // 只有图片才获取高宽
	            if (bufimg != null) {
	                width = bufimg.getWidth();
	                height = bufimg.getHeight();
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
	        UploadFilePathVO uploadFile = this.initFileUpload(userID, "test", suffix, width, height);
	        File file = new File(uploadFile.getRealPath());
	        System.out.println("上传文件的路径为： "+ uploadFile.getRealPath());
	        Advertisement advertisement = advertisementService.getById(Id);
	        advertisement.setPicurl(uploadFile.getRealPath());
	        advertisementService.update(advertisement);
	        multipartFile.transferTo(file);

	    return uploadFile;
	}

	// 接受图片，返回图片地址
	private Object storeIOc(String picHref,HttpServletRequest request) throws Exception {
		Integer userID = 0;
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile multipartFile = multipartRequest.getFile("Filedata");
        /** 写文件前先读出图片原始高宽 **/
        byte[] bytes = multipartFile.getBytes();
        InputStream is = new ByteArrayInputStream(bytes);
        int width = 0; // 原始图片宽
        int height = 0; // 原始图片高
        try {
            BufferedImage bufimg = ImageIO.read(is);
            // 只有图片才获取高宽
            if (bufimg != null) {
                width = bufimg.getWidth();
                height = bufimg.getHeight();
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
        UploadFilePathVO uploadFile = this.initFileUpload(userID, "test", suffix, width, height);
        File file1 = new File(uploadFile.getRealPath());
        multipartFile.transferTo(file1);

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

        String relativePath = StringUtils.join(imageType, "/", hashcode, "/", userID, "/", dateStr, "/");
        String realPath = StringUtils.join("", "/", relativePath);

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
        return uploadFile;
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
	public boolean isExist(int Id){
		Advertisement advertisement = advertisementService.isExist(Id);
		if(advertisement == null){
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
					advertisementService.delete(string);
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
					Advertisement advertisement = new Advertisement();
					advertisement.setId(Integer.parseInt(string));
					advertisementService.update(advertisement);
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
	public Map<String, Object> update(Model model, Advertisement advertisement) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
	
			//wxAccType.setPassword(Md5Tool.getMd5(wxAccType.getPassword()));
			advertisementService.update(advertisement);
			map.put("flag", "true");
		} catch (Exception e) {
			map.put("flag", "false");
		}
		return map;
	}
}