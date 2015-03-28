<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="/common/header.jsp"%>
<script type="text/javascript">
	var dialog;
	var grid;
	$(function() {
		grid = window.lanyuan.ui.lyGrid({
					id : 'paging',
					l_column : [ {
						colkey : "id",
						name : "id",
						width : "50px"
					}, {
						colkey : "wxAccountName",
						name : "微信公众号名称"
					}, {
						colkey : "wxAccountNo",
						name : "微信公众号",
						width:"70px"
					}, {
						colkey : "wxType",
						name : "类别",
						width:"70px"
					}, {						
						colkey : "state",
						name : "公众号状态",
						width : "150px"
					}, {
						colkey : "description",
						name : "描述"
					}, {
						colkey : "createTime",
						name : "创建时间"
					} ],
					jsonUrl : '${pageContext.request.contextPath}/background/wxaccount/query.html',
					checkbox : true
				});
		$("#seach").click("click", function() {//绑定查询按扭
			var searchParams = $("#fenye").serializeJson();
			grid.setOptions({
				data : searchParams
			}); 
		});
		$("#exportExcel").click("click", function() {//绑定查询按扭
			var f = $('#fenye');
			f.attr('target','_blank');
			f.attr('action','${pageContext.request.contextPath}/background/wxaccount/exportExcel.html');
			f.submit();
		});
		$("#add").click("click", function() {//绑定查询按扭
			dialog = parent.$.ligerDialog.open({
				width : 300,
				height : 310,
				url : rootPath + '/background/wxaccount/addUI.html',
				title : "增加账号",
				isHidden:false   //关闭对话框时是否只是隐藏，还是销毁对话框
			});
		});
		$("#editView").click("click", function() {//绑定查询按扭
			var cbox=grid.getSelectedCheckbox();
			if (cbox.length > 1||cbox=="") {
				parent.$.ligerDialog.alert("至少选中一个");
				return;
			}
			dialog = parent.$.ligerDialog.open({
				width : 300,
				height : 310,
				url : rootPath + '/background/wxaccount/editUI.html?accountId='+cbox,
				title : "修改账号",
				isHidden : false
			});
		});
		$("#perrole").click("click", function() {//绑定查询按扭
			var cbox=grid.selectRow();
			if (cbox.id == undefined || cbox.id=="") {
				parent.$.ligerDialog.alert("请选择一条数据!");
				return;
			}
			dialog = parent.$.ligerDialog.open({
				width : 500,
				height : 410,
				url : rootPath + '/background/wxaccount/accRole.html?id='+cbox.id+'&accountName='+encodeURI(encodeURI(cbox.accountName))+'&roleName='+encodeURI(encodeURI(cbox.roleName)),
				title : "分配角色",
				isHidden : false
			});
		});
		$("#deleteView").click("click", function() {//绑定查询按扭
			var cbox=grid.getSelectedCheckbox();
			if (cbox=="") {
				parent.$.ligerDialog.alert("请选择删除项！！");
				return;
			}
			parent.$.ligerDialog.confirm('删除后不能恢复，确定删除吗？', function(confirm) {
				if (confirm) {
					$.ajax({
					    type: "post", //使用get方法访问后台
					    dataType: "json", //json格式的数据
					    async: false, //同步   不写的情况下 默认为true
					    url: rootPath + '/background/wxaccount/deleteById.html', //要访问的后台地址
					    data: {ids:cbox.join(",")}, //要发送的数据
					    success: function(data){
					    	if (data.flag == "true") {
					    		parent.$.ligerDialog.success('删除成功!', '提示', function() {
					    			loadGird();//重新加载表格数据
								});
							}else{
								parent.$.ligerDialog.warn("删除失败！！");
							}
						}
					});
				}
			});
		});
		
        $("#executeCapture").click("click", function() {
        	var cbox=grid.getSelectedCheckbox();
			if (cbox=="") {
				parent.$.ligerDialog.alert("请选择执行项！！");
				return;
			}
			$.ajax({
		    type: "post", //使用get方法访问后台
		    dataType: "json", //json格式的数据
		    url: rootPath + '/background/wxaccount/executecapture.html', //要发送的请求地址
		    data: {ids:cbox.join(",")}, //要发送的数据
		    success: function(data){
		    	if (data.flag == "true") {
		    		//alert(data.wxaccountStr);
		    		parent.$.ligerDialog.success('开始执行!', '提示', function() {
	   					/*$.ajax({
						    type: "post", //使用get方法访问后台
						    dataType: "json", //json格式的数据
						    url: rootPath + '/background/wxaccount/executecapture.html', //要发送的请求地址
						    data: {wxaccounts:data.wxaccountStr}, //要发送的数据
						    success: function(data){
						    	alert(data.flag);
						    	if (data.flag == "true") {
						    		parent.$.ligerDialog.success('开始执行!', '提示', function() {
						    			loadGird();//重新加载表格数据
									});
								}else{
									parent.$.ligerDialog.warn("执行抓取失败！！");
								}
							}
						});*/
	   					loadGird();//重新加载表格数据
					});
				}else{
					parent.$.ligerDialog.warn("执行抓取失败！！");
				}
			}
		});
	        });
        /*
	        $("#stopCapture").click("click", function() {
	   					$.ajax({
						    type: "post", //使用get方法访问后台
						    dataType: "json", //json格式的数据
						    url: rootPath + '/background/wxarticle/savehtml.html', //要发送的请求地址
						    data: {ids:cbox.join(","),
						      eventtype:"stopcapture"
						          }, //要发送的数据
						    success: function(data){
						    	if (data.flag == "true") {
						    		parent.$.ligerDialog.success('停止抓取成功!', '提示', function() {
						    			loadGird();//重新加载表格数据
									});
								}else{
									parent.$.ligerDialog.warn("停止抓取失败！！");
								}
							}
						});
		     });*/
		
	});
	
	function loadGird(){
		grid.loadData();
	}
</script>
</head>
<body>
	<div class="divBody">
		<div class="search">
			<form name="fenye" id="fenye">
				公众号名称：<input type="text" name="wxAccountName" value="${param.name}"
					style="height: 20px" /> <a class="btn btn-primary"
					href="javascript:void(0)" id="seach"> 查询
				</a>
			</form>
		</div>
		<div class="topBtn">
			<a class="btn btn-primary" href="javascript:void(0)" id="add"> <i
				class="icon-zoom-add icon-white"></i> <span>增加</span>
			</a><a class="btn btn-info" href="javascript:void(0)" id="editView"> <i
				class="icon-edit icon-white"></i> 编辑
			</a> <a class="btn btn-danger" href="javascript:void(0)" id="deleteView"> <i
				class="icon-trash icon-white"></i> 删除
			</a>
			<a class="btn btn-large btn-info" href="javascript:void(0)" id="executeCapture">
			      执行抓取
			</a>
			<a class="btn btn-large btn-success" href="javascript:void(0)" id="exportExcel">
				导出excel
			</a>
		</div>
		<div id="paging" class="pagclass"></div>
	</div>
</body>
</html>