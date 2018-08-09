package pers.corgiframework.third.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.corgiframework.dao.domain.PaymentOrder;
import pers.corgiframework.service.IPaymentOrderService;
import pers.corgiframework.tool.constants.PayConstant;
import pers.corgiframework.tool.utils.PayUtil;
import pers.corgiframework.tool.utils.PropertiesUtil;
import pers.corgiframework.tool.utils.SignUtil;
import pers.corgiframework.tool.utils.XmlUtil;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 微信支付异步回调处理器
 * Created by syk on 2018/8/8.
 */
@RestController
@RequestMapping("/callback")
public class WxPayCallbackController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WxPayCallbackController.class);
    // 获取支付参数
    private static final String PUBLIC_PAY_KEY = PropertiesUtil.getString("public_pay_key");
    private static final String APP_PAY_KEY = PropertiesUtil.getString("open_pay_key");
    private static final String H5_PAY_KEY = PropertiesUtil.getString("h5_pay_key");

    @Autowired
    private IPaymentOrderService paymentOrderService;

    @PostMapping(value = "/wxpay/notify")
    public String notify(HttpServletRequest request) {
        LOGGER.info("weixin pay asynchronized notify:start");
        // 获取微信支付异步返回结果
        Map<String, String> returnMap = null;
        try {
            returnMap = XmlUtil.parseXml(request);
            String return_code = returnMap.get("return_code");
            // 通信标识
            if (return_code.equals("SUCCESS")) {
                // 商户系统的订单号
                String tradeNo = returnMap.get("out_trade_no");
                PaymentOrder order = paymentOrderService.selectByTradeNo(tradeNo);
                if (null == order) {
                    return "<xml><return_code><![CDATA[FAIL]]></return_code></xml>";
                } else {
                    int total_fee = 0;
                    Integer payStatus = order.getPayStatus();
                    // 查看该笔订单是否已成功支付
                    if (payStatus.equals(PayConstant.NO_PAY) || payStatus.equals(PayConstant.PAY_FAILED)) {
                        String result_code = returnMap.get("result_code");
                        // 交易标识
                        if (result_code.equals("SUCCESS")) {
                            String returnSign = returnMap.get("sign");
                            // 得到排序后的参数
                            returnMap.remove("sign");
                            String sortParams = PayUtil.assembleNoSignParamsBySort(returnMap);
                            String tradeType = order.getTradeType();
                            String payKey = "";
                            switch (tradeType) {
                                case PayConstant.TRADE_TYPE_APP:
                                    payKey = APP_PAY_KEY;
                                    break;
                                case PayConstant.TRADE_TYPE_PUBLIC:
                                    payKey = PUBLIC_PAY_KEY;
                                    break;
                                case PayConstant.TRADE_TYPE_H5:
                                    payKey = H5_PAY_KEY;
                                    break;
                            }
                            // 签名
                            String stringSignTemp = sortParams + "&key=" + payKey;
                            String sign = SignUtil.md5(stringSignTemp).toUpperCase();
                            // 验证签名
                            if (sign.equals(returnSign)) {
                                String trade_type = returnMap.get("trade_type");
                                total_fee = Integer.parseInt(returnMap.get("total_fee"));
                                if (total_fee == order.getAmount().multiply(new BigDecimal(100)).intValue()) {
                                    // 交易成功处理逻辑
                                    // 微信支付订单号
                                    String transaction_id = returnMap.get("transaction_id");
                                    // 支付完成时间
                                    String time_end = returnMap.get("time_end");
                                    // 付款银行
                                    String bank_type = returnMap.get("bank_type");
                                    paymentOrderService.handleOrderPayResult(tradeNo, PayConstant.PAY_SUCCESS, transaction_id);
                                    LOGGER.info("weixin pay asynchronized notify success:end");
                                    return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
                                }
                            } else {
                                paymentOrderService.handleOrderPayResult(tradeNo, PayConstant.PAY_FAILED, null);
                                return "<xml><return_code><![CDATA[FAIL]]></return_code></xml>";
                            }
                        } else {
                            paymentOrderService.handleOrderPayResult(tradeNo, PayConstant.PAY_FAILED, null);
                            return "<xml><return_code><![CDATA[FAIL]]></return_code></xml>";
                        }
                    } else {
                        return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
                    }
                }
            } else {
                String return_msg = returnMap.get("return_msg");
                LOGGER.info("weixin pay asynchronized notify return_msg = {}", return_msg);
                return "<xml><return_code><![CDATA[FAIL]]></return_code></xml>";
            }
        } catch (Exception e) {
            LOGGER.info("weixin pay asynchronized notify:" + e.getMessage(), e);
            return "<xml><return_code><![CDATA[FAIL]]></return_code></xml>";
        }
        return "<xml><return_code><![CDATA[FAIL]]></return_code></xml>";
    }

}
