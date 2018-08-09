<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%> 
<script type="text/javascript">
	//修改密码校验
	$(document).ready(function() { 
		//验证新密码与原密码不能相同
		//addMethod(name,method,message)
		jQuery.validator.addMethod("checkpass", function(value) {     
		      if(value == $('#oldPwd').val()){  
		          return false;  
		      }else{  
		          return true;  
		      }  
		 });  
		
		$('#modifyPassForm').validate( {
			rules : {
				oldPwd : {
					required : true,
					rangelength : [6,16]
				},
				newPwd : {
					required : true,
					rangelength : [6,16],
					checkpass : true
				},
				confirmPwd : {
					required : true,
					rangelength : [6,16],
					equalTo: "#newPwd" 
				}
			},
			messages : {
				oldPwd : {
					required : "请输入原密码",
					rangelength : jQuery.validator.format("原密码必须是{0}-{1}位，区分大小写")
				},
				newPwd : {
					required : "请输入新密码",
					rangelength : jQuery.validator.format("新密码必须是{0}-{1}位，区分大小写"),
					checkpass : "新密码与原密码不能相同"
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
			     var param = $("#modifyPassForm").serialize(); 
			     $.ajax({  
					url : "modifypass.do",  
					type : "post",  
					dataType : "json",  
					data : param,  
					success : function(result) {
						$('#oldPwd').val("");
						$('#newPwd').val("");
				        $('#confirmPwd').val("");
						if (result == 1) {
							// 密码修改成功
							$('#alert-danger').css("display", "none");
							$('#alert-error').css("display", "none");
							$('#alert-success').css("display", "block");
						}
						if(result == 2){
							// 原密码错误
							$('#alert-danger').css("display", "block");
					        $('#oldPwd').focus();
						}
						if(result == 0){
							// 密码修改失败
							$('#alert-error').css("display", "block");
					        $('#oldPwd').focus();
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
				修改密码
			</li>
		</ul>
		<!-- /.breadcrumb -->
	</div>

	<div class="page-content">
		<!-- page-header -->
		<div class="page-header">
			<h1>修改密码</h1>
		</div>
		<!-- /.page-header -->
		
		<div class="row">
			<div class="col-xs-12">
			
				<!-- alert-danger -->
				<div id="alert-danger" class="alert alert-danger" style="display: none;">
					<button type="button" class="close" data-dismiss="alert">
						<i class="ace-icon fa fa-times"></i>
					</button>
					<strong>
						<i class="ace-icon fa fa-times"></i>
						原始密码输入错误，请重试！
					</strong>
					<br />
				</div>
				
				<!-- alert-error -->
				<div id="alert-error" class="alert alert-danger" style="display: none;">
					<button type="button" class="close" data-dismiss="alert">
						<i class="ace-icon fa fa-times"></i>
					</button>
					<strong>
						<i class="ace-icon fa fa-times"></i>
						密码修改失败，请重试！
					</strong>
					<br />
				</div>
				
				<!-- alert-success -->
				<div id="alert-success" class="alert alert-success" style="display: none;">
					<button type="button" class="close" data-dismiss="alert">
						<i class="ace-icon fa fa-times"></i>
					</button>
					<strong>
						<i class="ace-icon fa fa-check"></i>
						密码修改成功，下次登录请输入新密码！
					</strong>
				</div>
				
				<!-- PAGE CONTENT BEGINS -->
				<form class="form-horizontal" role="form" name="modifyPassForm" id="modifyPassForm" action="modifypass.do" method="post">
				
					<div class="form-group">
						<label class="col-sm-4 control-label no-padding-right" for="oldPwd">原密码</label>
						<div class="col-sm-6">
							<input type="password" id="oldPwd" name="oldPwd" placeholder="原密码" class="col-xs-10 col-sm-5" />
						</div>
					</div>
					
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