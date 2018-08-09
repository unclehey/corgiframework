<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript">
    // 新增 弹出modal
    function showAddBanner() {
        $('#errorInfo').html("");
        $('#alert-error').css("display", "none");
        $('#alert-success').css("display", "none");
        $("#pic_title").val("");
        $("#pic_url").val("");
        $("#link_url").val("");
        // 模态对话框显示
        $('#addBannerModal').modal('show');
    }

    // 新增
    function addBanner() {
        var pic_type = $("#pic_type").val();
        var pic_title = $("#pic_title").val();
        var pic_url = $("#pic_url").val();
        var link_url = $("#link_url").val();
        var picWeight = $("#picWeight").val();
        // 字段校验
        if (pic_url.length == 0 || $.trim(pic_url) == "") {
            $('#errorInfo').html("请上传图片！");
            return;
        }
        if (pic_title.length == 0 || $.trim(pic_title) == "") {
            $('#errorInfo').html("标题不能为空！");
            $('#pic_title').focus();
            return;
        }
        var params = {
            pic_type: pic_type,
            pic_title: pic_title,
            pic_url: pic_url,
            link_url: link_url,
            picWeight: picWeight
        }
        $.ajax({
            url: "app/banner/insert.do",
            type: "post",
            dataType: "json",
            data: params,
            success: function (result) {
                var bisStatus = result.bisStatus;
                if (bisStatus == '1000') {
                    var data = result.bisObj;
                    var dataIds = $("#grid-table").jqGrid("getDataIDs");
                    var rowId = dataIds.length + 1;
                    var dataRow = {
                        id: data.id,
                        picType: data.picType,
                        picTitle: data.picTitle,
                        picUrl: data.picUrl,
                        status: data.status,
                        linkUrl: data.linkUrl,
                        picWeight: data.picWeight,
                        createTime: data.createTime,
                        updateTime: data.updateTime
                    };
                    // 添加数据到表格顶端
                    $("#grid-table").jqGrid("addRowData", rowId, dataRow, "first");
                    // 新增成功
                    $('#alert-success').css("display", "block");
                } else {
                    $('#error_msg').html(result.bisMsg);
                    $('#alert-error').css("display", "block");
                }
                // 模态对话框隐藏
                $('#addBannerModal').modal('hide');
            },
            error: function (e) {
                location.href = location.href;
            }
        });
    }

    // 上传图片
    function uploadPic() {
        var addAttr = $("input[name='addAttr']").val();
        if (addAttr.length == 0 || $.trim(addAttr) == "") {
            $('#errorInfo').html("请选择上传图片！");
            return;
        }
        $.ajaxFileUpload({
            url: 'app/banner/upload.do', // 用于文件上传的服务器端请求地址
            secureuri: false, // 是否需要安全协议，一般设置为false
            data: null,
            type: 'post',
            fileElementId: 'addAttr', // 文件上传域的ID
            dataType: 'json',
            success: function (data) {
                if (data.result == '1') {
                    $("#pic_url").val(data.url);
                    $("#effectiveDiv").html("图片上传成功");
                } else {
                    $("#effectiveDiv").html("图片上传失败");
                }
            },
            error: function (data, status, e) {
                location.href = location.href;
            }
        });
    }

    // 修改 弹出modal
    function showUpdateBanner(rowId) {
        $('#errorInfos').html("");
        $('#alert-error').css("display", "none");
        $('#alert-success').css("display", "none");
        var rowData = $('#grid-table').jqGrid('getRowData', rowId);
        $("#rowId").val(rowId);
        $("#updateId").val(rowData.id);
        var picType = rowData.picType;
        switch (picType) {
            case "首页":
                $("#upd_pic_type").val(0);
                break;
            case "邀请好友":
                $("#upd_pic_type").val(1);
                break;
        }
        $("#upd_link_url").val(rowData.linkUrl);
        $("#upd_pic_title").val(rowData.picTitle);
        var status = rowData.status;
        switch (status) {
            case "有效":
                $("#upd_status").val(0);
                break;
            case "下架":
                $("#upd_status").val(1);
                break;
        }
        $("#updPicWeight").val(rowData.picWeight);
        // 模态对话框显示
        $('#updateBannerModal').modal('show');
    }

    //保存更改
    function updateBanner() {
        var rowId = $("#rowId").val();
        var updateId = $("#updateId").val();
        var updPicType = $("#upd_pic_type").val();
        var updPicTitle = $("#upd_pic_title").val();
        var updPicUrl = $("#upd_pic_url").val();
        var updLinkUrl = $("#upd_link_url").val();
        var updStatus = $("#upd_status").val();
        var updPicWeight = $("#updPicWeight").val();
        if (updPicTitle.length == 0 || $.trim(updPicTitle) == "" ) {
            $('#errorInfos').html("标题不能为空！");
            $('#upd_pic_title').focus();
            return;
        }
        var params = {
            updateId: updateId,
            updPicType: updPicType,
            updPicTitle: updPicTitle,
            updPicUrl: updPicUrl,
            updLinkUrl: updLinkUrl,
            updStatus: updStatus,
            updPicWeight: updPicWeight
        }
        $.ajax({
            url: "app/banner/update.do",
            type: "post",
            dataType: "json",
            data: params,
            success: function (result) {
                var bisStatus = result.bisStatus;
                if (bisStatus == '1000') {
                    var data = result.bisObj;
                    var curRowData = $("#grid-table").jqGrid('getRowData', rowId);
                    $.extend(curRowData, {
                        picType: data.picType,
                        picTitle: data.picTitle,
                        picUrl: data.picUrl,
                        linkUrl: data.linkUrl,
                        status: data.status,
                        picWeight: data.picWeight,
                        updateTime: data.updateTime,
                        createTime: data.createTime
                    })
                    // 更新列表本行数据
                    $("#grid-table").jqGrid('setRowData', rowId, curRowData);
                    // 修改成功
                    $('#alert-success').css("display", "block");
                }  else {
                    $('#error_msg').html(result.bisMsg);
                    $('#alert-error').css("display", "block");
                }
                // 模态对话框隐藏
                $('#updateBannerModal').modal('hide');
            },
            error: function (e) {
                location.href = location.href;
            }
        });
    }

    // 更新上传图片
    function uploadPicUpd() {
        var addAttr = $("input[name='updAddAttr']").val();
        if (addAttr.length == 0 || $.trim(addAttr) == "") {
            $('#errorInfos').html("请选择上传图片！");
            return;
        }
        $.ajaxFileUpload({
            url: 'app/banner/upload/update.do', // 用于文件上传的服务器端请求地址
            secureuri: false, // 是否需要安全协议，一般设置为false
            data: null,
            type: 'post',
            fileElementId: 'updAddAttr', // 文件上传域的ID
            dataType: 'json',
            success: function (data) {
                if (data.result == '1') {
                    $("#upd_pic_url").val(data.url);
                    $("#effectiveDiv2").html("图片上传成功");
                } else {
                    $("#effectiveDiv2").html("图片上传失败");
                }
            },
            error: function (data, status, e) {
                alert(e);
            }
        });
    }

    // 删除
    function delBanner(rowId) {
        $('#alert-error').css("display", "none");
        $('#alert-success').css("display", "none");
        var rowData = $('#grid-table').jqGrid('getRowData', rowId);
        var delId = rowData.id;
        bootbox.confirm("<h4 style='text-align: center;'>您确定要删除这条信息吗？</h4>", function (result) {
            if (result) {
                var params = {
                    delId: delId
                }
                $.ajax({
                    url: "app/banner/delete.do",
                    type: "post",
                    dataType: "json",
                    data: params,
                    success: function (result) {
                        var bisStatus = result.bisStatus;
                        if (bisStatus == '1000') {
                            // 删除成功
                            $("#grid-table").jqGrid('delRowData', rowId);
                            $('#alert-success').css("display", "block");
                        }  else {
                            $('#error_msg').html(result.bisMsg);
                            $('#alert-error').css("display", "block");
                        }
                    },
                    error: function (e) {
                        location.href = location.href;
                    }
                });
            }
        });
    }
</script>
<div class="main-content-inner">
    <div class="breadcrumbs" id="breadcrumbs">
        <script type="text/javascript">
            try {
                ace.settings.check('breadcrumbs', 'fixed')
            } catch (e) {
            }
        </script>
        <!-- breadcrumb -->
        <ul class="breadcrumb">
            <li>
                <i class="ace-icon fa fa-home home-icon"></i>
                ${mgr_title}
            </li>
            <li>
                APP管理
            </li>
            <li>
                图片管理
            </li>
            <li>
                图片列表
            </li>
        </ul>
        <!-- /.breadcrumb -->
    </div>

    <div class="page-content">
        <!-- page-header -->
        <div class="page-header">
            <h1>图片列表&nbsp;&nbsp;&nbsp;&nbsp;<c:if test="${not empty sessionScope.mgrFuncMap.func_3_1_1_1}">
                <button class="btn btn-primary" onclick="showAddBanner();">上传图片</button>
            </c:if></h1>
        </div>
        <!-- /.page-header -->

        <div class="row">
            <div class="col-xs-12">

                <!-- alert-success -->
                <div id="alert-success" class="alert alert-success" style="display: none;">
                    <button type="button" class="close" data-dismiss="alert">
                        <i class="ace-icon fa fa-times"></i>
                    </button>
                    <strong>
                        <i class="ace-icon fa fa-check"></i>
                        操作成功！
                    </strong>
                </div>

                <!-- alert-error -->
                <div id="alert-error" class="alert alert-danger" style="display: none;">
                    <button type="button" class="close" data-dismiss="alert">
                        <i class="ace-icon fa fa-times"></i>
                    </button>
                    <strong>
                        <i class="ace-icon fa fa-times"></i>
                        <span id="error_msg"></span>
                    </strong>
                    <br />
                </div>

                <!-- modal start 新增 -->
                <div class="modal fade" id="addBannerModal" tabindex="-1" role="dialog" aria-labelledby="addBannerModalLabel" aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                                <h4 class="modal-title" id="addBannerModalLabel">新增App图片</h4>
                            </div>
                            <div class="modal-body">
                                <div id="errorInfo" style="color: red;"></div>
                                <table class="table table-bordered table-hover table-condensed table-style">
                                    <tbody>
                                        <tr>
                                            <td style="vertical-align: middle;">图片类型</td>
                                            <td>
                                                <select id="pic_type" name="pic_type">
                                                    <option value="0">首页</option>
                                                    <option value="1">邀请好友</option>
                                                </select>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td style="vertical-align: middle;">标题</td>
                                            <td>
                                                <input type="text" id="pic_title" style="width: 300px;"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td style="vertical-align: middle;">请选择上传图片</td>
                                            <td>
                                                <div class="row">
                                                    <div class="col-xs-9">
                                                        <input type="hidden" id="pic_url"/>
                                                        <input id="addAttr" name="addAttr" type="file" onchange=""/>
                                                    </div>
                                                    <div class="col-xs-3">
                                                        <button class="btn btn-minier btn-primary" onclick="uploadPic();">
                                                            上传图片
                                                        </button>
                                                    </div>
                                                </div>
                                                <div class="row">
                                                    <div id="effectiveDiv" style="color: red;"></div>
                                                </div>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td style="vertical-align: middle;">跳转链接</td>
                                            <td>
                                                <input type="text" id="link_url" style="width: 400px;"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td style="vertical-align: middle;">图片权重</td>
                                            <td>
                                                <select id="picWeight" name="picWeight">
                                                    <option value="1">1</option>
                                                    <option value="2">2</option>
                                                    <option value="3">3</option>
                                                    <option value="4">4</option>
                                                    <option value="5">5</option>
                                                </select>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-sm btn-default" data-dismiss="modal">取消</button>
                                <a class="btn btn-sm btn-primary" onclick="addBanner();">确定</a>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- modal end 新增 -->

                <!-- modal start 修改 -->
                <div class="modal fade" id="updateBannerModal" tabindex="-1" role="dialog" aria-labelledby="updateBannerModalLabel" aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
                                </button>
                                <h4 class="modal-title" id="updateBannerModalLabel">修改App图片</h4>
                            </div>
                            <div class="modal-body">
                                <div id="errorInfos" style="color: red;"></div>
                                <table class="table table-bordered table-hover table-condensed table-style">
                                    <tbody>
                                        <tr>
                                            <td style="vertical-align: middle;">图片类型</td>
                                            <td>
                                                <select id="upd_pic_type">
                                                    <option value="0">首页</option>
                                                    <option value="1">邀请好友</option>
                                                </select>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td style="vertical-align: middle;">标题</td>
                                            <td>
                                                <input type="text" id="upd_pic_title" style="width: 300px;"/>
                                                <input type="hidden" id="updateId" />
                                                <input type="hidden" id="rowId" />
                                            </td>
                                        </tr>
                                        <tr>
                                            <td style="vertical-align: middle;">请选择上传图片</td>
                                            <td>
                                                <div class="row">
                                                    <div class="col-xs-9">
                                                        <input type="hidden" id="upd_pic_url"/>
                                                        <input id="updAddAttr" name="updAddAttr" type="file" onchange=""/>
                                                    </div>
                                                    <div class="col-xs-3">
                                                        <button class="btn btn-minier btn-primary" onclick="uploadPicUpd();">
                                                            上传图片
                                                        </button>
                                                    </div>
                                                </div>
                                                <div class="row">
                                                    <div id="effectiveDiv2" style="color: red;"></div>
                                                </div>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td style="vertical-align: middle;">跳转链接</td>
                                            <td>
                                                <input type="text" id="upd_link_url" style="width: 400px;"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td style="vertical-align: middle;">图片权重</td>
                                            <td>
                                                <select id="updPicWeight">
                                                    <option value="1">1</option>
                                                    <option value="2">2</option>
                                                    <option value="3">3</option>
                                                    <option value="4">4</option>
                                                    <option value="5">5</option>
                                                </select>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td style="vertical-align: middle;">状态</td>
                                            <td>
                                                <select id="upd_status">
                                                    <option value="0">有效</option>
                                                    <option value="1">下架</option>
                                                </select>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-sm btn-default" data-dismiss="modal">取消</button>
                                <a class="btn btn-sm btn-primary" onclick="updateBanner();">确定</a>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- modal end 修改 -->

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
    var colNames = ['ID', '标题', '图片类型', '状态', '图片', '跳转链接', '权重', '创建时间', '更新时间', '操作'];
    var colModel = [{
        name: 'id',
        index: 'id',
        hidden: true
    }, {
        name: 'picTitle',
        index: 'picTitle',
        width: 200,
        sortable: false,
        align: "center"
    }, {
        name: 'picType',
        index: 'picType',
        width: 120,
        sortable: false,
        align: "center",
        stype: "select",
        searchoptions: {
            value: "0:首页;1:邀请好友"
        },
        formatter: function (cellvalue, options, rowObject) {
            if (cellvalue == '0') return "首页";
            if (cellvalue == '1') return "邀请好友";
        }
    }, {
        name: 'status',
        index: 'status',
        width: 100,
        sortable: false,
        align: "center",
        stype: "select",
        searchoptions: {
            value: "0:有效;1:下架"
        },
        formatter: function (cellvalue, options, rowObject) {
            if (cellvalue == '0') return "<span class='label label-sm label-success arrowed'>有效</span>";
            if (cellvalue == '1') return "<span class='label label-sm label-grey arrowed'>下架</span>";
        },
        unformat : function (cellvalue, options, rowObject) {
            return cellvalue;
        }
    }, {
        name: 'picUrl',
        index: 'picUrl',
        width: 200,
        sortable: false,
        align: "center",
        search: false,
        formatter: function (cellvalue, options, rowObject) {
            return "<img class='mgr-img' src='"+cellvalue+"' />";
        }
    }, {
        name: 'linkUrl',
        index: 'linkUrl',
        width: 350,
        sortable: false,
        align: "center",
        search: false
    }, {
        name: 'picWeight',
        index: 'picWeight',
        width: 100,
        sortable: false,
        align: "center",
        search: false
    }, {
        name: 'createTime',
        index: 'createTime',
        width: 150,
        sortable: false,
        align: "center",
        search: false
    }, {
        name: 'updateTime',
        index: 'updateTime',
        width: 150,
        sortable: false,
        align: "center",
        search: false
    }, {
        name: 'operate',
        index: 'operate',
        width: 100,
        sortable: false,
        align: "center",
        search: false,
        formatter: function (cellvalue, options, rowObject) {
            var btsHtml = "";
            if(typeof(mgrFuncMap["func_3_1_1_2"]) != "undefined"){
                btsHtml = btsHtml + "<button class='btn btn-minier btn-warning' type='button' onclick='showUpdateBanner("+ options.rowId + ");'>修改</button>";
            }
            if(typeof(mgrFuncMap["func_3_1_1_3"]) != "undefined"){
                btsHtml = btsHtml + "&nbsp;&nbsp;<button class='btn btn-minier btn-danger' type='button' onclick='delBanner("+ options.rowId + ");'>删除</button>";
            }
            return btsHtml;
        }
    }];
    var caption = "图片列表";
    reloadJqGrid("app/banner/list/json.do", "json", grid_data, colNames, colModel, false, caption, true, false);
</script>