package pers.corgiframework.dao.mapper;

import pers.corgiframework.dao.domain.SysCategory;
import pers.corgiframework.dao.domain.SysCategoryExample;

import java.util.List;

public interface SysCategoryMapper extends BaseMapper<SysCategory> {
    List<SysCategory> selectByExample(SysCategoryExample example);
}