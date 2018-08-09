<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript">
	// 修改 弹出modal
	function showUpdateMgrUser(rowId) {
		// 执行的操作
		$('#errorInfos').html("");
		$('#alert-error').css("display", "none");
		$('#alert-success').css("display", "none");
		var rowData = $('#grid-table').jqGrid('getRowData', rowId);
		$("#rowId").val(rowId);
		$("#updateId").val(rowData.id);
		$("#upd_account").html(rowData.account);
		$("#upd_real_name").val(rowData.realName);
		$("#upd_email").val(rowData.email);
		var status = rowData.status;
		switch (status) {
			case "未激活":
				$("#upd_status").val(0);
				break;
			case "已激活":
				$("#upd_status").val(1);
				break;
			case "激活失败":
				$("#upd_status").val(2);
				break;
			case "已禁用":
				$("#upd_status").val(3);
				break;
		}
		// 模态对话框显示
		$('#updateMgrUserModal').modal('show');
	}

	// 修改
	function updateMgrUser() {
		var rowId = $("#rowId").val();
		var updateId = $("#updateId").val();
		var updEmail = $("#upd_email").val();
		var updRealName = $("#upd_real_name").val();
		var updStatus = $("#upd_status").val();
		// 字段校验
		if (updEmail.length==0 || $.trim(updEmail)=="" || updRealName.length==0 || $.trim(updRealName)=="") {
			$('#errorInfos').html("邮箱或姓名不能为空！");
			return;
		}
		var params = {
			updateId : updateId,
			updEmail : updEmail,
			updRealName : updRealName,
			updStatus : updStatus
		}
		$.ajax({
			url : "mgr/user/update.do",
			type : "post",
			dataType : "json",
			data : params,
			success : function(result) {
				var bisStatus = result.bisStatus;
				if (bisStatus == '1000') {
					var data = result.bisObj;
					var curRowData = $("#grid-table").jqGrid('getRowData', rowId);
					$.extend(curRowData, {
						realName : data.realName,
						status : data.status,
						updateTime : data.updateTime
					})
					// 更新列表本行数据
					$("#grid-table").jqGrid('setRowData', rowId, curRowData);
					// 修改成功
					$('#alert-success').css("display", "block");
				}  else {
					$('#error_msg').html(result.bisMsg);
					$('#alert-error').css("display", "block");
				}
				// 模态对话框隐藏
				$('#updateMgrUserModal').modal('hide');
			},
			error : function(e) {
				location.href = location.href;
			}
		});
	}

	// 删除
	function delMgrUser(rowId) {
		$('#alert-error').css("display", "none");
		$('#alert-success').css("display", "none");
		var rowData = $('#grid-table').jqGrid('getRowData', rowId);
		var delId = rowData.id;
		var delAccount = rowData.account;
		bootbox.confirm("<h4 style='text-align: center;'>您确定要删除账号为【" + delAccount +"】的此用户吗？</h4>", function(result) {
			if(result) {
				var params = {
					delId : delId
				}
				$.ajax({
					url : "mgr/user/del.do",
					type : "post",
					dataType : "json",
					data : params,
					success : function(result) {
						var bisStatus = result.bisStatus;
						if (bisStatus == '1000') {
							// 删除成功
							$("#grid-table").jqGrid('delRowData', rowId);
							$('#alert-success').css("display", "block");
						}  else {
							$('#error_msg').html(result.bisMsg);
							$('#alert-error').css("display", "block");
						}
					},
					error : function(e) {
						location.href = location.href;
					}
				});
			}
		});
	}
</script>
<div class="main-content-inner">
	<div class="breadcrumbs" id="breadcrumbs">
		<script type="text/javascript">
			try{ace.settings.check('breadcrumbs', 'fixed')}catch(e){}
		</script>
		<!-- breadcrumb -->
		<ul class="breadcrumb">
			<li>
				<i class="ace-icon fa fa-home home-icon"></i>
				${mgr_title}
			</li>
			<li>
				用户管理
			</li>
			<li>
				后台用户列表
			</li>
		</ul>
		<!-- /.breadcrumb -->
	</div>

	<div class="page-content">
		<!-- page-header -->
		<div class="page-header">
			<h1>后台用户列表</h1>
		</div>
		<!-- /.page-header -->

		<div class="row">
			<div class="col-xs-12">

				<!-- alert-success -->
				<div id="alert-success" class="alert alert-success" style="display: none;">
					<button type="button" class="close" data-dismiss="alert">
						<i class="ace-icon fa fa-times"></i>
					</button>
					<strong>
						<i class="ace-icon fa fa-check"></i>
						操作成功！
					</strong>
				</div>

				<!-- alert-error -->
				<div id="alert-error" class="alert alert-danger" style="display: none;">
					<button type="button" class="close" data-dismiss="alert">
						<i class="ace-icon fa fa-times"></i>
					</button>
					<strong>
						<i class="ace-icon fa fa-times"></i>
						<span id="error_msg"></span>
					</strong>
					<br />
				</div>

				<!-- modal start 修改 -->
				<div class="modal fade" id="updateMgrUserModal" tabindex="-1" role="dialog" aria-labelledby="updateMgrUserModalLabel" aria-hidden="true">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
								<h4 class="modal-title" id="updateMgrUserModalLabel">修改用户信息</h4>
							</div>
							<div class="modal-body">
								<div id="errorInfos" style="color: red;"></div>
								<table class="table table-bordered table-hover table-condensed table-style">
									<tbody>
										<tr>
											<td style="vertical-align: middle;">账号</td>
											<td>
												<div id="upd_account"></div>
												<input type="hidden" id="updateId" />
												<input type="hidden" id="rowId" />
											</td>
										</tr>
										<tr>
											<td style="vertical-align: middle;">邮箱</td>
											<td>
												<input type="text" id="upd_email" style="width: 300px;" />
											</td>
										</tr>
										<tr>
											<td style="vertical-align: middle;">姓名</td>
											<td>
												<input type="text" id="upd_real_name" style="width: 300px;" />
											</td>
										</tr>
										<tr>
											<td style="vertical-align: middle;">状态</td>
											<td>
												<select id="upd_status">
													<option value="0">未激活</option>
													<option value="1">已激活</option>
													<option value="2">激活失败</option>
													<option value="3">已禁用</option>
												</select>
											</td>
										</tr>
									</tbody>
								</table>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-sm btn-default" data-dismiss="modal">取消</button>
								<a class="btn btn-sm btn-primary" onclick="updateMgrUser();">确定</a>
							</div>
						</div>
					</div>
				</div>
				<!-- modal end 修改 -->

				<!-- PAGE CONTENT BEGINS -->
				<table id="grid-table"></table>
				<div id="grid-pager"></div>
				<!-- PAGE CONTENT ENDS -->
			</div><!-- /.col -->
		</div><!-- /.row -->
	</div><!-- /.page-content -->
</div>
<!-- inline scripts related to this page -->
<script type="text/javascript">
	// 加载JqGrid数据
	var grid_data = "";
	var colNames = [ '用户ID', '账号', '邮箱', '真实姓名', '状态', '创建时间', '最后登录时间', '更新时间', '操作'];
	var colModel = [{
		name : 'id',
		index : 'id',
		hidden : true
	}, {
		name : 'account',
		index : 'account',
		width : 200,
		sortable : false,
		align : "center"
	}, {
		name : 'email',
		index : 'email',
		width : 300,
		sortable : false,
		align : "center"
	}, {
		name : 'realName',
		index : 'realName',
		width : 200,
		sortable : false,
		align : "center"
	}, {
		name : 'status',
		index : 'status',
		width : 180,
		sortable : false,
		align : "center",
		stype : "select",
		searchoptions: {
			value : "0:未激活;1:已激活;2:激活失败;3:已禁用"
		},
		formatter: function (cellvalue, options, rowObject) {
			if (cellvalue == '0') return "<span class='label label-sm label-warning arrowed'>未激活</span>";
			if (cellvalue == '1') return "<span class='label label-sm label-success arrowed'>已激活</span>";
			if (cellvalue == '2') return "<span class='label label-sm label-grey arrowed'>激活失败</span>";
			if (cellvalue == '3') return "<span class='label label-sm label-danger arrowed'>已禁用</span>";
		},
		unformat : function (cellvalue, options, rowObject) {
			return cellvalue;
		}
	}, {
		name : 'createTime',
		index : 'createTime',
		width : 200,
		align : "center",
        sortable : false,
        search : false
	}, {
		name : 'lastLoginTime',
		index : 'lastLoginTime',
		width : 200,
		align : "center",
        sortable : false,
        search : false
	}, {
		name : 'updateTime',
		index : 'updateTime',
		width : 200,
		align : "center",
        sortable : false,
        search : false
	},{
		name : 'operate',
		index : 'operate',
		width : 150,
		sortable : false,
		search : false,
		align : "center",
		formatter: function (cellvalue, options, rowObject) {
			var btsHtml = "";
			if (rowObject.account != "admin") {
				if(typeof(mgrFuncMap["func_2_2_1"]) != "undefined"){
					btsHtml = btsHtml + "<button class='btn btn-minier btn-warning' type='button' onclick='showUpdateMgrUser("+ options.rowId + ");'>修改</button>";
				}
				if(typeof(mgrFuncMap["func_2_2_2"]) != "undefined"){
					btsHtml = btsHtml + "&nbsp;&nbsp;<button class='btn btn-minier btn-danger' type='button' onclick='delMgrUser("+ options.rowId +");'>删除</button>";
				}
			}
			return btsHtml;
		}
	}];
	var caption = "后台用户列表";
	reloadJqGrid("mgr/user/list/json.do", "json", grid_data, colNames, colModel, false, caption, true);
</script>