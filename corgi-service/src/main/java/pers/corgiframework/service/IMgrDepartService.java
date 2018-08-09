package pers.corgiframework.service;

import pers.corgiframework.dao.domain.MgrDepart;

import java.util.List;
import java.util.Map;

/**
 * Created by syk on 2017/4/2.
 */
public interface IMgrDepartService {
    void insert(String cnName, Integer parentId, String type, String remark, Integer userId);
    void update(Integer proId, String cnName, String remark);
    List<MgrDepart> getByParentId(Integer parentId);
    List<MgrDepart> getAllByProId(Integer proId);
    List<MgrDepart> getAll();
    List<Map<String, Object>> getTreeByUserId(Integer userId);
    List<Map<String, Object>> getTreeAll();
    boolean isMyDepart(Integer departId, Integer userId);
    void delete(String proId);
}
