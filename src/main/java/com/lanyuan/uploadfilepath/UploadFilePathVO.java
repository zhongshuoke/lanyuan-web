package com.lanyuan.uploadfilepath;

public class UploadFilePathVO {

	public String realPath;
    public String relativePath;
    private int imgHeight; // 上传图片的高
    private int imgWidth; // 宽 

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
	
	
}
