package pers.corgiframework.api.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import pers.corgiframework.api.annotation.NoNeedParams;
import pers.corgiframework.api.annotation.NoNeedToken;
import pers.corgiframework.dao.model.BisPrompt;
import pers.corgiframework.service.IRedisService;
import pers.corgiframework.tool.constants.BisPromptConstant;
import pers.corgiframework.tool.utils.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

/**
 * APP拦截器
 * Created by syk on 2018/7/24.
 */
public class AppInterceptor extends HandlerInterceptorAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppInterceptor.class);

    @Autowired
    private IRedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 返回给移动端的信息
        BisPrompt bisPrompt = new BisPrompt();
        try {
            request.setCharacterEncoding("UTF-8");
            String reqIp = StringUtil.getIpAddr(request);
            LOGGER.info("IP = {}, url = {}, method = {}, contentType = {}, agent = {}", reqIp, request.getRequestURI(), request.getMethod(), request.getContentType(), request.getHeader("User-Agent"));
            // 判断请求是否需要参数
            HandlerMethod hm = (HandlerMethod) handler;
            Method method = hm.getMethod();
            NoNeedParams noNeedParams = method.getAnnotation(NoNeedParams.class);
            if (noNeedParams == null) {
                // 需要参数 获取Json请求参数
                Map<String, String> map = getJsonParamsToMap(request);
                if (map == null || map.size() == 0) {
                    // 非法请求
                    bisPrompt.setBisStatus(BisPromptConstant.INVALID_REQUEST_STATUS);
                    bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.INVALID_REQUEST_STATUS));
                    // 设置数据返回类型
                    response.setHeader("Content-Type", "application/json;charset=UTF-8");
                    response.getWriter().write(JsonUtil.objectToJson(bisPrompt));
                    return false;
                } else {
                    // 判断请求是否超时：5分钟
                    String timestamp = map.get("timestamp");
                    if (StringUtils.isBlank(timestamp)) {
                        bisPrompt.setBisStatus(BisPromptConstant.TIMESTAMP_EMPTY_STATUS);
                        bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.TIMESTAMP_EMPTY_STATUS));
                        // 设置数据返回类型
                        response.setHeader("Content-Type", "application/json;charset=UTF-8");
                        response.getWriter().write(JsonUtil.objectToJson(bisPrompt));
                        return false;
                    } else {
                        long seconds = DateTimeUtil.calculateSeconds(Long.valueOf(timestamp));
                        if (seconds > 300) {
                            bisPrompt.setBisStatus(BisPromptConstant.TIME_OUT_STATUS);
                            bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.TIME_OUT_STATUS));
                            // 设置数据返回类型
                            response.setHeader("Content-Type", "application/json;charset=UTF-8");
                            response.getWriter().write(JsonUtil.objectToJson(bisPrompt));
                            return false;
                        }
                    }
                    // 判断请求是否需要token
                    NoNeedToken noNeedToken = method.getAnnotation(NoNeedToken.class);
                    if (noNeedToken == null) {
                        // 请求需要token
                        String token = map.get("token");
                        if (StringUtils.isBlank(token)) {
                            bisPrompt.setBisStatus(BisPromptConstant.NEED_TOKEN_STATUS);
                            bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.NEED_TOKEN_STATUS));
                            // 设置数据返回类型
                            response.setHeader("Content-Type", "application/json;charset=UTF-8");
                            response.getWriter().write(JsonUtil.objectToJson(bisPrompt));
                            return false;
                        } else {
                            // 去redis取值
                            String userId = redisService.getString(token);
                            if (StringUtils.isBlank(userId)) {
                                // 该token对应的值不存在：登录信息已失效
                                bisPrompt.setBisStatus(BisPromptConstant.TOKEN_INVALID_STATUS);
                                bisPrompt.setBisMsg(BisPromptConstant.BUSINESS_MAP.get(BisPromptConstant.TOKEN_INVALID_STATUS));
                                // 设置数据返回类型
                                response.setHeader("Content-Type", "application/json;charset=UTF-8");
                                response.getWriter().write(JsonUtil.objectToJson(bisPrompt));
                                return false;
                            } else {
                                // 存在
                                map.put("userId", userId);
                            }
                        }
                    }
                }
                request.setAttribute("requestMap", map);
                return true;
            }
            return true;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取Json请求参数放入Map
     * @param request
     * @return
     */
    private static Map<String, String> getJsonParamsToMap(HttpServletRequest request) {
        // 获取请求类型
        String contentType = request.getContentType();
        if (null != contentType) {
            try {
                Map<String, String> resMap = Maps.newHashMap();
                if (contentType.contains("application/json")) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
                    StringBuffer sb = new StringBuffer();
                    String inputStr;
                    while ((inputStr = br.readLine()) != null) {
                        sb.append(inputStr);
                    }
                    // 关闭流
                    br.close();
                    if (sb.length() > 0) {
                        // 此处分3步
                        // 1、解析RSA加密字符串：得到AES秘钥和加密参数
                        String base64Str = sb.toString();
                        byte[] temp = Base64Util.decode(base64Str);
                        String rsaStr = new String(RSAUtil.decryptByPrivateKey(temp, RSAUtil.DEFAULT_PRIVATE_KEY), request.getCharacterEncoding());
                        // 转成Json对象
                        JSONObject jsonObject = JSONObject.parseObject(rsaStr);
                        Map<String, String> aesMap = Maps.newHashMap();
                        Iterator<Map.Entry<String, Object>> iterator = jsonObject.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<String, Object> param = iterator.next();
                            aesMap.put(param.getKey(), String.valueOf(param.getValue()));
                        }
                        // 2、AES解密得到明文参数
                        String requestParams = AESUtil.decrypt(aesMap.get("sign"), aesMap.get("key"));
                        // 3、将参数转成Map
                        jsonObject = JSONObject.parseObject(requestParams);
                        iterator = jsonObject.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<String, Object> param = iterator.next();
                            Object value = param.getValue();
                            if (value instanceof String) {
                                resMap.put(param.getKey(), String.valueOf(value));
                            } else {
                                resMap.put(param.getKey(), JSONObject.toJSONString(value, SerializerFeature.WriteClassName));
                            }
                        }
                    }
                } else if (contentType.contains("multipart/form-data")) {
                    // 文件上传
                    String token = request.getParameter("token");
                    String timestamp = request.getParameter("timestamp");
                    resMap.put("token", token);
                    resMap.put("timestamp", timestamp);
                }
                return resMap;
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return null;
    }

}
