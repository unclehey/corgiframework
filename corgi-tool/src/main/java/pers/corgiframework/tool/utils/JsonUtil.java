package pers.corgiframework.tool.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Json工具类
 * Created by syk on 2018/7/24.
 */
public class JsonUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);
    private static ObjectMapper mapper = new ObjectMapper();

    /**
     * 对象转Json
     * @param object
     * @return
     */
    public static String objectToJson(Object object) {
        String res = null;
        try {
            res = mapper.writeValueAsString(object);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return res;
    }

    /**
     * 使用泛型方法，把json字符串转换为相应的JavaBean对象。
     * (1)转换为普通JavaBean：readValue(json,Student.class)
     * (2)转换为List:readValue(json,List
     * .class).但是如果我们想把json转换为特定类型的List，比如List<Student>，就不能直接进行转换了。
     * 因为readValue(json
     * ,List.class)返回的其实是List<Map>类型，你不能指定readValue()的第二个参数是List<
     * Student>.class，所以不能直接转换。
     * 我们可以把readValue()的第二个参数传递为Student[].class.然后使用Arrays
     * .asList();方法把得到的数组转换为特定类型的List。 (3)转换为Map：readValue(json,Map.class)
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;我们使用泛型，得到的也是泛型
     *
     * @param content   要转换的JavaBean类型
     * @param valueType 原始json字符串数据
     * @return JavaBean对象
     */
    public static <T> T readValue(String content, Class<T> valueType) {
        ObjectMapper objectMapper = null;
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        try {
            return objectMapper.readValue(content, valueType);
        } catch (Exception e) {
            LOGGER.info(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 多层Json转List
     *
     * @param responseMessage
     * @return
     */
    public static List<Map<String, String>> parseJsonToListForMgr(String responseMessage) {
        JSONObject jsonObj = JSONObject.parseObject(responseMessage);
        JSONArray jsonArray = jsonObj.getJSONArray("rules");
        Iterator<Object> iterator = jsonArray.iterator();
        List<Map<String, String>> filters = Lists.newArrayList();
        while (iterator.hasNext()) {
            Map<String, String> map = Maps.newHashMap();
            JSONObject obj = (JSONObject) iterator.next();
            map.put(obj.getString("field"), obj.getString("data"));
            filters.add(map);
        }
        return filters;
    }

    /**
     * 多层Json转Map
     *
     * @param responseMessage
     * @return
     */
    public static Map<String, String> parseJsonToMap(String responseMessage) {
        Map<String, String> responseMap = Maps.newHashMap();
        JSONObject jsonObj = JSONObject.parseObject(responseMessage);
        Map firstMap = (Map) jsonObj;
        Iterator<Map.Entry<String, Object>> firstIterator = firstMap.entrySet().iterator();
        while (firstIterator.hasNext()) {
            Map.Entry<String, Object> firstEntry = firstIterator.next();
            String firstKey = firstEntry.getKey();
            Object firstValue = firstEntry.getValue();
            if (firstValue instanceof JSONObject) {
                // 第一层
                Map secondMap = (Map) firstValue;
                Iterator<Map.Entry<String, Object>> secondIterator = secondMap.entrySet().iterator();
                while (secondIterator.hasNext()) {
                    Map.Entry<String, Object> secondEntry = secondIterator.next();
                    String secondKey = secondEntry.getKey();
                    Object secondValue = secondEntry.getValue();
                    if (secondValue instanceof JSONObject) {
                        // 第二层
                        Map thirdMap = (Map) secondValue;
                        Iterator<Map.Entry<String, Object>> thirdIterator = thirdMap.entrySet().iterator();
                        while (thirdIterator.hasNext()) {
                            // 第三层
                            Map.Entry<String, Object> thirdEntry = thirdIterator.next();
                            String thirdKey = thirdEntry.getKey();
                            Object thirdValue = thirdEntry.getValue();
                            if (thirdValue instanceof JSONObject) {
                                Map forthMap = (Map) thirdValue;
                                Iterator<Map.Entry<String, Object>> forthIterator = forthMap.entrySet().iterator();
                                while (forthIterator.hasNext()) {
                                    // 第四层
                                    Map.Entry<String, Object> forthEntry = forthIterator.next();
                                    String forthKey = forthEntry.getKey();
                                    Object forthValue = forthEntry.getValue();
                                    responseMap.put(forthKey, String.valueOf(forthValue));
                                }
                            } else if (thirdValue instanceof JSONArray) {
                                // 如果是数组型 存最后一条数据
                                JSONArray jsonArray = (JSONArray) thirdValue;
                                int arraySize = jsonArray.size();
                                if (null != jsonArray && arraySize > 0) {
                                    for (int i = 0; i < arraySize; i++) {
                                        responseMap.put(thirdKey + i, String.valueOf(jsonArray.get(i)));
                                    }
                                }
                            } else {
                                responseMap.put(thirdKey, String.valueOf(thirdValue));
                            }
                        }
                    } else {
                        responseMap.put(secondKey, String.valueOf(secondValue));
                    }
                }
            } else {
                responseMap.put(firstKey, String.valueOf(firstValue));
            }
        }
        return responseMap;
    }

}
