package pers.corgiframework.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pers.corgiframework.dao.domain.SmsRecord;
import pers.corgiframework.dao.mongo.ApiLog;
import pers.corgiframework.service.IApiLogService;
import pers.corgiframework.service.IPublicService;
import pers.corgiframework.service.ISmsService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by syk on 2018/8/10.
 */
@Controller
public class LogSmsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogSmsController.class);

    @Autowired
    private ISmsService smsService;
    @Autowired
    private IApiLogService apiLogService;
    @Autowired
    private IPublicService publicService;

    @RequestMapping("/sms/record/list")
    public String recordList() {
        return "/log_sms/sms_list";
    }

    @RequestMapping("/sms/record/list/json")
    @ResponseBody
    public Object recordJsonList(HttpServletRequest request) {
        // 第几页
        String page = request.getParameter("page");
        // 每页记录数
        String rows = request.getParameter("rows");
        // 组装查询条件
        Map<String, Object> filtersMap = publicService.assembleSearchFilters(request);
        // 计算总页数
        int curpage = Integer.parseInt(page);
        int pageCount = Integer.parseInt(rows);
        // 返回数据
        Map<String, Object> responseMap = new HashMap<>();
        try {
            // 短信发送记录
            List<SmsRecord> list = smsService.selectListByCondition(filtersMap);
            // 总的记录条数
            int totalCount = smsService.selectListCountByCondition(filtersMap);
            responseMap.put("records", totalCount);
            // 计算总页数
            int total = (int) Math.ceil((double) totalCount / pageCount);
            responseMap.put("total", total);
            responseMap.put("page", curpage);
            // 返回数据
            responseMap.put("rows", list);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return responseMap;
    }

    @RequestMapping("/api/log/list")
    public String apiLogList() {
        return "/log_sms/log_list";
    }

    @RequestMapping("/api/log/list/json")
    @ResponseBody
    public Object apiLogJsonList(HttpServletRequest request) {
        // 第几页
        String page = request.getParameter("page");
        // 每页记录数
        String rows = request.getParameter("rows");
        // 组装查询条件
        Map<String, Object> filtersMap = publicService.assembleSearchFilters(request);
        // 计算总页数
        int curpage = Integer.parseInt(page);
        int pageCount = Integer.parseInt(rows);
        // 返回数据
        Map<String, Object> responseMap = new HashMap<>();
        try {
            // 日志列表
            List<ApiLog> list = apiLogService.selectListByCondition(filtersMap);
            // 总的记录条数
            long totalCount = apiLogService.selectListCountByCondition(filtersMap);
            responseMap.put("records", totalCount);
            // 计算总页数
            int total = (int) Math.ceil((double) totalCount / pageCount);
            responseMap.put("total", total);
            responseMap.put("page", curpage);
            // 返回数据
            responseMap.put("rows", list);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return responseMap;
    }

}
