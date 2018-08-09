package pers.corgiframework.third.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.corgiframework.service.IWeChatService;
import pers.corgiframework.tool.utils.DateTimeUtil;
import pers.corgiframework.tool.utils.JsonUtil;
import pers.corgiframework.tool.utils.PropertiesUtil;
import pers.corgiframework.tool.utils.SignUtil;
import pers.corgiframework.tool.utils.wechat.MessageUtil;
import pers.corgiframework.tool.utils.wechat.WeixinUtil;
import pers.corgiframework.tool.utils.wechat.pojo.JsApiTicket;
import pers.corgiframework.tool.utils.wechat.pojo.message.NewsMessage;
import pers.corgiframework.tool.utils.wechat.pojo.message.TextMessage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信公众号交互控制器
 * Created by syk on 2018/8/9.
 */
@RestController
@RequestMapping("/wechat")
public class WeChatController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeChatController.class);
    // 获取微信公众号参数
    private static final String PUBLIC_APP_ID = PropertiesUtil.getString("public_appid");
    private static final String PUBLIC_APP_SECRET = PropertiesUtil.getString("public_appsecret");
    private static final String PUBLIC_TOKEN_KEY = PropertiesUtil.getString("public_token_key");
    private static final String PUBLIC_TICKET_KEY = PropertiesUtil.getString("public_ticket_key");

    @Autowired
    private IWeChatService weChatService;

    /**
     * 确认请求来自微信服务器
     */
    @GetMapping(value = "/confirm")
    public void confirmWeChat(HttpServletRequest request, HttpServletResponse response) {
        // 微信加密签名
        String signature = request.getParameter("signature");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");
        // 随机字符串
        String echostr = request.getParameter("echostr");

        PrintWriter out = null;
        try {
            out = response.getWriter();
            // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
            if (SignUtil.checkSignature(signature, timestamp, nonce)) {
                out.print(echostr);
            }
            out.close();
            out = null;
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * 处理微信服务器发来的请求
     */
    @PostMapping(value = "/confirm")
    public void handleWeChat(HttpServletRequest request, HttpServletResponse response) {
        // 将请求、响应的编码均设置为UTF-8（防止中文乱码）
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        response.setCharacterEncoding("UTF-8");

        // 调用核心业务类接收消息、处理消息
        String respMessage = null;
        try {
            // TODO 此处可封装成自己的方法
            // 默认返回的文本消息内容
            String respContent = "默认返回的文本消息内容";
            // xml请求解析
            Map<String, String> requestMap = MessageUtil.parseXml(request);
            if (null != requestMap) {
                // 发送方帐号（open_id）
                String fromUserName = requestMap.get("FromUserName");
                // 公众帐号
                String toUserName = requestMap.get("ToUserName");
                // 消息类型
                String msgType = requestMap.get("MsgType");
                // 时间戳
                long timestamp = DateTimeUtil.getNowTimestamp();

                // 默认回复文本消息
                TextMessage textMessage = new TextMessage();
                textMessage.setToUserName(fromUserName);
                textMessage.setFromUserName(toUserName);
                textMessage.setCreateTime(timestamp);
                textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                textMessage.setFuncFlag(0);

                // 回复图文消息
                NewsMessage newsMessage = new NewsMessage();
                newsMessage.setToUserName(fromUserName);
                newsMessage.setFromUserName(toUserName);
                newsMessage.setCreateTime(timestamp);
                newsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
                newsMessage.setFuncFlag(0);

                // 文本消息
                if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
                    String content = requestMap.get("Content").trim();
                    if (StringUtils.isNotBlank(content)) {
                        // 转发到多客服系统
                        textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_SERVICE);
                        respMessage = MessageUtil.textMessageToXml(textMessage);
                    }
                }
                // 图片消息
                else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_IMAGE)) {
                    respContent = "Corgi欢迎您！";
                    textMessage.setContent(respContent);
                    respMessage = MessageUtil.textMessageToXml(textMessage);
                }
                // 地理位置消息
                else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LOCATION)) {
                    respContent = "Corgi欢迎您！";
                    textMessage.setContent(respContent);
                    respMessage = MessageUtil.textMessageToXml(textMessage);
                }
                // 链接消息
                else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LINK)) {
                    respContent = "Corgi欢迎您！";
                    textMessage.setContent(respContent);
                    respMessage = MessageUtil.textMessageToXml(textMessage);
                }
                // 音频消息
                else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VOICE)) {
                    respContent = "Corgi欢迎您！";
                    textMessage.setContent(respContent);
                    respMessage = MessageUtil.textMessageToXml(textMessage);
                }
                // 事件推送
                else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
                    // 事件类型
                    String eventType = requestMap.get("Event");
                    // 订阅
                    if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
                        // 关注类型
                        String eventKey = requestMap.get("EventKey");
                        if ("".equals(eventKey)) {
                            // 普通关注

                        } else {
                            // 扫描带参数二维码事件进行关注

                        }
                        textMessage.setContent(respContent);
                        respMessage = MessageUtil.textMessageToXml(textMessage);
                    }
                    // 取消订阅
                    else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {

                    }
                    // 扫描带参数二维码事件（已关注）
                    else if (eventType.equals(MessageUtil.EVENT_TYPE_SCAN)) {
                        respContent = "欢迎回来！";
                        textMessage.setContent(respContent);
                        respMessage = MessageUtil.textMessageToXml(textMessage);
                    }
                    // 自定义菜单点击事件（拉取消息）
                    else if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) {
                        // 事件KEY值，与创建自定义菜单时指定的KEY值对应
                        String eventKey = requestMap.get("EventKey");
                        // 在线客服
                        if (eventKey.equals("31")) {

                        }
                        // 联系我们
                        else if (eventKey.equals("32")) {

                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        // 响应请求
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.print(respMessage);
            out.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (null != out) {
                out.close();
            }
        }
    }

    /**
     * 生成带参数的临时二维码
     * @param request
     * @return
     */
    @RequestMapping(value = "/generateTempQrcode")
    public String generateTempQrcode(HttpServletRequest request) {
        // 获取生成二维码参数
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 邀请码
        String inviteCode = request.getParameter("inviteCode");
        // 加密签名
        String signature = request.getParameter("signature");
        // 生成的二维码
        String qrcode = "";
        try {
            // 当前时间戳
            Long nowTime = DateTimeUtil.getNowTimestamp();
            // 请求时间戳
            Long timeStamp = Long.parseLong(timestamp);
            // 判断请求是否超时
            if ((nowTime - timeStamp) < (5 * 60 * 1000)) {
                if (StringUtils.isNotBlank(inviteCode)) {
                    String sign = SignUtil.sha256(inviteCode + timestamp);
                    if (sign.equals(signature)) {
                        // 二维码信息
                        qrcode = weChatService.getTempQrcode(PUBLIC_APP_ID, PUBLIC_APP_SECRET, Integer.valueOf(inviteCode), PUBLIC_TOKEN_KEY);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return qrcode;
    }

    /**
     * 生成带参数的永久二维码
     * @param request
     * @return
     */
    @RequestMapping(value = "/generatePermanentQrcode")
    public String generatePermanentQrcode(HttpServletRequest request) {
        // 获取生成二维码参数
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 邀请码
        String officeId = request.getParameter("officeId");
        // 加密签名
        String signature = request.getParameter("signature");
        // 生成的二维码
        String qrcode = "";
        try {
            // 当前时间戳
            Long nowTime = DateTimeUtil.getNowTimestamp();
            // 请求时间戳
            Long timeStamp = Long.parseLong(timestamp);
            // 判断请求是否超时
            if ((nowTime - timeStamp) < (5 * 60 * 1000)) {
                if (StringUtils.isNotBlank(officeId)) {
                    String sign = SignUtil.sha256(officeId + timestamp);
                    if (sign.equals(signature)) {
                        // 二维码信息
                        qrcode = weChatService.getPermanentQrcode(PUBLIC_APP_ID, PUBLIC_APP_SECRET, officeId, PUBLIC_TOKEN_KEY);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return qrcode;
    }

    /**
     * 生成JsApiTicket
     * @param request
     * @return
     */
    @RequestMapping(value = "/getJsParameter")
    public String getJsParameter(HttpServletRequest request) {
        // 获取url
        String url = request.getParameter("js_url");
        // 生成签名的随机串
        String nonce_str = WeixinUtil.createNonceStr();
        // 生成签名的时间戳
        String timestamp = WeixinUtil.createTimestamp();
        // 获取jsapi_ticket
        JsApiTicket jat = null;
        if(StringUtils.isBlank(url)){
            return null;
        }
        try {
            jat = weChatService.getJsApiTicket(PUBLIC_APP_ID, PUBLIC_APP_SECRET, PUBLIC_TICKET_KEY);
            String string1;
            String signature = "";
            // 注意这里参数名必须全部小写，且必须有序
            string1 = "jsapi_ticket=" + jat.getTicket() + "&noncestr=" + nonce_str + "&timestamp=" + timestamp + "&url=" + url;
            // 签名算法
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes("UTF-8"));
            signature = WeixinUtil.byteToHex(crypt.digest());
            // 组装参数传给页面
            Map<String, String> ret = new HashMap<>();
            ret.put("url", url);
            ret.put("jsapi_ticket", jat.getTicket());
            ret.put("nonceStr", nonce_str);
            ret.put("timestamp", timestamp);
            ret.put("signature", signature);
            ret.put("appId", PUBLIC_APP_ID);
            return JsonUtil.objectToJson(ret);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

}
