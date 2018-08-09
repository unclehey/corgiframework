package pers.corgiframework.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.corgiframework.dao.model.BisPrompt;
import pers.corgiframework.service.IRedisService;
import pers.corgiframework.service.IWeChatService;
import pers.corgiframework.tool.constants.BisPromptConstant;
import pers.corgiframework.tool.constants.PayConstant;
import pers.corgiframework.tool.utils.OkHttpClientUtil;
import pers.corgiframework.tool.utils.PayUtil;
import pers.corgiframework.tool.utils.PropertiesUtil;
import pers.corgiframework.tool.utils.XmlUtil;
import pers.corgiframework.tool.utils.wechat.WeixinUtil;
import pers.corgiframework.tool.utils.wechat.pojo.AccessToken;
import pers.corgiframework.tool.utils.wechat.pojo.JsApiTicket;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by syk on 2018/8/7.
 */
@Service
public class WeChatServiceImpl implements IWeChatService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeChatServiceImpl.class);
    // 从配置文件获取参数
    // 从配置文件读取微信统一下单接口地址
    private static final String UNIFIED_ORDER_URL = PropertiesUtil.getString("unified_order_url");
    // 生成带参数的二维码时获取ticket接口地址
    private static final String TICKET_URL = PropertiesUtil.getString("public_tiket_url");
    // 生成带参数的二维码接口地址
    private static final String QRCODE_URL = PropertiesUtil.getString("public_qrcode_url");
    // 长链接转短链接接口地址
    private static final String LONG_TO_SHORT_URL = PropertiesUtil.getString("public_long2short_url");
    // 获取用户信息接口地址
    private static final String INFO_URL = PropertiesUtil.getString("public_info_url");
    // 向用户发送信息接口地址
    private static final String SEND_URL = PropertiesUtil.getString("public_send_url");
    // 申请退款接口地址
    private static final String REFUND_URL = PropertiesUtil.getString("public_refund_url");

    @Autowired
    private IRedisService redisService;

    @Override
    public BisPrompt wxPay(String appid, String mchId, String payKey, String tradeNo, String body, String totalAmount, String terminalIp, String tradeType, String openId, String notifyUrl, String sceneInfo) {
        BisPrompt bisPrompt = new BisPrompt();
        // 拼装统一下单请求参数
        String xml = PayUtil.assembleWxPayParams(appid, mchId, payKey, tradeNo, body, totalAmount, terminalIp, tradeType, openId, notifyUrl, sceneInfo);
        // 得到微信支付统一下单返回结果
        String resultData = OkHttpClientUtil.executeStringPost(UNIFIED_ORDER_URL, xml);
        Map<String, Object> returnMap = XmlUtil.parseXml(resultData);
        // 通信标识
        String return_code = String.valueOf(returnMap.get("return_code"));
        // 预订单生成成功
        if (return_code.equals("SUCCESS")) {
            // 交易标识
            String result_code = String.valueOf(returnMap.get("result_code"));
            if (result_code.equals("SUCCESS")) {
                // 调用接口提交的交易类型
                String trade_type = String.valueOf(returnMap.get("trade_type"));
                // 微信生成的预支付回话标识，用于后续接口调用中使用，该值有效期为2小时
                String prepay_id = String.valueOf(returnMap.get("prepay_id"));
                // 封装返回APP参数
                Map<String, String> responseMap;
                switch (tradeType) {
                    case PayConstant.TRADE_TYPE_APP:
                        // APP支付
                        responseMap = PayUtil.generateWxAppParams(appid, mchId, payKey, prepay_id);
                        bisPrompt.setBisObj(responseMap);
                        break;
                    case PayConstant.TRADE_TYPE_PUBLIC:
                        // 公众号支付
                        responseMap = PayUtil.generateWxPublicParams(appid, payKey, prepay_id);
                        bisPrompt.setBisObj(responseMap);
                        break;
                    case PayConstant.TRADE_TYPE_H5:
                        // H5支付
                        String mweb_url = String.valueOf(returnMap.get("mweb_url"));
                        bisPrompt.setBisObj(mweb_url);
                        break;
                }
            } else {
                // 错误代码描述
                String err_code_des = String.valueOf(returnMap.get("err_code_des"));
                LOGGER.info("weixin pay generate order err_code_des = {}", err_code_des);
                bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                bisPrompt.setBisMsg(err_code_des);
            }
        } else {
            String return_msg = String.valueOf(returnMap.get("return_msg"));
            LOGGER.info("weixin pay generate order return_msg = {}", return_msg);
            bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
            bisPrompt.setBisMsg(return_msg);
        }
        LOGGER.info("weixin pay generate order:end");
        return bisPrompt;
    }

    @Override
    public BisPrompt refundOrder(String mchId, String appId, String payKey, String tradeNo, String refundNo, BigDecimal totalAmount, BigDecimal refundAmount) {
        BisPrompt bisPrompt = new BisPrompt();
        String xml = "";
        Map<String, String> returnMap = null;
        try {
            // 拼装退款请求参数
            xml = PayUtil.assembleWxRefundParams(mchId, appId, payKey, tradeNo, refundNo, totalAmount, refundAmount);
            // 请求数据
            returnMap = WeixinUtil.httpsRequestSSL(REFUND_URL, "post", xml, mchId);
            // 通信标识
            String return_code = returnMap.get("return_code");
            // 预订单生成成功
            if (return_code.equals("SUCCESS")) {
                // 交易标识
                String result_code = returnMap.get("result_code");
                if (!result_code.equals("SUCCESS")) {
                    String err_code_des = returnMap.get("err_code_des");
                    bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                    bisPrompt.setBisMsg(err_code_des);
                }
            } else {
                String return_msg = returnMap.get("return_msg");
                bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                bisPrompt.setBisMsg(return_msg);
            }
        } catch (Exception e) {
            LOGGER.error(String.format("请求申请退款接口错误 参数：%s", xml), e);
            bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
            bisPrompt.setBisMsg("请求申请退款接口异常");
        }
        return bisPrompt;
    }

    @Override
    public String getTempTicket(String appId, String appSecret, int sceneId, String tokenKey) {
        String ticket = null;
        // 获取token
        AccessToken at = getWechatToken(appId, appSecret, tokenKey);
        String requestUrl = TICKET_URL.replace("TOKEN", at.getToken());
        String jsonStr = "{\"expire_seconds\": 1800, \"action_name\": \"QR_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": " + sceneId + "}}}";
        Map<Object, Object> map = WeixinUtil.httpRequest(requestUrl, "POST", jsonStr);
        // 如果请求成功
        if (null != map) {
            ticket = map.get("ticket").toString();
        }
        return ticket;
    }

    @Override
    public String getPermanentTicket(String appId, String appSecret, String sceneStr, String tokenKey) {
        String ticket = null;
        // 获取token
        AccessToken at = getWechatToken(appId, appSecret, tokenKey);
        String requestUrl = TICKET_URL.replace("TOKEN", at.getToken());
        String jsonStr = "{\"action_name\": \"QR_LIMIT_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \"" + sceneStr + "\"}}}";
        Map<Object, Object> map = WeixinUtil.httpRequest(requestUrl, "POST", jsonStr);
        // 如果请求成功
        if (null != map) {
            ticket = map.get("ticket").toString();
        }
        return ticket;
    }

    @Override
    public String getTempQrcode(String appId, String appSecret, int sceneId, String tokenKey) {
        // 调用接口获取ticket
        String ticket = getTempTicket(appId, appSecret, sceneId, tokenKey);
        String requestUrl = QRCODE_URL.replace("TICKET", ticket);
        return requestUrl;
    }

    @Override
    public String getPermanentQrcode(String appId, String appSecret, String sceneStr, String tokenKey) {
        // 调用接口获取ticket
        String ticket = getPermanentTicket(appId, appSecret, sceneStr, tokenKey);
        String requestUrl = QRCODE_URL.replace("TICKET", ticket);
        return requestUrl;
    }

    @Override
    public Map<Object, Object> getUserInfo(String appId, String appSecret, String openId, String tokenKey) {
        // 获取token
        AccessToken at = getWechatToken(appId, appSecret, tokenKey);
        String requestUrl = INFO_URL.replace("ACCESS_TOKEN", at.getToken()).replace("OPENID", openId);
        Map<Object, Object> map = WeixinUtil.httpRequest(requestUrl, "GET", null);
        return map;
    }

    @Override
    public Map<Object, Object> sendMessage(String appId, String appSecret, String openId, String content, String tokenKey) {
        try {
            // 获取token
            AccessToken at = getWechatToken(appId, appSecret, tokenKey);
            String requestUrl = SEND_URL.replace("ACCESS_TOKEN", at.getToken());
            //组装json串
            String jsonStr = "{\"touser\": \"" + openId + "\", \"msgtype\": \"text\", \"text\": {\"content\": \"" + content + "\"}}";
            Map<Object, Object> map = WeixinUtil.httpRequest(requestUrl, "POST", jsonStr);
            return map;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public JsApiTicket getJsApiTicket(String appId, String appSecret, String ticketKey) {
        JsApiTicket jat = null;
        try {
            // 先去redisService取，如果取不到在调用接口
            jat = (JsApiTicket) redisService.get(ticketKey);
            if (jat == null || StringUtils.isBlank(jat.getTicket())) {
                // 调用接口获取jat
                jat = WeixinUtil.getJsTicket(appId, appSecret);
                int second = jat.getExpiresIn() - 1800;
                redisService.set(ticketKey, second, jat);
            }
        } catch (Exception e) {
            // 调用接口获取jat
            jat = WeixinUtil.getJsTicket(appId, appSecret);
            LOGGER.error(e.getMessage(), e);
        }
        return jat;
    }

    @Override
    public String getLongToShortUrl(String longUrl, String appId, String appSecret, String tokenKey) {
        String shortUrl = null;
        // 获取token
        AccessToken at = getWechatToken(appId, appSecret, tokenKey);
        String requestUrl = LONG_TO_SHORT_URL.replace("ACCESS_TOKEN", at.getToken());
        String jsonStr = "{\"action\": \"long2short\", \"long_url\": " + longUrl + "}";
        Map<Object, Object> map = WeixinUtil.httpRequest(requestUrl, "POST", jsonStr);
        // 如果请求成功
        if (null != map) {
            shortUrl = map.get("short_url").toString();
        }
        return shortUrl;
    }

    /**
     * 获取access_token
     *
     * @param appId
     * @param appSecret
     * @param tokenKey  获取token缓存key
     * @return
     */
    private AccessToken getWechatToken(String appId, String appSecret, String tokenKey) {
        // 获取access_token
        AccessToken at = null;
        try {
            // 先去redisService取，如果取不到在调用接口
            at = (AccessToken) redisService.get(tokenKey);
            if (at == null || StringUtils.isBlank(at.getToken())) {
                // 调用接口获取access_token
                at = WeixinUtil.getAccessToken(appId, appSecret);
                int second = at.getExpiresIn() - 1800;
                redisService.set(tokenKey, second, at);
            }
        } catch (Exception e) {
            // 调用接口获取access_token
            at = WeixinUtil.getAccessToken(appId, appSecret);
            LOGGER.error(e.getMessage(), e);
        }
        return at;
    }

}
