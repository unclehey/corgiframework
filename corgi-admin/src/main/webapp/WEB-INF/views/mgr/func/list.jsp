<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
	<title>功能管理</title>
	<script type="text/javascript">
		var treeData = ${mgrFuncList};
		var account = "${account}";//数据
		var actionView = '#actionView';//功能列表视图id
		var actionViewAdmin = '#actionViewAdmin';//超级管理员功能视图id
		var addFuncsVeiw = '#addFuncsVeiw';//添加视图id
		var editFuncsVeiw = '#editFuncsVeiw';//修改视图id
		var treeContent = "#tree-content";//树的主体
		jQuery(function($) {
			jQuery(treeContent).treeview({
				data: [{
						text: '功能资源列表',
					  	type:'0',
					  	id:'0',
					  	nodes:treeData,}],
				color: "#428bca",
				expandIcon: "glyphicon glyphicon-plus",
				collapseIcon: "glyphicon glyphicon-minus",
				emptyIcon: "glyphicon glyphicon-leaf",
				showBorder: false,
				selectable: true,
				onNodeSelected: function(event, data) {
					showFuncsVeiw(data);
				}
			});
			jQuery(treeContent).treeview('expandAll');
		});
		
		function showFuncsVeiw(data){
			if(data.id != 0){
				$(actionView).modal('show');
			}else{
				if(account == "admin"){
					$(actionViewAdmin).modal('show');
				}
			}
		}
		
		function showAddVeiw(){

			$(actionView).modal('hide');
			$(actionViewAdmin).modal('hide');
			//$("#urlShow").hide();
			$(addFuncsVeiw).modal('show');
			
			var parentText = jQuery(treeContent).treeview("getSelected")[0].text;
			
			$("#parentcnName").html(parentText);
			
			$.ajax({
				url: "mgr/func/funcs.do",
				type: "post",
				dataType: "json",
				success: function (result) {
					$("#funcSelection").empty();
					$("#funcSelection").append("<option value=''>请选择...</option>");
					for(var i=0; i<result.length; i++){
						var func = result[i];
						var code = func.code;
						var cnName = func.cnName;
						var type = func.type;
						var url = func.url;
						var menuIcon = func.menuIcon;
						$("#funcSelection").append("<option value='"+code+"' cnName='"+cnName+"' type='"+type+"' url='"+url+"' menuIcon='"+menuIcon+"'>"+cnName+"</option>");
					}
				},
				error: function (e) {
					alert("失败" + e.toSource());
				}
			});
		}
		
		function showEditVeiw(){
			
			$(actionView).modal('hide');
			$(editFuncsVeiw).modal('show');
			
			var func = jQuery(treeContent).treeview("getSelected")[0];
			var parentText = jQuery(treeContent).treeview("getParent",func).text;
			var text = func.text;
			var url = func.url;
			var code = func.code;
			var type = func.type;
			var remark = func.remark;
			if(type == 1){
				$("#urlEditShow").hide();
			}
			
			$("#parentcnNameEdit").html(parentText);
			$("#cnNameEdit").val(text);
			$("#urlEdit").val(url);
			$("#codeEdit").val(code);
			$("#typeEdit").val(type);
			$("#remarkEdit").val(remark);
		}
		
		function showDeleteVeiw(){
			var func = jQuery(treeContent).treeview("getSelected")[0];
			var funcId = func.id;
			var funcCode = func.code;
			
			$(actionView).modal('hide');
			
			if(funcCode == "func_1"
					|| funcCode == "func_1_1"
					|| funcCode == "func_1_1_1"
					|| funcCode == "func_1_1_2"
					|| funcCode == "func_1_1_3"){
				alert("当前是基本功能，不可删除！！！");
				return;
			}
			
			bootbox.confirm("<h4 style='text-align: center;'>您确定要删除本功能及其所有子功能吗？</h4>", function(result) {
				if(result){
					var params = {
						proId: funcId
					};
					$.ajax({
						url: "mgr/func/delete.do",
						type: "post",
						data: params,
						dataType: "text",
						success: function (result) {
							showSidebar('','mgr-11','mgr-1','mgr/func/list.do');
						},
						error: function (e) {
							alert("失败" + e.toSource());
						}
					});
				}
			});
		}
		
		function addConfirm(){
			var func = jQuery(treeContent).treeview("getSelected")[0];
			var id = func.id;
			$("#type").removeAttr("disabled"); 
			var type = $('#type').val();
			var cnName = $('#cnName').val();
			var url = $('#url').val();
			var remark = $('#remark').val();
			var code = $('#code').val();
			var menuIcon = $('#menuIcon').val();
			
			if(code == null
					|| code == ""){
				alert("编码不能为空");
				return;
			}
			
			if(cnName == null
					|| cnName == ""){
				alert("名称不能为空");
				return;
			}
			
			if(type == 2){
				if(url == null
						|| url == ""){
					alert("url不能为空");
					return;
				}
			}
			
			var params = {
				parentId: id,
				type: type,
				url:url,
				menuIcon:menuIcon,
				remark:remark,
				code:code,
				cnName: cnName
			};
			
			$.ajax({
				url: "mgr/func/insert.do",
				type: "post",
				data: params,
				dataType: "text",
				success: function (result) {
					$(addFuncsVeiw).modal('hide');
					showSidebar('','mgr-11','mgr-1','mgr/func/list.do');
				},
				error: function (e) {
					alert("失败" + e.toSource());
				}
			});
		}
		
		function editConfirm(){
			
			var cnName = $('#cnNameEdit').val();
			var url = $('#urlEdit').val();
			var remark = $('#remarkEdit').val();
			var type = $('#typeEdit').val();
			var id = jQuery(treeContent).treeview("getSelected")[0].id;
			var code = $('#codeEdit').val();
			
			if(code == null
					|| code == ""){
				alert("编码不能为空");
				return;
			}
			
			if(cnName == null
					|| cnName == ""){
				alert("名称不能为空");
				return;
			}
			if(type == 2){
				if(url == null
						|| url == ""){
					alert("url不能为空");
					return;
				}
			}
			var params = {
				proId: id,
				url:url,
				type:type,
				remark:remark,
				code:code,
				cnName: cnName
			};
			$.ajax({
				url: "mgr/func/update.do",
				type: "post",
				data: params,
				dataType: "text",
				success: function (result) {
					$(editFuncsVeiw).modal('hide');
					showSidebar('','mgr-11','mgr-1','mgr/func/list.do');
				},
				error: function (e) {
					alert("失败" + e.toSource());
				}
			});
		}
		
		function changeType(){
			var type = $('#type').val();
			if(type == 1){
				$("#urlShow").hide();
			}else{
				$("#urlShow").show();
			}
		}
		
		function changeTypeEdit(){
			var type = $('#typeEdit').val();
			if(type == 1){
				$("#urlEditShow").hide();
			}else{
				$("#urlEditShow").show();
			}
		}
		
		function changeNode(){
			var node = $('#funcSelection option:selected');
			var code = node.val();
			var cnName = node.attr('cnName');
			var type = node.attr('type');
			var url = node.attr('url');
			var menuIcon = node.attr('menuIcon');
			$("#code").val(code);
			$("#cnName").val(cnName);
			$("#type").val(type);
			$("#url").val(url);
			$("#menuIcon").val(menuIcon);
		}
	</script>
</head>
<body class="no-skin">
	<div class="main-container" id="main-container">
		<!-- main-content -->
		<div class="main-content" id="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs" id="breadcrumbs">
					<ul class="breadcrumb">
						<li>
							<i class="ace-icon fa fa-home home-icon"></i>
							${mgr_title}
						</li>
						<li>
							权限管理
						</li>
						<li>
							功能资源列表
						</li>
					</ul>
				</div>
				<div class="page-content">
					<div class="page-header">
						<h1>功能资源列表</h1>
					</div>
					<div class="row">
						<div class="col-xs-12">
							<div id="tree-content"></div>
						</div>
						<div class="row">
							<div class="col-xs-12"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- 展示功能列表 -->
		<div class="modal fade bs-example-modal-sm" id="actionViewAdmin" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-sm">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
						<h4 class="modal-title">可操作列表</h4>
					</div>
					<div class="modal-body">
						<table class="table table-bordered table-hover table-condensed table-style">
							<tbody>
							<tr>
								<td>
									<a class="btn btn-sm btn-primary" onclick="showAddVeiw()">新增一个功能资源</a>
								</td>
							</tr>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
		<!-- 展示功能列表 -->
		<div class="modal fade bs-example-modal-sm" id="actionView" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-sm">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
						<h4 class="modal-title">可操作列表</h4>
					</div>
					<div class="modal-body">
						<table class="table table-bordered table-hover table-condensed table-style">
							<tbody>
							<tr>
								<td>
									<c:if test="${not empty sessionScope.mgrFuncMap.func_1_1_1}"><a class="btn btn-sm btn-primary" onclick="showAddVeiw()">新增一个功能资源</a></c:if>
								</td>
							</tr>
							<tr>
								<td>
									<c:if test="${not empty sessionScope.mgrFuncMap.func_1_1_2}"><a class="btn btn-sm btn-primary" onclick="showEditVeiw()">修改一个功能资源</a></c:if>
								</td>
							</tr>
							<tr>
								<td>
									<c:if test="${not empty sessionScope.mgrFuncMap.func_1_1_3}"><a class="btn btn-sm btn-primary" onclick="showDeleteVeiw()">删除一个功能资源</a></c:if>
								</td>
							</tr>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
		<!-- 新增功能列表 -->
		<div class="modal fade bs-example-modal-lg" id="addFuncsVeiw" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-sm" style="width:500px">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
						<h4 class="modal-title"></h4>
					</div>
					<div class="modal-body">
						<table class="table table-bordered table-hover table-condensed table-style">
							<tbody>
							<tr>
								<td style="vertical-align: middle;">父功能资源名称</td>
								<td>
									<div id="parentcnName"></div>
								</td>
							</tr>
							<tr>
								<td style="vertical-align: middle;">功能资源选择</td>
								<td>
									<select id="funcSelection" onchange="changeNode()"></select>
								</td>
							</tr>
							<tr>
								<td style="vertical-align: middle;">功能资源编码</td>
								<td>
									<input type="text" id="code" readonly= "true"/>
								</td>
							</tr>
							<tr>
								<td style="vertical-align: middle;">功能资源名称</td>
								<td>
									<input type="text" id="cnName"/>
									<input type="hidden" id="menuIcon"/>
								</td>
							</tr>
							<tr>
								<td style="vertical-align: middle;">功能类型</td>
								<td>
									<select id="type" disabled="disabled">
					                   <option value="1" selected>业务菜单</option>
					                   <option value="2">功能菜单</option>
					                   <option value="3">按钮</option>
					                </select>
								</td>
							</tr>
							<tr id="urlShow">
								<td style="vertical-align: middle;">url</td>
								<td>
									<input type="text" id="url" readonly= "true"/>
								</td>
							</tr>
							<tr>
								<td style="vertical-align: middle;">备注</td>
								<td>
									<input type="text" id="remark" />
								</td>
							</tr>
							</tbody>
						</table>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-sm btn-default" data-dismiss="modal">取消</button>
						<a class="btn btn-sm btn-primary" onclick="addConfirm()">确定</a>
					</div>
				</div>
			</div>
		</div>
		<!-- 修改功能列表 -->
		<div class="modal fade bs-example-modal-sm" id="editFuncsVeiw" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-sm">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
						<h4 class="modal-title"></h4>
					</div>
					<div class="modal-body">
						<table class="table table-bordered table-hover table-condensed table-style">
							<tbody>
							<tr>
								<td style="vertical-align: middle;">父功能资源名称</td>
								<td>
									<div id="parentcnNameEdit"></div>
								</td>
							</tr>
							<tr>
								<td style="vertical-align: middle;">功能资源编码</td>
								<td>
									<input type="text" id="codeEdit"  readonly= "true"/>
								</td>
							</tr>
							<tr>
								<td style="vertical-align: middle;">功能资源名称</td>
								<td>
									<input type="text" id="cnNameEdit" />
								</td>
							</tr>
							<tr>
								<td style="vertical-align: middle;">功能类型</td>
								<td>
									<select id="typeEdit" disabled="disabled">
					                   <option value="1" selected>菜单</option>
					                   <option value="2">功能菜单</option>
					                   <option value="3">按钮</option>
					                </select>
								</td>
							</tr>
							<tr id="urlEditShow">
								<td style="vertical-align: middle;">url</td>
								<td>
									<input type="text" id="urlEdit"  readonly= "true"/>
								</td>
							</tr>
							<tr>
								<td style="vertical-align: middle;">备注</td>
								<td>
									<input type="text" id="remarkEdit" />
								</td>
							</tr>
							</tbody>
						</table>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-sm btn-default" data-dismiss="modal">取消</button>
						<a class="btn btn-sm btn-primary" onclick="editConfirm()">确定</a>
					</div>
				</div>
			</div>
		</div>
		<a href="#" id="btn-scroll-up" class="btn-scroll-up btn btn-sm btn-inverse">
			<i class="ace-icon fa fa-angle-double-up icon-only bigger-110"></i>
		</a>
	</div>
</body>
</html>
