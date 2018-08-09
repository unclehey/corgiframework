package pers.corgiframework.service;

import pers.corgiframework.dao.domain.PaymentOrder;
import pers.corgiframework.dao.domain.SysPrice;
import pers.corgiframework.dao.model.BisPrompt;

import java.math.BigDecimal;

/**
 * Created by syk on 2018/8/6.
 */
public interface IPaymentOrderService extends IBaseService<PaymentOrder> {

    /**
     * 根据订单号查询订单
     * @param tradeNo
     * @return
     */
    PaymentOrder selectByTradeNo(String tradeNo);

    /**
     * 支付宝支付
     * @param userId 用户ID
     * @param mobile 手机号
     * @param orderId 支付已存在订单【订单ID】
     * @param productId 支付商品ID
     * @param orderType 订单类型
     * @param orderCount 订单数量
     * @param body 商品描述
     * @param sysPrice 商品价格对象
     * @param paySource 支付来源【iOS、Android】
     * @param appId 支付宝分配的appId
     * @param privateKey 支付宝分配的私钥
     * @param publicKey 支付宝分配的公钥
     * @param notifyUrl 异步回调url
     * @return
     */
    BisPrompt orderPayByAliPay(Integer userId, String mobile, String orderId, String productId, Integer orderType, Integer orderCount, String body, SysPrice sysPrice, String paySource, String appId, String privateKey, String publicKey, String notifyUrl);

    /**
     * 支付宝退款
     * @param transactionId
     * @param refundAmount
     * @param appId
     * @param privateKey
     * @param publicKey
     * @return
     */
    BisPrompt orderRefundByAliPay(String transactionId, BigDecimal refundAmount, String appId, String privateKey, String publicKey);

    /**
     * 微信支付
     * @param userId 用户ID
     * @param mobile 手机号
     * @param orderId 支付已存在订单【订单ID】
     * @param productId 支付商品ID
     * @param orderType 订单类型
     * @param orderCount 订单数量
     * @param terminalIp IP
     * @param body 商品描述
     * @param sysPrice 商品价格对象
     * @param paySource 支付来源【iOS、Android、wechat】
     * @param appId 微信分配的appId
     * @param mchId 微信分配的商户号
     * @param payKey 支付key
     * @param notifyUrl 异步回调url
     * @param openId 公众号支付必传
     * @param tradeType 交易类型【APP支付-APP；公众号支付-JSAPI；H5支付-MWEB】
     * @param sceneInfo H5支付必传
     * @return
     */
    BisPrompt orderPayByWxPay(Integer userId, String mobile, String orderId, String productId, Integer orderType, Integer orderCount, String terminalIp, String body, SysPrice sysPrice, String paySource, String appId, String mchId, String payKey, String notifyUrl, String openId, String tradeType, String sceneInfo);

    /**
     * 微信退款
     * @param tradeNo
     * @param refundAmount
     * @param appId
     * @param mchId
     * @param payKey
     * @return
     */
    BisPrompt orderRefundByWxPay(String tradeNo, BigDecimal refundAmount, String appId, String mchId, String payKey);

    /**
     * 支付异步回调处理
     * @param tradeNo
     * @param payStatus
     * @param transactionId
     * @return
     */
    BisPrompt handleOrderPayResult(String tradeNo, Integer payStatus, String transactionId);
}
