package pers.corgiframework.third.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.corgiframework.dao.model.BisPrompt;
import pers.corgiframework.service.IPaymentOrderService;
import pers.corgiframework.tool.constants.BisPromptConstant;
import pers.corgiframework.tool.constants.PayConstant;
import pers.corgiframework.tool.utils.PropertiesUtil;
import pers.corgiframework.tool.utils.alipay.AliPayTradeStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 支付宝支付异步回调处理器
 * Created by syk on 2018/8/8.
 */
@RestController
@RequestMapping("/callback")
public class AliPayCallbackController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AliPayCallbackController.class);

    @Autowired
    private IPaymentOrderService paymentOrderService;

    @PostMapping(value = "/alipay/notify")
    public String notify(HttpServletRequest request) {
        // 获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }
        // 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
        // 通知业务参数
        boolean flag = false;
        try {
            flag = AlipaySignature.rsaCheckV1(params, PropertiesUtil.getString("ali_publicKey"), "utf-8", "RSA2");
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return "fail";
        }
        if (flag) {//验证成功
            String tradeNo = params.get("out_trade_no");
            String payInfo = params.get("trade_no");//支付宝交易号
            String total_amount = params.get("total_amount");
            String seller_id = params.get("seller_id");
            String app_id = params.get("app_id");
            String trade_status = params.get("trade_status");//交易状态
            BisPrompt bisPrompt = null;
            // 交易标识
            if (trade_status.equals(AliPayTradeStatus.TRADE_SUCCESS) || trade_status.equals(AliPayTradeStatus.TRADE_FINISHED)) {
                // 交易成功处理逻辑
                // 1、商户需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号
                bisPrompt = paymentOrderService.handleOrderPayResult(tradeNo, PayConstant.PAY_SUCCESS, payInfo);
            } else {
                // 交易失败
                // 1、商户需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号
                bisPrompt = paymentOrderService.handleOrderPayResult(tradeNo, PayConstant.PAY_FAILED, payInfo);
            }
            if (BisPromptConstant.SUCCESS_STATUS.equals(bisPrompt.getBisStatus())) {
                LOGGER.info("notify ---------------------------- success");
                return "success";
            }
            return "fail";
        } else {
            // 验证失败
            LOGGER.info("notify ---------------------------- fail");
            return "fail";
        }
    }

}
