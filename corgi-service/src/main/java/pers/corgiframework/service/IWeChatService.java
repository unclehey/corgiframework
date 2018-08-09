package pers.corgiframework.service;

import pers.corgiframework.dao.model.BisPrompt;
import pers.corgiframework.tool.utils.wechat.pojo.JsApiTicket;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by syk on 2018/8/7.
 */
public interface IWeChatService {

    /**
     * 微信支付
     * @param appid
     * @param mchId
     * @param payKey
     * @param tradeNo
     * @param body
     * @param totalAmount
     * @param terminalIp
     * @param tradeType
     * @param openId
     * @param notifyUrl
     * @param sceneInfo
     * @return
     */
    BisPrompt wxPay(String appid, String mchId, String payKey, String tradeNo, String body, String totalAmount, String terminalIp, String tradeType, String openId, String notifyUrl, String sceneInfo);

    /**
     * 微信退款
     * @return
     */
    BisPrompt refundOrder(String mchId, String appId, String apiSecertKey, String tradeNo, String refundNo, BigDecimal totalAmount, BigDecimal refundAmount);

    /**
     * 创建临时二维码ticket
     * @param appId
     * @param appSecret
     * @param sceneId 开发者传入参数
     * @param tokenKey 获取token缓存key
     * @return
     */
    String getTempTicket(String appId, String appSecret, int sceneId, String tokenKey);

    /**
     * 创建永久二维码ticket
     * @param appId
     * @param appSecret
     * @param sceneStr 开发者传入参数
     * @param tokenKey 获取token缓存key
     * @return
     */
    String getPermanentTicket(String appId, String appSecret, String sceneStr, String tokenKey);

    /**
     * 生成带参数的临时二维码
     * @param appId
     * @param appSecret
     * @param sceneId 开发者传入参数
     * @param tokenKey 获取token缓存key
     * @return
     */
    String getTempQrcode(String appId, String appSecret, int sceneId, String tokenKey);

    /**
     * 生成带参数的永久二维码
     * @param appId
     * @param appSecret
     * @param sceneStr 开发者传入参数
     * @param tokenKey 获取token缓存key
     * @return
     */
    String getPermanentQrcode(String appId, String appSecret, String sceneStr, String tokenKey);

    /**
     * 获取用户信息
     * @param appId
     * @param appSecret
     * @param openId
     * @param tokenKey 获取token缓存key
     * @return
     */
    Map<Object, Object> getUserInfo(String appId, String appSecret, String openId, String tokenKey);

    /**
     * 向用户发送信息
     * @param appId
     * @param appSecret
     * @param openId
     * @param content
     * @param tokenKey 获取token缓存key
     * @return
     */
    Map<Object, Object> sendMessage(String appId, String appSecret, String openId, String content, String tokenKey);

    /**
     * 获取jsapi_ticket
     * @param appId
     * @param appSecret
     * @param ticketKey 获取ticket缓存key
     * @return
     */
    JsApiTicket getJsApiTicket(String appId, String appSecret, String ticketKey);

    /**
     * 长链接转短链接
     * @param longUrl
     * @param appId
     * @param appSecret
     * @param tokenKey 获取token缓存key
     * @return
     */
    String getLongToShortUrl(String longUrl, String appId, String appSecret, String tokenKey);
}
