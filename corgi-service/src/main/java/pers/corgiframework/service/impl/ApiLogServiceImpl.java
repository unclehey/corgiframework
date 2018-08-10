package pers.corgiframework.service.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pers.corgiframework.dao.mongo.ApiLog;
import pers.corgiframework.service.IApiLogService;

import java.util.List;
import java.util.Map;

/**
 * Created by syk on 2018/8/9.
 */
@Service
public class ApiLogServiceImpl extends BaseRepositoryImpl<ApiLog> implements IApiLogService {

    @Override
    public List<ApiLog> selectListByCondition(Map<String, Object> map) {
        // 组装查询条件
        DBObject dbObject = getQueryDbObject(map);
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        Integer currentPage = (Integer) map.get("currentPage");
        Integer pageCount = (Integer) map.get("pageCount");
        return this.selectListByCondition(dbObject, sort, currentPage, pageCount, ApiLog.class);
    }

    @Override
    public long selectListCountByCondition(Map<String, Object> map) {
        // 组装查询条件
        DBObject dbObject = getQueryDbObject(map);
        return this.selectListCountByCondition(dbObject, ApiLog.class);
    }

    /**
     * 组装查询条件
     * @param map
     * @return
     */
    private DBObject getQueryDbObject(Map<String, Object> map) {
        DBObject dbObject = new BasicDBObject();
        // 查询条件
        if (null != map.get("userId")){
            dbObject.put("userId", Integer.valueOf(String.valueOf(map.get("userId"))));
        }
        if (null != map.get("mobile")){
            dbObject.put("mobile", map.get("mobile"));
        }
        if (null != map.get("serviceId")){
            dbObject.put("serviceId", map.get("serviceId"));
        }
        if (null != map.get("logSource")){
            dbObject.put("logSource", Integer.valueOf(String.valueOf(map.get("logSource"))));
        }
        if (null != map.get("status")){
            dbObject.put("status", Integer.valueOf(String.valueOf(map.get("status"))));
        }
        return dbObject;
    }

}
