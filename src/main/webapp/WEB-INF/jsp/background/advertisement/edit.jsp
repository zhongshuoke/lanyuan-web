<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="/common/header.jsp"%>
<script type="text/javascript">
jQuery.validator.addMethod("checkpass", function(value, element) {
	 return this.optional(element) || ((value.length <= 16) && (value.length>=6));
}, "密码由6至16位字符组合构成");
	$(function() {
		
		$("form").validate({
			submitHandler : function(form) {//必须写在验证前面，否则无法ajax提交
				$(form).ajaxSubmit({//验证新增是否成功
					type : "post",
					dataType:"json",
					success : function(data) {
						if (data.flag == "true") {
							$.ligerDialog.success('提交成功!', '提示', function() {
								//这个是调用同一个页面趾两个iframe里的js方法
								//wx_advertisement是iframe的id
								parent.wx_advertisement.loadGird();
								closeWin();
							});
							//parent.window.document.getElementById("username").focus();
						} else {
							$.ligerDialog.warn("提交失败！！");
						}
					}
				});
			},
			rules : {
				 title : {
					required : true
				 },
				url : {
					required : true
				}
			},
			messages : {
				title : {
					required : "请输入标题"
				},
				url : {
					required : "请输url"
				}
			},
			errorPlacement : function(error, element) {//自定义提示错误位置
				$(".l_err").css('display','block');
				//element.css('border','3px solid #FFCCCC');
				$(".l_err").html(error.html());
			},
			success: function(label) {//验证通过后
				$(".l_err").css('display','none');
			}
		});
	});
	function saveWin() {
		$("#form").submit();
	}
	function closeWin() {
		 parent.$.ligerDialog.close(); //关闭弹出窗; //关闭弹出窗
		parent.$(".l-dialog,.l-window-mask").css("display","none"); 
	}
</script>
</head>
<body>
<div class="divdialog">
	<div class="l_err" style="width: 270px;"></div>
	<form name="form" id="form" action="${ctx}/background/advertisement/update.html" method="post">
		<table style="width: 285px; height: 200px;">
			<tbody>
				<tr>
					<td class="l_right">id：</td>
					<td class="l_left">
					<input id='id' name="id" type="hidden" value="${advertisement.id}">
					${advertisement.id}</td>
				</tr>
				<tr>
					<td class="l_right">标题：</td>
					<td class="l_left">
					<div class="lanyuan_input">
					<input id='title'
						name="title" type="text" class="isNum" value="${advertisement.title}">
					</div>
					</td>
				</tr>
				<tr>
					<td class="l_right">url：</td>
					<td class="l_left">
					<div class="lanyuan_input">
					<input id='url'
						name="url" type="text" class="isNum" value="${advertisement.url}">
					</div>
					</td>
				</tr>
				<tr>
					<td class="l_right">图片：</td>
					<td class="l_left">
					<div class="lanyuan_input">
					<input id='picurl'
						name="picurl" type="text" class="isNum" value="${advertisement.picurl}">
						</div>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<div class="l_btn_centent">
								<!-- saveWin_form   from是表单Ｉd-->
								<a class="btn btn-primary" href="javascript:void(0)"
									id="saveWin_form" onclick="saveWin();"><span>保存</span> </a> <a
									class="btn btn-primary" href="javascript:void(0)" id="closeWin"
									onclick="closeWin()"><span>关闭</span> </a>
							</div>
					</td>
				</tr>
			</tbody>
		</table>
	</form>
	</div>
</body>
</html>