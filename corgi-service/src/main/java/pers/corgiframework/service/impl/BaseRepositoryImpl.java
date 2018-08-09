package pers.corgiframework.service.impl;

import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import pers.corgiframework.service.IBaseRepository;

import java.util.List;

/**
 * Created by syk on 2018/8/9.
 */
@Repository
public abstract class BaseRepositoryImpl<T> implements IBaseRepository<T> {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<T> selectListByCondition(DBObject queryObject, Sort sort, Integer currentPage, Integer pageCount, Class<T> collectionClass) {
        Query query = new BasicQuery(queryObject);
        query.with(sort);
        if (null != currentPage) {
            query.skip(currentPage);
        }
        if (null != pageCount) {
            query.limit(pageCount);
        }
        return mongoTemplate.find(query, collectionClass);
    }

    @Override
    public long selectListCountByCondition(DBObject queryObject, Class<T> collectionClass) {
        Query query = new BasicQuery(queryObject);
        return mongoTemplate.count(query, collectionClass);
    }

    @Override
    public List<T> getByCondition(DBObject queryObject, DBObject fieldsObject, Class<T> collectionClass) {
        Query query = new BasicQuery(queryObject, fieldsObject);
        return mongoTemplate.find(query, collectionClass);
    }

    @Override
    public T selectOneByCondition(DBObject queryObject, Class<T> collectionClass) {
        Query query = new BasicQuery(queryObject);
        return mongoTemplate.findOne(query, collectionClass);
    }

    @Override
    public void save(T t) {
        mongoTemplate.insert(t);
    }

    @Override
    public T selectById(String id, Class<T> collectionClass) {
        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(id)), collectionClass);
    }

    @Override
    public WriteResult update(String idProperty, String id, String propertyName, String propertyValue, Class<T> collectionClass) {
        return mongoTemplate.updateFirst(
                new Query(Criteria.where(idProperty).is(id)),
                Update.update(propertyName, propertyValue), collectionClass);
    }

    @Override
    public void deleteById(String id, Class<T> collectionClass) {
        mongoTemplate.remove(new Query(Criteria.where("_id").is(id)), collectionClass);
    }

    @Override
    public void deleteByParam(String paramName, Object paramValue, Class<T> collectionClass) {
        mongoTemplate.remove(new Query(Criteria.where(paramName).is(paramValue)), collectionClass);
    }

    @Override
    public void createCollection(Class<T> collectionClass) {
        if (!mongoTemplate.collectionExists(collectionClass)) {
            mongoTemplate.createCollection(collectionClass);
        }
    }

    @Override
    public void dropCollection(Class<T> collectionClass) {
        if (mongoTemplate.collectionExists(collectionClass)) {
            mongoTemplate.dropCollection(collectionClass);
        }
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
