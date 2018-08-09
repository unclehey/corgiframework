<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
	<title>部门列表</title>
	<script type="text/javascript">
	
		var treeData = ${mgrDepartList};//数据
		var account = "${account}";//数据
		var actionView = '#actionView';//功能列表视图id
		var actionViewAdmin = '#actionViewAdmin';//超级管理员功能视图id
		var addVeiw = '#addVeiw';//添加视图id
		var editVeiw = '#editVeiw';//修改视图id
		var givePermVeiw = '#givePermVeiw';//赋权视图id
		var giveUserVeiw = '#giveUserVeiw';//添加用户视图id
		var givePermContent = "#givePermContent";//赋权树的主体
		var giveUserContent = "#giveUserContent";//添加人员主体
		var treeContent = "#tree-content";//树的主体

		//1、加载职能部门树
		jQuery(function($) {
			jQuery(treeContent).treeview({
				data: [{
						text: '职能部门列表',
						  type:'0',
						  id:'0',
						  nodes:treeData,}],
				expandIcon: "glyphicon glyphicon-plus",
				collapseIcon: "glyphicon glyphicon-minus",
				emptyIcon: "glyphicon glyphicon-leaf",
				color: "#428bca",
				showBorder: false,
				selectable: true,
				onNodeSelected: function(event, data) {
					showActionView(data);
				}
			});
			jQuery(treeContent).treeview('expandAll');
		});
		
		function showActionView(data){
			if(data.id != 0){
				$(actionView).modal('show');
			}else{
				if(account == "admin"){
					$(actionViewAdmin).modal('show');
				}
			}
		}
		
		//2、展示添加视图
		function showAddVeiw(){
			$(actionView).modal('hide');
			$(actionViewAdmin).modal('hide');
			$(addVeiw).modal('show');

			var parentText = jQuery(treeContent).treeview("getSelected")[0].text;
			
			$("#parentCnName").html(parentText);
		}
		
		//3、展示修改视图
		function showEditVeiw(){

			var depart = jQuery(treeContent).treeview("getSelected")[0];
			
			var params = {
					departId: depart.id
				};
			$.ajax({
				url: "mgr/depart/isMyDepart.do",
				type: "post",
				data: params,
				dataType: "text",
				success: function (result) {
					if(result == "true"){
						$(actionView).modal('hide');
						$(editVeiw).modal('show');
						
						var parentText = jQuery(treeContent).treeview("getParent",depart).text;
						var text = depart.text; 
						var remark = depart.remark;
						
						$("#parentCnNameEdit").html(parentText);
						$("#cnNameEdit").val(text);
						$("#remarkEdit").val(remark);
					}else{
						alert("当前部门非您所创建，您没有权限修改");
						$(actionView).modal('hide');
					}
				},
				error: function (e) {
					alert("失败" + e.toSource());
				}
			});
		}
		
		//5、展示删除视图
		function showDeleteVeiw(){
			var id = jQuery(treeContent).treeview("getSelected")[0].id;
			
			var params = {
					departId: id
				};
			$.ajax({
				url: "mgr/depart/isMyDepart.do",
				type: "post",
				data: params,
				dataType: "text",
				success: function (result) {
					if(result == "true"){
						$(actionView).modal('hide');
						bootbox.confirm("<h4 style='text-align: center;'>您确定要作废此条配置信息吗？</h4>", function(resultCan) {
							if(resultCan){
								var params = {
									proId: id
								};
								$.ajax({
									url: "mgr/depart/delete.do",
									type: "post",
									data: params,
									dataType: "text",
									success: function (result) {
										showSidebar('','mgr-12','mgr-1','mgr/depart/list.do');
									},
									error: function (e) {
										alert("失败" + e.toSource());
									}
								});
							}
						});
					}else{
						alert("当前部门非您所创建，您没有权限删除");
						$(actionView).modal('hide');
					}
				},
				error: function (e) {
					alert("失败" + e.toSource());
				}
			});
		}
		
		//6、展示赋权视图
		function showGivePermVeiw(){
			var departId = jQuery(treeContent).treeview("getSelected")[0].id;
			var params = {
				departId: departId,
			};
			$.ajax({
				url: "mgr/func/rules.do",
				type: "post",
				data: params,
				dataType: "json",
				success: function (result) {
					$(actionView).modal('hide');
					$(givePermVeiw).modal('show');
					jQuery(givePermContent).treeview({
						data: [{
								text: '功能资源列表',
								type:'0',
								id:'0',
								nodes:result,}],
						expandIcon: "glyphicon glyphicon-plus",
						collapseIcon: "glyphicon glyphicon-minus",
						emptyIcon: "glyphicon glyphicon-leaf",
						color: "#428bca",
						showBorder: false,
						selectable: false,
						showCheckbox: true,
						highlightSelected: true,
						multiSelect: false,
						onNodeChecked: function (event,node) {
							//checkPerm(node);
	                        var nodes = node.nodes;
	                        if (typeof(nodes) != "undefined") { 
		                        if(nodes.length>0){
			                        for(var i=0; i<nodes.length; i++){
			                        	jQuery(givePermContent).treeview('checkNode', nodes[i].nodeId);
			                        }
		                        }
	                        }  
	                    },
	                    onNodeUnchecked: function (event,node) {
	                        var nodes = node.nodes; 
	                        if (typeof(nodes) != "undefined") { 
		                        if(nodes.length>0){
			                        for(var i=0; i<nodes.length; i++){
			                        	jQuery(givePermContent).treeview('uncheckNode', nodes[i].nodeId);
			                        }
		                        }
	                        }
	                    },
					});
					jQuery(givePermContent).treeview('expandAll');
				},
				error: function (e) {
					alert("失败" + e.toSource());
				}
			});
		}
		
		//7、展示添加用户视图
		function showGiveUserVeiw(){
			var departId = jQuery(treeContent).treeview("getSelected")[0].id;
			var params = {
					departId: departId,
				};
			$.ajax({
				url: "mgr/depart/users.do",
				type: "post",
				data: params,
				dataType: "json",
				success: function (result) {
					$(actionView).modal('hide');
					$(giveUserVeiw).modal('show');
					jQuery(giveUserContent).trigger("reloadGrid");
					jQuery(giveUserContent).jqGrid({
						subGrid : false,
						data : result.departList,
						datatype : "local",
						height : 165,
						colNames : [ '账号', '真实姓名', '邮箱'],
						colModel : [{
							name : 'account',
							index : 'account',
							width : 100,
							sortable : false,
							align : "center"
						}, {
							name : 'realName',
							index : 'realName',
							width : 100,
							sortable : false,
							align : "center"
						}, {
							name : 'email',
							index : 'email',
							width : 230,
							sortable : false,
							align : "center"
						}],
				        sortname : 'id',
						viewrecords : true,
						rowNum : 100,
						altRows : true,
						multiselect : true,
						multiboxonly : true,
						rownumbers : true,  
						rownumWidth : 20, 
						caption : "后台用户列表"
					});

					checkUser(result.checkedList);
				},
				error: function (e) {
					alert("失败" + e.toSource());
				}
			});
		}
		
		
		//8、添加一个部门
		function addConfirm(){
			
			var cnName = $('#cnName').val();
			var remark = $('#remark').val();
			var depart = jQuery(treeContent).treeview("getSelected")[0];
			var id = depart.id;
			var type = depart.type;
			
			if(cnName == null
					|| cnName == ""){
				alert("名称不能为空");
				return;
			}
			
			var params = {
				parentId: id,
				type: type,
				remark:remark,
				cnName: cnName
			};
			$.ajax({
				url: "mgr/depart/insert.do",
				type: "post",
				data: params,
				dataType: "text",
				success: function (result) {
					$(addVeiw).modal('hide');
					showSidebar('','mgr-12','mgr-1','mgr/depart/list.do');
				},
				error: function (e) {
					alert("失败" + e.toSource());
				}
			});
		}
		
		//9、修改一个部门的名称备注等
		function editConfirm(){
			var cnName = $('#cnNameEdit').val();
			var remark = $('#remarkEdit').val();
			var id = jQuery(treeContent).treeview("getSelected")[0].id;
			
			if(cnName == null
					|| cnName == ""){
				alert("名称不能为空");
				return;
			}
			var params = {
				proId: id,
				remark:remark,
				cnName: cnName
			};
			$.ajax({
				url: "mgr/depart/update.do",
				type: "post",
				data: params,
				dataType: "text",
				success: function (result) {
					$(editVeiw).modal('hide');
					showSidebar('','mgr-12','mgr-1','mgr/depart/list.do');
				},
				error: function (e) {
					alert("失败" + e.toSource());
				}
			});
		}
		
		//10、赋权
		function givePermConfirm(){
			var checkedIds = jQuery(givePermContent).treeview("getChecked");
			var funcIds = new Array();
			for(var i=0; i<checkedIds.length; i++){
				funcIds[i]=checkedIds[i].id;
			}
			var departId = jQuery(treeContent).treeview("getSelected")[0].id;
			var params = {
				funcIds: JSON.stringify(funcIds),
				departId:departId
			};
			$.ajax({
				url: "mgr/depart/givePerm.do",
				type: "post",
				data: params,
				dataType: "text",
				success: function (result) {
					$(givePermVeiw).modal('hide');
				},
				error: function (e) {
					alert("失败" + e.toSource());
				}
			});
		}
		
		//11、给部门添加用户
		function giveUserConfirm(){
			var departId = jQuery(treeContent).treeview("getSelected")[0].id;
			var userIds = jQuery(giveUserContent).jqGrid('getGridParam','selarrrow');
			var params = {
					userIds: JSON.stringify(userIds),
					departId:departId
				};
			$.ajax({
				url: "mgr/depart/giveUser.do",
				type: "post",
				data: params,
				dataType: "text",
				success: function (result) {
					$(giveUserVeiw).modal('hide');
				},
				error: function (e) {
					alert("失败" + e.toSource());
				}
			});
		}
		
		//12、选择用户
		function checkUser(checkedList){
			for(var i=0; i<checkedList.length; i++){
				var userId = checkedList[i].id;
				jQuery(giveUserContent).jqGrid('setSelection',userId,true);
			};
		}
		
		function checkPerm(node){
			
        	var parentNode = jQuery(givePermContent).treeview('getParent', node.nodeId);
        	jQuery(givePermContent).treeview('checkNode', parentNode.nodeId, {silent:false,});

        	var ppNode = jQuery(givePermContent).treeview('getParent', parentNode.nodeId);
        	if(typeof(ppNode.nodeId) != "undefined"){
        		alert(33);
        		checkPerm(ppNode);
        	}
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
							部门列表
						</li>
					</ul>
				</div>
				<div class="page-content">
					<div class="page-header">
						<h1>部门列表</h1>
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
		<!-- 超级管理员展示功能列表 -->
		<div class="modal fade bs-example-modal-sm" id="actionViewAdmin" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-sm">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
						<h4 class="modal-title" id="actionViewAdmin_title">可操作列表</h4>
					</div>
					<div class="modal-body">
						<table class="table table-bordered table-hover table-condensed table-style">
							<tbody>
							<tr>
								<td>
									<a class="btn btn-sm btn-primary" onclick="showAddVeiw()">新增一个部门</a>
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
						<h4 class="modal-title" id="actionView_title">可操作列表</h4>
					</div>
					<div class="modal-body">
						<table class="table table-bordered table-hover table-condensed table-style">
							<tbody>
							<tr>
								<td>
									<c:if test="${not empty sessionScope.mgrFuncMap.func_1_2_1}"><a class="btn btn-sm btn-primary" onclick="showAddVeiw()">新增一个部门</a></c:if>
								</td>
							</tr>
							<tr>
								<td>
									<c:if test="${not empty sessionScope.mgrFuncMap.func_1_2_2}"><a class="btn btn-sm btn-primary" onclick="showEditVeiw()">修改一个部门</a></c:if>
								</td>
							</tr>
							<tr>
								<td>
									<c:if test="${not empty sessionScope.mgrFuncMap.func_1_2_3}"><a class="btn btn-sm btn-primary" onclick="showDeleteVeiw()">删除一个部门</a></c:if>
								</td>
							</tr>
							<tr>
								<td>
									<c:if test="${not empty sessionScope.mgrFuncMap.func_1_2_5}"><a class="btn btn-sm btn-primary" onclick="showGiveUserVeiw()">分配人员</a></c:if>
								</td>
							</tr>
							<tr>
								<td>
									<c:if test="${not empty sessionScope.mgrFuncMap.func_1_2_4}"><a class="btn btn-sm btn-primary" onclick="showGivePermVeiw()">赋予权限</a></c:if>
								</td>
							</tr>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
		<!-- 新增功能列表 -->
		<div class="modal fade bs-example-modal-sm" id="addVeiw" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-sm">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
						<h4 class="modal-title" id="addEditVeiw_title">新增</h4>
					</div>
					<div class="modal-body">
						<table class="table table-bordered table-hover table-condensed table-style">
							<tbody>
							<tr>
								<td style="vertical-align: middle;">父部门名称</td>
								<td>
									<div id="parentCnName"></div>
								</td>
							</tr>
							<tr>
								<td style="vertical-align: middle;">部门名称</td>
								<td>
									<input type="text" id="cnName" />
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
		<div class="modal fade bs-example-modal-sm" id="editVeiw" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-sm">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
						<h4 class="modal-title" id="addEditVeiw_title">修改</h4>
					</div>
					<div class="modal-body">
						<table class="table table-bordered table-hover table-condensed table-style">
							<tbody>
							<tr>
								<td style="vertical-align: middle;">父部门名称</td>
								<td>
									<div id="parentCnNameEdit"></div>
								</td>
							</tr>
							<tr>
								<td style="vertical-align: middle;">部门名称</td>
								<td>
									<input type="text" id="cnNameEdit" />
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
		<!-- 权限赋予 -->
		<div class="modal fade bs-example-modal-sm" id="givePermVeiw" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-sm" style="width:500px">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
						<h4 class="modal-title" id="givePermVeiw_title">赋权</h4>
					</div>
					<div class="modal-body">
						<div id="givePermContent"></div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-sm btn-default" data-dismiss="modal">取消</button>
						<a class="btn btn-sm btn-primary" onclick="givePermConfirm()">确定</a>
					</div>
				</div>
			</div>
		</div>
		<!-- 给部门添加用户 -->
		<div class="modal fade bs-example-modal-lg" id="giveUserVeiw" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-sm">
				<div class="modal-content" style="width: 500px;">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
						<h4 class="modal-title" id="giveUserVeiw_title">添加用户</h4>
					</div>
					<div class="modal-body">
						<table id="giveUserContent"></table>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-sm btn-default" data-dismiss="modal">取消</button>
						<a class="btn btn-sm btn-primary" onclick="giveUserConfirm()">确定</a>
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
