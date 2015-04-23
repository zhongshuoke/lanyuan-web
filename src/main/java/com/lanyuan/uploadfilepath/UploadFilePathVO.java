package com.lanyuan.uploadfilepath;

public class UploadFilePathVO {

	public String realPath;	//绝对路径
    public String relativePath;	//相对路径
    private int imgHeight; // 上传图片的高
    private int imgWidth; // 宽
    private boolean success;//上传是否成功标志
    private String errorMsg;//提示

    public String getRealPath() {
		return realPath;
	}
	public void setRealPath(String realPath) {
		this.realPath = realPath;
	}
	public String getRelativePath() {
		return relativePath;
	}
	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}
	public int getImgHeight() {
		return imgHeight;
	}
	public void setImgHeight(int imgHeight) {
		this.imgHeight = imgHeight;
	}
	public int getImgWidth() {
		return imgWidth;
	}
	public void setImgWidth(int imgWidth) {
		this.imgWidth = imgWidth;
	}
	@Override
	public String toString() {
		return "UploadFilePathVO [realPath=" + realPath + ", relativePath="
				+ relativePath + ", imgHeight=" + imgHeight + ", imgWidth="
				+ imgWidth + "]";
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	
}
