<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript">
    // 新增 弹出modal
    function showAdd() {
        // 模态对话框点击外部不关闭
        $('#addModal').modal({backdrop: 'static', keyboard: false});
        // 执行的操作
        $('#errorInfo').html("");
        $('#alert-error').css("display", "none");
        $('#alert-success').css("display", "none");
        $("#title").val("");
        $("#price").val("");
        $("#duration").val("");
        $("#type").val("");
        // 模态对话框显示
        $('#addModal').modal('show');
    }

    // 新增
    function addObj() {
        var title = $("#title").val();
        var price = $("#price").val();
        var duration = $("#duration").val();
        var type = $("#type").val();
        // 字段校验
        if (title.length == 0 || $.trim(title) == "") {
            $('#errorInfo').html("标题不能为空！");
            return;
        }
        if (price.length == 0 || $.trim(price) == "") {
            $('#errorInfo').html("价格不能为空！");
            return;
        }
        if (duration.length == 0 || $.trim(duration) == "") {
            $('#errorInfo').html("时长不能为空！");
            return;
        }
        var params = {
       		title: title,
       		price: price,
       		duration: duration,
       		type: type
        }
        $.ajax({
            url: "sys/price/add.do",
            type: "post",
            dataType: "json",
            data: params,
            success: function (result) {
                var bisStatus = result.bisStatus;
                if (bisStatus == '1000') {
                    var data = result.bisObj;
                    var dataIds = $("#grid-table").jqGrid("getRowData");
                    var rowId = dataIds.length + 1;
                    var dataRow = {
                        id: data.id,
                        title: data.title,
                        price: data.price,
                        duration: data.duration,
                        type: data.type,
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
                $('#addModal').modal('hide');
            },
            error: function (e) {
                location.href = location.href;
            }
        });
    }
    
    // 修改 弹出modal
    function showUpdate(rowId) {
        $('#errorInfos').html("");
        $('#alert-error').css("display", "none");
        $('#alert-success').css("display", "none");
        var rowData = $('#grid-table').jqGrid('getRowData', rowId);
        $("#updtitle").val(rowData.title);
        $("#updprice").val(rowData.price);
        $("#upid").val(rowData.id);
        var type = rowData.type;
        $("#updduration").val(rowData.duration);
        switch (type) {
            case "个人会员":
                $("#type").val(1);
                break;
        }
        // 模态对话框显示
        $('#updModal').modal('show');
    }
  //保存更改
    function update() {
        var id = $("#upid").val();
        var title = $("#updtitle").val();
        var price = $("#updprice").val();
        var duration = $("#updduration").val();
        var type = $("#updatetype").val();
        // 字段校验
        if (title.length == 0 || $.trim(title) == "") {
            $('#errorInfos').html("标题不能为空！");
            return;
        }
        if (price.length == 0 || $.trim(price) == "") {
            $('#errorInfos').html("价格不能为空！");
            return;
        }
        if (duration.length == 0 || $.trim(duration) == "") {
            $('#errorInfos').html("时长不能为空！");
            return;
        }
        var params = {
        		id: id,
        		title: title,
        		price:price,
        		duration:duration,
        		type:type
        }
        $.ajax({
            url: "sys/price/update.do",
            type: "post",
            dataType: "json",
            data: params,
            success: function (result) {
            	var bisStatus = result.bisStatus;
                if (bisStatus == '1000') {
                    var data = result.bisObj;
                    var curRowData = $("#grid-table").jqGrid('getRowData', id);
                    $.extend(curRowData, {
                		title: data.title,
                		price:data.price,
                		duration:data.duration,
                		type:data.type,
                		updateTime:data.updateTime
                    })
                    // 更新列表本行数据
                    $("#grid-table").jqGrid('setRowData', id, curRowData);
                    // 修改成功
                    $('#alert-success').css("display", "block");
                }  else {
                    $('#error_msg').html(result.bisMsg);
                    $('#alert-error').css("display", "block");
                }
                // 模态对话框隐藏
                $('#updModal').modal('hide');
            },
            error: function (e) {
                location.href = location.href;
            }
        });
    }
    
    // 删除
    function del(rowId) {
        $('#alert-error').css("display", "none");
        $('#alert-success').css("display", "none");
        var rowData = $('#grid-table').jqGrid('getRowData', rowId);
        var delId = rowData.id;
        bootbox.confirm("<h4 style='text-align: center;'>您确定要删除这条信息吗？</h4>", function (result) {
            if (result) {
                var params = {
                		priceId: delId
                }
                $.ajax({
                    url: "sys/price/del.do",
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
                系统管理
            </li>
            <li>
                价格管理
            </li>
            <li>
                价格列表
            </li>
        </ul>
        <!-- /.breadcrumb -->
    </div>

    <div class="page-content">
        <!-- page-header -->
        <div class="page-header">
            <h1>价格列表&nbsp;&nbsp;&nbsp;&nbsp;
             <c:if test="${not empty sessionScope.mgrFuncMap.func_0_3_1_1}">
            <button class="btn btn-primary" onclick="showAdd();">新增价格</button>
            </c:if>
            </h1>
        </div>
        <!-- /.page-header -->

        <div class="row">
            <div class="col-xs-12">

                <!-- modal start 新增 -->
                <div class="modal fade" id="addModal" tabindex="-1" role="dialog" aria-labelledby="addModalLabel" aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                                <h4 class="modal-title" id="addModalLabel">新增价格</h4>
                            </div>
                            <div class="modal-body">
                                <div id="errorInfo" style="color: red;"></div>
                                <table class="table table-bordered table-hover table-condensed table-style">
                                    <tbody>
                                        <tr>
                                            <td style="vertical-align: middle;">标题</td>
                                            <td>
                                                <input type="text" id="title" style="width: 400px;" maxlength="50" />
                                            </td>
                                        </tr>
                                        <tr>
                                            <td style="vertical-align: middle;">价格</td>
                                            <td>
                                                <input type="text" id="price" onkeyup="value=(value.match(/\d+(\.\d{0,2})?/)||[''])[0]" />
                                            </td>
                                        </tr>
                                        <tr>
                                            <td style="vertical-align: middle;">时长（月）</td>
                                            <td>
                                                <input type="text" id="duration" onkeyup="this.value=this.value.replace(/\D/g,'')" />
                                            </td>
                                        </tr>
                                        <tr>
                                            <td style="vertical-align: middle;">类型</td>
                                            <td>
                                                <select id="type">
                                                    <option value="1">个人会员</option>
                                                    <option value="2">企业会员</option>
                                                </select>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-sm btn-default" data-dismiss="modal">取消</button>
                                <a class="btn btn-sm btn-primary" onclick="addObj();">确定</a>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- modal end 新增 -->
                <!-- modal start 修改 -->
                <div class="modal fade" id="updModal" tabindex="-1" role="dialog" aria-labelledby="updModalLabel" aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                                <h4 class="modal-title" id="updModalLabel">修改价格</h4>
                            </div>
                            <div class="modal-body">
                                <div id="errorInfos" style="color: red;"></div>
                                <table class="table table-bordered table-hover table-condensed table-style">
                                    <tbody>
                                    <tr>
                                        <td style="vertical-align: middle;">标题</td>
                                        <td>
                                            <input type="text" id="updtitle" style="width: 400px;" maxlength="50" />
                                            <input type="hidden" id="upid"/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="vertical-align: middle;">价格</td>
                                        <td>
                                            <input type="text" id="updprice" onkeyup="value=(value.match(/\d+(\.\d{0,2})?/)||[''])[0]" />
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="vertical-align: middle;">时长（月）</td>
                                        <td>
                                            <input type="text" id="updduration" onkeyup="this.value=this.value.replace(/\D/g,'')" />
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="vertical-align: middle;">类型</td>
                                        <td>
                                            <select id="updatetype">
                                                <option value="1">个人会员</option>
                                                <option value="2">企业会员</option>
                                            </select>
                                        </td>
                                    </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-sm btn-default" data-dismiss="modal">取消</button>
                                <a class="btn btn-sm btn-primary" onclick="update();">确定</a>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- modal end 修改 -->
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
    var colNames = ['ID', '标题', '价格','时长（月）', '类型', '创建时间', '更新时间', '操作'];
    var colModel = [{
        name: 'id',
        index: 'id',
        hidden: true
    }, {
        name: 'title',
        index: 'title',
        width: 200,
        sortable: false,
        align: "center"
    }, {
        name: 'price',
        index: 'price',
        width: 80,
        sortable: false,
        search: false,
        align: "center",
        formatter: "currency"
    }, {
        name: 'duration',
        index: 'duration',
        width: 100,
        search: false,
        sortable: false,
        align: "center"
    }, {
        name: 'type',
        index: 'type',
        width: 80,
        sortable: false,
        align: "center",
        stype: "select",
        searchoptions: {
            value: "1:个人会员;2:企业会员"
        },
        formatter: function (cellvalue, options, rowObject) {
            if (cellvalue == '1') return "个人会员";
            if (cellvalue == '2') return "企业会员";
        }
    },{
        name: 'createTime',
        index: 'createTime',
        width: 140,
        sortable: false,
        search: false,
        align: "center"
    }, {
        name: 'updateTime',
        index: 'updateTime',
        width: 140,
        sortable: false,
        search: false,
        align: "center"
    }, {
        name: 'operate',
        index: 'operate',
        width: 120,
        sortable: false,
        search: false,
        align: "center",
        formatter: function (cellvalue, options, rowObject) {
        	var btsHtml = "";
            if(typeof(mgrFuncMap["func_0_3_1_2"]) != "undefined"){
                btsHtml = btsHtml + "<button class='btn btn-minier btn-warning' type='button' onclick='showUpdate("+ options.rowId + ");'>修改</button>";
            }
            if(typeof(mgrFuncMap["func_0_3_1_3"]) != "undefined"){
                btsHtml = btsHtml + "&nbsp;&nbsp;<button class='btn btn-minier btn-danger' type='button' onclick='del("+ options.rowId + ");'>删除</button>";
            }
            return btsHtml;
        }
    }];
    var caption = "价格列表";
    reloadJqGrid("sys/price/list/json.do", "json", grid_data, colNames, colModel, false, caption, true, false);
</script>