<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="/common/header.jsp"%>
  <link href="${ctx}/uploadify/uploadify.css" rel="stylesheet" type="text/css" />
  <script type="text/javascript" src="${ctx}/uploadify/jquery.uploadify.min.js"></script>
  <script type="text/javascript" src="${ctx}/uploadify/jquery.uploadify.js"></script>
</head>
<body>
<input id="acctypeid" type="hidden" value="${wxAccType.id}">
<input id="file_upload" type="file" name="file"/>
<a href="javascript:$('#file_upload').uploadify('upload', '*')">上传文件</a> | <a href="javascript:$('#file_upload').uploadify('stop')">停止上传!</a>
<script type="text/javascript">

$(document).ready(function(){
    $(function() {
        $('#file_upload').uploadify({
            'swf'      : '${ctx}/uploadify/uploadify.swf',
            'uploader' : '${ctx}/background/wxacctype/saveOrUpdatePic.html?acctypeId='+$("#acctypeid").val(),
            'height': 25,
            'whith' :120,
            'auto'  : false,
            'fileDataName':'file',
            'buttonText' : '选择图片...',
            'fileTypeExts' : '*.gif; *.jpg; *.png',
            'multi'    : false,
            'method'   :'post',
            'debug':false,
            'onUploadStart' : function(file) {
                var param = {};
                param.picHref = $('#file_upload_href').val();
                     $("#file_upload").uploadify("settings", "formData", param);
                },
            'onUploadSuccess' : function(file, data, response) {
                var imgUrl = uploadCommon.getPath(data);
                $("#imgUrl").val(imgUrl);// 返回的图片路径保存起来
                $("#thumbImg").attr("src", IMAGE_FILE_PATH + imgUrl);// 更新logo显示
                /*uploadCommon.uploadImageBtnStyle("imgUrl");
                uploadCommon.initPreviewAfterUpload(data); // 新图片预览*/            
            },
            'onUploadError' : function(file, errorCode, errorMsg, errorString) {
                alert('The file ' + file.name + ' could not be uploaded: ' + errorString);
               }

        });
    });
});
</script>
</body>
</html>