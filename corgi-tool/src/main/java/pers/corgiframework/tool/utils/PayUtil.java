package pers.corgiframework.tool.utils;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.corgiframework.tool.constants.PayConstant;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/**
 * 支付工具类
 * Created by syk on 2018/7/23.
 */
public class PayUtil {
    private final static Logger LOGGER = LoggerFactory.getLogger(PayUtil.class);

    /**
     * 将集合params内非空参数值的参数按照参数名ASCII码从小到大排序（字典序）
     * 使用URL键值对的格式（即key1=value1&key2=value2…）拼接成字符串
     * @param params
     * @return
     */
    public static String assembleNoSignParamsBySort(Map<String, String> params) {
        StringBuffer sb = new StringBuffer();
        try {
            SortedMap<String, String> sortParams = Maps.newTreeMap();
            sortParams.putAll(params);
            Set entrySet = sortParams.entrySet();
            Iterator iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
                String key = entry.getKey();
                String value = entry.getValue();
                if (StringUtils.isNotBlank(value)) {
                    sb.append(key + "=" + value + "&");
                }
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return sb.toString();
    }

    /**
     * 拼装微信支付统一下单请求参数
     * @param appid
     * @param mch_id
     * @param payKey
     * @param out_trade_no
     * @param body
     * @param amount
     * @param terminalIp
     * @param tradeType
     * @param openId 【公众号支付需要】
     * @param notify_url
     * @param sceneInfo【H5支付需要】
     * @return
     */
    public static String assembleWxPayParams(String appid, String mch_id, String payKey, String out_trade_no, String body, String amount, String terminalIp, String tradeType, String openId, String notify_url, String sceneInfo) {
        // 生成16位随机字符串
        String nonce_str = StringUtil.createNoncestr();
        // 交易金额 转化成分
        String total_fee = String.valueOf(new BigDecimal(amount).multiply(new BigDecimal("100")).intValue());
        // 拼接生成预支付交易单参数
        Map<String, String> params = Maps.newHashMap();
        params.put("appid", appid);
        params.put("mch_id", mch_id);
        params.put("nonce_str", nonce_str);
        if (tradeType.equals(PayConstant.TRADE_TYPE_PUBLIC)) {
            params.put("openid", openId);
        }
        params.put("body", body);
        params.put("out_trade_no", out_trade_no);
        params.put("total_fee", total_fee);
        params.put("spbill_create_ip", terminalIp);
        params.put("notify_url", notify_url);
        params.put("trade_type", tradeType);
        if (tradeType.equals(PayConstant.TRADE_TYPE_H5)) {
            params.put("scene_info", sceneInfo);
        }
        params.put("input_charset", "UTF-8");
        // 请求参数按字典排序
        String sortParams = assembleNoSignParamsBySort(params);
        // 签名
        String stringSignTemp = sortParams + "&key=" + payKey;
        String sign = SignUtil.md5(stringSignTemp).toUpperCase();
        // 添加签名
        params.put("sign", sign);
        return XmlUtil.mapToXml(params);
    }

    /**
     * 拼装微信退款请求参数
     * @param mchId
     * @param appId
     * @param payKey
     * @param tradeNo
     * @param refundNo
     * @param totalAmount
     * @param refundAmount
     * @return
     */
    public static String assembleWxRefundParams(String mchId, String appId, String payKey, String tradeNo, String refundNo, BigDecimal totalAmount, BigDecimal refundAmount) {
        // 生成16位随机字符串
        String nonce_str = StringUtil.createNoncestr();
        // 交易金额 转化成分
        String total_fee = String.valueOf(totalAmount.multiply(new BigDecimal("100")).intValue());
        String refund_fee = String.valueOf(refundAmount.multiply(new BigDecimal("100")).intValue());
        // 拼接生成预支付交易单参数
        Map<String, String> params = Maps.newHashMap();
        params.put("appid", appId);
        params.put("mch_id", mchId);
        params.put("nonce_str", nonce_str);
        params.put("out_trade_no", tradeNo);
        params.put("out_refund_no", refundNo);
        params.put("total_fee", total_fee);
        params.put("refund_fee", refund_fee);
        // 请求参数按字典排序
        String sortParams = assembleNoSignParamsBySort(params);
        // 签名
        String stringSignTemp = sortParams + "&key=" + payKey;
        String sign = SignUtil.md5(stringSignTemp).toUpperCase();
        params.put("sign", sign);
        return XmlUtil.mapToXml(params);
    }

    /**
     * 拼接APP支付所需参数
     * @param appid
     * @param mch_id
     * @param pay_key
     * @param prepay_id
     * @return
     */
    public static Map<String, String> generateWxAppParams(String appid, String mch_id, String pay_key, String prepay_id) {
        // 生成16位随机字符串
        String noncestr = StringUtil.createNoncestr();
        // 生成时间戳
        String timestamp = StringUtil.createTimestamp();
        // 拼接生成预支付交易单参数
        Map<String, String> params = Maps.newHashMap();
        params.put("appid", appid);
        params.put("partnerid", mch_id);
        params.put("prepayid", prepay_id);
        params.put("package", "Sign=WXPay");
        params.put("noncestr", noncestr);
        params.put("timestamp", timestamp);
        // 得到排序后的参数
        String sortParams = assembleNoSignParamsBySort(params);
        // 签名
        String stringSignTemp = sortParams + "&key=" + pay_key;
        String sign = SignUtil.md5(stringSignTemp).toUpperCase();
        params.put("sign", sign);
        return params;
    }

    /**
     * 拼接公众号支付所需参数
     * @param appid
     * @param pay_key
     * @param prepay_id
     * @return
     */
    public static Map<String, String> generateWxPublicParams(String appid, String pay_key, String prepay_id) {
        // 生成16位随机字符串
        String noncestr = StringUtil.createNoncestr();
        // 生成时间戳
        String timestamp = StringUtil.createTimestamp();
        //签名所需参数
        Map<String, String> signParams = Maps.newHashMap();
        signParams.put("appId", appid);
        signParams.put("timeStamp", timestamp);
        signParams.put("nonceStr", noncestr);
        signParams.put("package", "prepay_id=" + prepay_id);
        signParams.put("signType", "MD5");
        // 得到排序后的参数
        String sortParams = assembleNoSignParamsBySort(signParams);
        // 签名
        String stringSignTemp = sortParams + "&key=" + pay_key;
        String sign = SignUtil.md5(stringSignTemp).toUpperCase();
        signParams.put("paySign", sign);
        return signParams;
    }

}
