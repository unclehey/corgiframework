<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript">
    // 退款
    function orderRefund(rowId, type) {
        $('#alert-error').css("display", "none");
        $('#alert-success').css("display", "none");
        var rowData = $('#grid-table').jqGrid('getRowData', rowId);
        var orderId = rowData.id;
        var operateName = "";
        if (type == 0) {
            operateName = "部分退款";
        } else if (type == 1) {
            operateName = "全额退款";
        }
        bootbox.confirm("<h4 style='text-align: center;'>您确定要对该笔订单进行【" + operateName +"】操作吗？</h4>", function(result) {
            if(result) {
                var params = {
                    orderId : orderId,
                    refundType : type,
                }
                $.ajax({
                    url : "order/refund.do",
                    type : "post",
                    dataType : "json",
                    data : params,
                    success : function(result) {
                        var bisStatus = result.bisStatus;
                        var bisMsg = result.bisMsg;
                        if (bisStatus != '1000') {
                            // 操作失败
                            $('#error_msg').html(bisMsg);
                            $('#alert-error').css("display", "block");
                            return;
                        } else {
                            var curRowData = $("#grid-table").jqGrid('getRowData', rowId);
                            var data = result.bisObj;
                            $.extend(curRowData, {
                                payStatus : data.payStatus,
                                refundStatus : data.refundStatus,
                                refundAmount : data.refundAmount,
                                updateTime : data.updateTime
                            })
                            // 更新列表本行数据
                            $("#grid-table").jqGrid('setRowData', rowId, curRowData);
                            // 操作成功
                            $('#alert-success').css("display", "block");
                        }
                    },
                    error : function(e) {
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
            try{ace.settings.check('breadcrumbs', 'fixed')}catch(e){}
        </script>
        <!-- breadcrumb -->
        <ul class="breadcrumb">
            <li>
                <i class="ace-icon fa fa-home home-icon"></i>
                ${mgr_title}
            </li>
            <li>
                订单管理
            </li>
            <li>
                订单列表
            </li>
        </ul>
        <!-- /.breadcrumb -->
    </div>

    <div class="page-content">
        <!-- page-header -->
        <div class="page-header">
            <h1>订单列表</h1>
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
    var colNames = ['ID', '用户ID', '手机号', '订单号', '订单类型', '商品名称', '金额', '支付渠道', '支付状态', '退款状态', '退款金额', '创建时间', '更新时间', '操作'];
    var colModel = [{
        name: 'id',
        index: 'id',
        hidden: true
    }, {
        name: 'userId',
        index: 'userId',
        width: 60,
        sortable: false,
        search: false,
        align: "center"
    }, {
        name: 'mobile',
        index: 'mobile',
        width: 100,
        sortable: false,
        align: "center"
    }, {
        name: 'tradeNo',
        index: 'tradeNo',
        width: 200,
        sortable: false,
        align: "center"
    }, {
        name: 'orderType',
        index: 'orderType',
        width: 100,
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
    }, {
        name: 'body',
        index: 'body',
        width: 200,
        search: false,
        sortable: false,
        align: "center"
    }, {
        name: 'amount',
        index: 'amount',
        width: 80,
        search: false,
        sortable: false,
        align: "center",
        formatter: "currency"
    }, {
        name: 'payChannel',
        index: 'payChannel',
        width: 100,
        sortable: false,
        align: "center",
        stype: "select",
        searchoptions: {
            value: "W:微信;A:支付宝"
        },
        formatter: function (cellvalue, options, rowObject) {
            if (cellvalue == 'W') return "微信";
            if (cellvalue == 'A') return "支付宝";
        }
    }, {
        name: 'payStatus',
        index: 'payStatus',
        width: 100,
        sortable: false,
        align: "center",
        stype: "select",
        searchoptions: {
            value: "0:未支付;1:支付成功;2:支付失败;3:已退款"
        },
        formatter: function (cellvalue, options, rowObject) {
            if (cellvalue == '0') return "<span class='label label-sm label-grey arrowed'>未支付</span>";
            if (cellvalue == '1') return "<span class='label label-sm label-success arrowed'>支付成功</span>";
            if (cellvalue == '2') return "<span class='label label-sm label-warning arrowed'>支付失败</span>";
            if (cellvalue == '3') return "<span class='label label-sm label-danger arrowed'>已退款</span>";
        }
    }, {
        name: 'refundStatus',
        index: 'refundStatus',
        width: 100,
        sortable: false,
        align: "center",
        stype: "select",
        searchoptions: {
            value: "0:未退款;1:部分退款;2:全额退款"
        },
        formatter: function (cellvalue, options, rowObject) {
            if (cellvalue == '0') return "<span class='label label-sm label-grey arrowed'>未退款</span>";
            if (cellvalue == '1') return "<span class='label label-sm label-pink arrowed'>部分退款</span>";
            if (cellvalue == '2') return "<span class='label label-sm label-light arrowed'>全额退款</span>";
        }
    }, {
        name: 'refundAmount',
        index: 'refundAmount',
        width: 80,
        search: false,
        sortable: false,
        align: "center",
        formatter: "currency"
    }, {
        name: 'createTime',
        index: 'createTime',
        width: 150,
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
        width: 150,
        sortable: false,
        search: false,
        align: "center",
        formatter: function (cellvalue, options, rowObject) {
            var btsHtml = "";
            if (rowObject.payStatus == 1) {
                if (typeof(mgrFuncMap["func_4_1_1"]) != "undefined") {
                    btsHtml = btsHtml + "<button class='btn btn-minier btn-yellow' type='button' onclick='orderRefund(" + options.rowId + ", 0);'>部分退款</button>";
                }
                if (typeof(mgrFuncMap["func_4_1_2"]) != "undefined") {
                    btsHtml = btsHtml + "&nbsp;&nbsp;<button class='btn btn-minier btn-danger' type='button' onclick='orderRefund(" + options.rowId + ", 1);'>全额退款</button>";
                }
            }
            return btsHtml;
        }
    }];
    var caption = "订单列表";
    reloadJqGrid("order/list/json.do", "json", grid_data, colNames, colModel, false, caption, true);
</script>