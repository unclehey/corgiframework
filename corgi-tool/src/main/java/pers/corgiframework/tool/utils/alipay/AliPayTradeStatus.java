package pers.corgiframework.tool.utils.alipay;

/**
 * 支付宝支付状态
 * Created by syk on 2018/8/7.
 */
public class AliPayTradeStatus {
    // 交易关闭
    public static final String TRADE_CLOSED = "TRADE_CLOSED";
    // 卖家已发货（该状态只有担保交易才有）
    public static final String WAIT_SELLER_SEND_GOODS = "WAIT_SELLER_SEND_GOODS";
    // 等待买家确认收货（该状态只有担保交易才有）
    public static final String WAIT_BUYER_CONFIRM_GOODS = "WAIT_BUYER_CONFIRM_GOODS";
    // 等待买家付款
    public static final String WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
    //  交易成功（该状态只有担保交易才有）
    public static final String TRADE_SUCCESS = "TRADE_SUCCESS";
    // 交易完成
    public static final String TRADE_FINISHED = "TRADE_FINISHED";
}
