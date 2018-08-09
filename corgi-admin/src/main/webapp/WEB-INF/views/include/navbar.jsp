<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!-- navbar-container -->
<div id="navbar" class="navbar navbar-default">
	<script type="text/javascript">
		try{ace.settings.check('navbar', 'fixed')}catch(e){}
	</script>
	<div class="navbar-container" id="navbar-container">
		<button type="button" class="navbar-toggle menu-toggler pull-left" id="menu-toggler" data-target="#sidebar">
			<span class="sr-only">Toggle sidebar</span> 
			<span class="icon-bar"></span>
			<span class="icon-bar"></span> 
			<span class="icon-bar"></span>
		</button>
		<div class="navbar-header pull-left">
			<a href="index.do" class="navbar-brand">
				<small> 
					<i class="fa fa-leaf"></i>
					${mgr_title}
				</small>
			</a>
		</div>
		<div class="navbar-buttons navbar-header pull-right" role="navigation">
			<ul class="nav ace-nav">
				<li class="light-blue">
					<a data-toggle="dropdown" href="#" class="dropdown-toggle"> 
						<img class="nav-user-photo" src="static/avatars/user-default.jpg" alt="Jason's Photo" />
						<span class="user-info"> 
							<small>Welcome,</small> ${mgrUser.account}
						</span> 
						<i class="ace-icon fa fa-caret-down"></i>
					</a>
					<ul class="user-menu dropdown-menu-right dropdown-menu dropdown-yellow dropdown-caret dropdown-close">
						<!-- <li>
							<a href="#">
								<i class="ace-icon fa fa-cog"></i>
								系统设置
							</a>
						</li> -->
						<li>
							<a href="javascript:void(0);" onclick="showSidebar('','','mgr-99','tomodifypass.do')">
								<i class="ace-icon fa fa-lock"></i>
								修改密码
							</a>
						</li>
						<li class="divider"></li>
						<li>
							<a href="logout.do">
								<i class="ace-icon fa fa-power-off"></i>
								退出
							</a>
						</li>
					</ul>
				</li>
			</ul>
		</div>
	</div>
</div>
<!-- /.navbar-container -->

<!-- modal start 图片放大显示 -->
<div class="modal fade text-center" id="imgBigModal" tabindex="-1" role="dialog" aria-labelledby="imgBigModalLabel" aria-hidden="true">
	<div class="modal-dialog" style="display: inline-block; width: auto;">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
			</div>
			<div class="modal-body">
				<img id="imgBig" src="" style="max-width: 800px;" />
			</div>
		</div>
	</div>
</div>
<!-- modal end 图片放大显示 -->