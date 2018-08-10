package pers.corgiframework.admin.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pers.corgiframework.dao.domain.PaymentOrder;
import pers.corgiframework.dao.model.BisPrompt;
import pers.corgiframework.service.IPaymentOrderService;
import pers.corgiframework.service.IPublicService;
import pers.corgiframework.tool.constants.BisPromptConstant;
import pers.corgiframework.tool.constants.PayConstant;
import pers.corgiframework.tool.utils.PropertiesUtil;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by syk on 2018/8/8.
 */
@Controller
@RequestMapping("/order")
public class OrderController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);
    // 获取支付参数
    private static final String PUBLIC_APP_ID = PropertiesUtil.getString("public_appid");
    private static final String PUBLIC_MCH_ID = PropertiesUtil.getString("public_mch_id");
    private static final String PUBLIC_PAY_KEY = PropertiesUtil.getString("public_pay_key");
    private static final String ALIPAY_APP_ID = PropertiesUtil.getString("aliPay_appId");
    private static final String ALIPAY_PRIVATE_KEY = PropertiesUtil.getString("aliPay_privateKey");
    private static final String ALIPAY_PUBLIC_KEY = PropertiesUtil.getString("ali_publicKey");

    @Autowired
    private IPublicService publicService;
    @Autowired
    private IPaymentOrderService paymentOrderService;

    @RequestMapping("/list")
    public String list() {
        return "/order/list";
    }

    @RequestMapping("/list/json")
    @ResponseBody
    public Object jsonList(HttpServletRequest request)  {
        // 第几页
        String page = request.getParameter("page");
        // 每页记录数
        String rows = request.getParameter("rows");
        // 组装查询条件
        Map<String, Object> filtersMap = publicService.assembleSearchFilters(request);
        // 计算总页数
        int curpage = Integer.parseInt(page);
        int pageCount = Integer.parseInt(rows);
        // 返回数据
        Map<String, Object> responseMap = new HashMap<>();
        try {
            // 订单列表
            List<PaymentOrder> list = paymentOrderService.selectListByCondition(filtersMap);
            // 总的记录条数
            int totalCount = paymentOrderService.selectListCountByCondition(filtersMap);
            responseMap.put("records", totalCount);
            int total = (int) Math.ceil((double) totalCount / pageCount);
            responseMap.put("total", total);
            responseMap.put("page", curpage);
            // 返回数据
            responseMap.put("rows", list);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return responseMap;
    }

    @RequestMapping("/refund")
    @ResponseBody
    public Object orderRefund(HttpServletRequest request) {
        String orderId = request.getParameter("orderId");
        // 退款类型：0部分退款，1全额退款
        String refundType = request.getParameter("refundType");
        BisPrompt bisPrompt = new BisPrompt();
        try {
            if (StringUtils.isNotBlank(orderId) && StringUtils.isNotBlank(refundType)) {
                PaymentOrder paymentOrder = paymentOrderService.selectByPrimaryKey(Integer.valueOf(orderId));
                if (null != paymentOrder) {
                    Integer payStatus = paymentOrder.getPayStatus();
                    Integer refundStatus = paymentOrder.getRefundStatus();
                    if (payStatus.equals(PayConstant.PAY_SUCCESS) && !refundStatus.equals(PayConstant.REFUND_ALL)) {
                        Integer type = Integer.valueOf(refundType);
                        String payChannel = paymentOrder.getPayChannel();
                        BigDecimal actualAmount = paymentOrder.getAmount();
                        BigDecimal refundAmount;
                        if (type.equals(0)) {
                            refundAmount = actualAmount.multiply(new BigDecimal("0.8"));
                        } else {
                            refundAmount = actualAmount;
                        }
                        switch (payChannel) {
                            case PayConstant.ALI_PAY:
                                bisPrompt = paymentOrderService.orderRefundByAliPay(paymentOrder.getTransactionId(), refundAmount, ALIPAY_APP_ID, ALIPAY_PRIVATE_KEY, ALIPAY_PUBLIC_KEY);
                                break;
                            case PayConstant.WEIXIN_PAY:
                                bisPrompt = paymentOrderService.orderRefundByWxPay(paymentOrder.getTradeNo(), refundAmount, PUBLIC_APP_ID, PUBLIC_MCH_ID, PUBLIC_PAY_KEY);
                                break;
                        }
                        if (BisPromptConstant.SUCCESS_STATUS.equals(bisPrompt.getBisStatus())) {
                            // TODO 具体退款逻辑
                        }
                    } else {
                        // 非法请求
                        bisPrompt.setBisStatus(BisPromptConstant.INVALID_REQUEST_STATUS);
                        bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.INVALID_REQUEST_STATUS));
                    }
                } else {
                    // 非法请求
                    bisPrompt.setBisStatus(BisPromptConstant.INVALID_REQUEST_STATUS);
                    bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.INVALID_REQUEST_STATUS));
                }
            } else {
                // 非法请求
                bisPrompt.setBisStatus(BisPromptConstant.INVALID_REQUEST_STATUS);
                bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.INVALID_REQUEST_STATUS));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return bisPrompt;
    }

}
