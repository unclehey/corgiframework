package pers.corgiframework.service.impl;

import org.springframework.stereotype.Service;
import pers.corgiframework.dao.mapper.BaseMapper;
import pers.corgiframework.service.IBaseService;

import java.util.List;
import java.util.Map;

/**
 * Created by syk on 2018/7/18.
 */
@Service
public abstract class BaseServiceImpl<T> implements IBaseService<T> {

    protected abstract BaseMapper<T> getMapper();

    @Override
    public int insert(T entity) {
        return getMapper().insertSelective(entity);
    }

    @Override
    public int delete(Integer id) {
        return getMapper().deleteByPrimaryKey(id);
    }

    @Override
    public int update(T entity) {
        return getMapper().updateByPrimaryKeySelective(entity);
    }

    @Override
    public T selectByPrimaryKey(Integer id) {
        return getMapper().selectByPrimaryKey(id);
    }

    @Override
    public List<T> selectListsByCondition(Map<String, Object> map) {
        return getMapper().selectListsByCondition(map);
    }

    @Override
    public int selectListsCountByCondition(Map<String, Object> map) {
        return getMapper().selectListsCountByCondition(map);
    }
}
