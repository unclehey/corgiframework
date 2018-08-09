package pers.corgiframework.tool.constants;

/**
 * 支付常量
 * Created by syk on 2018/8/7.
 */
public interface PayConstant {

    // 支付状态
    Integer NO_PAY = 0;
    Integer PAY_SUCCESS = 1;
    Integer PAY_FAILED = 2;
    Integer REFUND_SUCCESS = 3;
    // 退款状态
    Integer NO_REFUND = 0;
    Integer REFUND_PART = 1;
    Integer REFUND_ALL = 2;
    // 支付渠道
    String WEIXIN_PAY = "W";
    String ALI_PAY = "A";
    // 支付类型
    String TRADE_TYPE_APP = "APP";
    String TRADE_TYPE_PUBLIC = "JSAPI";
    String TRADE_TYPE_H5 = "MWEB";
    // 支付来源
    String SOURCE_IOS = "iOS";
    String SOURCE_ANDROID = "Android";
    String SOURCE_WECHAT = "wechat";
}
