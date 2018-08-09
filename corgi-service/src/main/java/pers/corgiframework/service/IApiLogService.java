package pers.corgiframework.service;

import pers.corgiframework.dao.mongo.ApiLog;

import java.util.List;
import java.util.Map;

/**
 * Created by syk on 2018/8/9.
 */
public interface IApiLogService extends IBaseRepository<ApiLog> {
    List<ApiLog> selectListByCondition(Map<String, Object> map);
    long selectListCountByCondition(Map<String, Object> map);
}
