<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript">
	// 新增 弹出modal
	function showAddCategory() {
		$('#errorInfo').html("");
		$("#categoryName").val("");
		$("#categoryWeight").val("");
		$('#alert-error').css("display", "none");
		$('#alert-success').css("display", "none");
		// 模态对话框显示
		$('#addCategoryModal').modal('show');
	}

	// 新增
	function addCategory() {
		var categoryName = $("#categoryName").val();
		var categoryWeight = $("#categoryWeight").val();
		var categoryType = $("#categoryType").val();
		var categoryPic = $("#category_pic").val();
		// 字段校验
		if (categoryName.length==0 || $.trim(categoryName)=="" || categoryWeight.length==0 || $.trim(categoryWeight)=="") {
			$('#errorInfo').html("分类名称或权重不能为空！");
			$('#categoryName').focus();
			return;
		}
		var params = {
			categoryName : categoryName,
			categoryType : categoryType,
			categoryWeight : categoryWeight,
			categoryPic : categoryPic
		}
		$.ajax({
			url : "sys/category/add.do",
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
						id: data.id,
						categoryName: data.categoryName,
						categoryWeight: data.categoryWeight,
						categoryType: data.categoryType,
						categoryPic : data.categoryPic,
						createTime: data.createTime,
						updateTime: data.updateTime
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
				$('#addCategoryModal').modal('hide');
			},
			error : function(e) {
				location.href = location.href;
			}
		});
	}

	// 修改 弹出modal
	function showUpdateCategory(rowId) {
		// 执行的操作
		$('#errorInfos').html("");
		$('#alert-error').css("display", "none");
		$('#alert-success').css("display", "none");
		var rowData = $('#grid-table').jqGrid('getRowData', rowId);
		$("#rowId").val(rowId);
		$("#updateId").val(rowData.id);
		$("#updCategoryName").val(rowData.categoryName);
		$("#updCategoryWeight").val(rowData.categoryWeight);
		var categoryType = rowData.categoryType;
		switch (categoryType) {
			case "头条":
				$("#updCategoryType").val(1);
				break;
			case "杂志":
				$("#updCategoryType").val(2);
				break;
		}
		// 模态对话框显示
		$('#updateCategoryModal').modal('show');
	}

	// 修改
	function updateCategory() {
		var rowId = $("#rowId").val();
		var updateId = $("#updateId").val();
		var updCategoryName = $("#updCategoryName").val();
		var updCategoryWeight = $("#updCategoryWeight").val();
		var updCategoryType = $("#updCategoryType").val();
		var updCategoryPic = $("#upd_category_pic").val();
		//字段校验
		if (updCategoryName.length==0 || $.trim(updCategoryName)=="" || updCategoryWeight.length==0 || $.trim(updCategoryWeight)=="") {
			$('#errorInfos').html("分类名称或权重不能为空！");
			$('#updCategoryName').focus();
			return;
		}
		var params = {
			updateId : updateId,
			updCategoryName : updCategoryName,
			updCategoryType : updCategoryType,
			updCategoryWeight : updCategoryWeight,
			updCategoryPic : updCategoryPic
		}
		$.ajax({
			url : "sys/category/update.do",
			type : "post",
			dataType : "json",
			data : params,
			success : function(result) {
				var bisStatus = result.bisStatus;
				if (bisStatus == '1000') {
					var data = result.bisObj;
					var curRowData = $("#grid-table").jqGrid('getRowData', rowId);
					$.extend(curRowData, {
						categoryName : data.categoryName,
						categoryType: data.categoryType,
						categoryWeight: data.categoryWeight,
						categoryPic: data.categoryPic,
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
				$('#updateCategoryModal').modal('hide');
			},
			error : function(e) {
				location.href = location.href;
			}
		});
	}

	// 上传图片
	function uploadPic() {
		// 部分用户
		var addAttr = $("input[name='addAttr']").val();
		if (addAttr.length == 0 || $.trim(addAttr) == "") {
			$('#errorInfo').html("请选择上传图片！");
			return;
		}
		$.ajaxFileUpload({
			url: 'sys/upload/image.do', // 用于文件上传的服务器端请求地址
			secureuri: false, // 是否需要安全协议，一般设置为false
			type: 'post',
			fileElementId: 'addAttr', // 文件上传域的ID
			dataType: 'json',
			success: function (data) {
				if (data.result == '1') {
					$("#category_pic").val(data.url);
					$("#effectiveDiv").html("图片上传成功");
				} else {
					$("#effectiveDiv").html("图片上传失败");
				}
			},
			error: function (data, status, e) {
				location.href = location.href;
			}
		});
	}

	// 上传图片
	function uploadPicUpd() {
		// 部分用户
		var addAttrUpd = $("input[name='addAttrUpd']").val();
		if (addAttrUpd.length == 0 || $.trim(addAttrUpd) == "") {
			$('#errorInfo').html("请选择上传图片！");
			return;
		}
		$.ajaxFileUpload({
			url: 'sys/upload/image/upd.do', // 用于文件上传的服务器端请求地址
			secureuri: false, // 是否需要安全协议，一般设置为false
			type: 'post',
			fileElementId: 'addAttrUpd', // 文件上传域的ID
			dataType: 'json',
			success: function (data) {
				if (data.result == '1') {
					$("#upd_category_pic").val(data.url);
					$("#effectiveDivUpd").html("图片上传成功");
				} else {
					$("#effectiveDivUpd").html("图片上传失败");
				}
			},
			error: function (data, status, e) {
				location.href = location.href;
			}
		});
	}

    // 删除分类
    function delCategory(rowId) {
        $('#alert-error').css("display", "none");
        $('#alert-success').css("display", "none");
        var rowData = $('#grid-table').jqGrid('getRowData', rowId);
        var delId = rowData.id;
        var delCategoryName = rowData.categoryName;
        bootbox.confirm("<h4 style='text-align: center;'>您确定要删除名称为【" + delCategoryName +"】的分类吗？</h4>", function(result) {
            if(result) {
                var params = {
                    delId : delId
                }
                $.ajax({
                    url : "book/category/del.do",
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
				分类管理
			</li>
			<li>
				分类列表
			</li>
		</ul>
		<!-- /.breadcrumb -->
	</div>

	<div class="page-content">
		<!-- page-header -->
		<div class="page-header">
			<h1>分类列表&nbsp;&nbsp;&nbsp;&nbsp;<c:if test="${not empty sessionScope.mgrFuncMap.func_0_2_1_1}"><button class="btn btn-primary" onclick="showAddCategory();">新增分类</button></c:if></h1>
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

				<!-- modal start 新增 -->
				<div class="modal fade" id="addCategoryModal" tabindex="-1" role="dialog" aria-labelledby="addCategoryModalLabel" aria-hidden="true">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
								<h4 class="modal-title" id="addCategoryModalLabel">新增分类</h4>
							</div>
							<div class="modal-body">
								<div id="errorInfo" style="color: red;"></div>
								<table class="table table-bordered table-hover table-condensed table-style">
									<tbody>
										<tr>
											<td style="vertical-align: middle;">分类名称</td>
											<td>
												<input type="text" id="categoryName" />
											</td>
										</tr>
										<tr>
											<td style="vertical-align: middle;">类型</td>
											<td>
												<select id="categoryType">
													<option value="1">头条</option>
													<option value="2">杂志</option>
												</select>
											</td>
										</tr>
										<tr>
											<td style="vertical-align: middle;">图片</td>
											<td>
												<div class="row">
													<div class="col-xs-9">
														<input type="hidden" id="category_pic"/>
														<input id="addAttr" name="addAttr" type="file" onchange=""/>
													</div>
													<div class="col-xs-3">
														<button class="btn btn-minier btn-primary" onclick="uploadPic();">
															上传图片
														</button>
													</div>
												</div>
												<div class="row">
													<div id="effectiveDiv" style="color: red;"></div>
												</div>
											</td>
										</tr>
										<tr>
											<td style="vertical-align: middle;">权重</td>
											<td>
												<input type="text" id="categoryWeight" onkeyup="this.value=this.value.replace(/\D/g,'')" />
											</td>
										</tr>
									</tbody>
								</table>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-sm btn-default" data-dismiss="modal">取消</button>
								<a class="btn btn-sm btn-primary" onclick="addCategory();">确定</a>
							</div>
						</div>
					</div>
				</div>
				<!-- modal end 新增 -->

				<!-- modal start 修改 -->
				<div class="modal fade" id="updateCategoryModal" tabindex="-1" role="dialog" aria-labelledby="updateCategoryModalLabel" aria-hidden="true">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
								<h4 class="modal-title" id="updateCategoryModalLabel">修改分类</h4>
							</div>
							<div class="modal-body">
								<div id="errorInfos" style="color: red;"></div>
								<table class="table table-bordered table-hover table-condensed table-style">
									<tbody>
										<tr>
											<td style="vertical-align: middle;">分类名称</td>
											<td>
												<input type="text" id="updCategoryName"  />
												<input type="hidden" id="updateId" />
												<input type="hidden" id="rowId" />
											</td>
										</tr>
										<tr>
											<td style="vertical-align: middle;">类型</td>
											<td>
												<select id="updCategoryType">
													<option value="1">头条</option>
													<option value="2">杂志</option>
												</select>
											</td>
										</tr>
										<tr>
											<td style="vertical-align: middle;">图片</td>
											<td>
												<div class="row">
													<div class="col-xs-9">
														<input type="hidden" id="upd_category_pic"/>
														<input id="addAttrUpd" name="addAttrUpd" type="file" onchange=""/>
													</div>
													<div class="col-xs-3">
														<button class="btn btn-minier btn-primary" onclick="uploadPicUpd();">
															上传图片
														</button>
													</div>
												</div>
												<div class="row">
													<div id="effectiveDivUpd" style="color: red;"></div>
												</div>
											</td>
										</tr>
										<tr>
											<td style="vertical-align: middle;">权重</td>
											<td>
												<input type="text" id="updCategoryWeight" onkeyup="this.value=this.value.replace(/\D/g,'')" />
											</td>
										</tr>
									</tbody>
								</table>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-sm btn-default" data-dismiss="modal">取消</button>
								<a class="btn btn-sm btn-primary" onclick="updateCategory();">确定</a>
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
	var colNames = [ '分类ID', '分类名称', '类型', '图片','权重', '创建时间', '更新时间', '操作'];
	var colModel = [{
		name : 'id',
		index : 'id',
		width : 200,
		align : "center",
        sortable : false,
        search : false
	}, {
		name : 'categoryName',
		index : 'categoryName',
		width : 300,
		sortable : false,
		align : "center"
	}, {
		name: 'categoryType',
		index: 'categoryType',
		width: 200,
		sortable: false,
		align: "center",
		stype: "select",
		searchoptions: {
			value: "1:头条;2:杂志"
		},
		formatter: function (cellvalue, options, rowObject) {
			if (cellvalue == '1') return "<span class='label label-sm label-grey arrowed'>头条</span>";
			if (cellvalue == '2') return "<span class='label label-sm label-yellow arrowed'>杂志</span>";
		},
		unformat : function (cellvalue, options, rowObject) {
			return cellvalue;
		}
	}, {
		name : 'categoryPic',
		index : 'categoryPic',
		width : 200,
		align : "center",
        sortable : false,
        search : false,
		formatter: function (cellvalue, options, rowObject) {
			if(cellvalue!=null){
				return "<img class='mgr-img' src='"+cellvalue+"'/>";
			}
			return "";
		}
	}, {
		name : 'categoryWeight',
		index : 'categoryWeight',
		width : 200,
		align : "center",
        sortable : false,
        search : false
	}, {
		name : 'createTime',
		index : 'createTime',
		width : 350,
		align : "center",
        sortable : false,
        search : false
	}, {
		name : 'updateTime',
		index : 'updateTime',
		width : 350,
		align : "center",
        sortable : false,
        search : false
	}, {
		name : 'operate',
		index : 'operate',
		width : 200,
		sortable : false,
		search : false,
		align : "center",
		formatter: function (cellvalue, options, rowObject) {
			var btsHtml = "";
			if(typeof(mgrFuncMap["func_0_2_1_2"]) != "undefined"){
				btsHtml = btsHtml + "<button class='btn btn-minier btn-warning' type='button' onclick='showUpdateCategory("+ options.rowId + ");'>修改</button>";
			}
            if(typeof(mgrFuncMap["func_0_2_1_3"]) != "undefined"){
                btsHtml = btsHtml + "&nbsp;&nbsp;<button class='btn btn-minier btn-danger' type='button' onclick='delCategory("+ options.rowId + ");'>删除</button>";
            }
			return btsHtml;
		}
	}];
	var caption = "分类列表";
	reloadJqGrid("sys/category/list/json.do", "json", grid_data, colNames, colModel, false, caption, true, false);
</script>