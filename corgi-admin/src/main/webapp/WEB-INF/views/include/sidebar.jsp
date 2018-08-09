<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<div id="sidebar" class="sidebar responsive">
	<script type="text/javascript">
		try{ace.settings.check('sidebar', 'fixed')}catch(e){}
	</script>

	<div class="sidebar-shortcuts" id="sidebar-shortcuts">
		<div class="sidebar-shortcuts-large" id="sidebar-shortcuts-large">
			<button class="btn disabled btn-success">
				<i class="ace-icon fa fa-signal"></i>
			</button>
			<button class="btn disabled btn-info">
				<i class="ace-icon fa fa-pencil"></i>
			</button>
			<button class="btn disabled btn-warning">
				<i class="ace-icon fa fa-users"></i>
			</button>
			<button class="btn disabled btn-danger">
				<i class="ace-icon fa fa-cogs"></i>
			</button>
		</div>
		<div class="sidebar-shortcuts-mini" id="sidebar-shortcuts-mini">
			<span class="btn btn-success"></span>
			<span class="btn btn-info"></span>
			<span class="btn btn-warning"></span>
			<span class="btn btn-danger"></span>
		</div>
	</div>
	<!-- /.sidebar-shortcuts -->

	<!-- nav-list -->
	<ul class="nav nav-list">
	</ul>
	<!-- /.nav-list -->

	<div class="sidebar-toggle sidebar-collapse" id="sidebar-collapse">
		<i class="ace-icon fa fa-angle-double-left" data-icon1="ace-icon fa fa-angle-double-left" data-icon2="ace-icon fa fa-angle-double-right"></i>
	</div>
	<script type="text/javascript">
		try{ace.settings.check('sidebar', 'collapsed')}catch(e){}
	</script>
</div>
