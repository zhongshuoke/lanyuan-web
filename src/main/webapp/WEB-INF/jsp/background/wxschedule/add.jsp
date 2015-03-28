<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/header.jsp"%>
<script type="text/javascript">
//单独验证某一个input  class="checkpass"
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
								//wx_schedule是iframe的id
								parent.wx_schedule.loadGird();
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
				hour : {
				required : true,
				remote:{ //异步验证是否存在
					type:"POST",
					url: rootPath + '/background/wxschedule/isExist.html',
					data:{
						name:function(){return $("#hour").val();}
					 }
					}
				},
				minute : {
					required : true
				}
			},
			messages : {
				hour : {
					required : "请输入小时数",
				    remote:"该小时数已经存在"
				},
				minute : {
					required : "请输入分钟数"
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
</script>
</head>
<body>
<div class="divdialog">
	<div class="l_err" style="width: 270px;"></div>
	<form name="form" id="form" action="${ctx}/background/wxschedule/add.html" method="post">
		<table style="width: 285px; height: 200px;">
			<tbody>
				<tr>
					<td class="l_right">小时(0-23)：</td>
					<td class="l_left">
					<div class="lanyuan_input">
					<input id='hour' name="hour" class="isNum" type="text" value="">
					</div>
					</td>
				</tr>
				<tr>
					<td class="l_right">分钟(0-59)：</td>
					<td class="l_left">
					<div class="lanyuan_input">
					<input id='minute'
						name="minute" type="text" class="isNum" value="">
					</div>
					</td>
				</tr>
				<tr>
					<td class="l_right">一周第几天：</td>
					<td class="l_left">
					<div class="lanyuan_input">
					<input id='day_of_week'
						name="day_of_week" type="text" class="isNum" value="*" readonly="true">
					</div>
					</td>
				</tr>
				<tr>
					<td class="l_right">一月第几天：</td>
					<td class="l_left">
					<div class="lanyuan_input">
					<input id='day_of_month'
						name="day_of_month" type="text" class="isNum" value="*" readonly="true">
					</div>
					</td>
				</tr>
				<tr>
					<td class="l_right">一年第几月：</td>
					<td class="l_left">
					<div class="lanyuan_input">
					<input id='month_of_year'
						name="month_of_year" type="text" class="isNum" value="*" readonly="true">
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