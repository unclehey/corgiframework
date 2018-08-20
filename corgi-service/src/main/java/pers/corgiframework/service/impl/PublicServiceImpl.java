package pers.corgiframework.service.impl;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import pers.corgiframework.dao.domain.SmsRecord;
import pers.corgiframework.dao.model.BisPrompt;
import pers.corgiframework.service.IPublicService;
import pers.corgiframework.service.IRedisService;
import pers.corgiframework.service.ISmsService;
import pers.corgiframework.tool.constants.BisPromptConstant;
import pers.corgiframework.tool.constants.SmsConstant;
import pers.corgiframework.tool.utils.DateTimeUtil;
import pers.corgiframework.tool.utils.JsonUtil;
import pers.corgiframework.tool.utils.PropertiesUtil;
import pers.corgiframework.tool.utils.StringUtil;
import pers.corgiframework.websocket.MyHandler;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by syk on 2017/8/30.
 */
@Service
public class PublicServiceImpl implements IPublicService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PublicServiceImpl.class);

    static {
        System.setProperty("mail.mime.charset", "UTF-8");
    }
    // 从配置文件读取参数
    private static final String MAILNAME = PropertiesUtil.getString("mail_name");
    private static final String MAILPWD = PropertiesUtil.getString("mail_pwd");
    private static final String MAILFROM = PropertiesUtil.getString("mail_from");
    private static final String MAILHOST = PropertiesUtil.getString("mail_host");
    private static final String MAILPROTOCOL = PropertiesUtil.getString("mail_protocol");
    private static final String MAILAUTH = PropertiesUtil.getString("mail_auth");

    @Autowired
    private IRedisService redisService;
    @Autowired
    private ISmsService smsService;
    @Autowired
    private MyHandler myHandler;

    @Override
    public Map<String, Object> assembleSearchConditions(String pageNo, int pageCount) {
        Map<String, Object> filtersMap = Maps.newHashMap();
        int curpage = 1;
        if (StringUtils.isNotBlank(pageNo) && Integer.valueOf(pageNo) > 0) {
            curpage = Integer.parseInt(pageNo);
        }
        // 从第几条开始查询
        filtersMap.put("currentPage", (curpage - 1) * pageCount);
        // 要查询多少条数据
        filtersMap.put("pageCount", pageCount);
        return filtersMap;
    }

    @Override
    public Map<String, Object> assembleSearchFilters(HttpServletRequest request) {
        // 第几页
        String page = request.getParameter("page");
        // 每页记录数
        String rows = request.getParameter("rows");
        // 查询条件
        String filters = request.getParameter("filters");
        List<Map<String, String>> requestList = null;
        if (StringUtils.isNotBlank(filters)) {
            requestList = JsonUtil.parseJsonToListForMgr(filters);
        }
        Map<String, Object> filtersMap = Maps.newHashMap();
        // 默认每页显示20条数据
        int curpage = Integer.parseInt(page);
        int pageCount = Integer.parseInt(rows);
        // 从第几条开始查询
        filtersMap.put("currentPage", (curpage - 1) * pageCount);
        // 要查询多少条数据
        filtersMap.put("pageCount", pageCount);
        if (null != requestList && requestList.size() > 0) {
            for (int i = 0; i < requestList.size(); i++) {
                Map<String, String> map = requestList.get(i);
                Iterator<Map.Entry<String, String>> entries = map.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry<String, String> entry = entries.next();
                    filtersMap.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return filtersMap;
    }

    @Override
    public boolean checkVerifyCode(String mobile, String verifyCode, BisPrompt bisPrompt) {
        // 读取redis中的验证码
        String msgCode = redisService.getString("verify:" + mobile);
        // 验证验证码是否超时
        if (StringUtils.isBlank(msgCode)) {
            // 已超时
            bisPrompt.setBisStatus(BisPromptConstant.VERIFY_CODE_TIMEOUT_STATUS);
            bisPrompt.setBisMsg(BisPromptConstant.BUSINESS_MAP.get(BisPromptConstant.VERIFY_CODE_TIMEOUT_STATUS));
            return true;
        } else {
            // 验证验证码
            if (!verifyCode.equals(msgCode)) {
                // 验证码错误
                bisPrompt.setBisStatus(BisPromptConstant.VERIFY_CODE_WRONG_STATUS);
                bisPrompt.setBisMsg(BisPromptConstant.BUSINESS_MAP.get(BisPromptConstant.VERIFY_CODE_WRONG_STATUS));
                return true;
            }
        }
        return false;
    }

    @Override
    public BisPrompt getVerifyCode(String mobile, String smsType) {
        BisPrompt bisPrompt = new BisPrompt();
        // 检验参数是否为空
        if (StringUtils.isBlank(mobile) || StringUtils.isBlank(smsType)) {
            bisPrompt.setBisStatus(BisPromptConstant.PHONE_EMPTY_STATUS);
            bisPrompt.setBisMsg(BisPromptConstant.BUSINESS_MAP.get(BisPromptConstant.PHONE_EMPTY_STATUS));
            return bisPrompt;
        }
        // 正则检验手机号
        mobile = StringUtil.parsePhoneParam(mobile);
        if (mobile == null) {
            bisPrompt.setBisStatus(BisPromptConstant.PHONE_INVALID_STATUS);
            bisPrompt.setBisMsg(BisPromptConstant.BUSINESS_MAP.get(BisPromptConstant.PHONE_INVALID_STATUS));
            return bisPrompt;
        }
        List<SmsRecord> recordList = smsService.getSmsRecordsLimitByMobile(mobile);
        if (null != recordList && recordList.size() > 0) {
            // 一个手机号一天最多发送5条验证码类短信
            if (recordList.size() >= SmsConstant.SMS_RECORD_LIMIT) {
                bisPrompt.setBisStatus(BisPromptConstant.SMS_DAY_LIMIT_STATUS);
                bisPrompt.setBisMsg(BisPromptConstant.BUSINESS_MAP.get(BisPromptConstant.SMS_DAY_LIMIT_STATUS));
                return bisPrompt;
            } else {
                // 取最新一条短信发送时间
                LocalDateTime createTime = recordList.get(0).getCreateTime();
                // 计算与当前时间秒差
                long seconds = DateTimeUtil.getSeconds(createTime, DateTimeUtil.getNowDateTime());
                // 短信间隔60s
                if (seconds <= SmsConstant.SMS_INTERVAL_TIME) {
                    bisPrompt.setBisStatus(BisPromptConstant.SMS_INTERVAL_TIME_LESS_STATUS);
                    bisPrompt.setBisMsg(BisPromptConstant.BUSINESS_MAP.get(BisPromptConstant.SMS_INTERVAL_TIME_LESS_STATUS));
                    return bisPrompt;
                }
            }
        }
        // 随机出来的验证码
        String verifyCode = StringUtil.generateRandomNumber(6);
        // 调用短信发送接口
        smsService.sendSms(mobile, Integer.valueOf(smsType), verifyCode);
        // 短信发送成功，将验证码放入redis
        String verifyKey = "verify:" + mobile;
        redisService.setString(verifyKey, 300, verifyCode);
        return bisPrompt;
    }


    @Override
    @Async
    public void sendRegisterMail(String to, String title, String content) {
        // 调用邮件发送服务
        sendMail(to, title, content);
    }

    @Override
    @Async
    public void sendRetrievePwdMail(String to, String title, String content) {
        // 调用邮件发送服务
        sendMail(to, title, content);
    }

    @Override
    @Async
    public void sendAttachmentMail(String to, String title, String filePath) {
        sendMail2(to, title, filePath);
    }

    /**
     * 发送内容
     * @param mailTo
     * @param mailTitle
     * @param mailContent
     */
    public void sendMail(String mailTo, String mailTitle, String mailContent) {
        Properties props = null;
        Transport transport = null;
        try {
            props = new Properties();
            // 使用smtp：简单邮件传输协议
            props.put("mail.smtp.host", MAILHOST);  // 存储发送邮件服务器的信息
            props.put("mail.smtp.auth", MAILAUTH);    // 同时通过验证
            Session session = Session.getInstance(props);   // 根据属性新建一个邮件会话
            MimeMessage message = new MimeMessage(session); // 由邮件会话新建一个消息对象
            message.setFrom(MAILFROM); // 设置发件人的地址
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(mailTo));// 设置收件人,并设置其接收类型为TO
            message.setSubject(mailTitle); // 设置标题
            message.setContent(mailContent, "text/html;charset=utf-8"); // 发送HTML邮件，内容样式比较丰富
            message.setSentDate(new Date()); // 设置发信时间
            message.saveChanges(); // 存储邮件信息
            // 发送邮件
            transport = session.getTransport(MAILPROTOCOL);
            transport.connect(MAILNAME, MAILPWD);
            transport.sendMessage(message, message.getAllRecipients()); // 发送邮件,其中第二个参数是所有已设好的收件人地址
            transport.close();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (null != transport) {
                try {
                    transport.close();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 发送附件
     * @param mailTo
     * @param mailTitle
     * @param filePath
     */
    public void sendMail2(String mailTo, String mailTitle, String filePath) {
        Properties props = null;
        Transport transport = null;
        try {
            props = new Properties();
            // 使用smtp：简单邮件传输协议
            props.put("mail.smtp.host", MAILHOST);  // 存储发送邮件服务器的信息
            props.put("mail.smtp.auth", MAILAUTH);    // 同时通过验证
            Session session = Session.getInstance(props);   // 根据属性新建一个邮件会话
            MimeMessage message = new MimeMessage(session); // 由邮件会话新建一个消息对象
            message.setFrom(MAILFROM); // 设置发件人的地址
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(mailTo));// 设置收件人,并设置其接收类型为TO
            message.setSubject(mailTitle); // 设置标题
            BodyPart bp = new MimeBodyPart();
            FileDataSource fds = new FileDataSource(filePath);
            bp.setDataHandler(new DataHandler(fds));
            bp.setFileName(MimeUtility.encodeText(fds.getName(), "UTF-8", "B"));
            Multipart mp = new MimeMultipart();
            mp.addBodyPart(bp);
            message.setContent(mp);
            message.setSentDate(new Date()); // 设置发信时间
            message.saveChanges(); // 存储邮件信息
            // 发送邮件
            transport = session.getTransport(MAILPROTOCOL);
            transport.connect(MAILNAME, MAILPWD);
            transport.sendMessage(message, message.getAllRecipients()); // 发送邮件,其中第二个参数是所有已设好的收件人地址
            transport.close();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (null != transport) {
                try {
                    transport.close();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    @Async
    public void pushWSMessageForAllUsers(String message) {
        myHandler.sendMessageToAllUsers(new TextMessage(message));
    }

    @Override
    @Async
    public void pushWSMessageForSpecifyUsers(List<Integer> userIds, String message) {
        myHandler.sendMessageToUser(userIds, new TextMessage(message));
    }
}
