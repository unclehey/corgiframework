package pers.corgiframework.service;

import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

/**
 * Created by syk on 2018/8/9.
 * mongo 操作基类
 * @param <T> 实体对象类型
 */
public interface IBaseRepository<T> {

    /**
     * 根据特定条件查询指定字段的集合
     * @param queryObject 查询条件
     * @param sort 排序条件
     * @param currentPage 页码 不需要传入null
     * @param pageCount 每页条数 不需要传入null
     * @param collectionClass
     * @return
     */
    List<T> selectListByCondition(DBObject queryObject, Sort sort, Integer currentPage, Integer pageCount, Class<T> collectionClass);

    /**
     * 根据特定条件查询指定字段的集合数量
     * @param queryObject 查询条件
     * @param collectionClass
     * @return
     */
    long selectListCountByCondition(DBObject queryObject, Class<T> collectionClass);

    /**
     * 根据特定条件查询指定字段的集合
     * @param queryObject 查询条件
     * @param fieldsObject 返回指定字段
     * @param collectionClass
     * @return
     */
    List<T> getByCondition(DBObject queryObject, DBObject fieldsObject, Class<T> collectionClass);

    /**
     * 根据特定条件查询集合
     * @param queryObject 查询条件
     * @param collectionClass
     * @return
     */
    T selectOneByCondition(DBObject queryObject, Class<T> collectionClass);

    /**
     * 保存一个文档到集合
     */
    void save(T t);

    /**
     * 根据id获取一个文档
     */
    T selectById(String id, Class<T> collectionClass);

    /**
     * 更新文档
     * @param idProperty 唯一标识的属性
     * @param id 唯一标识的值
     * @param propertyName 要修改的属性名
     * @param propertyValue 要修改的属性值
     * @param collectionClass 集合对应的bean
     * @return
     */
    WriteResult update(String idProperty, String id, String propertyName, String propertyValue, Class<T> collectionClass);

    /**
     * 根据Id删除文档
     */
    void deleteById(String id, Class<T> collectionClass);

    /**
     * 根据传入参数删除文档
     * @param paramName 参数名
     * @param paramValue 参数值
     * @param collectionClass
     */
    void deleteByParam(String paramName, Object paramValue, Class<T> collectionClass);

    /**
     * 创建集合
     */
    void createCollection(Class<T> collectionClass);

    /**
     * 删除集合
     */
    void dropCollection(Class<T> collectionClass);

    /**
     * 获取mongoTemplate，想干什么就能干什么，他是操作mongodb的入口
     * @return
     */
    MongoTemplate getMongoTemplate();

}
