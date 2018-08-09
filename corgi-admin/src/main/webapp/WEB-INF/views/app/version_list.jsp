<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript">
    // 新增 弹出modal
    function showAddVersion() {
        $('#errorInfo').html("");
        $('#alert-error').css("display", "none");
        $('#alert-success').css("display", "none");
        $("#apiVersion").val("");
        $("#versionNumber").val("");
        $("#description").val("");
        $("#downloadUrl").val("");
        $("#operatingSystem").val("");
        $("#forceUpgrade").val("");
        // 模态对话框显示
        $('#addVersionModal').modal('show');
    }

    // 新增
    function addVersion() {
        var versionNumber = $("#versionNumber").val();
        var apiVersion = $("#apiVersion").val();
        var description = $("#description").val();
        var downloadUrl = $("#downloadUrl").val();
        var operatingSystem = $("#operatingSystem").val();
        var forceUpgrade = $("#forceUpgrade").val();
        // 字段校验
        if (versionNumber.length == 0 || $.trim(versionNumber) == "") {
            $('#errorInfo').html("APP版本号不能为空！");
            return;
        }
        if (apiVersion.length == 0 || $.trim(apiVersion) == "") {
            $('#errorInfo').html("Api版本号不能为空！");
            return;
        }
        if (description.length == 0 || $.trim(description) == "") {
            $('#errorInfo').html("版本说明不能为空！");
            return;
        }
        if (downloadUrl.length == 0 || $.trim(downloadUrl) == "") {
            $('#errorInfo').html("下载地址不能为空！");
            return;
        }
        var params = {
            versionNumber: versionNumber,
            apiVersion: apiVersion,
            description: description,
            downloadUrl: downloadUrl,
            operatingSystem: operatingSystem,
            forceUpgrade: forceUpgrade
        }
        $.ajax({
            url: "app/version/insert.do",
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
                        versionNumber: data.versionNumber,
                        apiVersion: data.apiVersion,
                        description: data.description,
                        downloadUrl: data.downloadUrl,
                        operatingSystem: data.operatingSystem,
                        forceUpgrade: data.forceUpgrade,
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
                $('#addVersionModal').modal('hide');
            },
            error: function (e) {
                location.href = location.href;
            }
        });
    }

    // 修改 弹出modal
    function showUpdateVersion(rowId) {
        $('#errorInfos').html("");
        $('#alert-error').css("display", "none");
        $('#alert-success').css("display", "none");
        var rowData = $('#grid-table').jqGrid('getRowData', rowId);
        $("#rowId").val(rowId);
        $("#Id").val(rowData.id);
        $("#upVersionNumber").val(rowData.versionNumber);
        $("#upApiVersion").val(rowData.apiVersion);
        $("#upDownloadUrl").val(rowData.downloadUrl);
        $("#upDescription").val(rowData.description);
        $("#upOperatingSystem").val(rowData.operatingSystem);
        var upForceUpgrade = rowData.forceUpgrade;
        switch (upForceUpgrade) {
            case "非强制":
                $("#upForceUpgrade").val("0");
                break;
            case "强制":
                $("#upForceUpgrade").val("1");
                break;
        }
        // 模态对话框显示
        $('#updateVersionModal').modal('show');
    }

    //保存更改
    function updateVersion() {
        var rowId = $("#rowId").val();
        var Id = $("#Id").val();
        var upVersionNumber = $("#upVersionNumber").val();
        var upApiVersion = $("#upApiVersion").val();
        var upDescription = $("#upDescription").val();
        var upDownloadUrl = $("#upDownloadUrl").val();
        var upOperatingSystem = $("#upOperatingSystem option:selected").val();
        var upForceUpgrade = $("#upForceUpgrade option:selected").val();
        // 字段校验
        if (upVersionNumber.length == 0 || $.trim(upVersionNumber) == "") {
            $('#errorInfos').html("APP版本号不能为空！");
            return;
        }
        if (upApiVersion.length == 0 || $.trim(upApiVersion) == "") {
            $('#errorInfos').html("Api版本号不能为空！");
            return;
        }
        if (upDescription.length == 0 || $.trim(upDescription) == "") {
            $('#errorInfos').html("版本说明不能为空！");
            return;
        }
        if (upDownloadUrl.length == 0 || $.trim(upDownloadUrl) == "") {
            $('#errorInfos').html("下载地址不能为空！");
            return;
        }
        var params = {
            Id: Id,
            upVersionNumber: upVersionNumber,
            upApiVersion: upApiVersion,
            upDescription: upDescription,
            upDownloadUrl: upDownloadUrl,
            upOperatingSystem: upOperatingSystem,
            upForceUpgrade: upForceUpgrade
        }
        $.ajax({
            url: "app/version/update.do",
            type: "POST",
            dataType: "json",
            data: params,
            success: function (result) {
                var bisStatus = result.bisStatus;
                if (bisStatus == '1000') {
                    var data = result.bisObj;
                    var curRowData = $("#grid-table").jqGrid('getRowData', rowId);
                    $.extend(curRowData, {
                        versionNumber: data.versionNumber,
                        apiVersion: data.apiVersion,
                        description: data.description,
                        downloadUrl: data.downloadUrl,
                        operatingSystem: data.operatingSystem,
                        forceUpgrade: data.forceUpgrade,
                        createTime: data.createTime,
                        updateTime: data.updateTime
                    })
                    // 更新列表本行数据
                    $("#grid-table").jqGrid('setRowData', rowId, curRowData);
                    // 修改成功
                    $('#alert-success').css("display", "block");
                } else {
                    $('#error_msg').html(result.bisMsg);
                    $('#alert-error').css("display", "block");
                }
                // 模态对话框隐藏
                $('#updateVersionModal').modal('hide');
            },
            error: function (e) {
//                console.log(123)
                location.href = location.href;
            }
        });
    }

    // 删除
    function delVersion(rowId) {
        $('#alert-error').css("display", "none");
        $('#alert-success').css("display", "none");
        var rowData = $('#grid-table').jqGrid('getRowData', rowId);
        var Id = rowData.id;
        bootbox.confirm("<h4 style='text-align: center;'>您确定要删除这条信息吗？</h4>", function (result) {
            if (result) {
                var params = {
                    Id: Id
                }
                $.ajax({
                    url: "app/version/delete.do",
                    type: "post",
                    dataType: "json",
                    data: params,
                    success: function (result) {
                        var bisStatus = result.bisStatus;
                        if (bisStatus == '1000') {
                            // 删除成功
                            $("#grid-table").jqGrid('delRowData', rowId);
                            $('#alert-success').css("display", "block");
                        } else {
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
                版本管理
            </li>
            <li>
                版本列表
            </li>
        </ul>
        <!-- /.breadcrumb -->
    </div>

    <div class="page-content">
        <!-- page-header -->
        <div class="page-header">
            <h1>版本列表&nbsp;&nbsp;&nbsp;&nbsp;<c:if test="${not empty sessionScope.mgrFuncMap.func_3_2_1_1}">
                <button class="btn btn-primary" onclick="showAddVersion();">上传版本</button>
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
                    <br/>
                </div>

                <!-- modal start 新增 -->
                <div class="modal fade" id="addVersionModal" tabindex="-1" role="dialog"
                     aria-labelledby="addVersionModalLabel" aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal"><span
                                        aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                                <h4 class="modal-title" id="addVersionModalLabel">新增App版本</h4>
                            </div>
                            <div class="modal-body">
                                <div id="errorInfo" style="color: red;"></div>
                                <table class="table table-bordered table-hover table-condensed table-style">
                                    <tbody>
                                    <tbody>
                                    <tr>
                                        <td style="vertical-align: middle;">App版本号</td>
                                        <td>
                                            <input type="text" id="versionNumber" style="width: 300px;"/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="vertical-align: middle;">Api版本号</td>
                                        <td>
                                            <input type="text" id="apiVersion" style="width: 300px;"/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="vertical-align: middle;">版本说明</td>
                                        <td>
                                            <textarea style="resize:none" id="description" rows="3" cols="46"></textarea>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="vertical-align: middle;">下载地址</td>
                                        <td>
                                            <input type="text" id="downloadUrl" style="width: 400px;"/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="vertical-align: middle;">操作系统</td>
                                        <td><select id="operatingSystem" name="operatingSystem">
                                            <option value="iOS">iOS</option>
                                            <option value="Android">Android</option>
                                        </select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="vertical-align: middle;">强制更新</td>
                                        <td>
                                            <select id="forceUpgrade" name="forceUpgrade">
                                                <option value="0">非强制</option>
                                                <option value="1">强制</option>
                                            </select>
                                        </td>
                                    </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-sm btn-default" data-dismiss="modal">取消</button>
                                <a class="btn btn-sm btn-primary" onclick="addVersion();">确定</a>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- modal end 新增 -->

                <!-- modal start 修改 -->
                <div class="modal fade" id="updateVersionModal" tabindex="-1" role="dialog"
                     aria-labelledby="updateVersionModalLabel" aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal"><span
                                        aria-hidden="true">&times;</span><span class="sr-only">Close</span>
                                </button>
                                <h4 class="modal-title" id="updateVersionModalLabel">修改App</h4>
                            </div>
                            <div class="modal-body">
                                <div id="errorInfos" style="color: red;"></div>
                                <table class="table table-bordered table-hover table-condensed table-style">
                                    <tbody>
                                    <tr>
                                        <td style="vertical-align: middle;">版本号</td>
                                        <td>
                                            <input type="text" id="Id" style="display:none"/>
                                            <input type="hidden" id="rowId"/>
                                            <input type="text" id="upVersionNumber" style="width: 300px;"/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="vertical-align: middle;">Api版本号</td>
                                        <td>
                                            <input type="text" id="upApiVersion" style="width: 300px;"/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="vertical-align: middle;">版本说明</td>
                                        <td>
                                            <textarea style="resize:none" id="upDescription" rows="3" cols="46"></textarea>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="vertical-align: middle;">下载地址</td>
                                        <td>
                                            <input type="text" id="upDownloadUrl" style="width: 400px;"/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="vertical-align: middle;">操作系统</td>
                                        <td><select id="upOperatingSystem" name="upOperatingSystem">
                                            <option value="iOS">iOS</option>
                                            <option value="Android">Android</option>
                                        </select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="vertical-align: middle;">强制更新</td>
                                        <td>
                                            <select id="upForceUpgrade" name="forceUpgrade">
                                                <option value="0">非强制</option>
                                                <option value="1">强制</option>
                                            </select>
                                        </td>
                                    </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-sm btn-default" data-dismiss="modal">取消</button>
                                <a class="btn btn-sm btn-primary" onclick="updateVersion();">确定</a>
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
    var grid_data = "";
    var colNames = ['ID', 'App版本号', 'Api版本号', '版本说明', '下载地址', '操作系统', '强制更新', '创建时间', '更新时间', '操作'];
    var colModel = [{
        name: 'id',
        index: 'id',
        hidden: true
    }, {
        name: 'versionNumber',
        index: 'versionNumber',
        width: 100,
        sortable: false,
        align: "center"
    }, {
        name: 'apiVersion',
        index: 'apiVersion',
        width: 100,
        sortable: false,
        align: "center"
    }, {
        name: 'description',
        index: 'description',
        width: 550,
        sortable: false,
        search: false,
        align: "center"
    }, {
        name: 'downloadUrl',
        index: 'downloadUrl',
        width: 400,
        sortable: false,
        search: false,
        align: "center"
    }, {
        name: 'operatingSystem',
        index: 'operatingSystem',
        width: 80,
        sortable: false,
        align: "center",
        stype: "select",
        searchoptions: {
            value: "iOS:iOS;Android:Android"
        }
    }, {
        name: 'forceUpgrade',
        index: 'forceUpgrade',
        width: 80,
        sortable: false,
        align: "center",
        stype: "select",
        searchoptions: {
            value: "0:非强制;1:强制"
        },
        formatter: function (cellvalue, options, rowObject) {
            if (cellvalue == '0') return "非强制";
            if (cellvalue == '1') return "强制";
        }
    }, {
        name: 'createTime',
        index: 'createTime',
        width: 140,
        align: "center",
        search: false,
        sortable: false
    }, {
        name: 'updateTime',
        index: 'updateTime',
        width: 140,
        align: "center",
        search: false,
        sortable: false
    }, {
        name: 'operate',
        index: 'operate',
        width: 120,
        sortable: false,
        search: false,
        align: "center",
        formatter: function (cellvalue, options, rowObject) {
            var btsHtml = "";
            if (typeof(mgrFuncMap["func_3_2_1_2"]) != "undefined") {
                btsHtml = btsHtml + "<button class='btn btn-minier btn-yellow' type='button' onclick='showUpdateVersion(" + options.rowId + ", 0);'>修改</button>";
            }
            if (typeof(mgrFuncMap["func_3_2_1_3"]) != "undefined") {
                btsHtml = btsHtml + "&nbsp;&nbsp;<button class='btn btn-minier btn-danger' type='button' onclick='delVersion(" + options.rowId + ", 1);'>删除</button>";
            }
            return btsHtml;
        }
    }];
    var caption = "版本列表";
    reloadJqGrid("app/version/list/json.do", "json", grid_data, colNames, colModel, false, caption, true);
</script>