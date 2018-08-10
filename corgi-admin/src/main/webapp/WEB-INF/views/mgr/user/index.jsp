<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<!DOCTYPE html>
<html lang="zh-CN">
	<head>
		<jsp:include page="/WEB-INF/views/include/jscss.jsp" flush="true" />
		<title>${mgr_title}</title>
		<script type="text/javascript">
			var mgrFuncMap = ${sessionScope.mgrFuncList};
			jQuery(function($) {
				var mgrFuncList = ${sessionScope.mgrFuncList};
				showSidebarTree(mgrFuncList);
			});
		</script>
	</head>
	<body class="no-skin">
		<!-- navbar -->
		<jsp:include page="/WEB-INF/views/include/navbar.jsp" flush="true" />
		
		<!-- main-container -->
		<div class="main-container" id="main-container">
			<input type="hidden" id="parentId" />
			<input type="hidden" id="firstSubId" />
			<input type="hidden" id="secondSubId" />
			<script type="text/javascript">
				try{ace.settings.check('main-container', 'fixed')}catch(e){}
			</script>
			
			<!-- sidebar -->
			<jsp:include page="/WEB-INF/views/include/sidebar.jsp" flush="true" />

			<!-- main-content -->
			<div class="main-content" id="main-content">
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
								后台首页
							</li>
						</ul>
						<!-- /.breadcrumb -->
					</div>
				
					<div class="page-content">
						<!-- page-header -->
						<div class="page-header">
							<h1>后台首页</h1>
						</div>
						<!-- /.page-header -->
						
						<div class="row">
							<div class="col-xs-12">

								<!-- PAGE CONTENT BEGINS -->
								<div>
									<div id="user-profile-1" class="user-profile row">
										<div class="col-xs-12 col-sm-3 center">
											<div>
												<span class="profile-picture">
													<img id="avatar" class="editable img-responsive" alt="Alex's Avatar" src="static/avatars/profile-pic.jpg" />
												</span>

												<div class="space-4"></div>

												<div class="width-80 label label-info label-xlg arrowed-in arrowed-in-right">
													<div class="inline position-relative">
														<a class="user-title-label dropdown-toggle" data-toggle="dropdown">
															<i class="ace-icon fa fa-circle light-green"></i>
															&nbsp;
															<span class="white">${mgrUser.account}</span>
														</a>
													</div>
												</div>
											</div>
										</div>

										<div class="col-xs-12 col-sm-9">

											<div class="space-12"></div>

											<div class="profile-user-info profile-user-info-striped">
												<div class="profile-info-row">
													<div class="profile-info-name">登录账号</div>
													<div class="profile-info-value">
														<span id="account">${mgrUser.account}</span>
													</div>
												</div>

												<div class="profile-info-row">
													<div class="profile-info-name">姓名</div>
													<div class="profile-info-value">
														<span id="realName">${mgrUser.realName}</span>
													</div>
												</div>

												<div class="profile-info-row">
													<div class="profile-info-name">邮箱</div>
													<div class="profile-info-value">
														<span id="email">${mgrUser.email}</span>
													</div>
												</div>

												<div class="profile-info-row">
													<div class="profile-info-name">账号状态</div>
													<div class="profile-info-value">
														<span id="status">
															<c:if test="${mgrUser.status==0}">未激活</c:if>
															<c:if test="${mgrUser.status==1}">已激活</c:if>
															<c:if test="${mgrUser.status==2}">激活失败</c:if>
															<c:if test="${mgrUser.status==3}">已禁用</c:if>
														</span>
													</div>
												</div>

												<div class="profile-info-row">
													<div class="profile-info-name">注册时间</div>
													<div class="profile-info-value">
														<span id="createTime">
															<%--S=Short, M=Medium, L=Long, F=Full, -=None--%>
															<javatime:format value="${mgrUser.createTime}" style="FL" />
														</span>
													</div>
												</div>

												<div class="profile-info-row">
													<div class="profile-info-name">最后登录时间</div>
													<div class="profile-info-value">
														<span id="lastLoginTime">
															<javatime:format value="${mgrUser.lastLoginTime}" style="FL" />
														</span>
													</div>
												</div>

											</div>
										</div>
									</div>
								</div>
								<!-- PAGE CONTENT ENDS -->

								<%--<div class="hr hr-16"></div>

								<!-- 为ECharts准备一个具备大小（宽高）的Dom -->
								<div id="main" style="width: 600px;height:400px;">ssss</div>--%>

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
