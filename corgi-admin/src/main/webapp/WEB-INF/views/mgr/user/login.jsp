<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
	<head>
		<%
			application.setAttribute("mgr_title", "Corgi后台管理系统");
		%>
		<jsp:include page="/WEB-INF/views/include/jscss.jsp" flush="true" />
		<title>${mgr_title}－登录</title>
		<script type="text/javascript">
	    	function changeCode(){
				var ran = Math.random();
				document.getElementById("verify_image").src="verifycode.do?r=" + ran;   
			}
		</script>
	</head>
	<body class="login-layout">
		<div class="main-container">
			<div class="main-content">
				<div class="row">
					<div class="col-sm-10 col-sm-offset-1">

						<!-- alert-success -->
						<div id="alert-success" class="alert alert-success" style="display: none;">
							<button type="button" class="close" data-dismiss="alert">
							</button>
							<strong>
								<i class="ace-icon fa fa-check"></i>
								带有确认链接的信息已发送到您的邮箱，请尽快激活您的账号！
							</strong>
						</div>

						<div class="login-container">

							<div class="space"></div>
							<div class="space"></div>
							
							<div class="position-relative">
								<div id="login-box"
									class="login-box visible widget-box no-border">
									<div class="widget-body">
										<div class="widget-main">
											<h4 class="header blue lighter bigger">
												<i class="ace-icon fa fa-coffee green"></i>
												${mgr_title}
											</h4>
											<div class="space-6"></div>
											<form name="loginForm" id="loginForm" action="login.do" method="post">
												<fieldset>
													<font color="red">${errorTips}</font>
													<label class="block clearfix"> 
														<span class="block input-icon input-icon-right"> 
															<input type="text" class="form-control" name="userName" id="userName" value="${userName}" placeholder="账号" /> 
															<i class="ace-icon fa fa-user"></i>
														</span>
													</label> 
													
													<label class="block clearfix"> 
														<span class="block input-icon input-icon-right"> 
															<input type="password" class="form-control" name="userPass" id="userPass" placeholder="密码" maxlength="16" /> 
															<i class="ace-icon fa fa-lock"></i>
														</span>
													</label> 
													
													<label class="block clearfix"> 
														<span class="block input-icon input-icon-right"> 
															<input type="text" class="form-control" name="verifycode" id="verifycode" placeholder="验证码" maxlength="4" /> 
															<i class="ace-icon fa fa-eye"></i>
														</span>
													</label> 
													
													<label class="block clearfix"> 
														<span class="block input-icon input-icon-right"> 
															<img id="verify_image" src="verifycode.do" width="70" height="25" />&nbsp;&nbsp; 
															<a id="refresh_verify_image" href="javascript:void(0);" onclick="changeCode();">换一张</a>
														</span>
													</label>
	
													<div class="space"></div>
	
													<div class="clearfix">
														
														<input type="hidden" id="pwdCodePage" name="pwdCodePage" />
														<input type="hidden" id="userNamePage" name="userNamePage" />
														<input type="hidden" id="verifycodePage" name="verifycodePage" />
	
														<button type="submit" class="btn btn-lg btn-primary btn-block">
															<i class="ace-icon fa fa-key"></i> 
															<span class="bigger-110">立即登录</span>
														</button>
													</div>
													<div class="space-4"></div>
												</fieldset>
											</form>
										</div>

										<div class="toolbar clearfix">
											<div>
												<a href="#" data-target="#forgot-box" class="forgot-password-link">
													<i class="ace-icon fa fa-arrow-left"></i>
													忘记密码
												</a>
											</div>

											<div>
												<a href="#" data-target="#signup-box" class="user-signup-link">
													没有账号，立即注册
													<i class="ace-icon fa fa-arrow-right"></i>
												</a>
											</div>
										</div>
									</div>
								</div>

								<div id="forgot-box" class="forgot-box widget-box no-border">
									<div class="widget-body">
										<div class="widget-main">
											<h4 class="header red lighter bigger">
												<i class="ace-icon fa fa-key"></i>
												密码找回
											</h4>

											<div class="space-6"></div>

											<form  name="retrieveForm" id="retrieveForm" action="sendretrieve.do" method="post">
												<fieldset>
													<font color="red">${retrieveTips}</font>
													<label class="block clearfix">
														<span class="block input-icon input-icon-right">
															<input type="email" class="form-control" name="retrieveEmail" id="retrieveEmail" placeholder="请输入注册邮箱" />
															<i class="ace-icon fa fa-envelope"></i>
														</span>
													</label>

													<div class="space-14"></div>

													<div class="clearfix">
														<button type="submit" class="btn btn-lg btn-danger btn-block">
															<i class="ace-icon fa fa-lightbulb-o"></i>
															<span class="bigger-110">立即发送</span>
														</button>
													</div>
												</fieldset>
											</form>
										</div><!-- /.widget-main -->

										<div class="toolbar center">
											<a href="#" data-target="#login-box" class="back-to-login-link">
												返回登录
												<i class="ace-icon fa fa-arrow-right"></i>
											</a>
										</div>
									</div><!-- /.widget-body -->
								</div><!-- /.forgot-box -->

								<div id="signup-box" class="signup-box widget-box no-border">
									<div class="widget-body">
										<div class="widget-main">
											<h4 class="header green lighter bigger">
												<i class="ace-icon fa fa-users blue"></i>
												账号注册
											</h4>
											<div class="space-6"></div>
											<form name="registerForm" id="registerForm" action="register.do" method="post">
												<fieldset>
													<label id="errorTips"></label>
													<label class="block clearfix">
														<span class="block input-icon input-icon-right">
															<input type="email" class="form-control" name="email" id="email" placeholder="邮箱" />
															<i class="ace-icon fa fa-envelope"></i>
														</span>
													</label>

													<label class="block clearfix">
														<span class="block input-icon input-icon-right">
															<input type="text" class="form-control" name="account" id="account" placeholder="账号" maxlength="20" />
															<i class="ace-icon fa fa-user"></i>
														</span>
													</label>

													<label class="block clearfix">
														<span class="block input-icon input-icon-right">
															<input type="text" class="form-control" name="realName" id="realName" placeholder="姓名" maxlength="20" />
															<i class="ace-icon fa fa-user-md"></i>
														</span>
													</label>

													<label class="block clearfix">
														<span class="block input-icon input-icon-right">
															<input type="password" class="form-control" name="pass" id="pass" placeholder="密码" maxlength="16" />
															<i class="ace-icon fa fa-lock"></i>
														</span>
													</label>

													<div class="space-14"></div>
													<div class="clearfix">
														<button type="reset" class="width-30 pull-left btn btn-sm">
															<i class="ace-icon fa fa-refresh"></i>
															<span class="bigger-110">重置</span>
														</button>

														<button type="submit" class="width-65 pull-right btn btn-sm btn-primary btn-success">
															<span class="bigger-110">立即注册</span>
															<i class="ace-icon fa fa-arrow-right icon-on-right"></i>
														</button>
													</div>
												</fieldset>
											</form>
										</div>

										<div class="toolbar center">
											<a href="#" data-target="#login-box" class="back-to-login-link">
												<i class="ace-icon fa fa-arrow-left"></i>
												返回登录
											</a>
										</div>
									</div><!-- /.widget-body -->
								</div><!-- /.signup-box -->
							</div>
							<!-- /.position-relative -->
							
							<div class="space-6"></div>
							
							<div class="center">
								<h1>
									<i class="ace-icon fa fa-leaf green"></i> 
									<span class="red">Corgi</span>
									<span class="white" id="id-text2">Application</span>
								</h1>
								<h4 class="blue" id="id-company-text">&copy; 嘿の大叔</h4>
							</div>
	
							<div class="navbar-fixed-top align-right">
								<br />
								&nbsp;
								<a id="btn-login-dark" href="#">Dark</a>
								&nbsp;
								<span class="blue">/</span>
								&nbsp;
								<a id="btn-login-blur" href="#">Blur</a>
								&nbsp;
								<span class="blue">/</span>
								&nbsp;
								<a id="btn-login-light" href="#">Light</a>
								&nbsp; &nbsp; &nbsp;
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	
		<!-- inline scripts related to this page -->
		<script type="text/javascript">
			var dataTarget = '${dataTarget}';
			jQuery(function($) {
				if (dataTarget != '') {
					if (dataTarget == '#signup-box') {
						$('#errorTips').html("<font color='red'>链接已过期，邮件已重新发送！</font>");
					}
					$('.widget-box.visible').removeClass('visible');//hide others
					$(dataTarget).addClass('visible');//show target
				}
				$(document).on('click', '.toolbar a[data-target]', function(e) {
					e.preventDefault();
					var target = $(this).data('target');
					$('.widget-box.visible').removeClass('visible');//hide others
					$(target).addClass('visible');//show target
				});
			});

			function setDark(){
				$('body').attr('class', 'login-layout');
				$('#id-text2').attr('class', 'white');
				$('#id-company-text').attr('class', 'blue');
			}
			function setLight(){
				$('body').attr('class', 'login-layout light-login');
				$('#id-text2').attr('class', 'grey');
				$('#id-company-text').attr('class', 'blue');
			}
			function setBlur(){
				$('body').attr('class', 'login-layout blur-login');
				$('#id-text2').attr('class', 'white');
				$('#id-company-text').attr('class', 'light-blue');
			}
			//you don't need this, just used for changing background
			jQuery(function($) {
				$('#btn-login-dark').on('click', function(e) {
					setDark();
				});
				$('#btn-login-light').on('click', function(e) {
					setLight();
				});
				$('#btn-login-blur').on('click', function(e) {
					setBlur();
				});

				switch (new Date().getTime()%3){
					case 0:
						setDark();
						break;
					case 1:
						setLight();
						break;
					case 2:
						setBlur();
						break;
				}
			});
		</script>
	</body>
</html>
