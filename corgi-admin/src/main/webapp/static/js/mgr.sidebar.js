//sidebar切换
function showSidebar(secondsubid, firstsubid, parentid, url) {
	//alert("secondsubid:" + secondsubid + ",firstsubid" + firstsubid + ",parentid" + parentid + ",url" + url);
	$(".open").removeClass();
	$(".active").removeClass();
	$(".submenu").css("display", "none");
	$("#" + parentid).children("ul").css("display", "block");
	if (secondsubid != '') {
		$("#" + firstsubid).children("ul").css("display", "block");
		$("#" + parentid).attr("class", "active open");
		$("#" + firstsubid).attr("class", "active open");
		$("#" + secondsubid).attr("class", "active");
	} else {
		if (firstsubid != '') {
			$("#" + parentid).attr("class", "active open");
			$("#" + firstsubid).attr("class", "active");
		} else {
			$("#" + parentid).attr("class", "active");
		}
	}
	// ajax请求
	$.ajax({
		url : url,
		type : "get",
		dataType : "html",
		success : function(data) {
			if (!data) {
				location.href = "tologin.do";
			} else {
				$("#parentId").val(parentid);
				$("#firstSubId").val(firstsubid);
				$("#secondSubId").val(secondsubid);
				$("#main-content").html(data);
			}
		},
		error : function(e) {
			location.href = location.href;
		}
	});
}

function showSidebarTree(mgrFuncList){
	
	var topHtml = '<li class="active" id="mgr-index">'
					+'<a href="index.do">'
						+'<i class="menu-icon fa fa-home"></i>'
						+'<span class="menu-text">后台首页</span>'
					+'</a>'
					+'<b class="arrow"></b>'
				+'</li>';
				
	$("ul[class='nav nav-list']").append(topHtml);
	makeTreeOutside(mgrFuncList);
	var footHtml = '<li id="mgr-99">'
		+'<a style="cursor:pointer;" onclick="showSidebar(\'\',\'\',\'mgr-99\',\'tomodifypass.do\')">'
			+'<i class="menu-icon fa fa-lock"></i>'
			+'<span class="menu-text">修改密码</span>'
			+'</a>'
			+'<b class="arrow"></b>'
			+'</li>';

	$("ul[class='nav nav-list']").append(footHtml);
}

function makeTreeOutside(mgrFuncList){
	
	var notPar = {};
	
	for(var key in mgrFuncList){
		var mgrFunc = mgrFuncList[key];
		var funcId = mgrFunc.funcId;
		var parentId = mgrFunc.parentId;
		var cnName = mgrFunc.cnName;
		var type = mgrFunc.type;
		var url = mgrFunc.url;
		var menuIcon = mgrFunc.menuIcon;
		var ppId = "";
		
		var treeHtml = "";
		if(type==1){
			treeHtml = '<li id="mgr-'+funcId+'">'
							+'<a style="cursor:pointer;" class="dropdown-toggle">'
							+'<i class="menu-icon '+menuIcon+'"></i>'
							+'<span class="menu-text">'+cnName+'</span>'
							+'<b class="arrow fa fa-angle-down"></b>'
						+'</a>'
						+'<b class="arrow"></b>'
						+'</li>';
		}else if(type==2){
			treeHtml = '<li id="mgr-'+funcId+'">'
							+'<a style="cursor:pointer;" onclick="showSidebar(\''+ppId+'\',\'mgr-'+funcId+'\',\'mgr-'+parentId+'\',\''+url+'\')">'
							+'<i class="menu-icon fa fa-caret-right"></i>'
							+cnName
							+'</a>'
							+'<b class="arrow"></b>'
							+'</li>';
		}

		if(typeof(mgrFuncList[parentId]) == "undefined"){
			$("ul[class='nav nav-list']").append(treeHtml);
		}else{
			var ppNodeId = "";
			var ppNode = mgrFuncList[mgrFuncList[parentId].parentId];
			if(typeof(ppNode) != "undefined"){
				ppNodeId = "mgr-"+ppNode.funcId;
			}
			mgrFunc.ppNodeId = ppNodeId;
			notPar[funcId] = mgrFunc;
		}
	}
	
	if(Object.keys(notPar).length > 0){
		makeTreeInside(notPar);
	}
}

function makeTreeInside(mgrFuncList){
	
	var notPar = {};
	
	for(var key in mgrFuncList){
		var mgrFunc = mgrFuncList[key];
		var funcId = mgrFunc.funcId;
		var parentId = mgrFunc.parentId;
		var cnName = mgrFunc.cnName;
		var type = mgrFunc.type;
		var url = mgrFunc.url;
		var menuIcon = mgrFunc.menuIcon;
		var ppId = mgrFunc.ppNodeId;
		
		var treeHtml = "";
		if(type==1){
			treeHtml = '<li id="mgr-'+funcId+'">'
							+'<a style="cursor:pointer;" class="dropdown-toggle">'
							+'<i class="menu-icon '+menuIcon+'"></i>'
							+cnName
							+'<b class="arrow fa fa-angle-down"></b>'
						+'</a>'
						+'<b class="arrow"></b>'
						+'</li>';
		}else if(type==2){
			var treeHtmlClick = "";
			if(ppId == ""){
				treeHtmlClick = '<a style="cursor:pointer;" onclick="showSidebar(\''+ppId+'\',\'mgr-'+funcId+'\',\'mgr-'+parentId+'\',\''+url+'\')">';
			}else{
				treeHtmlClick = '<a style="cursor:pointer;" onclick="showSidebar(\'mgr-'+funcId+'\',\'mgr-'+parentId+'\',\''+ppId+'\',\''+url+'\')">';
			}
			treeHtml = '<li id="mgr-'+funcId+'">'
							+treeHtmlClick
							+'<i class="menu-icon fa fa-caret-right"></i>'
							+cnName
							+'</a>'
							+'<b class="arrow"></b>'
							+'</li>';
		}

		if(typeof(mgrFuncList[parentId]) == "undefined"){
			if($("#ur-mgr-"+parentId).length){
				$("#ur-mgr-"+parentId).append(treeHtml);
			}else{
				var ulHtml = '<ul class="submenu" id="ur-mgr-'+parentId+'">'
							+'</ul>';
				$("#mgr-"+parentId).append(ulHtml);
				$("#ur-mgr-"+parentId).append(treeHtml);
			}
		}else{
			notPar[funcId] = mgrFunc;
		}
	}
	
	if(Object.keys(notPar).length > 0){
		makeTreeInside(notPar);
	}
}

//10、展示错误提示框
function alertLabel(node){
	
	var ckLay = node.attr("ckLay");
	if(typeof(ckLay) != "undefined"){
		var type = node.parents(".modal-body").find("input[name='"+ckLay+"']").attr("type");
		if(type == "radio"){
			var checked = node.parent().find("input[name='"+ckLay+"']").prop("checked");
			if(checked){
				alertEmpty();
				alertNum();
				alertLength();
				alertSize();
			}
		}else if(type == "text"){
			alertLayValue();
		}
	}else{
		alertEmpty();
		alertNum();
		alertLength();
		alertSize();
	}

	function alertLayValue(){
		
		var valueLay = node.parents(".modal-body").find("input[name='"+ckLay+"']").val();
		var value = node.val();
		if(valueLay >= value){
			if(node.parent().find("label").length <= 0){
				node.parent().append("<label class='alert-label'>请输入大于开始时间</label>");
			}
			node.parent().parent().parent().parent().find("input[name='isValid']").attr("valided","unValid");
		}
	}

	function alertEmpty(){
		if(node.val()==""
			&& node.attr("type")!="hidden"){
			if(node.parent().find("label").length <= 0){
				node.parent().append("<label class='alert-label'>不能为空</label>");
			}
			node.parent().parent().parent().parent().find("input[name='isValid']").attr("valided","unValid");
			
		}
	}

	function alertNum(){
		
		var ckRule = node.attr("ckRule");
		if(typeof(ckRule) != "undefined"){

		    var regzs = new RegExp("^[0-9]*$");
		    var regxs = new RegExp("^(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*))$");
		    var regzw = new RegExp("^\\w+$");
			if(ckRule == "num"
					&& !regzs.test(node.val())){  
				if(node.parent().find("label").length <= 0){
					node.parent().append("<label class='alert-label'>请输入正整数</label>");
				}
				node.parent().parent().parent().parent().find("input[name='isValid']").attr("valided","unValid");
		    }
			if(ckRule == "float"
				&& !regxs.test(node.val())){
				if(node.parent().find("label").length <= 0){
					node.parent().append("<label class='alert-label'>请输入浮点数</label>");
				}
				node.parent().parent().parent().parent().find("input[name='isValid']").attr("valided","unValid");
		    }
			if(ckRule == "nochina"
				&& !regzw.test(node.val())){  
				if(node.parent().find("label").length <= 0){
					node.parent().append("<label class='alert-label'>请输入字母或数字或下划线</label>");
				}
				node.parent().parent().parent().parent().find("input[name='isValid']").attr("valided","unValid");
	    	}
		}
	}

	function alertLength(){
		var ckLength = node.attr("ckLength");
		if(typeof(ckLength) != "undefined"){
			var value = node.val();
			if(value.length>ckLength){
				if(node.parent().find("label").length <= 0){
					node.parent().append("<label class='alert-label'>输入长度不能大于"+ckLength+"</label>");
				}
				node.parent().parent().parent().parent().find("input[name='isValid']").attr("valided","unValid");
			}
		}
	}

	function alertSize(){
		var ckSize = node.attr("ckSize");
		if(typeof(ckSize) != "undefined"){
			var value = node.val();
			if(parseInt(value)>parseInt(ckSize)){
				if(node.parent().find("label").length <= 0){
					node.parent().append("<label class='alert-label'>输入不能大于"+ckSize+"小于0</label>");
				}
				node.parent().parent().parent().parent().find("input[name='isValid']").attr("valided","unValid");
			}
		}
	}
}
