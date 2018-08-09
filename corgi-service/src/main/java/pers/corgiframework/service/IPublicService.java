package pers.corgiframework.service;

import pers.corgiframework.dao.model.BisPrompt;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by syk on 2017/8/30.
 */
public interface IPublicService {

    /**
     * 组装App端查询条件
     * @param pageNo
     * @param pageCount
     * @return
     */
    Map<String, Object> assembleSearchConditions(String pageNo, int pageCount);

    /**
     * 组装后台查询条件
     * @param request
     * @return
     */
    Map<String, Object> assembleSearchFilters(HttpServletRequest request);

    /**
     * 校验用户输入的验证码与redis中的验证码是否一致
     * @param mobile
     * @param verifyCode
     * @param bisPrompt
     * @return 一致返回false，不一致返回true
     */
    boolean checkVerifyCode(String mobile, String verifyCode, BisPrompt bisPrompt);

    /**
     * 获取短信验证码
     * @param mobile
     * @param smsType
     * @return
     */
    BisPrompt getVerifyCode(String mobile, String smsType);

    /**
     * 发送注册邮件
     * @param to
     * @param title
     * @param content
     */
    void sendRegisterMail(String to, String title, String content);

    /**
     * 发送找回密码邮件
     * @param to
     * @param title
     * @param content
     */
    void sendRetrievePwdMail(String to, String title, String content);

    /**
     * 发送带附件的邮件
     * @param to
     * @param title
     * @param filePath
     */
    void sendAttachmentMail(String to, String title, String filePath);

}
