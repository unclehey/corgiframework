package pers.corgiframework.service;

import pers.corgiframework.dao.model.BisPrompt;
import pers.corgiframework.tool.utils.alipay.AppAliPay;
import pers.corgiframework.tool.utils.alipay.WebAliPay;

import java.util.Map;

/**
 * 支付宝支付接口
 * Created by syk on 2018/8/7.
 */
public interface IAliPayService {
    /**
     * 支付宝即时到账异步调用
     * @param params
     * @return
     */
    Boolean aliPayNotify(Map<String, String> params);

    /**
     * 支付宝即时到账退款异步调用
     * @param params
     * @return
     */
    Boolean aliBunchRefundNotify(Map<String, String> params);

    /**
     * 结算单支付异步调用
     * @param params
     * @return
     */
    Boolean settleOrderPayNotify(Map<String, String> params);

    /**
     * 支付宝条码支付
     * @param out_trade_no 订单号
     * @param auth_code 支付授权码（条码号）
     * @param total_amount 支付金额
     * @param subject 订单描述
     * @param appID
     * @param privateKey RSA私钥
     * @param publicKey RSA公钥
     * @return
     */
    BisPrompt orderAliBarPay(String out_trade_no, String auth_code, String total_amount, String subject, String appID, String privateKey, String publicKey);

    /**
     * 支付宝扫码支付
     * @param out_trade_no  订单号
     * @param total_amount  支付金额
     * @param subject  订单描述
     * @param appID
     * @param privateKey RSA私钥
     * @param publicKey RSA公钥
     * @return
     */
    BisPrompt qrPay(String out_trade_no, String total_amount, String subject, String appID, String privateKey, String publicKey);

    /**
     * 订单支付情况查询
     * @param out_trade_no 订单号
     * @param appID
     * @param privateKey RSA私钥
     * @param publicKey RSA公钥
     * @return
     */
    BisPrompt queryBarPayOrder(String out_trade_no, String appID, String privateKey, String publicKey);

    /**
     * 订单撤销
     * @param out_trade_no  订单号
     * @param appID
     * @param privateKey RSA私钥
     * @param publicKey RSA公钥
     * @return
     */
    BisPrompt cancelOrder(String out_trade_no, String appID, String privateKey, String publicKey);

    /**
     * 订单退款
     * @param trade_no 支付宝交易流水号
     * @param refund_amount 退款金额
     * @param out_request_no 退款单号
     * @param appID
     * @param privateKey RSA私钥
     * @param publicKey RSA公钥
     * @return
     */
    BisPrompt orderRefund(String trade_no, String refund_amount, String out_request_no, String appID, String privateKey, String publicKey);

    /**
     * app支付获取预付单信息
     * @param appPay
     * @return
     */
    BisPrompt getSignOrder(AppAliPay appPay);

    /**
     * web端阿里支付
     * @param webPay webPay
     * @return
     */
    String webAliPay(WebAliPay webPay);
}
