// 登录加密
function encryptPwd(myName,myPass){
	var code = new Hashes.MD5().hex(myName + myPass);
	var codeMsg = myName + code + "Corgi2018";
	return new Hashes.SHA256().hex(codeMsg);
}

$(document).ready(function() {  
	// 后台登录校验
	$('#loginForm').validate( {
		rules : {
			userName : {
				required : true
			},
			userPass : {
				required : true,
				minlength : 6
			},
			verifycode : {
				required : true,
				minlength : 4
			}
		},
		messages : {
			userName : {
				required : "请输入登录账号"
			},
			userPass : {
				required : "请输入登录密码",
				minlength : jQuery.format("登录密码至少{0}位")
			},
			verifycode : {
				required : "请输入验证码",
				minlength : jQuery.format("验证码必须是{0}位有效数字")
			}
		},
		errorPlacement : function(error, element) {
			error.insertAfter(element.parent());
		},
        submitHandler:function(form){  
			var _username = $('#userName').val();
			var _password = $('#userPass').val();
			var code = encryptPwd(_username,_password);
			$('#pwdCodePage').val(code);
			$('#userNamePage').val(_username);
			$('#verifycodePage').val($('#verifycode').val());
			$('#userPass').val("");
			form.submit();
        }  
	});

	// 后台注册校验
	$('#registerForm').validate( {
		rules : {
			email : {
				required : true
			},
			account : {
				required : true
			},
			realName : {
				required : true
			},
			pass : {
				required : true,
				minlength : 6
			}
		},
		messages : {
			email : {
				required : "请输入邮箱"
			},
			account : {
				required : "请输入登录账号"
			},
			realName : {
				required : "请输入姓名"
			},
			pass : {
				required : "请输入登录密码",
				minlength : jQuery.format("登录密码至少{0}位")
			}
		},
		errorPlacement : function(error, element) {
			error.insertAfter(element.parent());
		},
		submitHandler:function(form){
			var param = $("#registerForm").serialize();
			$.ajax({
				url : "register.do",
				type : "post",
				dataType : "json",
				data : param,
				success : function(result) {
					if (result == 0) {
						// 邮件发送成功
						$('#errorTips').html("");
						$('#alert-success').css("display", "block");
						return;
					}
					if(result == 1){
						// 邮箱或账号已存在
						$('#errorTips').html("<font color='red'>邮箱或账号已存在！</font>");
						return;
					}
				},
				error : function(e) {
					location.href = location.href;
				}
			});
		}
	});

	// 后台找回密码校验
	$('#retrieveForm').validate( {
		rules : {
			retrieveEmail : {
				required : true
			}
		},
		messages : {
			retrieveEmail : {
				required : "请输入注册邮箱"
			}
		},
		errorPlacement : function(error, element) {
			error.insertAfter(element.parent());
		},
		submitHandler:function(form){
			form.submit();
		}
	});

});

//格式化时间
Date.prototype.Format = function (fmt) { //author: syk 
    var o = {
        "M+": this.getMonth() + 1, //月份 
        "d+": this.getDate(), //日 
        "h+": this.getHours(), //小时 
        "m+": this.getMinutes(), //分 
        "s+": this.getSeconds(), //秒 
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
        "S": this.getMilliseconds() //毫秒 
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}
