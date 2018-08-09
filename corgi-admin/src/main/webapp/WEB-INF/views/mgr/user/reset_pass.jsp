<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
	<head>
		<jsp:include page="/WEB-INF/views/include/jscss.jsp" flush="true" />
		<title>${mgr_title}－重置密码</title>
		<script type="text/javascript">
			//重置密码校验
			$(document).ready(function() {
				$('#resetPassForm').validate( {
					rules : {
						newPwd : {
							required : true,
							rangelength : [6,16]
						},
						confirmPwd : {
							required : true,
							rangelength : [6,16],
							equalTo: "#newPwd"
						}
					},
					messages : {
						newPwd : {
							required : "请输入新密码",
							rangelength : jQuery.validator.format("新密码必须是{0}-{1}位，区分大小写")
						},
						confirmPwd : {
							required : "请输入确认密码",
							rangelength : jQuery.validator.format("确认密码必须是{0}-{1}位，区分大小写"),
							equalTo : "确认密码与新密码不一致"
						}
					},
					errorPlacement : function(error, element) {
						error.insertAfter(element);
					},
					submitHandler : function(form) {
						var param = $("#resetPassForm").serialize();
						$.ajax({
							url : "retrieve.do",
							type : "post",
							dataType : "json",
							data : param,
							success : function(result) {
								$('#newPwd').val("");
								$('#confirmPwd').val("");
								if (result == 0) {
									// 重置密码成功 5秒后直接跳转到登录页面
									$('#errorTips').html("<font color='red'>重置密码成功5秒后自动跳转到登录页面</font>");
									setTimeout(function () {
										location.href = "index.do";
									}, 5000);
									return;
								}
								if(result == 1){
									// 邮箱未注册或参数已篡改
									$('#errorTips').html("<font color='red'>邮箱未注册或参数已篡改！</font>");
									$('#newPwd').focus();
									return;
								}
								if(result == 2){
									// 账号未激活
									$('#errorTips').html("<font color='red'>账号未激活！</font>");
									$('#newPwd').focus();
									return;
								}
								if(result == 3){
									// 重置密码失败
									$('#errorTips').html("<font color='red'>重置密码失败，请重试！</font>");
									$('#newPwd').focus();
									return;
								}
							},
							error : function(e) {
								location.href = location.href;
							}
						});
					}
				});
			});
		</script>
	</head>
	<body class="no-skin">

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
					<a href="index.jsp" class="navbar-brand">
						<small>
							<i class="fa fa-leaf"></i>
							${mgr_title}
						</small>
					</a>
				</div>
			</div>
		</div>
		<!-- /.navbar-container -->

		<!-- main-container -->
		<div class="main-container" id="main-container">
			<script type="text/javascript">
				try{ace.settings.check('main-container', 'fixed')}catch(e){}
			</script>

			<!-- main-content -->
			<div class="main-content" id="main-content">
				<div class="main-content-inner">

					<div class="page-content">
						<!-- page-header -->
						<div class="page-header">
							<h1>重置密码</h1>
						</div>
						<!-- /.page-header -->

						<div class="row">
							<div class="col-xs-12">

								<!-- PAGE CONTENT BEGINS -->
								<form class="form-horizontal" role="form" name="resetPassForm" id="resetPassForm" action="retrieve.do" method="post">
									<label id="errorTips"></label>
									<div class="form-group">
										<label class="col-sm-4 control-label no-padding-right" for="newPwd">新密码</label>
										<div class="col-sm-6">
											<input type="password" id="newPwd" name="newPwd" placeholder="新密码" class="col-xs-10 col-sm-5" />
										</div>
									</div>

									<div class="form-group">
										<label class="col-sm-4 control-label no-padding-right" for="confirmPwd">确认密码</label>
										<div class="col-sm-6">
											<input type="password" id="confirmPwd" name="confirmPwd" placeholder="确认密码" class="col-xs-10 col-sm-5" />
										</div>
									</div>

									<div class="clearfix form-actions">
										<div class="col-md-offset-4 col-md-9">
											<input type="hidden" name="email" id="email" value="${email}" />
											<input type="hidden" name="token" id="token" value="${token}" />
											<button class="btn btn-info" type="submit">
												<i class="ace-icon fa fa-check bigger-110"></i>
												确认
											</button>

											&nbsp; &nbsp; &nbsp;
											<button class="btn" type="reset">
												<i class="ace-icon fa fa-undo bigger-110"></i>
												重置
											</button>
										</div>
									</div>
								</form>
								<!-- PAGE CONTENT ENDS -->

							</div><!-- /.col -->
						</div><!-- /.row -->
					</div><!-- /.page-content -->
				</div>
			</div>
			<!-- /.main-content -->

			<!-- footer -->
			<jsp:include page="/WEB-INF/views/include/footer.jsp" flush="true" />

			<!-- back top -->
			<a href="#" id="btn-scroll-up" class="btn-scroll-up btn btn-sm btn-inverse">
				<i class="ace-icon fa fa-angle-double-up icon-only bigger-110"></i>
			</a>
		</div>
		<!-- /.main-container -->
	</body>
</html>
