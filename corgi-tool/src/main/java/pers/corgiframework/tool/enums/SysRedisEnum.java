package pers.corgiframework.tool.enums;

/**
 * Created by syk on 2017/8/18.
 * 需要配置到数据库的redis缓存枚举
 */
public enum SysRedisEnum {

    /***
     * 短信开关 有效期30天
     */
    CORGI_SMS_SWITCH("CORGI_SMS_SWITCH", "短信发送开关：【ON】发送短信，【OFF】关闭短信", 60*60*24*30),

    /***
     * 个人VIP价格 有效期30天
     */
    CORGI_PER_VIP_AMOUNT("CORGI_PER_VIP_AMOUNT", "个人VIP价格：【90】元", 60*60*24*30);

    private String key;
    private String description;
    private int term;

    SysRedisEnum(String key, String description, int term) {
        this.key = key;
        this.description = description;
        this.term = term;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }

    public int getTerm() {
        return term;
    }

}
