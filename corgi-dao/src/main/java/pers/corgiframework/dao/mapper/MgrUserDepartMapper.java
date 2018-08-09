package pers.corgiframework.dao.mapper;

import pers.corgiframework.dao.domain.MgrUser;
import pers.corgiframework.dao.domain.MgrUserDepart;
import pers.corgiframework.dao.domain.MgrUserDepartExample;

import java.util.List;
import java.util.Map;

public interface MgrUserDepartMapper extends BaseMapper<MgrUserDepart> {
    List<MgrUserDepart> selectByExample(MgrUserDepartExample example);
    List<MgrUser> selectByDepart(Integer departId);
    List<Map<String, Object>> selectUserByDepart(Integer departId);
    List<Map<String, Object>> selectAllUserByDepart();
    List<MgrUser> getUserByUserId(Integer userId);
}