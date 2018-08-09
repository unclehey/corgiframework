package pers.corgiframework.service.impl;

import com.alipay.api.response.AlipayTradeRefundResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pers.corgiframework.dao.domain.PaymentOrder;
import pers.corgiframework.dao.domain.PaymentOrderExample;
import pers.corgiframework.dao.domain.SysPrice;
import pers.corgiframework.dao.mapper.BaseMapper;
import pers.corgiframework.dao.mapper.PaymentOrderMapper;
import pers.corgiframework.dao.model.BisPrompt;
import pers.corgiframework.service.IAliPayService;
import pers.corgiframework.service.IPaymentOrderService;
import pers.corgiframework.service.IRedisService;
import pers.corgiframework.service.IWeChatService;
import pers.corgiframework.tool.constants.BisPromptConstant;
import pers.corgiframework.tool.constants.PayConstant;
import pers.corgiframework.tool.utils.DateTimeUtil;
import pers.corgiframework.tool.utils.alipay.AppAliPay;
import pers.corgiframework.util.SysExceptionUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by syk on 2018/8/6.
 */
@Service
public class PaymentOrderServiceImpl extends BaseServiceImpl<PaymentOrder> implements IPaymentOrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentOrderServiceImpl.class);
    @Autowired
    private PaymentOrderMapper paymentOrderMapper;
    @Autowired
    private IRedisService redisService;
    @Autowired
    private IAliPayService aliPayService;
    @Autowired
    private IWeChatService weChatService;

    @Override
    protected BaseMapper<PaymentOrder> getMapper() {
        return paymentOrderMapper;
    }

    @Override
    public PaymentOrder selectByTradeNo(String tradeNo) {
        PaymentOrder paymentOrder = null;
        PaymentOrderExample example = new PaymentOrderExample();
        example.createCriteria().andTradeNoEqualTo(tradeNo);
        List<PaymentOrder> list = paymentOrderMapper.selectByExample(example);
        if(!list.isEmpty()) {
            paymentOrder = list.get(0);
        }
        return paymentOrder;
    }

    @Override
    public BisPrompt orderPayByAliPay(Integer userId, String mobile, String orderId, String productId, Integer orderType, Integer orderCount, String body, SysPrice sysPrice, String paySource, String appId, String privateKey, String publicKey, String notifyUrl) {
        BisPrompt bisPrompt = new BisPrompt();
        try {
            // 拼装支付宝预下单数据
            AppAliPay appPay = new AppAliPay();
            if (StringUtils.isBlank(orderId)) {
                //【首次支付，产生订单逻辑】
                BigDecimal totalAmount = sysPrice.getPrice();
                if (null != orderCount) {
                    totalAmount = new BigDecimal(orderCount).multiply(totalAmount).setScale(2);
                }
                // 拼装订单数据
                appPay.setTotal_amount(String.valueOf(totalAmount));
                // 生成订单号
                String out_trade_no = redisService.generateTradeNo();
                appPay.setApp_id(appId);
                appPay.setPrivateKey(privateKey);
                appPay.setPublicKey(publicKey);
                appPay.setBody(body);
                appPay.setSubject(body);
                appPay.setTimeout_express("30m");
                appPay.setOut_trade_no(out_trade_no);
                appPay.setNotify_url(notifyUrl);
                bisPrompt = aliPayService.getSignOrder(appPay);
                if (BisPromptConstant.SUCCESS_STATUS.equals(bisPrompt.getBisStatus())) {
                    int orderInsCount = generatePaymentOrder(userId, mobile, productId, orderType, out_trade_no, orderCount, body, totalAmount, PayConstant.ALI_PAY, paySource, PayConstant.TRADE_TYPE_APP, null, sysPrice);
                    if (orderInsCount > 0) {
                        return bisPrompt;
                    }
                    bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                    bisPrompt.setBisMsg("操作订单表异常");
                    return bisPrompt;
                }
            } else {
                //【支付已存在订单，继续支付逻辑】
                PaymentOrder paymentOrder = paymentOrderMapper.selectByPrimaryKey(Integer.valueOf(orderId));
                if (null == paymentOrder){
                    bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                    bisPrompt.setBisMsg("订单不存在");
                    return bisPrompt;
                }
                if (paymentOrder.getPayStatus().equals(PayConstant.PAY_SUCCESS)) {
                    bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                    bisPrompt.setBisMsg("订单已支付");
                    return bisPrompt;
                }
                // 拼装订单数据
                appPay.setTotal_amount(String.valueOf(paymentOrder.getAmount()));
                appPay.setApp_id(appId);
                appPay.setPrivateKey(privateKey);
                appPay.setPublicKey(publicKey);
                appPay.setBody(paymentOrder.getBody());
                appPay.setSubject(paymentOrder.getBody());
                appPay.setTimeout_express("30m");
                appPay.setOut_trade_no(paymentOrder.getTradeNo());
                appPay.setNotify_url(notifyUrl);
                bisPrompt = aliPayService.getSignOrder(appPay);
                if (BisPromptConstant.SUCCESS_STATUS.equals(bisPrompt.getBisStatus())) {
                    paymentOrder.setPayChannel(PayConstant.ALI_PAY);
                    int orderInsCount = paymentOrderMapper.updateByPrimaryKeySelective(paymentOrder);
                    if (orderInsCount > 0) {
                        return bisPrompt;
                    }
                    bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                    bisPrompt.setBisMsg("操作订单表异常");
                    return bisPrompt;
                }
            }
            return bisPrompt;
        } catch (Exception e) {
            LOGGER.error("orderPayByAliPay Exception", e);
            bisPrompt.setBisStatus(BisPromptConstant.SYSTEM_EXCEPTION_STATUS);
            bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.SYSTEM_EXCEPTION_STATUS));
        }
        return bisPrompt;
    }

    @Override
    public BisPrompt orderRefundByAliPay(String transactionId, BigDecimal refundAmount, String appId, String privateKey, String publicKey) {
        BisPrompt bisPrompt = new BisPrompt();
        try {
            PaymentOrder paymentOrder = null;
            PaymentOrderExample example = new PaymentOrderExample();
            example.createCriteria().andTransactionIdEqualTo(transactionId);
            List<PaymentOrder> list = paymentOrderMapper.selectByExample(example);
            if (CollectionUtils.isEmpty(list)) {
                bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                bisPrompt.setBisMsg("订单不存在！");
                return bisPrompt;
            } else {
                paymentOrder = list.get(0);
            }
            // 调用支付宝退款接口进行退款
            // 标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传。
            String out_request_no  = redisService.generateTradeNo();
            BisPrompt refundBIs = aliPayService.orderRefund(transactionId, refundAmount.toString(), out_request_no, appId, privateKey, publicKey);
            if(BisPromptConstant.SUCCESS_STATUS.equals(refundBIs.getBisStatus())){
                // 支付宝退款成功，处理paymentOrder表
                if (refundAmount.compareTo(paymentOrder.getAmount()) < 0) {
                    // 部分退款
                    paymentOrder.setRefundStatus(PayConstant.REFUND_PART);
                } else {
                    // 全额退款
                    paymentOrder.setRefundStatus(PayConstant.REFUND_ALL);
                }
                paymentOrder.setRefundAmount(refundAmount);
                paymentOrder.setRefundRequestNo(out_request_no);
                paymentOrder.setPayStatus(PayConstant.REFUND_SUCCESS);
                paymentOrder.setUpdateTime(DateTimeUtil.getNowDateTime());
                int updCount = paymentOrderMapper.updateByPrimaryKeySelective(paymentOrder);
                if(updCount != 1){
                    bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                    bisPrompt.setBisMsg("支付宝退款成功，更新paymentOrder表失败");
                    return bisPrompt;
                }
            } else {
                LOGGER.info("支付宝退款失败");
                bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                AlipayTradeRefundResponse response = (AlipayTradeRefundResponse) refundBIs.getBisObj();
                bisPrompt.setBisMsg(response.getSubMsg());
                return bisPrompt;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            bisPrompt.setBisStatus(BisPromptConstant.SYSTEM_EXCEPTION_STATUS);
            bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.SYSTEM_EXCEPTION_STATUS));
        }
        return bisPrompt;
    }

    @Override
    public BisPrompt orderPayByWxPay(Integer userId, String mobile, String orderId, String productId, Integer orderType, Integer orderCount, String terminalIp, String body, SysPrice sysPrice, String paySource, String appId, String mchId, String payKey, String notifyUrl, String openId, String tradeType, String sceneInfo) {
        BisPrompt bisPrompt = new BisPrompt();
        BigDecimal totalAmount;
        try {
            if (StringUtils.isBlank(orderId)) {
                //【首次支付，产生订单逻辑】
                totalAmount = sysPrice.getPrice();
                if (null != orderCount) {
                    totalAmount = new BigDecimal(orderCount).multiply(totalAmount).setScale(2);
                }
                // 生成订单号
                String tradeNo = redisService.generateTradeNo();
                // 获取支付参数
                bisPrompt = weChatService.wxPay(appId, mchId, payKey, tradeNo, body, totalAmount.toString(), terminalIp, tradeType, openId, notifyUrl, sceneInfo);
                if (BisPromptConstant.SUCCESS_STATUS.equals(bisPrompt.getBisStatus())) {
                    int orderInsCount = generatePaymentOrder(userId, mobile, productId, orderType, tradeNo, orderCount, body, totalAmount, PayConstant.WEIXIN_PAY, paySource, tradeType, openId, sysPrice);
                    if (orderInsCount > 0) {
                        return bisPrompt;
                    }
                    bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                    bisPrompt.setBisMsg("操作订单表异常");
                    return bisPrompt;
                }
            } else {
                //【支付已存在订单，继续支付逻辑】
                PaymentOrder paymentOrder = paymentOrderMapper.selectByPrimaryKey(Integer.valueOf(orderId));
                if (null == paymentOrder){
                    bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                    bisPrompt.setBisMsg("订单不存在");
                    return bisPrompt;
                }
                if (paymentOrder.getPayStatus().equals(PayConstant.PAY_SUCCESS)) {
                    bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                    bisPrompt.setBisMsg("订单已支付");
                    return bisPrompt;
                }
                totalAmount = paymentOrder.getAmount();
                // 生成订单号
                String tradeNo = redisService.generateTradeNo();
                bisPrompt = weChatService.wxPay(appId, mchId, payKey, tradeNo, body, totalAmount.toString(), terminalIp, tradeType, openId, notifyUrl, sceneInfo);
                if (BisPromptConstant.SUCCESS_STATUS.equals(bisPrompt.getBisStatus())) {
                    // 更新订单号
                    paymentOrder.setTradeNo(tradeNo);
                    paymentOrder.setPayChannel(PayConstant.WEIXIN_PAY);
                    paymentOrder.setTradeType(tradeType);
                    int orderInsCount = paymentOrderMapper.updateByPrimaryKeySelective(paymentOrder);
                    if (orderInsCount > 0) {
                        return bisPrompt;
                    }
                    bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                    bisPrompt.setBisMsg("操作订单表异常");
                    return bisPrompt;
                }
            }
            return bisPrompt;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            bisPrompt.setBisStatus(BisPromptConstant.SYSTEM_EXCEPTION_STATUS);
            bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.SYSTEM_EXCEPTION_STATUS));
        }
        return bisPrompt;
    }

    @Override
    public BisPrompt orderRefundByWxPay(String tradeNo, BigDecimal refundAmount, String appId, String mchId, String payKey) {
        BisPrompt bisPrompt = new BisPrompt();
        try{
            PaymentOrder paymentOrder = this.selectByTradeNo(tradeNo);
            if (paymentOrder == null) {
                bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                bisPrompt.setBisMsg("订单不存在！");
                return bisPrompt;
            }
            // 调用微信退款接口进行退款
            BigDecimal totalAmount = paymentOrder.getAmount();
            // 退款单号
            String refundNo = redisService.generateTradeNo();
            bisPrompt = weChatService.refundOrder(mchId, appId, payKey, tradeNo, refundNo, totalAmount, refundAmount);
            if (BisPromptConstant.SUCCESS_STATUS.equals(bisPrompt.getBisStatus())) {
                // 微信退款成功，处理paymentOrder表
                if (refundAmount.compareTo(totalAmount) < 0) {
                    // 部分退款
                    paymentOrder.setRefundStatus(PayConstant.REFUND_PART);
                } else {
                    // 全额退款
                    paymentOrder.setRefundStatus(PayConstant.REFUND_ALL);
                }
                paymentOrder.setRefundAmount(refundAmount);
                paymentOrder.setRefundRequestNo(refundNo);
                paymentOrder.setPayStatus(PayConstant.REFUND_SUCCESS);
                paymentOrder.setUpdateTime(DateTimeUtil.getNowDateTime());
                int updCount = paymentOrderMapper.updateByPrimaryKeySelective(paymentOrder);
                if(updCount != 1){
                    bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                    bisPrompt.setBisMsg("微信退款成功，更新paymentOrder表失败");
                    return bisPrompt;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            bisPrompt.setBisStatus(BisPromptConstant.SYSTEM_EXCEPTION_STATUS);
            bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.SYSTEM_EXCEPTION_STATUS));
        }
        return bisPrompt;
    }

    /**
     * 生成支付订单
     * @param userId
     * @param mobile
     * @param productId
     * @param orderType
     * @param tradeNo
     * @param orderCount
     * @param body
     * @param amount
     * @param payChannel
     * @param paySource
     * @param tradeType
     * @param openId
     * @param sysPrice
     * @return
     */
    private int generatePaymentOrder(Integer userId, String mobile, String productId, Integer orderType, String tradeNo, Integer orderCount, String body, BigDecimal amount, String payChannel, String paySource, String tradeType, String openId, SysPrice sysPrice) {
        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setUserId(userId);
        paymentOrder.setMobile(mobile);
        paymentOrder.setOrderType(orderType);
        paymentOrder.setTradeNo(tradeNo);
        paymentOrder.setAmount(amount);
        paymentOrder.setOrderCount(orderCount);
        paymentOrder.setProductId(productId);
        paymentOrder.setBody(body);
        paymentOrder.setPayChannel(payChannel);
        paymentOrder.setTradeType(tradeType);
        paymentOrder.setPaySource(paySource);
        paymentOrder.setPayStatus(PayConstant.NO_PAY);
        paymentOrder.setOpenId(openId);
        // 获取当前时间
        LocalDateTime now = DateTimeUtil.getNowDateTime();
        paymentOrder.setCreateTime(now);
        paymentOrder.setUpdateTime(now);
        if (null != sysPrice) {
            LocalDate nowDate = DateTimeUtil.getNowDate();
            paymentOrder.setStartDate(nowDate);
            LocalDate endDate = DateTimeUtil.plus(nowDate, sysPrice.getDuration(), DateTimeUtil.MONTHS);
            paymentOrder.setEndDate(endDate);
        }
        return paymentOrderMapper.insertSelective(paymentOrder);
    }

    @Override
    public BisPrompt handleOrderPayResult(String tradeNo, Integer payStatus, String transactionId) {
        BisPrompt bisPrompt = new BisPrompt();
        try {
            PaymentOrder paymentOrder = selectByTradeNo(tradeNo);
            if (null == paymentOrder) {
                bisPrompt.setBisStatus(BisPromptConstant.SUCCESS_STATUS);
                bisPrompt.setBisMsg("订单不存在");
                return bisPrompt;
            }
            if (PayConstant.NO_PAY.equals(paymentOrder.getPayStatus()) || PayConstant.PAY_FAILED.equals(paymentOrder.getPayStatus())) {
                PaymentOrder updOrder = new PaymentOrder();
                updOrder.setId(paymentOrder.getId());
                updOrder.setPayStatus(payStatus);
                updOrder.setTransactionId(transactionId);
                // 获取当前时间
                LocalDateTime now = DateTimeUtil.getNowDateTime();
                updOrder.setSuccessTime(now);
                updOrder.setUpdateTime(now);
                int updCount = paymentOrderMapper.updateByPrimaryKeySelective(updOrder);
                if (updCount > 0) {
                    if (payStatus.equals(PayConstant.PAY_SUCCESS)) {
                        Integer orderType = paymentOrder.getOrderType();
                        Integer userId = paymentOrder.getUserId();
                        switch (orderType) {
                            case 1:
                                // TODO 具体处理逻辑
                                // bisPrompt = xxx
                                break;
                        }
                    }
                } else {
                    // 操作失败
                    bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                    bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.FAIL_STATUS));
                }
            }
        } catch (Exception e) {
            // 系统异常
            return SysExceptionUtil.response(e);
        }
        return bisPrompt;
    }
}
