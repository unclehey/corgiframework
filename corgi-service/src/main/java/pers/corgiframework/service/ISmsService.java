package pers.corgiframework.service;

import pers.corgiframework.dao.domain.SmsRecord;

import java.util.List;

/**
 * Created by syk on 2018/7/17.
 */
public interface ISmsService extends IBaseService<SmsRecord> {
    /**
     * 发短信
     */
    void sendSms(String mobile, Integer type, String message, String userName);

    /**
     * 根据手机号查询短信记录
     * @param mobile
     * @return
     */
    List<SmsRecord> getSmsRecordsLimitByMobile(String mobile);
}
