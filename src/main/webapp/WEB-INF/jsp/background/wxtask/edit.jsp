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
								//wx_account是iframe的id
								parent.wx_task.loadGird();
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
				crontab_id : {
					required : true,
					remote:{ //异步验证是否存在
						type:"POST",
						url: rootPath + '/background/wxtask/isExist.html',
						data:{
							crontab_id:function(){return $("#crontab_id").val();}
						 }
					  }
				 },
				name : {
					required : true
				}
			},
			messages : {
				crontab_id : {
					required : "请输入CRON表达式ID",
				    remote:"该ID已经存在"
				},
				name : {
					required : "请输入任务名称"
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
	$(function() {
		$("input:radio[value='${wxAccount.state}']").attr('checked','true');
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
	<form name="form" id="form" action="${ctx}/background/wxtask/update.html" method="post">
		<table style="width: 285px; height: 200px;">
			<tbody>
				<tr>
					<td class="l_right">id：</td>
					<td class="l_left">
					<input id='id' name="id" type="hidden" value="${wxTask.id}">
					${wxTask.id}</td>
				</tr>
				<tr>
					<td class="l_right">任务名称：</td>
					<td class="l_left">
					<div class="lanyuan_input">
					<input id='name'
						name="name" type="text" class="isNum" value="${wxTask.name}">
					</div>
					</td>
				</tr>
				<tr>
					<td class="l_right">是否有效：</td>
					<td class="l_left">
					<select name="enabled" id="enabled" style="width: 140px;">
					<option value="1">--请选择--</option>
					<option value="1">有效</option>
					<option value="0">无效</option>
					</select>
					</td>
				</tr>
				<!-- 
				<tr>
					<td class="l_right">cronTabID：</td>
					<td class="l_left">
					<select name="crontab_id" id="crontab_id" style="width: 140px;">
					<option value="0">--请选择--</option>
					<c:forEach items="${wxScheduleList}" var="key">
					<option value="${key.id}">(id)${key.id}_(hour)${key.hour}_(min)${key.minute}</option>
					</c:forEach>
					</select>
					</td>
				</tr>
				 -->				
				<tr>
					<td class="l_right">描述：</td>
					<td class="l_left">
					<div class="lanyuan_input">
					<input id='description'
						name="description" type="text" class="isNum" value="${wxTask.description}">
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