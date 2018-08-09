package pers.corgiframework.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.corgiframework.dao.domain.SysCategory;
import pers.corgiframework.dao.domain.SysCategoryExample;
import pers.corgiframework.dao.mapper.BaseMapper;
import pers.corgiframework.dao.mapper.SysCategoryMapper;
import pers.corgiframework.service.ISysCategoryService;
import pers.corgiframework.tool.utils.PropertiesUtil;

import java.util.List;

/**
 * Created by syk on 2018/8/3.
 */
@Service
public class SysCategoryServiceImpl extends BaseServiceImpl<SysCategory> implements ISysCategoryService {
    private final String CATEGORY_URL = PropertiesUtil.getString("category_url");

    @Autowired
    private SysCategoryMapper sysCategoryMapper;

    @Override
    protected BaseMapper<SysCategory> getMapper() {
        return sysCategoryMapper;
    }

    @Override
    public SysCategory selectByNameAndType(String name, Integer type) {
        SysCategory sysCategory = null;
        SysCategoryExample example = new SysCategoryExample();
        example.createCriteria().andCategoryTypeEqualTo(type).andCategoryNameEqualTo(name);
        List<SysCategory> list = sysCategoryMapper.selectByExample(example);
        if(!list.isEmpty()) {
            sysCategory = list.get(0);
        }
        return sysCategory;
    }

    @Override
    public List<SysCategory> selectByType(Integer type) {
        SysCategoryExample example = new SysCategoryExample();
        example.createCriteria().andCategoryTypeEqualTo(type);
        example.setOrderByClause("category_weight asc,create_time desc");
        List<SysCategory> list = sysCategoryMapper.selectByExample(example);
        for (SysCategory sysCategory : list){
            if (StringUtils.isNotBlank(sysCategory.getCategoryPic())){
                sysCategory.setCategoryPic(CATEGORY_URL + sysCategory.getCategoryPic());
            }
        }
        return list;
    }
}
