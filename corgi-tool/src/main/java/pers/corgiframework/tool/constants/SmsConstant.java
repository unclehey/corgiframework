package pers.corgiframework.tool.constants;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * 短信常量
 * Created by syk on 2018/7/20.
 */
public interface SmsConstant {
    /**
     * 短信类型常量
     */
    int VERIFY_CODE = 1;
    int FORGET_PASS = 2;
    Map<Integer, String> SMS_TYPE_MAP =
            ImmutableMap.<Integer, String>builder().
                    put(VERIFY_CODE, "验证码").
                    put(FORGET_PASS, "忘记密码").
                    build();

    /**
     * 短信每日发送条数限制：5条
     */
    Integer SMS_RECORD_LIMIT = 5;

    /**
     * 短信发送间隔时间：60秒
     */
    Integer SMS_INTERVAL_TIME = 60;
}
