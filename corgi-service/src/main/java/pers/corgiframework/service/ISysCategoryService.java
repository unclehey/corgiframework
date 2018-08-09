package pers.corgiframework.service;

import pers.corgiframework.dao.domain.SysCategory;

import java.util.List;

/**
 * Created by syk on 2018/8/3.
 */
public interface ISysCategoryService extends IBaseService<SysCategory> {
    SysCategory selectByNameAndType(String name, Integer type);
    List<SysCategory> selectByType(Integer type);
}
