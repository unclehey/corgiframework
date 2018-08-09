package pers.corgiframework.dao.mapper;

import pers.corgiframework.dao.domain.MgrFunc;
import pers.corgiframework.dao.domain.MgrFuncExample;

import java.util.List;

public interface MgrFuncMapper extends BaseMapper<MgrFunc> {
    List<MgrFunc> selectByExample(MgrFuncExample example);
    List<MgrFunc> getByUserId(Integer userId);
    List<MgrFunc> getAll();
}