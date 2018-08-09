package pers.corgiframework.tool.utils.wechat.pay;

/**
 * Created by ph on 2017/7/6.
 * 微信支付基础类
 */
public class WXPay {

    //微信AppId
    private String appid;
    //商户号
    private String mch_id;
    //商户API秘钥
    private String apiSecert;
    //微信支付签名
    private String sign;
    //    随机字符串
    private String nonce_str;
    //商户安全证书地址
    private String certPath;


    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getApiSecert() {
        return apiSecert;
    }

    public void setApiSecert(String apiSecert) {
        this.apiSecert = apiSecert;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getCertPath() {
        return certPath;
    }

    public void setCertPath(String certPath) {
        this.certPath = certPath;
    }
}
