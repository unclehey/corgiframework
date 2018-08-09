package pers.corgiframework.dao.mapper;

import pers.corgiframework.dao.domain.MgrDepart;
import pers.corgiframework.dao.domain.MgrDepartExample;

import java.util.List;

public interface MgrDepartMapper extends BaseMapper<MgrDepart> {
    List<MgrDepart> selectByExample(MgrDepartExample example);
    List<MgrDepart> getAll();
}