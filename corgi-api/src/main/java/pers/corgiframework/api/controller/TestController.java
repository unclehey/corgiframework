package pers.corgiframework.api.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import pers.corgiframework.api.annotation.NoNeedParams;
import pers.corgiframework.api.annotation.NoNeedToken;
import pers.corgiframework.dao.domain.SmsRecord;
import pers.corgiframework.dao.domain.SysPrice;
import pers.corgiframework.dao.model.BisPrompt;
import pers.corgiframework.service.IPaymentOrderService;
import pers.corgiframework.service.ISmsService;
import pers.corgiframework.service.ISysPriceService;
import pers.corgiframework.tool.constants.BisPromptConstant;
import pers.corgiframework.tool.constants.PayConstant;
import pers.corgiframework.tool.utils.FileUtil;
import pers.corgiframework.tool.utils.PropertiesUtil;
import pers.corgiframework.tool.utils.StringUtil;
import pers.corgiframework.util.SysExceptionUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by syk on 2018/7/20.
 */
@RestController
@RequestMapping("/test")
public class TestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);
    // 获取支付参数
    private static final String PUBLIC_APP_ID = PropertiesUtil.getString("public_appid");
    private static final String PUBLIC_MCH_ID = PropertiesUtil.getString("public_mch_id");
    private static final String PUBLIC_PAY_KEY = PropertiesUtil.getString("public_pay_key");
    private static final String WECHAT_NOTIFY_URL = PropertiesUtil.getString("wechat_notify_url");
    private static final String ALIPAY_APP_ID = PropertiesUtil.getString("aliPay_appId");
    private static final String ALIPAY_PRIVATE_KEY = PropertiesUtil.getString("aliPay_privateKey");
    private static final String ALIPAY_PUBLIC_KEY = PropertiesUtil.getString("ali_publicKey");
    private static final String ALIPAY_NOTIFY_URL = PropertiesUtil.getString("alipay_notify_url");

    @Autowired
    private ISmsService smsService;
    @Autowired
    private IPaymentOrderService paymentOrderService;
    @Autowired
    private ISysPriceService sysPriceService;

    @NoNeedParams
    @RequestMapping(value = "/NoNeedParams")
    public Object test(){
        // 返回给移动端的信息
        BisPrompt bisPrompt = new BisPrompt();
        try {
            Map<String, Object> map = new HashMap<>();
            // 从第几条开始查询
            map.put("currentPage", 0);
            // 要查询多少条数据
            map.put("pageCount", Integer.MAX_VALUE);
            List<SmsRecord> smsRecords = smsService.selectListByCondition(map);
            bisPrompt.setBisObj(smsRecords);
        } catch (Exception e) {
            // 系统异常
            LOGGER.error(e.getMessage(), e);
            bisPrompt.setBisStatus(BisPromptConstant.SYSTEM_EXCEPTION_STATUS);
            bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.SYSTEM_EXCEPTION_STATUS));
        }
        return bisPrompt;
    }

    @NoNeedToken
    @RequestMapping(value = "/NoNeedToken")
    public Object no(HttpServletRequest request){
        // 接受移动端请求参数
        Map<String, String> requestMap = (Map<String, String>) request.getAttribute("requestMap");
        String pageNo = requestMap.get("pageNo");
        // 返回给移动端的信息
        BisPrompt bisPrompt = new BisPrompt();
        try {
        } catch (Exception e) {
            // 系统异常
            bisPrompt = SysExceptionUtil.response(e);
        }
        return bisPrompt;
    }

    @RequestMapping(value = "/user")
    public Object user(HttpServletRequest request){
        // 接受移动端请求参数
        Map<String, String> requestMap = (Map<String, String>) request.getAttribute("requestMap");
        String userId = requestMap.get("userId");
        // 返回给移动端的信息
        BisPrompt bisPrompt = new BisPrompt();
        try {

        } catch (Exception e) {
            // 系统异常
            bisPrompt = SysExceptionUtil.response(e);
        }
        return bisPrompt;
    }

    @PostMapping(value = "/upload")
    public Object upload(MultipartHttpServletRequest request) {
        // 接受移动端请求参数
        Map<String, String> requestMap = (Map<String, String>) request.getAttribute("requestMap");
        String userId = requestMap.get("userId");
        MultipartFile headUrl = request.getFile("headUrl");
        // 返回给移动端的信息
        BisPrompt bisPrompt = new BisPrompt();
        try {
            if (headUrl == null) {
                bisPrompt.setBisStatus(BisPromptConstant.PARAMS_EMPTY_STATUS);
                bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.PARAMS_EMPTY_STATUS));
                return bisPrompt;
            }
            String fileName = FileUtil.upload(headUrl, Integer.valueOf(userId), "/data/webapps/static/mrxz/avatar/");
            if (StringUtils.isNotBlank(fileName)) {
               /* User user = userService.selectByPrimaryKey(Integer.valueOf(userId));
                user.setHeadUrl(fileName);
                user.setUpdateTime(DateTimeUtil.getNowDateTime());
                userService.update(user);
                bisPrompt.setBisObj(user);*/
            } else {
                bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.FAIL_STATUS));
            }
        } catch (Exception e) {
            // 系统异常
            bisPrompt = SysExceptionUtil.response(e);
        }
        return bisPrompt;
    }

    @NoNeedParams
    @RequestMapping(value = "/weixin/pay")
    public Object weiXinPay(HttpServletRequest request) {
        try {
            SysPrice sysPrice = sysPriceService.selectByPrimaryKey(1);
            BisPrompt bisPrompt = paymentOrderService.orderPayByWxPay(1, "17600669027", null, null, 1, null, StringUtil.getIpAddr(request), "升级个人会员", sysPrice, PayConstant.SOURCE_IOS,
                    PUBLIC_APP_ID, PUBLIC_MCH_ID, PUBLIC_PAY_KEY, WECHAT_NOTIFY_URL, null, PayConstant.TRADE_TYPE_APP, null);
            return bisPrompt;
        } catch (Exception e) {
            // 系统异常
            return SysExceptionUtil.response(e);
        }
    }

    @NoNeedParams
    @RequestMapping(value = "/ali/pay")
    public Object aliPay(HttpServletRequest request) {
        try {
            SysPrice sysPrice = sysPriceService.selectByPrimaryKey(2);
            BisPrompt bisPrompt = paymentOrderService.orderPayByAliPay(2, "15911186198", null, null, 2, null,"升级企业会员", sysPrice, PayConstant.SOURCE_ANDROID,
                    ALIPAY_APP_ID, ALIPAY_PRIVATE_KEY, ALIPAY_PUBLIC_KEY, ALIPAY_NOTIFY_URL);
            return bisPrompt;
        } catch (Exception e) {
            // 系统异常
            return SysExceptionUtil.response(e);
        }
    }

}
