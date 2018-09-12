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
import pers.corgiframework.dao.domain.SysCategory;
import pers.corgiframework.dao.domain.SysPrice;
import pers.corgiframework.dao.domain.SysProperties;
import pers.corgiframework.dao.model.BisPrompt;
import pers.corgiframework.service.*;
import pers.corgiframework.tool.constants.BisPromptConstant;
import pers.corgiframework.tool.enums.SysRedisEnum;
import pers.corgiframework.tool.utils.DateTimeUtil;
import pers.corgiframework.tool.utils.FileUtil;
import pers.corgiframework.tool.utils.JsonUtil;
import pers.corgiframework.tool.utils.PropertiesUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Created by syk on 2017/9/7.
 */
@Controller
@RequestMapping("/sys")
public class SysController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SysController.class);
    private final String CATEGORY_URL = PropertiesUtil.getString("category_url");
    private final String CATEGORY_PATH = PropertiesUtil.getString("category_path");

    @Autowired
    private ISysPropertiesService sysPropertiesService;
    @Autowired
    private ISysPriceService sysPriceService;
    @Autowired
    private ISysCategoryService sysCategoryService;
    @Autowired
    private IRedisService redisService;
    @Autowired
    private IPublicService publicService;
    
    @RequestMapping("/properties/list")
    public Object propertiesList() {
        return "/sys/properties_list";
    }

    @RequestMapping("/properties/list/json")
    @ResponseBody
    public Object propertiesListJson(HttpServletRequest request)  {
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
            // 系统配置列表
            List<SysProperties> list = sysPropertiesService.selectListByCondition(filtersMap);
            // 总的记录条数
            int totalCount = sysPropertiesService.selectListCountByCondition(filtersMap);
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
    
    @RequestMapping("/properties/enums")
    @ResponseBody
    public Object propertiesEnums() {
        List<Map<String, Object>> propertiesEnums = sysPropertiesService.getSysPropertiesNotInSql();
        return propertiesEnums;
    }

    @RequestMapping("/properties/insert")
    @ResponseBody
    public Object propertiesInsert(HttpServletRequest request) {
        String propKey = request.getParameter("propKey");
        String propValue = request.getParameter("propValue");
        String description = request.getParameter("description");
        BisPrompt bisPrompt = new BisPrompt();
        try {
            if (StringUtils.isNotBlank(propKey) && StringUtils.isNotBlank(propValue) && StringUtils.isNotBlank(description)) {
                // 查询该key值是否已存在
                SysProperties sysProperties = sysPropertiesService.selectByKey(propKey);
                if (null != sysProperties) {
                    // key值已存在
                    bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                    bisPrompt.setBisMsg("key值已存在");
                } else {
                    sysProperties = new SysProperties();
                    // 获取当前时间
                    LocalDateTime now = DateTimeUtil.getNowDateTime();
                    // 插入数据
                    sysProperties.setPropKey(propKey);
                    sysProperties.setPropValue(propValue);
                    sysProperties.setDescription(description);
                    sysProperties.setCreateTime(now);
                    sysProperties.setUpdateTime(now);
                    int flag = sysPropertiesService.insert(sysProperties);
                    if (flag == 1) {
                        // 添加成功 存入redis
                        redisService.setString(propKey, SysRedisEnum.valueOf(propKey).getTerm(), propValue);
                        bisPrompt.setBisObj(sysProperties);
                    } else {
                        bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                        bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.FAIL_STATUS));
                    }
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

    @RequestMapping("/properties/update")
    @ResponseBody
    public Object propertiesUpdate(HttpServletRequest request) {
        String updateId = request.getParameter("updateId");
        String updPropValue = request.getParameter("updPropValue");
        BisPrompt bisPrompt = new BisPrompt();
        try {
            if (StringUtils.isNotBlank(updateId) && StringUtils.isNotBlank(updPropValue)) {
                SysProperties sysProperties = sysPropertiesService.selectByPrimaryKey(Integer.valueOf(updateId));
                if (null != sysProperties) {
                    sysProperties.setPropValue(updPropValue);
                    sysProperties.setUpdateTime(DateTimeUtil.getNowDateTime());
                    int flag = sysPropertiesService.update(sysProperties);
                    if (flag == 1) {
                        // 更新成功 更新redis
                        String propKey = sysProperties.getPropKey();
                        redisService.setString(propKey, SysRedisEnum.valueOf(propKey).getTerm(), updPropValue);
                        bisPrompt.setBisObj(sysProperties);
                    } else {
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

    @RequestMapping("/properties/delete")
    @ResponseBody
    public Object propertiesDelete(HttpServletRequest request) {
        String delId = request.getParameter("delId");
        BisPrompt bisPrompt = new BisPrompt();
        try {
            if (StringUtils.isNotBlank(delId)) {
                Integer id = Integer.valueOf(delId);
                SysProperties sysProperties = sysPropertiesService.selectByPrimaryKey(id);
                if (null != sysProperties) {
                    int flag = sysPropertiesService.delete(id);
                    if (flag == 1) {
                        // 删除成功 删除redis
                        redisService.delByKey(sysProperties.getPropKey());
                    } else {
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

    @RequestMapping("/category/list")
    public Object categoryList() {
        return "/sys/category_list";
    }

    @RequestMapping("/category/list/json")
    @ResponseBody
    public Object categoryListJson(HttpServletRequest request)  {
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
            // 分类列表
            List<SysCategory> list = sysCategoryService.selectListByCondition(filtersMap);
            // 总的记录条数
            int totalCount = sysCategoryService.selectListCountByCondition(filtersMap);
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

    @RequestMapping("/category/add")
    @ResponseBody
    public Object sysCategoryAdd(HttpServletRequest request) {
        String categoryName = request.getParameter("categoryName");
        String categoryWeight = request.getParameter("categoryWeight");
        String categoryType = request.getParameter("categoryType");
        String categoryPic = request.getParameter("categoryPic");
        BisPrompt bisPrompt = new BisPrompt();
        try {
            if (StringUtils.isNotBlank(categoryName) && StringUtils.isNotBlank(categoryWeight)) {
                Integer type = Integer.valueOf(categoryType);
                SysCategory bookCategory = sysCategoryService.selectByNameAndType(categoryName, type);
                if (null != bookCategory) {
                    bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                    bisPrompt.setBisMsg("该分类名称已存在");
                } else {
                    bookCategory = new SysCategory();
                    bookCategory.setCategoryName(categoryName);
                    bookCategory.setCategoryWeight(Integer.valueOf(categoryWeight));
                    bookCategory.setCategoryType(type);
                    bookCategory.setCategoryPic(categoryPic);
                    // 获取当前时间
                    LocalDateTime now = DateTimeUtil.getNowDateTime();
                    bookCategory.setCreateTime(now);
                    bookCategory.setUpdateTime(now);
                    int flag = sysCategoryService.insert(bookCategory);
                    // 加路径
                    if( StringUtils.isNotBlank(bookCategory.getCategoryPic())){
                        bookCategory.setCategoryPic(CATEGORY_URL + bookCategory.getCategoryPic());
                    }
                    if (flag == 1) {
                        bisPrompt.setBisObj(bookCategory);
                    } else {
                        bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                        bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.FAIL_STATUS));
                    }
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

    @RequestMapping("/category/update")
    @ResponseBody
    public Object sysCategoryUpdate(HttpServletRequest request) {
        String updateId = request.getParameter("updateId");
        String categoryName = request.getParameter("updCategoryName");
        String categoryWeight = request.getParameter("updCategoryWeight");
        String categoryType = request.getParameter("updCategoryType");
        String updCategoryPic = request.getParameter("updCategoryPic");
        BisPrompt bisPrompt = new BisPrompt();
        try {
            if (StringUtils.isNotBlank(categoryName) && StringUtils.isNotBlank(updateId) && StringUtils.isNotBlank(categoryWeight)) {
                SysCategory bookCategory = sysCategoryService.selectByPrimaryKey(Integer.valueOf(updateId));
                if (null != bookCategory) {
                    Integer type = Integer.valueOf(categoryType);
                    SysCategory oldCategory = sysCategoryService.selectByNameAndType(categoryName, type);
                    if (null != oldCategory && !oldCategory.getId().equals(Integer.valueOf(updateId))) {
                        bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                        bisPrompt.setBisMsg("该分类名称已存在");
                    } else {
                        bookCategory.setCategoryName(categoryName);
                        bookCategory.setCategoryWeight(Integer.valueOf(categoryWeight));
                        bookCategory.setCategoryType(type);
                        if(StringUtils.isNotBlank(updCategoryPic)){
                            bookCategory.setCategoryPic(updCategoryPic);
                        }
                        bookCategory.setUpdateTime(DateTimeUtil.getNowDateTime());
                        int flag = sysCategoryService.update(bookCategory);
                        // 加路径
                        if( StringUtils.isNotBlank(bookCategory.getCategoryPic())){
                            bookCategory.setCategoryPic(CATEGORY_URL + bookCategory.getCategoryPic());
                        }
                        if (flag == 1) {
                            bisPrompt.setBisObj(bookCategory);
                        } else {
                            bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                            bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.FAIL_STATUS));
                        }
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

    @RequestMapping("/category/del")
    @ResponseBody
    public Object categoryDel(HttpServletRequest request) {
        String delId = request.getParameter("delId");
        BisPrompt bisPrompt = new BisPrompt();
        try {
            if (StringUtils.isNotBlank(delId)) {
                Integer categoryId = Integer.valueOf(delId);
                BookCategory category = sysCategoryService.selectByPrimaryKey(categoryId);
                if (null != category) {
                    int flag = sysCategoryService.deleteByPrimaryKey(categoryId);
                    if (flag == 1) {
                        // 删除成功
                        bisPrompt.setBisStatus(BisPromptConstant.SUCCESS_STATUS);
                        bisPrompt.setBisMsg(BisPromptConstant.SUCCESS_MESSAGE);
                    } else {
                        bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                        bisPrompt.setBisMsg(BisPromptConstant.FAIL_MESSAGE);
                    }
                } else {
                    // 非法请求
                    bisPrompt.setBisStatus(BisPromptConstant.INVALID_REQUEST_STATUS);
                    bisPrompt.setBisMsg(BisPromptConstant.INVALID_REQUEST_MESSAGE);
                }
            } else {
                // 参数有误
                bisPrompt.setBisStatus(BisPromptConstant.INVALID_REQUEST_STATUS);
                bisPrompt.setBisMsg(BisPromptConstant.INVALID_REQUEST_MESSAGE);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return bisPrompt;
    }

    @RequestMapping("/upload/image")
    public void uploadCategoryImage(HttpServletResponse response, @RequestParam MultipartFile addAttr) {
        try {
            if (null != addAttr) {
                // 调用图片上传工具类
                String fileName = FileUtil.upload(addAttr, null, CATEGORY_PATH);
                Map<String, Object> map = Maps.newHashMap();
                map.put("result", 1);
                map.put("url", fileName);
                response.getWriter().write(JsonUtil.objectToJson(map));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @RequestMapping("/upload/image/upd")
    public void uploadCategoryImageUpd(HttpServletResponse response, @RequestParam MultipartFile addAttrUpd) {
        try {
            if (null != addAttrUpd) {
                // 调用图片上传工具类
                String fileName = FileUtil.upload(addAttrUpd, null, CATEGORY_PATH);
                Map<String, Object> map = Maps.newHashMap();
                map.put("result", 1);
                map.put("url", fileName);
                response.getWriter().write(JsonUtil.objectToJson(map));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @RequestMapping("/price/list")
    public Object priceList() {
        return "/sys/price_list";
    }

    @RequestMapping("/price/list/json")
    @ResponseBody
    public Object priceListJson(HttpServletRequest request)  {
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
            // 价格列表
            List<SysPrice> list = sysPriceService.selectListByCondition(filtersMap);
            // 总的记录条数
            int totalCount = sysPriceService.selectListCountByCondition(filtersMap);
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

    @RequestMapping("/price/add")
    @ResponseBody
    public Object priceAdd(HttpServletRequest request) {
        String tycoonprice = request.getParameter("price");
        String title = request.getParameter("title");
        String type = request.getParameter("type");
        String duration = request.getParameter("duration");
        BisPrompt bisPrompt = new BisPrompt();
        try {
            if (StringUtils.isNotBlank(tycoonprice) && StringUtils.isNotBlank(title)
                    && StringUtils.isNotBlank(duration)) {
                SysPrice price = new SysPrice();
                price.setTitle(title);
                price.setType(Integer.valueOf(type));
                price.setPrice(new BigDecimal(tycoonprice));
                price.setDuration(Integer.valueOf(duration));
                // 获取当前时间
                LocalDateTime now = DateTimeUtil.getNowDateTime();
                price.setCreateTime(now);
                price.setUpdateTime(now);
                int flag = sysPriceService.insert(price);
                if (flag == 1) {
                    bisPrompt.setBisObj(price);
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

    @RequestMapping("/price/update")
    @ResponseBody
    public Object priceUpdate(HttpServletRequest request) {
        String tycoonprice = request.getParameter("price");
        String title = request.getParameter("title");
        String type = request.getParameter("type");
        String duration = request.getParameter("duration");
        String id = request.getParameter("id");
        BisPrompt bisPrompt = new BisPrompt();
        try {
            if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(tycoonprice)
                    && StringUtils.isNotBlank(title) && StringUtils.isNotBlank(duration)) {
                SysPrice price = sysPriceService.selectByPrimaryKey(Integer.valueOf(id));
                if (null != price) {
                    price.setTitle(title);
                    price.setType(Integer.valueOf(type));
                    price.setPrice(new BigDecimal(tycoonprice));
                    price.setDuration(Integer.valueOf(duration));
                    price.setUpdateTime(DateTimeUtil.getNowDateTime());
                    int flag = sysPriceService.update(price);
                    if (flag == 1) {
                        bisPrompt.setBisObj(price);
                    } else {
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

    @RequestMapping("/price/del")
    @ResponseBody
    public Object priceDel(HttpServletRequest request) {
        String priceId = request.getParameter("priceId");
        BisPrompt bisPrompt = new BisPrompt();
        try {
            if(StringUtils.isNotBlank(priceId)){
                Integer id = Integer.valueOf(priceId);
                int flag = sysPriceService.delete(id);
                if (flag != 1) {
                    bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                    bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.FAIL_STATUS));
                }
            }else{
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
