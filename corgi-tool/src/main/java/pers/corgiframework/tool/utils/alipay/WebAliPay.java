package pers.corgiframework.tool.utils.alipay;

/**
 * web端支付实体
 * Created by syk on 2018/8/7.
 */
public class WebAliPay {
    /**
     *私钥
     */
    private String privateKey;
    /**
     * 公钥
     */
    private String publicKey;
    /**
     * 支付宝分配给开发者的应用ID
     */
    private String app_id;

    /**
     * 接口名称
     */
    private String method;

    /**
     * 	仅支持JSON
     */
    private String format;

    /**
     * 请求使用的编码格式，如utf-8,gbk,gb2312等
     */
    private String charset;

    /**
     * 商户生成签名字符串所使用的签名算法类型，目前支持RSA
     */
    private String sign_type;

    /**
     * 商户请求参数的签名串
     */
    private String sign;

    /**
     * 发送请求的时间，格式"yyyy-MM-dd HH:mm:ss"
     */
    private  String timestamp;

    /**
     * 调用的接口版本，固定为：1.0
     */
    private String version;

    /**
     * 支付宝服务器主动通知商户服务器里指定的页面http/https路径。建议商户使用https
     */
    private  String notify_url;

    /**
     * 业务请求参数的集合，最大长度不限，除公共参数外所有请求参数都必须放在这个参数中传递，具体参照各产品快速接入文档
     */
    private String biz_content;

    // 订单号
    private String out_trade_no;

    // 订单标题
    private String subject;

    //实际收款账号，一般填写商户PID即可
    private String seller_id;

    // 订单金额
    private String total_amount;

    //回调地址
    private String returnUrl;


    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getBiz_content() {
        return biz_content;
    }

    public void setBiz_content(String biz_content) {
        this.biz_content = biz_content;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }
}
