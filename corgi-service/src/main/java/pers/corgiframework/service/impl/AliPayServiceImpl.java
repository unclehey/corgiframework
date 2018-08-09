package pers.corgiframework.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pers.corgiframework.dao.model.BisPrompt;
import pers.corgiframework.service.IAliPayService;
import pers.corgiframework.tool.constants.BisPromptConstant;
import pers.corgiframework.tool.utils.JsonUtil;
import pers.corgiframework.tool.utils.alipay.AliPayTradeStatus;
import pers.corgiframework.tool.utils.alipay.AppAliPay;
import pers.corgiframework.tool.utils.alipay.WebAliPay;

import java.util.Map;

/**
 * Created by syk on 2018/8/7.
 */
@Service
public class AliPayServiceImpl implements IAliPayService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AliPayServiceImpl.class);

    @Override
    public Boolean aliPayNotify(Map<String, String> params) {
        return null;
    }

    @Override
    public Boolean aliBunchRefundNotify(Map<String, String> params) {
        return null;
    }

    @Override
    public Boolean settleOrderPayNotify(Map<String, String> params) {
        return null;
    }

    @Override
    public BisPrompt orderAliBarPay(String out_trade_no, String auth_code, String total_amount, String subject, String appID, String privateKey, String publicKey) {
        return null;
    }

    @Override
    public BisPrompt qrPay(String out_trade_no, String total_amount, String subject, String appID, String privateKey, String publicKey) {
        return null;
    }

    @Override
    public BisPrompt queryBarPayOrder(String out_trade_no, String appID, String privateKey, String publicKey) {
        BisPrompt bisPrompt = new BisPrompt();
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        String biz_content = "{\"out_trade_no\":\"" + out_trade_no + "\"}";
        request.setBizContent(biz_content);
        AlipayTradeQueryResponse response = null;
        //实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", appID, privateKey,
                "json", "utf-8", publicKey, "RSA2");
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if (null != response && response.isSuccess()) {
            if (response.getCode().equals("10000")) {
                if (AliPayTradeStatus.TRADE_SUCCESS.equalsIgnoreCase(response.getTradeStatus())) {//支付成功
                    String outTradeNo = response.getOutTradeNo();//订单号
                    String totalAmount = response.getTotalAmount();//支付金额
                    String tradeNo = response.getTradeNo();//支付宝交易号

                    // TODO 是否更新业务

                    bisPrompt.setBisObj(response);
                } else if ("WAIT_BUYER_PAY".equalsIgnoreCase(response.getTradeStatus())) {//等待支付
                    // 等待用户付款状态，需要轮询查询用户的付款结果(未付款)
                    bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                    bisPrompt.setBisMsg("顾客未支付，等待支付");
                } else if ("TRADE_CLOSED".equalsIgnoreCase(response.getTradeStatus())) {//已经关闭
                    bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                    bisPrompt.setBisMsg("该订单已关闭");
                    // 表示未付款关闭，或已付款的订单全额退款后关闭
                } else if ("TRADE_FINISHED".equalsIgnoreCase(response.getTradeStatus())) {
                    bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                    bisPrompt.setBisMsg("该订单订单不可退款或撤销");
                    // 此状态，订单不可退款或撤销
                }
            } else {//请求失败
                // 如果请求未成功，请重试
                bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                bisPrompt.setBisMsg("支付宝接口调用失败");
            }
        } else {
            bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
            bisPrompt.setBisMsg("支付宝接口调用出错" + JsonUtil.objectToJson(response));
            LOGGER.error("queryBarPayOrder Exception" + JsonUtil.objectToJson(response));
        }
        return bisPrompt;
    }

    @Override
    public BisPrompt cancelOrder(String out_trade_no, String appID, String privateKey, String publicKey) {
        return null;
    }

    @Override
    public BisPrompt orderRefund(String trade_no, String refund_amount, String out_request_no, String appID, String privateKey, String publicKey) {
        BisPrompt bisPrompt = new BisPrompt();
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", appID, privateKey,
                "json", "utf-8", publicKey, "RSA2");
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        request.setBizContent("{" +
//                "\"out_trade_no\":\"\"," +
                "\"trade_no\":\"" + trade_no + "\"," +
                "\"refund_amount\":" + refund_amount + "," +
                "\"refund_reason\":\"正常退款\"," +
                "\"out_request_no\":\"" + out_request_no + "\"" +
//                "\"operator_id\":\"OP001\"," +
//                "\"store_id\":\"NJ_S_001\"," +
//                "\"terminal_id\":\"NJ_T_001\"" +
                "  }");
        AlipayTradeRefundResponse response = null;
        try {
            response = alipayClient.execute(request);
            if (null != response && response.isSuccess()) {
                if (response.getCode().equals("10000")) {
                    // 退款成功
                    bisPrompt.setBisObj(response);
                    return bisPrompt;
                } else {
                    bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                    bisPrompt.setBisMsg("退款失败" + response.getCode() + "错误信息为" + response.getMsg());
                    bisPrompt.setBisObj(response);
                }
            } else {
                bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                bisPrompt.setBisObj(response);
                bisPrompt.setBisMsg("退款失败");
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
            bisPrompt.setBisMsg("退款失败");
        }
        return bisPrompt;
    }

    @Override
    public BisPrompt getSignOrder(AppAliPay appPay) {
        BisPrompt bisPrompt = new BisPrompt();
        // 实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", appPay.getApp_id(), appPay.getPrivateKey(),
                "json", "utf-8", appPay.getPublicKey(), "RSA2");
        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody(appPay.getBody());
        model.setSubject(appPay.getSubject());
        model.setOutTradeNo(appPay.getOut_trade_no());
        model.setTimeoutExpress(appPay.getTimeout_express());
        model.setTotalAmount(appPay.getTotal_amount());
        model.setProductCode("QUICK_MSECURITY_PAY");
        request.setBizModel(model);
        request.setNotifyUrl(appPay.getNotify_url());
        try {
            // 这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            bisPrompt.setBisStatus(BisPromptConstant.SUCCESS_STATUS);
            bisPrompt.setBisMsg(response.getMsg());
            bisPrompt.setBisObj(response.getBody());
        } catch (AlipayApiException e) {
            bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
            bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.FAIL_STATUS));
            LOGGER.error(e.getMessage(), e);
        }
        return bisPrompt;
    }

    @Override
    public String webAliPay(WebAliPay webPay) {
        // 实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", webPay.getApp_id(), webPay.getPrivateKey(),
                "json", "utf-8", webPay.getPublicKey(), "RSA2");
        // 服务器异步通知页面路径
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//创建API对应的request
        alipayRequest.setReturnUrl(webPay.getReturnUrl());
        alipayRequest.setNotifyUrl(webPay.getNotify_url());//在公共参数中设置回跳和通知地址
        alipayRequest.setBizContent("{" +
                "    \"out_trade_no\":\"" + webPay.getOut_trade_no() + "\"," +
                "    \"total_amount\":" + webPay.getTotal_amount() + "," +
                "    \"subject\":\"" + webPay.getSubject() + "\"," +
                "    \"seller_id\":\"" + webPay.getSeller_id() + "\"," +
                "    \"product_code\":\"QUICK_WAP_PAY\"" +
                "  }");//填充业务参数
        try {
            String form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
            return form;
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return "";
    }

}
