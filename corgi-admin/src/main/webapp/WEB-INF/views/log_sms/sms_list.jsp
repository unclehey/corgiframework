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
				短信发送记录
			</li>
		</ul>
		<!-- /.breadcrumb -->
	</div>

	<div class="page-content">
		<!-- page-header -->
		<div class="page-header">
			<h1>短信发送记录</h1>
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
	var colNames = ['ID', '手机号', '短信内容', '短信类型', '发送状态', '发送时间'];
	var colModel = [{
		name : 'id',
		index : 'id',
		hidden : true
	}, {
		name : 'mobile',
		index : 'mobile',
		width : 170,
		sortable : false,
		align : "center"
	}, {
		name : 'content',
		index : 'content',
		width : 900,
		search : false,
		sortable : false,
		align : "center"
	}, {
		name : 'type',
		index : 'type',
		width : 160,
		sortable : false,
		align : "center",
		stype : "select",
		searchoptions: {
			value : "1:验证码;2:忘记密码"
		},
		formatter: function (cellvalue, options, rowObject) {
			if (cellvalue == '1') return "验证码";
			if (cellvalue == '2') return "忘记密码";
		}
	}, {
		name : 'flag',
		index : 'flag',
		width : 120,
		sortable : false,
		align : "center",
		stype : "select",
		searchoptions: {
			value : "0:发送失败;1:发送成功"
		},
		formatter: function (cellvalue, options, rowObject) {
            if (cellvalue == '0') return "<span class='label label-sm label-grey arrowed'>发送失败</span>";
            if (cellvalue == '1') return "<span class='label label-sm label-success arrowed'>发送成功</span>";
		}
	}, {
		name : 'createTime',
		index : 'createTime',
		width : 170,
		align : "center",
		search : false,
		sortable : false
	} ];
	var caption = "短信发送记录";
	reloadJqGrid("sms/record/list/json.do", "json", grid_data, colNames, colModel, false, caption, true);
</script>