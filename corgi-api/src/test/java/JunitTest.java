import com.google.common.collect.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.CollectionUtils;
import pers.corgiframework.dao.mongo.ApiLog;
import pers.corgiframework.service.IApiLogService;

import java.util.List;
import java.util.Map;

/**
 * Created by syk on 2017/8/18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring.xml"})
public class JunitTest {
    final static Logger LOGGER = LoggerFactory.getLogger(JunitTest.class);

    @Autowired
    private IApiLogService apiLogService;

    @Test
    public void test(){
        try {
            /*ApiLog apiLog = new ApiLog();
            apiLog.setUserId(2);
            apiLog.setMobile("17600669027");
            apiLog.setServiceId("idIdentity");
            apiLog.setParamIn("110110198011235698");
            apiLog.setParamOut("认证失败");
            apiLog.setExecTime(987);
            apiLog.setLogSource(3);
            apiLog.setStatus(0);
            apiLog.setCreateTime(DateTimeUtil.getNowDateTime());
            apiLogService.save(apiLog);*/
            Map<String, Object> map = Maps.newHashMap();
            // 从第几条开始查询
            map.put("currentPage", 0);
            // 要查询多少条数据
            map.put("pageCount", 10);
            map.put("mobile", "17600669027");
            long count = apiLogService.selectListCountByCondition(map);
            LOGGER.info("查询数据 = {} 条", count);
            List<ApiLog> list = apiLogService.selectListByCondition(map);
            if (!CollectionUtils.isEmpty(list)) {
                for (ApiLog apiLog : list) {
                    LOGGER.info("手机号 = {}，日志内容 = {}，存储时间 = {}", apiLog.getMobile(), apiLog.getParamIn(), apiLog.getCreateTime());
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

}