package pers.corgiframework.dao.mapper;

import java.util.List;
import java.util.Map;

/**
 * Created by syk on 2018/7/18.
 */
public interface BaseMapper<T> {

    /**
     * 增加
     * @param entity
     * @return
     */
    int insertSelective(T entity);

    /**
     * 删除
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 修改
     * @param entity
     * @return
     */
    int updateByPrimaryKeySelective(T entity);

    /**
     * 按主键查询
     * @param id
     * @return
     */
    T selectByPrimaryKey(Integer id);

    /**
     * 分页查询列表
     * @param map
     * @return
     */
    List<T> selectListByCondition(Map<String, Object> map);

    /**
     * 查询数量
     * @param map
     * @return
     */
    int selectListCountByCondition(Map<String, Object> map);
}
