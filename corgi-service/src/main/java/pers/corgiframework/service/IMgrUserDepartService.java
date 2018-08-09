package pers.corgiframework.service;

import pers.corgiframework.dao.domain.MgrUser;
import pers.corgiframework.dao.domain.MgrUserDepart;

import java.util.List;
import java.util.Map;

/**
 * Created by syk on 2017/4/2.
 */
public interface IMgrUserDepartService {
    void insert(Integer userId, Integer departId);
    List<MgrUser> getUser();
    List<MgrUser> getUserByDepart(String departId);
    List<MgrUser> getUserByUserId(String userId);
    List<Map<String,Object>> getDepartUserByUserId(String userId);
    List<Map<String,Object>> getAllDepartUserByUserId();
    void giveUser(String userIds, String departId);
    List<MgrUserDepart> getByDepart(String departId);
    List<MgrUserDepart> getByUser(Integer userId);
    void deleteByDepart(String departId);
}
