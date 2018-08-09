import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pers.corgiframework.dao.domain.SmsRecord;
import pers.corgiframework.service.ISmsService;
import pers.corgiframework.tool.constants.SmsConstant;
import pers.corgiframework.tool.utils.DateTimeUtil;

/**
 * Created by syk on 2017/8/18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring.xml", "classpath:spring-mvc.xml", "classpath:spring-mybatis.xml", "classpath:spring-redis.xml"})
public class JunitTest {

    final static Logger LOGGER = LoggerFactory.getLogger(JunitTest.class);

    @Autowired
    private ISmsService smsService;

    @Test
    public void test(){
        try {
            /*SmsRecord smsRecord = new SmsRecord();
            smsRecord.setMobile("17600669027");
            smsRecord.setContent("八月的第二天，祝你好运！");
            smsRecord.setType(SmsConstant.FORGET_PASS);
            smsRecord.setFlag(1);
            smsRecord.setCreateTime(DateTimeUtil.getNowDateTime());
            smsService.insert(smsRecord);
            //SmsRecord smsRecord = smsService.selectByPrimaryKey(1);
            LOGGER.info("短信内容 = {}，发送时间 = {}", smsRecord.getContent(), smsRecord.getCreateTime());*/
            smsService.sendSms("17600669027", SmsConstant.VERIFY_CODE, "123456", "嘿の大叔");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

}