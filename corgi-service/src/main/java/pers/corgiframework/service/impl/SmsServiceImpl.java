package pers.corgiframework.service.impl;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pers.corgiframework.dao.domain.SmsRecord;
import pers.corgiframework.dao.domain.SmsRecordExample;
import pers.corgiframework.dao.mapper.BaseMapper;
import pers.corgiframework.dao.mapper.SmsRecordMapper;
import pers.corgiframework.service.IRedisService;
import pers.corgiframework.service.ISmsService;
import pers.corgiframework.service.ISysPropertiesService;
import pers.corgiframework.tool.constants.SmsConstant;
import pers.corgiframework.tool.enums.SysRedisEnum;
import pers.corgiframework.tool.utils.DateTimeUtil;
import pers.corgiframework.tool.utils.SmsUtil;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by syk on 2018/7/17.
 */
@Service
public class SmsServiceImpl extends BaseServiceImpl<SmsRecord> implements ISmsService {
    private final static Logger LOGGER = LoggerFactory.getLogger(SmsServiceImpl.class);

    @Autowired
    private SmsRecordMapper smsRecordMapper;
    @Autowired
    private IRedisService redisService;
    @Autowired
    private ISysPropertiesService sysPropertiesService;

    @Override
    protected BaseMapper<SmsRecord> getMapper() {
        return smsRecordMapper;
    }

    @Override
    @Async
    public void sendSms(String mobile, Integer smsType, String smsContent) {
        boolean isSendMsgSuccess = false;
        try {
            // 短信开关
            String smsSwitchKey = SysRedisEnum.CORGI_SMS_SWITCH.getKey();
            String smsSwitchValue = redisService.getString(smsSwitchKey);
            if (StringUtils.isBlank(smsSwitchValue)) {
                smsSwitchValue = sysPropertiesService.selectValueByKey(smsSwitchKey);
                redisService.setString(smsSwitchKey, SysRedisEnum.CORGI_SMS_SWITCH.getTerm(), smsSwitchValue);
            }
            if (smsSwitchValue.equals("ON")) {
                isSendMsgSuccess = SmsUtil.sendSms(mobile, smsContent);
            }
            insertSmsRecord(isSendMsgSuccess, mobile, smsContent, smsType);
        } catch (Exception e) {
            LOGGER.error("Send Message Exception", e);
        }
    }

    /**
     * 插入短信发送记录
     *
     * @param isSendMsgSuccess
     * @param mobile
     * @param msgContent
     * @param smsType
     */
    private void insertSmsRecord(boolean isSendMsgSuccess, String mobile, String msgContent, int smsType) {
        try {
            // 插入短信发送记录
            SmsRecord smsRecord = new SmsRecord();
            smsRecord.setMobile(mobile);
            smsRecord.setContent(msgContent);
            smsRecord.setType(smsType);
            if (isSendMsgSuccess) {
                smsRecord.setFlag(1);// 短信发送状态：1成功
            } else {
                smsRecord.setFlag(0);// 短信发送状态：0失败
            }
            smsRecord.setCreateTime(DateTimeUtil.getNowDateTime());
            smsRecordMapper.insertSelective(smsRecord);
        } catch (Exception e) {
            LOGGER.error("Insert SmsRecord Exception", e);
        }
    }

    @Override
    public List<SmsRecord> getSmsRecordsLimitByMobile(String mobile) {
        List<Integer> typeList = Lists.newArrayList();
        typeList.add(SmsConstant.VERIFY_CODE);
        typeList.add(SmsConstant.FORGET_PASS);
        String nowDate = DateTimeUtil.getNowDate(DateTimeUtil.FORMAT_SHORT_LINE);
        LocalDateTime startTime = DateTimeUtil.parseStrToLocalDateTime(nowDate + " 00:00:01", DateTimeUtil.FORMAT_LONG);
        LocalDateTime endTime = DateTimeUtil.parseStrToLocalDateTime(nowDate + " 23:59:59", DateTimeUtil.FORMAT_LONG);
        SmsRecordExample example = new SmsRecordExample();
        example.createCriteria().andMobileEqualTo(mobile).andTypeIn(typeList).andCreateTimeBetween(startTime, endTime);
        example.setOrderByClause("create_time desc");
        return smsRecordMapper.selectByExample(example);
    }
}
