package pers.corgiframework.service;

import pers.corgiframework.dao.domain.SysProperties;

import java.util.List;
import java.util.Map;

/**
 * Created by syk on 2018/8/3.
 */
public interface ISysPropertiesService extends IBaseService<SysProperties> {
    SysProperties selectByKey(String key);
    List<Map<String, Object>> getSysPropertiesNotInSql();
    String selectValueByKey(String key);
}
