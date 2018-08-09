package pers.corgiframework.dao.mapper;

import pers.corgiframework.dao.domain.SmsRecord;
import pers.corgiframework.dao.domain.SmsRecordExample;

import java.util.List;

public interface SmsRecordMapper extends BaseMapper<SmsRecord> {
    List<SmsRecord> selectByExample(SmsRecordExample example);
}