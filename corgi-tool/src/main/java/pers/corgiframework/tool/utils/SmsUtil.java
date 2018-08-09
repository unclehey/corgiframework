package pers.corgiframework.tool.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 短信发送工具类（阿里云）
 * Created by syk on 2017/8/18.
 */
public class SmsUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(SmsUtil.class);

    // 从配置文件读取发送短信相关参数
    private static final String ACCESS_KEY_ID = PropertiesUtil.getString("aliyun_sms_id");
    private static final String ACCESS_KEY_SECRET = PropertiesUtil.getString("aliyun_sms_secret");

    // 产品名称:云通信短信API产品,开发者无需替换
    private static final String PRODUCT = "Dysmsapi";
    // 产品域名,开发者无需替换
    private static final String DOMAIN = "dysmsapi.aliyuncs.com";

    public static boolean sendSms(String mobile, String msgContent) {
        boolean flag = false;
        try {
            //可自助调整超时时间
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");

            //初始化acsClient,暂不支持region化
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", ACCESS_KEY_ID, ACCESS_KEY_SECRET);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", PRODUCT, DOMAIN);
            IAcsClient acsClient = new DefaultAcsClient(profile);

            //组装请求对象-具体描述见控制台-文档部分内容
            SendSmsRequest request = new SendSmsRequest();
            //必填:待发送手机号
            request.setPhoneNumbers(mobile);
            //必填:短信签名-可在短信控制台中找到
            request.setSignName("短信签名");
            //必填:短信模板-可在短信控制台中找到
            request.setTemplateCode("SMS_137427644");
            //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
            //request.setTemplateParam("{\"name\":\"Tom\", \"code\":\"123\"}");
            request.setTemplateParam("{\"code\":\"" + msgContent + "\"}");

            //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
            //request.setSmsUpExtendCode("90997");

            //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
            //request.setOutId("yourOutId");

            //hint 此处可能会抛出异常，注意catch
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            String code = sendSmsResponse.getCode();
            if (code.equals("OK")) {
                // 发送成功
                flag = true;
            } else {
                LOGGER.info(sendSmsResponse.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return flag;
    }

}
