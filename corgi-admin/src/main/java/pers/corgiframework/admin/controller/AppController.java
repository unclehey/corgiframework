package pers.corgiframework.admin.controller;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import pers.corgiframework.dao.domain.AppBanner;
import pers.corgiframework.dao.domain.AppRule;
import pers.corgiframework.dao.domain.AppVersion;
import pers.corgiframework.dao.model.BisPrompt;
import pers.corgiframework.service.IAppBannerService;
import pers.corgiframework.service.IAppRuleService;
import pers.corgiframework.service.IAppVersionService;
import pers.corgiframework.service.IPublicService;
import pers.corgiframework.tool.constants.BisPromptConstant;
import pers.corgiframework.tool.utils.DateTimeUtil;
import pers.corgiframework.tool.utils.FileUtil;
import pers.corgiframework.tool.utils.PropertiesUtil;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Created by syk on 2018/8/1.
 */
@Controller
@RequestMapping("/app")
public class AppController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppController.class);
    private final String BANNER_URL = PropertiesUtil.getString("banner_url");
    private final String BANNER_PATH = PropertiesUtil.getString("banner_path");

    @Autowired
    private IAppBannerService appBannerService;
    @Autowired
    private IAppRuleService appRuleService;
    @Autowired
    private IAppVersionService appVersionService;
    @Autowired
    private IPublicService publicService;

    @RequestMapping("/banner/list")
    public Object bannerList() {
        return "/app/banner_list";
    }

    @RequestMapping("/banner/list/json")
    @ResponseBody
    public Object bannerListJson(HttpServletRequest request)  {
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
        Map<String, Object> responseMap = Maps.newHashMap();
        try {
            // banner列表
            List<AppBanner> list = appBannerService.selectAppBannersByCondition(filtersMap);
            // 总的记录条数
            int totalCount = appBannerService.selectListCountByCondition(filtersMap);
            responseMap.put("records", totalCount);
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

    @RequestMapping("/banner/upload")
    @ResponseBody
    public Object bannerUpload(@RequestParam MultipartFile addAttr) {
        Map<String, Object> map = Maps.newHashMap();
        try {
            if (null != addAttr) {
                // 调用图片上传工具类
                String fileName = FileUtil.upload(addAttr, null, BANNER_PATH);
                map.put("result", 1);
                map.put("url", fileName);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return  map;
    }

    @RequestMapping("/banner/upload/update")
    @ResponseBody
    public Object bannerUploadUpdate(@RequestParam MultipartFile updAddAttr) {
        Map<String, Object> map = Maps.newHashMap();
        try {
            if (null != updAddAttr) {
                // 调用图片上传工具类
                String fileName = FileUtil.upload(updAddAttr, null, BANNER_PATH);
                map.put("result", 1);
                map.put("url", fileName);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return  map;
    }

    @RequestMapping("/banner/insert")
    @ResponseBody
    public Object bannerInsert(HttpServletRequest request) {
        String picType = request.getParameter("pic_type");
        String picTitle = request.getParameter("pic_title");
        String picUrl = request.getParameter("pic_url");
        String linkUrl = request.getParameter("link_url");
        String picWeight = request.getParameter("picWeight");
        BisPrompt bisPrompt = new BisPrompt();
        try {
            if (StringUtils.isNotBlank(picTitle) && StringUtils.isNotBlank(picUrl) && StringUtils.isNotBlank(picWeight)) {
                AppBanner appBanner = new AppBanner();
                appBanner.setPicTitle(picTitle);
                appBanner.setStatus(0);
                appBanner.setPicType(Integer.valueOf(picType));
                appBanner.setPicUrl(picUrl);
                appBanner.setLinkUrl(linkUrl);
                appBanner.setPicWeight(Integer.valueOf(picWeight));
                // 获取当前时间
                LocalDateTime now = DateTimeUtil.getNowDateTime();
                appBanner.setCreateTime(now);
                appBanner.setUpdateTime(now);
                int flag = appBannerService.insert(appBanner);
                if (flag == 1) {
                    appBanner.setPicUrl(BANNER_URL + appBanner.getPicUrl());
                    bisPrompt.setBisObj(appBanner);
                } else {
                    bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                    bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.FAIL_STATUS));
                }
            } else {
                // 非法请求
                bisPrompt.setBisStatus(BisPromptConstant.INVALID_REQUEST_STATUS);
                bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.INVALID_REQUEST_STATUS));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return bisPrompt;
    }

    @RequestMapping("/banner/update")
    @ResponseBody
    public Object bannerUpdate(HttpServletRequest request) {
        String updateId = request.getParameter("updateId");
        String updPicType = request.getParameter("updPicType");
        String updPicTitle = request.getParameter("updPicTitle");
        String updPicUrl = request.getParameter("updPicUrl");
        String updLinkUrl = request.getParameter("updLinkUrl");
        String updStatus = request.getParameter("updStatus");
        String updPicWeight = request.getParameter("updPicWeight");
        BisPrompt bisPrompt = new BisPrompt();
        try {
            if (StringUtils.isNotBlank(updateId) && StringUtils.isNotBlank(updPicTitle) && StringUtils.isNotBlank(updPicWeight)) {
                AppBanner appBanner = appBannerService.selectByPrimaryKey(Integer.valueOf(updateId));
                if (null != appBanner) {
                    appBanner.setPicTitle(updPicTitle);
                    appBanner.setPicType(Integer.valueOf(updPicType));
                    if (StringUtils.isNotBlank(updPicUrl)) {
                        appBanner.setPicUrl(updPicUrl);
                    }
                    appBanner.setLinkUrl(updLinkUrl);
                    appBanner.setStatus(Integer.valueOf(updStatus));
                    appBanner.setPicWeight(Integer.valueOf(updPicWeight));
                    appBanner.setUpdateTime(DateTimeUtil.getNowDateTime());
                    int flag = appBannerService.update(appBanner);
                    if (flag == 1) {
                        appBanner.setPicUrl(BANNER_URL + appBanner.getPicUrl());
                        bisPrompt.setBisObj(appBanner);
                    } else {
                        // 操作失败
                        bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                        bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.FAIL_STATUS));
                    }
                } else {
                    // 非法请求
                    bisPrompt.setBisStatus(BisPromptConstant.INVALID_REQUEST_STATUS);
                    bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.INVALID_REQUEST_STATUS));
                }
            } else {
                // 非法请求
                bisPrompt.setBisStatus(BisPromptConstant.INVALID_REQUEST_STATUS);
                bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.INVALID_REQUEST_STATUS));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return bisPrompt;
    }

    @RequestMapping("/banner/delete")
    @ResponseBody
    public Object bannerDelete(HttpServletRequest request) {
        String delId = request.getParameter("delId");
        BisPrompt bisPrompt = new BisPrompt();
        try {
            if (StringUtils.isNotBlank(delId)) {
                Integer id = Integer.valueOf(delId);
                AppBanner appBanner = appBannerService.selectByPrimaryKey(id);
                if (null != appBanner) {
                    int flag = appBannerService.delete(id);
                    if (flag != 1) {
                        // 操作失败
                        bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                        bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.FAIL_STATUS));
                    }
                } else {
                    // 非法请求
                    bisPrompt.setBisStatus(BisPromptConstant.INVALID_REQUEST_STATUS);
                    bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.INVALID_REQUEST_STATUS));
                }
            } else {
                // 非法请求
                bisPrompt.setBisStatus(BisPromptConstant.INVALID_REQUEST_STATUS);
                bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.INVALID_REQUEST_STATUS));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return bisPrompt;
    }

    @RequestMapping("/rule/list")
    public Object ruleList() {
        return "/app/rule_list";
    }

    @RequestMapping("/rule/list/json")
    @ResponseBody
    public Object ruleListJson(HttpServletRequest request)  {
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
        Map<String, Object> responseMap = Maps.newHashMap();
        try {
            // 规则列表
            List<AppRule> list = appRuleService.selectListByCondition(filtersMap);
            // 总的记录条数
            int totalCount = appRuleService.selectListCountByCondition(filtersMap);
            responseMap.put("records", totalCount);
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

    @RequestMapping("/rule/insert")
    @ResponseBody
    public Object ruleInsert(HttpServletRequest request) {
        String rule_name = request.getParameter("rule_name");
        String rule_type = request.getParameter("rule_type");
        String rule_content = request.getParameter("rule_content");
        String start_date = request.getParameter("start_date");
        String end_date = request.getParameter("end_date");
        BisPrompt bisPrompt = new BisPrompt();
        try {
            if (StringUtils.isNotBlank(rule_name) && StringUtils.isNotBlank(rule_content)
                    && StringUtils.isNotBlank(rule_type)) {
                AppRule appRule = new AppRule();
                if( StringUtils.isNotBlank(start_date) && StringUtils.isNotBlank(end_date)){
                    LocalDate startDate = DateTimeUtil.parseStrToLocalDate(start_date, DateTimeUtil.FORMAT_SHORT_LINE);
                    LocalDate endDate = DateTimeUtil.parseStrToLocalDate(end_date, DateTimeUtil.FORMAT_SHORT_LINE);
                    if (startDate.isAfter(endDate)) {
                        // 开始日期不能晚于结束日期
                        bisPrompt.setBisStatus(BisPromptConstant.START_END_DATE_STATUS);
                        bisPrompt.setBisMsg(BisPromptConstant.BUSINESS_MAP.get(BisPromptConstant.START_END_DATE_STATUS));
                        return bisPrompt;
                    }
                    appRule.setStartDate(startDate);
                    appRule.setEndDate(endDate);
                }
                appRule.setRuleName(rule_name);
                appRule.setRuleType(Integer.valueOf(rule_type));
                appRule.setRuleContent(rule_content);
                appRule.setStatus(0);
                // 获取当前时间
                LocalDateTime now = DateTimeUtil.getNowDateTime();
                appRule.setCreateTime(now);
                appRule.setUpdateTime(now);
                int flag = appRuleService.insert(appRule);
                if (flag == 1) {
                    bisPrompt.setBisObj(appRule);
                } else {
                    // 操作失败
                    bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                    bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.FAIL_STATUS));
                }
            } else {
                // 非法请求
                bisPrompt.setBisStatus(BisPromptConstant.INVALID_REQUEST_STATUS);
                bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.INVALID_REQUEST_STATUS));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return bisPrompt;
    }

    @RequestMapping("/rule/delete")
    @ResponseBody
    public Object deleteRole(HttpServletRequest request) {
        String removeId = request.getParameter("removeId");
        BisPrompt bisPrompt = new BisPrompt();
        try {
            if(StringUtils.isNotBlank(removeId)){
                int flag = appRuleService.delete(Integer.valueOf(removeId));
                if (flag != 1) {
                    // 操作失败
                    bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                    bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.FAIL_STATUS));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bisPrompt;
    }

    @RequestMapping("/rule/update")
    @ResponseBody
    public Object updateRule(HttpServletRequest request) {
        String updId = request.getParameter("updId");
        String ruleName = request.getParameter("ruleName");
        String ruleType = request.getParameter("ruleType");
        String ruleContent = request.getParameter("ruleContent");
        String status = request.getParameter("status");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        BisPrompt bisPrompt = new BisPrompt();
        try {
            if (StringUtils.isNotBlank(updId) && StringUtils.isNotBlank(ruleName)
                    && StringUtils.isNotBlank(ruleType) && StringUtils.isNotBlank(ruleContent) && StringUtils.isNotBlank(status)) {
                AppRule appRule = appRuleService.selectByPrimaryKey(Integer.valueOf(updId));
                if (null != appRule) {
                    if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
                        LocalDate start = DateTimeUtil.parseStrToLocalDate(startDate, DateTimeUtil.FORMAT_SHORT_LINE);
                        LocalDate end = DateTimeUtil.parseStrToLocalDate(endDate, DateTimeUtil.FORMAT_SHORT_LINE);
                        if (start.isAfter(end)) {
                            // 开始日期不能晚于结束日期
                            bisPrompt.setBisStatus(BisPromptConstant.START_END_DATE_STATUS);
                            bisPrompt.setBisMsg(BisPromptConstant.BUSINESS_MAP.get(BisPromptConstant.START_END_DATE_STATUS));
                            return bisPrompt;
                        }
                        appRule.setStartDate(start);
                        appRule.setEndDate(end);
                    }
                    appRule.setRuleName(ruleName);
                    appRule.setRuleType(Integer.valueOf(ruleType));
                    appRule.setRuleContent(ruleContent);
                    appRule.setStatus(Integer.valueOf(status));
                    appRule.setUpdateTime(DateTimeUtil.getNowDateTime());
                    int flag = appRuleService.update(appRule);
                    if (flag == 1) {
                        bisPrompt.setBisObj(appRule);
                    } else {
                        // 操作失败
                        bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                        bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.FAIL_STATUS));
                    }
                } else {
                    // 非法请求
                    bisPrompt.setBisStatus(BisPromptConstant.INVALID_REQUEST_STATUS);
                    bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.INVALID_REQUEST_STATUS));
                }
            } else {
                // 非法请求
                bisPrompt.setBisStatus(BisPromptConstant.INVALID_REQUEST_STATUS);
                bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.INVALID_REQUEST_STATUS));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bisPrompt;
    }

    @RequestMapping("/version/list")
    public Object versionList() {
        return "/app/version_list";
    }

    @RequestMapping("/version/list/json")
    @ResponseBody
    public Object versionListJson(HttpServletRequest request)  {
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
        Map<String, Object> responseMap = Maps.newHashMap();
        try {
            // 版本列表
            List<AppVersion> list = appVersionService.selectListByCondition(filtersMap);
            // 总的记录条数
            int totalCount = appVersionService.selectListCountByCondition(filtersMap);
            responseMap.put("records", totalCount);
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

    @RequestMapping("/version/insert")
    @ResponseBody
    public Object versionInsert(HttpServletRequest request) {
        String versionNumber = request.getParameter("versionNumber");
        String apiVersion = request.getParameter("apiVersion");
        String description = request.getParameter("description");
        String downloadUrl = request.getParameter("downloadUrl");
        String operatingSystem = request.getParameter("operatingSystem");
        String forceUpgrade = request.getParameter("forceUpgrade");
        BisPrompt bisPrompt = new BisPrompt();
        try {
            if (StringUtils.isNotBlank(versionNumber) && StringUtils.isNotBlank(apiVersion) && StringUtils.isNotBlank(description) && StringUtils.isNotBlank(downloadUrl)) {
                AppVersion appVersion = new AppVersion();
                appVersion.setVersionNumber(versionNumber);
                appVersion.setApiVersion(apiVersion);
                appVersion.setDescription(description);
                appVersion.setDownloadUrl(downloadUrl);
                appVersion.setForceUpgrade(Integer.valueOf(forceUpgrade));
                appVersion.setOperatingSystem(operatingSystem);
                // 获取当前时间
                LocalDateTime now = DateTimeUtil.getNowDateTime();
                appVersion.setCreateTime(now);
                appVersion.setUpdateTime(now);
                int flag = appVersionService.insert(appVersion);
                if (flag == 1) {
                    bisPrompt.setBisObj(appVersion);
                } else {
                    // 操作失败
                    bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                    bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.FAIL_STATUS));
                }
            } else {
                // 非法请求
                bisPrompt.setBisStatus(BisPromptConstant.INVALID_REQUEST_STATUS);
                bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.INVALID_REQUEST_STATUS));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return bisPrompt;
    }

    @RequestMapping("/version/delete")
    @ResponseBody
    public Object versionDelete(HttpServletRequest request) {
        String Id = request.getParameter("Id");
        BisPrompt bisPrompt = new BisPrompt();
        try {
            if(StringUtils.isNotBlank(Id)){
                int flag = appVersionService.delete(Integer.valueOf(Id));
                if (flag != 1) {
                    // 操作失败
                    bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                    bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.FAIL_STATUS));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bisPrompt;
    }

    @RequestMapping("/version/update")
    @ResponseBody
    public Object versionUpdate(HttpServletRequest request) {
        String Id = request.getParameter("Id");
        String versionNumber = request.getParameter("upVersionNumber");
        String apiVersion = request.getParameter("upApiVersion");
        String description = request.getParameter("upDescription");
        String downloadUrl = request.getParameter("upDownloadUrl");
        String operatingSystem = request.getParameter("upOperatingSystem");
        String forceUpgrade = request.getParameter("upForceUpgrade");
        BisPrompt bisPrompt = new BisPrompt();
        try {
            if (StringUtils.isNotBlank(Id) && StringUtils.isNotBlank(versionNumber) && StringUtils.isNotBlank(apiVersion) && StringUtils.isNotBlank(description) && StringUtils.isNotBlank(downloadUrl)) {
                AppVersion appVersion = appVersionService.selectByPrimaryKey(Integer.parseInt(Id));
                if (null != appVersion) {
                    appVersion.setVersionNumber(versionNumber);
                    appVersion.setApiVersion(apiVersion);
                    appVersion.setDescription(description);
                    appVersion.setDownloadUrl(downloadUrl);
                    appVersion.setOperatingSystem(operatingSystem);
                    appVersion.setForceUpgrade(Integer.valueOf(forceUpgrade));
                    appVersion.setUpdateTime(DateTimeUtil.getNowDateTime());
                    int flag = appVersionService.update(appVersion);
                    if (flag == 1) {
                        bisPrompt.setBisObj(appVersion);
                    } else {
                        // 操作失败
                        bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                        bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.FAIL_STATUS));
                    }
                } else {
                    // 非法请求
                    bisPrompt.setBisStatus(BisPromptConstant.INVALID_REQUEST_STATUS);
                    bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.INVALID_REQUEST_STATUS));
                }
            } else {
                // 非法请求
                bisPrompt.setBisStatus(BisPromptConstant.INVALID_REQUEST_STATUS);
                bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.INVALID_REQUEST_STATUS));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return bisPrompt;
    }

}
