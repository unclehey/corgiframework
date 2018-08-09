package pers.corgiframework.dao.mapper;

import pers.corgiframework.dao.domain.MgrUser;
import pers.corgiframework.dao.domain.MgrUserExample;

import java.util.List;

public interface MgrUserMapper extends BaseMapper<MgrUser> {
    List<MgrUser> selectByExample(MgrUserExample example);
}