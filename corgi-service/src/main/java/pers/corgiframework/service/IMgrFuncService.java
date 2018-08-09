package pers.corgiframework.service;

import pers.corgiframework.dao.domain.MgrFunc;

import java.util.List;
import java.util.Map;

/**
 * Created by syk on 2017/4/2.
 */
public interface IMgrFuncService {
    void insert(String cnName, Integer parentId, String type, String url, String remark, Integer userId, String code, String menuIcon);
    void update(Integer proId, String cnName, String url, String remark, String type, String code);
    void delete(Integer proId);
    List<MgrFunc> getByUserId(Integer userId);
    Map<String, Map<String, Object>> getByUserIdToMap(Integer userId);
    List<MgrFunc> getAll();
    Map<String, Map<String, Object>> getAllToMap();
    List<Map<String, Object>> getTreeByUserIdCheck(Integer userId, String departId);
    List<Map<String, Object>> getTreeAllCheck(String departId);
    List<Map<String, Object>> getTreeByUserId(Integer userId);
    List<Map<String, Object>> getTreeAll();
    List<Map<String, Object>> getFuncs();
    MgrFunc selectByPrimaryKey(Integer id);
}
