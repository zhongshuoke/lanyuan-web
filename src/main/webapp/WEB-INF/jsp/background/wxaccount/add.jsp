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
								//wx_account是iframe的id
								parent.wx_account.loadGird();
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
				wxAccountName : {
				required : true,
				remote:{ //异步验证是否存在
					type:"POST",
					url: rootPath + '/background/account/isExist.html',
					data:{
						name:function(){return $("#wxAccountName").val();}
					 }
					}
				},
				state : {
					required : true
				}
			},
			messages : {
				wxAccountName : {
					required : "请输入账号",
				    remote:"该账号已经存在"
				},
				state : {
					required : "选择状态"
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
	<form name="form" id="form" action="${ctx}/background/wxaccount/add.html" method="post">
		<table style="width: 285px; height: 200px;">
			<tbody>
				<tr>
					<td class="l_right">公众号名称：</td>
					<td class="l_left">
					<div class="lanyuan_input">
					<input id='wxAccountName' name="wxAccountName" class="isNum" type="text" value="">
						</div></td>
				</tr>
				<tr>
					<td class="l_right">公众号：</td>
					<td class="l_left">
					<div class="lanyuan_input">
					<input id='wxAccountNo'
						name="wxAccountNo" type="text" class="isNum" value="">
						</div>
						</td>
				</tr>
				<tr>
					<!-- <td class="l_right">公众号类别：</td>
					<td class="l_left">
					<div class="lanyuan_input">
					<input id='wxType'
						name="wxType" type="text" class="isNum" value="">
						</div>
					</td>
					 -->
					<td class="l_right">公众号类别：</td>
					<td class="l_left">
					<select name="wxType" id="wxType" style="width: 140px;">
					<option value="0">--请选择--</option>
					<c:forEach items="${wxAccTypeList}" var="key">
					<option value="${key.wxType}">${key.wxType}</option>
					</c:forEach>
					</select>
					</td>
				</tr>				
				<tr>
					<td class="l_right">公众号说明：</td>
					<td class="l_left">
					<div class="lanyuan_input">
					<input id='description'
						name="description" class="checkdesc" type="text" value="">
						</div>
						</td>
				</tr>
				<tr>
					<td class="l_right">城市：</td>
					<td class="l_left">
					<select name="cityId" id="cityId" style="width: 140px;">
					<option value="0">--请选择--</option>
					<c:forEach items="${cityList}" var="key">
					<option value="${key.id}">${key.cityName}</option>
					</c:forEach>
					</select>
					</td>
				</tr>				
				<tr>
					<td class="l_right">公众号状态：</td>
					<td class="l_left">
					<input id='state' name="state" value="1" type="radio" checked="checked"> 启用　　
					<input id='state' name="state" value="0" type="radio"> 停用
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