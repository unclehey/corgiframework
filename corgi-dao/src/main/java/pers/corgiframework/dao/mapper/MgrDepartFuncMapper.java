package pers.corgiframework.dao.mapper;

import pers.corgiframework.dao.domain.MgrDepartFunc;
import pers.corgiframework.dao.domain.MgrDepartFuncExample;

import java.util.List;

public interface MgrDepartFuncMapper extends BaseMapper<MgrDepartFunc> {
    List<MgrDepartFunc> selectByExample(MgrDepartFuncExample example);
    int deleteByFuncId(Integer funcId);
}