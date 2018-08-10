<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
				日志短信管理
			</li>
			<li>
				Api接口日志
			</li>
		</ul>
		<!-- /.breadcrumb -->
	</div>

	<div class="page-content">
		<!-- page-header -->
		<div class="page-header">
			<h1>Api接口日志</h1>
		</div>
		<!-- /.page-header -->
		
		<div class="row">
			<div class="col-xs-12">
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
	var colNames = ['ID', '用户ID', '手机号', '接口编号', '入参', '出参', '执行时间', '来源', '状态', '创建时间'];
	var colModel = [{
		name : 'id',
		index : 'id',
		hidden : true
	}, {
		name : 'userId',
		index : 'userId',
		width : 60,
		sortable : false,
		align : "center"
	}, {
		name : 'mobile',
		index : 'mobile',
		width : 170,
		sortable : false,
		align : "center"
	}, {
		name : 'serviceId',
		index : 'serviceId',
		width : 140,
		sortable : false,
		align : "center"
	}, {
		name : 'paramIn',
		index : 'paramIn',
		width : 400,
		search : false,
		sortable : false,
		align : "center"
	}, {
		name : 'paramOut',
		index : 'paramOut',
		width : 400,
		search : false,
		sortable : false,
		align : "center"
	}, {
		name : 'execTime',
		index : 'execTime',
		width : 100,
		search : false,
		sortable : false,
		align : "center"
	}, {
		name : 'logSource',
		index : 'logSource',
		width : 100,
		sortable : false,
		align : "center",
		stype : "select",
		searchoptions: {
			value : "0:宝付;1:国誉;2:信德;3:face++"
		},
		formatter: function (cellvalue, options, rowObject) {
			if (cellvalue == '0') return "宝付";
			if (cellvalue == '1') return "国誉";
			if (cellvalue == '2') return "信德";
			if (cellvalue == '3') return "face++";
		}
	}, {
		name : 'status',
		index : 'status',
		width : 80,
		sortable : false,
		align : "center",
		stype : "select",
		searchoptions: {
			value : "0:失败;1:成功"
		},
		formatter: function (cellvalue, options, rowObject) {
            if (cellvalue == '0') return "<span class='label label-sm label-grey arrowed'>失败</span>";
            if (cellvalue == '1') return "<span class='label label-sm label-success arrowed'>成功</span>";
		}
	}, {
		name : 'createTime',
		index : 'createTime',
		width : 140,
		align : "center",
		search : false,
		sortable : false
	} ];
	var caption = "Api接口日志";
	reloadJqGrid("api/log/list/json.do", "json", grid_data, colNames, colModel, false, caption, true);
</script>