package pers.corgiframework.tool.utils.wechat.pay;

/**
 * Created by ph on 2017/7/6.
 * 微信退款单实体
 */
public class WXRefund extends WXPay {

    //微信订单号（交易号）
    private String transaction_id;
    //本地订单号
    private String out_trade_no;
    //订单金额（单位:分 不保留小数）
    private int total_fee;
    //本地退款单号
    private String out_refund_no;
    //退款金额（单位:分 不保留小数）
    private int refund_fee;
    //微信退款单号
    private String refund_id;
    //代金券或立减优惠退款金额（单位:分 不保留小数）
    private int coupon_refund_fee;
    //代金券或立减优惠使用数量(整数)
    private int coupon_refund_count;
    // 业务结果 (SUCCESS/FAIL)
    private String result_code;
    //返回状态码(SUCCESS/FAIL)
    private String return_code;
    //返回错误信息
    private String return_msg;
    //现金退款金额  （单位:分 不保留小数）
    private int cash_refund_fee;
    //现金支付金额  （单位:分 不保留小数）
    private int cash_fee;
    //错误代码
    private String err_code;
    //错误描述
    private String err_code_des;
    //退款状态
    private String refund_status;

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public int getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(int total_fee) {
        this.total_fee = total_fee;
    }

    public String getOut_refund_no() {
        return out_refund_no;
    }

    public void setOut_refund_no(String out_refund_no) {
        this.out_refund_no = out_refund_no;
    }

    public int getRefund_fee() {
        return refund_fee;
    }

    public void setRefund_fee(int refund_fee) {
        this.refund_fee = refund_fee;
    }

    public String getRefund_id() {
        return refund_id;
    }

    public void setRefund_id(String refund_id) {
        this.refund_id = refund_id;
    }

    public int getCoupon_refund_fee() {
        return coupon_refund_fee;
    }

    public void setCoupon_refund_fee(int coupon_refund_fee) {
        this.coupon_refund_fee = coupon_refund_fee;
    }

    public int getCoupon_refund_count() {
        return coupon_refund_count;
    }

    public void setCoupon_refund_count(int coupon_refund_count) {
        this.coupon_refund_count = coupon_refund_count;
    }

    public String getResult_code() {
        return result_code;
    }

    public void setResult_code(String result_code) {
        this.result_code = result_code;
    }

    public String getReturn_code() {
        return return_code;
    }

    public void setReturn_code(String return_code) {
        this.return_code = return_code;
    }

    public String getReturn_msg() {
        return return_msg;
    }

    public void setReturn_msg(String return_msg) {
        this.return_msg = return_msg;
    }

    public int getCash_refund_fee() {
        return cash_refund_fee;
    }

    public void setCash_refund_fee(int cash_refund_fee) {
        this.cash_refund_fee = cash_refund_fee;
    }

    public int getCash_fee() {
        return cash_fee;
    }

    public void setCash_fee(int cash_fee) {
        this.cash_fee = cash_fee;
    }

    public String getErr_code() {
        return err_code;
    }

    public void setErr_code(String err_code) {
        this.err_code = err_code;
    }

    public String getErr_code_des() {
        return err_code_des;
    }

    public void setErr_code_des(String err_code_des) {
        this.err_code_des = err_code_des;
    }

    public String getRefund_status() {
        return refund_status;
    }

    public void setRefund_status(String refund_status) {
        this.refund_status = refund_status;
    }
}
