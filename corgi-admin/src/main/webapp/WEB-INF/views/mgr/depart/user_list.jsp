<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%> 
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
				权限管理
			</li>
			<li>
				部门用户列表
			</li>
		</ul>
		<!-- /.breadcrumb -->
	</div>

	<div class="page-content">
		<!-- page-header -->
		<div class="page-header">
			<h1>部门用户列表</h1>
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
	var grid_data = ${userList};
	var colNames = ['帐号', '邮箱', '真实姓名', '所属部门'];
	var colModel = [ {
		name : 'account',
		index : 'account',
		width : 350,
		sortable : false,
		align : "center"
	}, {
		name : 'email',
		index : 'email',
		width : 600,
		sortable : false,
		align : "center"
	}, {
		name : 'realName',
		index : 'realName',
		width : 320,
		sortable : false,
		align : "center"
	}, {
		name : 'departName',
		index : 'departName',
		width : 350,
		sortable : false,
		align : "center"
	}];
	var caption = "部门用户列表";
	reloadJqGrid("", "local", grid_data, colNames, colModel, false, caption, true);
</script>