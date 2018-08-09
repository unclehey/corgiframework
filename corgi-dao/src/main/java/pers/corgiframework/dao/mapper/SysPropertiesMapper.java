package pers.corgiframework.dao.mapper;

import pers.corgiframework.dao.domain.SysProperties;
import pers.corgiframework.dao.domain.SysPropertiesExample;

import java.util.List;

public interface SysPropertiesMapper extends BaseMapper<SysProperties> {
    List<SysProperties> selectByExample(SysPropertiesExample example);
}