package pers.corgiframework.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.corgiframework.dao.domain.SysProperties;
import pers.corgiframework.dao.domain.SysPropertiesExample;
import pers.corgiframework.dao.mapper.BaseMapper;
import pers.corgiframework.dao.mapper.SysPropertiesMapper;
import pers.corgiframework.service.ISysPropertiesService;
import pers.corgiframework.tool.enums.SysRedisEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by syk on 2018/8/3.
 */
@Service
public class SysPropertiesServiceImpl extends BaseServiceImpl<SysProperties> implements ISysPropertiesService {

    @Autowired
    private SysPropertiesMapper sysPropertiesMapper;

    @Override
    protected BaseMapper<SysProperties> getMapper() {
        return sysPropertiesMapper;
    }

    @Override
    public SysProperties selectByKey(String key) {
        SysProperties sysProperties = null;
        SysPropertiesExample example = new SysPropertiesExample();
        example.createCriteria().andPropKeyEqualTo(key);
        List<SysProperties> list = sysPropertiesMapper.selectByExample(example);
        if(!list.isEmpty()) {
            sysProperties = list.get(0);
        }
        return sysProperties;
    }

    @Override
    public List<Map<String, Object>> getSysPropertiesNotInSql() {
        List<Map<String, Object>> propertiesList = new ArrayList<>();
        // 查询系统配置列表
        Map<String, Object> map = new HashMap<>();
        // 从第几条开始查询
        map.put("currentPage", 0);
        // 要查询多少条数据
        map.put("pageCount", Integer.MAX_VALUE);
        List<SysProperties> sysPropertiesList = sysPropertiesMapper.selectListByCondition(map);
        Map<String, Object> sysPropertiesMap = new HashMap<>();
        for(SysProperties sysProperties : sysPropertiesList){
            String key = sysProperties.getPropKey();
            String value = sysProperties.getPropValue();
            sysPropertiesMap.put(key, value);
        }
        // 枚举里的所有配置
        SysRedisEnum[] enums = SysRedisEnum.values();
        for (int i = 0; i < enums.length; i++) {
            String propKey = enums[i].getKey();
            String description = enums[i].getDescription();
            if (!sysPropertiesMap.containsKey(propKey)) {
                Map<String, Object> propertiesMap = new HashMap<>();
                propertiesMap.put("propKey", propKey);
                propertiesMap.put("description", description);
                propertiesList.add(propertiesMap);
            }
        }
        return propertiesList;
    }

    @Override
    public String selectValueByKey(String key) {
        String value = null;
        SysPropertiesExample example = new SysPropertiesExample();
        example.createCriteria().andPropKeyEqualTo(key);
        List<SysProperties> list = sysPropertiesMapper.selectByExample(example);
        if(!list.isEmpty()) {
            value = list.get(0).getPropValue();
        }
        return value;
    }
}
