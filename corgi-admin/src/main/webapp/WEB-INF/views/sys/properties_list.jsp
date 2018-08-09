<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript">
	// 新增系统配置 弹出modal
	function showAddSysProperties() {
		// 执行的操作
		$('#errorInfo').html("");
		$("#prop_value").val("");
		$("#description").html("");
		$('#alert-error').css("display", "none");
		$('#alert-success').css("display", "none");
		$.ajax({
			url : "sys/properties/enums.do",
			type : "post",
			dataType : "json",
			success : function(data) {
				var html = '<option value="此选项不能选">请选择...</option>';
				for (i = 0; i < data.length; i++) {
					html += '<option value=' + data[i].description + '>' + data[i].propKey + '</option>';
				}
				$("#prop_key").empty();
				$("#prop_key").append(html);
			}
		});
		// 模态对话框显示
		$('#addSysPropertiesModal').modal('show');
	}

	function changeNode(){
		$("#description").html($("#prop_key").val());
	}

	// 新增系统配置
	function addSysProperties() {
		var propKey = $("#prop_key").find("option:selected").text();
		var propValue = $("#prop_value").val();
		var description = $("#description").val();
		// 字段校验
		if (propKey == "请选择...") {
			$('#errorInfo').html("此选项不能选！");
			return;
		}
		if (propValue.length==0 || $.trim(propValue)=="") {
			$('#errorInfo').html("value值不能为空！");
			return;
		}
		var params = {
			propKey : propKey,
			propValue : propValue,
			description: description
		}
		$.ajax({
			url : "sys/properties/insert.do",
			type : "post",
			dataType : "json",
			data : params,
			success : function(result) {
				var bisStatus = result.bisStatus;
				if (bisStatus == '1000') {
					var data = result.bisObj;
					var dataIds = $("#grid-table").jqGrid("getDataIDs");
					var rowId = dataIds.length + 1;
					var dataRow = {
						id : data.id,
						propKey : data.propKey,
						propValue : data.propValue,
						description : data.description,
						createTime : data.createTime,
						updateTime : data.updateTime
					};
					// 添加数据到表格顶端
					$("#grid-table").jqGrid("addRowData", rowId, dataRow, "first");
					// 新增成功
					$('#alert-success').css("display", "block");
				} else {
					$('#error_msg').html(result.bisMsg);
					$('#alert-error').css("display", "block");
				}
				// 模态对话框隐藏
				$('#addSysPropertiesModal').modal('hide');
			},
			error : function(e) {
				location.href = location.href;
			}
		});
	}

	// 修改系统配置 弹出modal
	function showUpdateSysProperties(rowId) {
		// 执行的操作
		$('#errorInfos').html("");
		$('#alert-error').css("display", "none");
		$('#alert-success').css("display", "none");
		var rowData = $('#grid-table').jqGrid('getRowData', rowId);
		$("#rowId").val(rowId);
		$("#updateId").val(rowData.id);
		$("#upd_prop_key").html(rowData.propKey);
		$("#upd_prop_value").val(rowData.propValue);
		$("#upd_description").html(rowData.description);
		// 模态对话框显示
		$('#updateSysPropertiesModal').modal('show');
	}

	// 修改系统配置
	function updateSysProperties() {
		var rowId = $("#rowId").val();
		var updateId = $("#updateId").val();
		var updPropValue = $("#upd_prop_value").val();
		// 字段校验
		if (updPropValue.length==0 || $.trim(updPropValue)=="") {
			$('#errorInfos').html("value值不能为空！");
			return;
		}
		var params = {
			updateId : updateId,
			updPropValue : updPropValue
		}
		$.ajax({
			url : "sys/properties/update.do",
			type : "post",
			dataType : "json",
			data : params,
			success : function(result) {
				var bisStatus = result.bisStatus;
				if (bisStatus == '1000') {
					var data = result.bisObj;
					var curRowData = $("#grid-table").jqGrid('getRowData', rowId);
					$.extend(curRowData, {
						propValue : data.propValue,
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
				$('#updateSysPropertiesModal').modal('hide');
			},
			error : function(e) {
				location.href = location.href;
			}
		});
	}

	// 删除系统配置
	function delSysProperties(rowId) {
		$('#alert-error').css("display", "none");
		$('#alert-success').css("display", "none");
		var rowData = $('#grid-table').jqGrid('getRowData', rowId);
		var delId = rowData.id;
		var delKey = rowData.propKey;
		bootbox.confirm("<h4 style='text-align: center;'>您确定要删除key为【" + delKey +"】的此条配置吗？</h4>", function(result) {
			if(result) {
				var params = {
					delId : delId
				}
				$.ajax({
					url : "sys/properties/delete.do",
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
				系统管理
			</li>
			<li>
				配置管理
			</li>
			<li>
				配置列表
			</li>
		</ul>
		<!-- /.breadcrumb -->
	</div>

	<div class="page-content">
		<!-- page-header -->
		<div class="page-header">
			<h1>配置列表&nbsp;&nbsp;&nbsp;&nbsp;<c:if test="${not empty sessionScope.mgrFuncMap.func_0_1_1_1}"><button class="btn btn-primary" onclick="showAddSysProperties();">新增配置</button></c:if></h1>
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

				<!-- modal start 新增系统配置 -->
				<div class="modal fade" id="addSysPropertiesModal" tabindex="-1" role="dialog" aria-labelledby="addSysPropertiesModalLabel" aria-hidden="true">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
								<h4 class="modal-title" id="addSysPropertiesModalLabel">新增配置</h4>
							</div>
							<div class="modal-body">
								<div id="errorInfo" style="color: red;"></div>
								<table class="table table-bordered table-hover table-condensed table-style">
									<tbody>
										<tr>
											<td style="vertical-align: middle;">key值</td>
											<td>
												<select id="prop_key" onchange="changeNode()"></select>
											</td>
										</tr>
										<tr>
											<td style="vertical-align: middle;">value值</td>
											<td>
												<input type="text" id="prop_value" />
											</td>
										</tr>
										<tr>
											<td style="vertical-align: middle;">描述</td>
											<td>
												<textarea style="resize:none" id="description" rows="2" cols="46" readonly="readonly"></textarea>
											</td>
										</tr>
									</tbody>
								</table>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-sm btn-default" data-dismiss="modal">取消</button>
								<a class="btn btn-sm btn-primary" onclick="addSysProperties();">确定</a>
							</div>
						</div>
					</div>
				</div>
				<!-- modal end 新增系统配置 -->

				<!-- modal start 修改系统配置 -->
				<div class="modal fade" id="updateSysPropertiesModal" tabindex="-1" role="dialog" aria-labelledby="updateSysPropertiesModalLabel" aria-hidden="true">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
								<h4 class="modal-title" id="updateSysPropertiesModalLabel">修改配置</h4>
							</div>
							<div class="modal-body">
								<div id="errorInfos" style="color: red;"></div>
								<table class="table table-bordered table-hover table-condensed table-style">
									<tbody>
										<tr>
											<td style="vertical-align: middle;">key值</td>
											<td>
												<div id="upd_prop_key"></div>
												<input type="hidden" id="updateId" />
												<input type="hidden" id="rowId" />
											</td>
										</tr>
										<tr>
											<td style="vertical-align: middle;">value值</td>
											<td>
												<input type="text" id="upd_prop_value" />
											</td>
										</tr>
										<tr>
											<td style="vertical-align: middle;">描述</td>
											<td>
												<textarea style="resize:none" id="upd_description" rows="2" cols="46" readonly= "true"></textarea>
											</td>
										</tr>
									</tbody>
								</table>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-sm btn-default" data-dismiss="modal">取消</button>
								<a class="btn btn-sm btn-primary" onclick="updateSysProperties();">确定</a>
							</div>
						</div>
					</div>
				</div>
				<!-- modal end 修改系统配置 -->

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
	var colNames = [ 'ID', 'key值', 'value值', '描述', '创建时间', '更新时间', '操作'];
	var colModel = [{
		name : 'id',
		index : 'id',
		width : 80,
		align : "center",
        sortable : false,
        search : false
	}, {
		name : 'propKey',
		index : 'propKey',
		width : 240,
		sortable : false,
		align : "center"
	}, {
		name : 'propValue',
		index : 'propValue',
		width : 200,
		sortable : false,
		search: false,
		align : "center"
	}, {
		name : 'description',
		index : 'description',
		width : 550,
		sortable : false,
		align : "center"
	}, {
		name : 'createTime',
		index : 'createTime',
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
			if(typeof(mgrFuncMap["func_0_1_1_2"]) != "undefined"){
				btsHtml = btsHtml+"<button class='btn btn-minier btn-warning' type='button' onclick='showUpdateSysProperties("+ options.rowId + ");'>修改</button>";
			}
			if(typeof(mgrFuncMap["func_0_1_1_3"]) != "undefined"){
				btsHtml = btsHtml+"&nbsp;&nbsp;"+"<button class='btn btn-minier btn-danger' type='button' onclick='delSysProperties("+ options.rowId +");'>删除</button>";
			}
			return btsHtml;
		}
	} ];
	var caption = "配置列表";
	reloadJqGrid("sys/properties/list/json.do", "json", grid_data, colNames, colModel, false, caption, true, false);
</script>