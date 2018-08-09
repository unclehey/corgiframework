package pers.corgiframework.service;

import pers.corgiframework.dao.domain.MgrDepartFunc;

import java.util.List;

/**
 * Created by syk on 2017/4/2.
 */
public interface IMgrDepartFuncService {
    void givePerm(String funcIds, String departId);
    void insert(Integer funcId, Integer departId);
    List<MgrDepartFunc> getByDepart(String departId);
    List<MgrDepartFunc> getByFunc(String departId);
    void deleteByDepart(String departId);
    void deleteByFunc(String funcId);
}
