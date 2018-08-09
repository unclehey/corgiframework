package pers.corgiframework.service;

import java.util.List;
import java.util.Map;

/**
 * Created by syk on 2018/7/18.
 * service 基类
 * @param <T> 实体对象类型
 */
public interface IBaseService<T> {
    /**
     * 增加
     * @param entity
     * @return
     */
    int insert(T entity);

    /**
     * 删除
     * @param id
     * @return
     */
    int delete(Integer id);

    /**
     * 修改
     * @param entity
     * @return
     */
    int update(T entity);

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
    List<T> selectListsByCondition(Map<String, Object> map);

    /**
     * 查询数量
     * @param map
     * @return
     */
    int selectListsCountByCondition(Map<String, Object> map);
}
