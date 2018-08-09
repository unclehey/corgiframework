<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript">
    $('.form_datetime').datetimepicker({
        weekStart: 1,
        todayBtn: 1,
        autoclose: 1,
        todayHighlight: 1,
        startView: 2,
        forceParse: 0,
        minView: "month", //选择日期后，不会再跳转去选择时分秒
        startDate: new Date(),
        language: 'zh-CN',
        format: 'yyyy-mm-dd'
    });
    // 模态框调用前的操作
    $('#addRuleModal').on('show.bs.modal', function () {
        // 执行的操作
        $('#errorInfo').html("");
        $('#alert-error').css("display", "none");
        $('#alert-success').css("display", "none");
    })

    // 新增规则 弹出modal
    function showAddRule() {
        $('#errorInfo').html("");
        $('#alert-error').css("display", "none");
        $('#alert-success').css("display", "none");
        $("#rule_name").val("");
        $("#rule_type").val("");
        $("#rule_content").val("");
        $("#start_date").val("");
        $("#end_date").val("");
        // 模态对话框显示
        $('#addRuleModal').modal('show');
    }
    
    // 新增规则
    function addRule() {
        var rule_name = $("#rule_name").val();
        var rule_type = $("#rule_type").val();
        var rule_content = $("#rule_content").val();
        var start_date = $("#start_date").val();
        var end_date = $("#end_date").val();
        // 字段校验
        if (rule_name.length == 0 || $.trim(rule_name) == "" || rule_content.length == 0 || $.trim(rule_content) == "") {
            $('#errorInfo').html("规则名称或内容不能为空！");
            $('#rule_name').focus();
            return;
        }
        if(compareDate(start_date,end_date)){
            $('#errorInfo').html("开始日期不能大于结束日期！");
            return;
        }
        var params = {
            rule_name: rule_name,
            rule_type: rule_type,
            rule_content: rule_content,
            start_date: start_date,
            end_date: end_date
        }
        $.ajax({
            url: "app/rule/insert.do",
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
                        ruleName: data.ruleName,
                        ruleType: data.ruleType,
                        ruleContent: data.ruleContent,
                        status: data.status,
                        startDate: data.startDate,
                        endDate: data.endDate,
                        createTime: data.createTime,
                        updateTime: data.updateTime
                    };
                    // 添加数据到表格顶端
                    $("#grid-table").jqGrid("addRowData", rowId, dataRow, "first");
                    // 新增成功
                    $('#alert-success').css("display", "block");
                }else{
                    $('#error_msg').html(result.bisMsg);
                    $('#alert-error').css("display", "block");
                }
                // 模态对话框隐藏
                $('#addRuleModal').modal('hide');
            },
            error: function (e) {
                location.href = location.href;
            }
        });
    }

    // 删除规则
    function removeRule(rowId) {
        $('#alert-error').css("display", "none");
        $('#alert-success').css("display", "none");
        bootbox.confirm("<h4 style='text-align: center;'>您确定要删除这条信息吗？</h4>", function(result) {
            if(result) {
                var rowData = $('#grid-table').jqGrid('getRowData', rowId);
                var removeId = rowData.id;
                var param = {
                    removeId: removeId
                }
                $.ajax({
                    url: "app/rule/delete.do",
                    type: "post",
                    dataType: "json",
                    data: param,
                    success: function (result) {
                        var bisStatus = result.bisStatus;
                        if (bisStatus == '1000') {
                            // 删除成功
                            $("#grid-table").jqGrid('delRowData', rowId);
                        }else{
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

    // 更新Role弹出更新框
    function showUpdateRole(rowId) {
        $('#alert-error').css("display", "none");
        $('#alert-success').css("display", "none");
        var rowData = $('#grid-table').jqGrid('getRowData', rowId);
        var roleId = rowData.id;
        $("#hiddenRowId").val(roleId);
        $("#upd_rule_name").val(rowData.ruleName);
        $("#upd_rule_type").val(rowData.ruleType);
        $("#upd_rule_content").val(rowData.ruleContent);
        var startDate = rowData.startDate;
        var endDate = rowData.endDate;
        $("#upd_start_date").val(startDate);
        $("#upd_end_date").val(endDate);
        var ruleType = rowData.ruleType;
        switch (ruleType) {
            case "邀请企业":
                $("#upd_rule_type").val(0);
                break;
            case "绑定银行卡":
                $("#upd_rule_type").val(1);
                break;
        }
        var status = rowData.status;
        switch (status) {
            case "有效":
                $("#upd_rule_status").val(0);
                break;
            case "作废":
                $("#upd_rule_status").val(1);
                break;
        }
        $("#updateRuleModal").modal("show");
    }

    // 保存更新的信息
    function updateRule() {
        var updId = $("#hiddenRowId").val();
        var ruleName = $("#upd_rule_name").val();
        var ruleType = $("#upd_rule_type").val();
        var ruleContent = $("#upd_rule_content").val();
        var status = $("#upd_rule_status").val();
        var startDate = $("#upd_start_date").val().replace(/\s+/g,"");
        var endDate = $("#upd_end_date").val().replace(/\s+/g,"");
        if (ruleName.length == 0 || $.trim(ruleName) == "" || ruleContent.length == 0 || $.trim(ruleContent) == "") {
            $('#upd_errorInfo').html("规则名称或内容不能为空！");
            $('#ruleName').focus();
            return;
        }
        if(compareDate(startDate,endDate)){
            $('#errorInfo1').html("开始日期不能大于结束日期！");
            return;
        }
        var params = {
            updId: updId,
            ruleName: ruleName,
            ruleType: ruleType,
            ruleContent: ruleContent,
            status:status,
            startDate: startDate,
            endDate: endDate
        }
        $.ajax({
            url: "app/rule/update.do",
            type: "post",
            dataType: "json",
            data: params,
            success: function (result) {
                var bisStatus = result.bisStatus;
                if (bisStatus == '1000') {
                    var data = result.bisObj;
                    var updRowData = $("#grid-table").jqGrid("getRowData", updId);
                    var startDate = data.startDate;
                    var endDate = data.endDate;
                    // 日期截串处理
                    if (startDate != null && endDate != null) {
                        startDate = startDate.substring(0, 10);
                        endDate = endDate.substring(0, 10);
                    }
                    $.extend(updRowData, {
                        updId: data.updId,
                        ruleName: data.ruleName,
                        ruleType: data.ruleType,
                        ruleContent: data.ruleContent,
                        status:data.status,
                        startDate: startDate,
                        endDate: endDate,
                        status: data.status,
                        updateTime:data.updateTime
                    })
                    // 刷新该条数据
                    $("#grid-table").jqGrid("setRowData", updId, updRowData);
                    // 更新成功
                    $('#alert-success').css("display", "block");
                }else{
                    $('#error_msg').html(result.bisMsg);
                    $('#alert-error').css("display", "block");
                }
                // 模态对话框隐藏
                $("#updateRuleModal").modal("hide");
            },
            error: function (e) {
                location.href = location.href;
            }
        });
    }
    // 比较日期
    function  compareDate(startDate,endDate) {
        var newStartDate = new Date(startDate);
        var newEndDate = new Date(endDate);
        if (newStartDate.getTime() > newEndDate.getTime()){
            return true;
        }else{
            return false;
        }
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
                规则管理
            </li>
            <li>
                规则列表
            </li>
        </ul>
        <!-- /.breadcrumb -->
    </div>

    <div class="page-content">
        <!-- page-header -->
        <div class="page-header">
            <h1>规则列表&nbsp;&nbsp;&nbsp;&nbsp;
                <c:if test="${not empty sessionScope.mgrFuncMap.func_3_3_1_1}">
                    <button class="btn btn-primary" onclick="showAddRule();">新增规则</button>
                </c:if>
            </h1>
        </div>
        <!-- /.page-header -->

        <div class="row">
            <div class="col-xs-12">

                <!-- modal start 新增规则 -->
                <div class="modal fade" id="addRuleModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
                     aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal"><span
                                        aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                                <h4 class="modal-title" id="myModalLabel">新增规则</h4>
                            </div>
                            <div class="modal-body">
                                <div id="errorInfo" style="color: red;"></div>
                                <table class="table table-bordered table-hover table-condensed table-style">
                                    <tbody>
                                    <tr>
                                        <td style="vertical-align: middle;">规则名称</td>
                                        <td>
                                            <input type="text" id="rule_name"/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="vertical-align: middle;">规则类型</td>
                                        <td>
                                            <select id="rule_type" name="rule_type">
                                                <option value="0">邀请企业</option>
                                                <option value="1">绑定银行卡</option>
                                            </select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="vertical-align: middle;">规则内容</td>
                                        <td>
                                            <textarea style="resize:none" id="rule_content" rows="10"
                                                      cols="46"></textarea>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="vertical-align: middle;">规则开始日期</td>
                                        <td>
                                            <input class="form-control date form_datetime" type="text" id="start_date"
                                                   readonly="readonly"/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="vertical-align: middle;">规则结束日期</td>
                                        <td>
                                            <input class="form-control date form_datetime" type="text" id="end_date"
                                                   readonly="readonly"/>
                                        </td>
                                    </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-sm btn-default" data-dismiss="modal">取消</button>
                                <a class="btn btn-sm btn-primary" onclick="addRule();">确定</a>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- modal end 新增规则 -->

                <!--model start 修改规则-->
                <div class="modal fade" id="updateRuleModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
                     aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal"><span
                                        aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                                <h4 class="modal-title" id="myModalLabel1">修改规则</h4>
                            </div>
                            <div class="modal-body">
                                <div id="errorInfo1" style="color: red;"></div>
                                <table class="table table-bordered table-hover table-condensed table-style">
                                    <tbody>
                                    <tr>
                                        <td style="vertical-align: middle;">规则名称</td>
                                        <td>
                                            <input type="text" id="upd_rule_name"/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="vertical-align: middle;">规则类型</td>
                                        <td>
                                            <select id="upd_rule_type">
                                                <option value="0">邀请企业</option>
                                                <option value="1">绑定银行卡</option>
                                            </select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="vertical-align: middle;">规则内容</td>
                                        <td>
                                            <textarea style="resize:none" id="upd_rule_content" rows="10"
                                                      cols="46"></textarea>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="vertical-align: middle;">规则状态</td>
                                        <td>
                                            <select id="upd_rule_status">
                                                <option value="0">有效</option>
                                                <option value="1">作废</option>
                                            </select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="vertical-align: middle;">规则开始日期</td>
                                        <td>
                                            <input class="form-control date form_datetime" type="text" id="upd_start_date"
                                                   readonly="readonly"/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="vertical-align: middle;">规则结束日期</td>
                                        <td>
                                            <input class="form-control date form_datetime" type="text" id="upd_end_date"
                                                   readonly="readonly"/>
                                        </td>
                                    </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-sm btn-default" data-dismiss="modal">取消</button>
                                <a class="btn btn-sm btn-primary" onclick="updateRule();">确定</a>
                                <input type="hidden" id="hiddenRowId">
                            </div>
                        </div>
                    </div>
                </div>
                <!--model end 修改规则-->
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
    var colNames =  ['ID', '规则名称', '规则类型', '规则内容', '规则状态', '规则开始日期', '规则结束日期', '创建时间', '更新时间', '操作'];
    var colModel =  [{
        name: 'id',
        index: 'id',
        hidden: true
    }, {
        name: 'ruleName',
        index: 'ruleName',
        width: 150,
        sortable: false,
        align: "center"
    }, {
        name: 'ruleType',
        index: 'ruleType',
        width: 100,
        sortable: false,
        align: "center",
        stype: "select",
        searchoptions: {
            value: "0:邀请企业;1:绑定银行卡"
        },
        formatter: function (cellvalue, options, rowObject) {
            if (cellvalue == '0') return "邀请企业";
            if (cellvalue == '1') return "绑定银行卡";
        }
    }, {
        name: 'ruleContent',
        index: 'ruleContent',
        width: 680,
        sortable: false,
        align: "center",
        search: false,
    }, {
        name: 'status',
        index: 'status',
        width: 80,
        sortable: false,
        align: "center",
        stype: "select",
        searchoptions: {
            value: "0:有效;1:作废"
        },
        formatter: function (cellvalue, options, rowObject) {
            if (cellvalue == '0') return "<span class='label label-sm label-success arrowed'>有效</span>";
            if (cellvalue == '1') return "<span class='label label-sm label-grey arrowed'>作废</span>";
        },
        unformat : function (cellvalue, options, rowObject) {
            return cellvalue;
        }
    }, {
        name: 'startDate',
        index: 'startDate',
        width: 100,
        sortable: false,
        align: "center",
        search: false,
        formatter: "date",
        formatoptions: {
            srcformat: 'Y-m-d H:i:s',
            newformat: 'Y-m-d'
        }
    }, {
        name: 'endDate',
        index: 'endDate',
        width: 100,
        align: "center",
        sortable: false,
        align: "center",
        search: false,
        formatter: "date",
        formatoptions: {
            srcformat: 'Y-m-d H:i:s',
            newformat: 'Y-m-d'
        }
    }, {
        name: 'createTime',
        index: 'createTime',
        width: 140,
        sortable: false,
        align: "center",
        search: false,
    }, {
        name: 'updateTime',
        index: 'updateTime',
        width: 140,
        sortable: false,
        align: "center",
        search: false,
    }, {
        name: 'operate',
        index: 'operate',
        width: 120,
        sortable: false,
        search: false,
        align: "center",
        search: false,
        formatter: function (cellvalue, options, rowObject) {
            var btsHtml = "";
            //修改按钮
            if (typeof(mgrFuncMap["func_3_3_1_2"]) != "undefined") {
                btsHtml = btsHtml + "<button class='btn btn-minier btn-warning' type='button' onclick='showUpdateRole(" + options.rowId + ");'>修改</button>";
            }
            //删除按钮
            if (typeof(mgrFuncMap["func_3_3_1_3"]) != "undefined") {
                btsHtml = btsHtml + "&nbsp;&nbsp;<button class='btn btn-minier btn-danger' type='button' onclick='removeRule(" + options.rowId + ");'>删除</button>";
            }
            return btsHtml;
        }
    }];
    var caption = "规则列表";
    reloadJqGrid("app/rule/list/json.do", "json", grid_data, colNames, colModel, false, caption, true, false);
</script>