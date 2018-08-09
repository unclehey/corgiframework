package pers.corgiframework.tool.constants;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * BisPrompt常量
 * Created by syk on 2018/7/20.
 */
public interface BisPromptConstant {

    /**
     * 系统级状态码及描述
     */
    String SUCCESS_STATUS = "1000";
    String FAIL_STATUS = "1001";
    String PARAMS_EMPTY_STATUS = "1002";
    String SYSTEM_EXCEPTION_STATUS = "1003";
    String INVALID_REQUEST_STATUS = "1004";
    String TIMESTAMP_EMPTY_STATUS = "1005";
    String TIME_OUT_STATUS = "1006";
    String NEED_TOKEN_STATUS = "1007";
    String NOT_IN_IP_WHITE_STATUS = "1008";
    Map<String, String> BISPROMPT_MAP =
            ImmutableMap.<String, String>builder().
                    put(SUCCESS_STATUS, "操作成功").
                    put(FAIL_STATUS, "操作失败").
                    put(PARAMS_EMPTY_STATUS, "参数为空").
                    put(SYSTEM_EXCEPTION_STATUS, "系统异常").
                    put(INVALID_REQUEST_STATUS, "非法请求").
                    put(TIMESTAMP_EMPTY_STATUS, "时间戳为空").
                    put(TIME_OUT_STATUS, "请求超时").
                    put(NEED_TOKEN_STATUS, "请求参数缺少token").
                    put(NOT_IN_IP_WHITE_STATUS, "IP未配置白名单").
                    build();

    /**
     * 业务级状态码及描述
     */
    String TOKEN_EMPTY_STATUS = "2001";
    String TOKEN_INVALID_STATUS = "2002";
    String USER_NOT_EXIST_STATUS = "2003";
    String VERIFY_CODE_LENGTH_STATUS = "2004";
    String VERIFY_CODE_TIMEOUT_STATUS = "2005";
    String VERIFY_CODE_WRONG_STATUS = "2006";
    String VERIFYCODE_EMPTY_STATUS = "2007";
    String PHONE_EMPTY_STATUS = "2008";
    String PHONE_INVALID_STATUS = "2009";
    String SMS_DAY_LIMIT_STATUS = "2010";
    String SMS_INTERVAL_TIME_LESS_STATUS = "2011";
    String START_END_DATE_STATUS = "2012";
    Map<String, String> BUSINESS_MAP =
            ImmutableMap.<String, String>builder().
                    put(TOKEN_EMPTY_STATUS, "token为空").
                    put(TOKEN_INVALID_STATUS, "登录信息已失效").
                    put(USER_NOT_EXIST_STATUS, "用户不存在").
                    put(VERIFY_CODE_LENGTH_STATUS, "验证码必须是6位").
                    put(VERIFY_CODE_TIMEOUT_STATUS, "验证码超时").
                    put(VERIFY_CODE_WRONG_STATUS, "验证码错误").
                    put(VERIFYCODE_EMPTY_STATUS, "请输入验证码").
                    put(PHONE_EMPTY_STATUS, "手机号为空").
                    put(PHONE_INVALID_STATUS, "手机号无效").
                    put(SMS_DAY_LIMIT_STATUS, "短信每日发送条数超限").
                    put(SMS_INTERVAL_TIME_LESS_STATUS, "短信间隔时间小于60秒").
                    put(START_END_DATE_STATUS, "开始日期不能晚于结束日期").
                    build();



    static void main(String[] args) {
        /*ImmutableSet<String> imSet = ImmutableSet.of("peida", "jerry", "harry", "lisa");
        System.out.println("imSet：" + imSet);
        ImmutableList<String> imlist = ImmutableList.copyOf(imSet);
        System.out.println("imlist：" + imlist);
        ImmutableSortedSet<String> imSortSet = ImmutableSortedSet.copyOf(imSet);
        System.out.println("imSortSet：" + imSortSet);*/

    }
}
