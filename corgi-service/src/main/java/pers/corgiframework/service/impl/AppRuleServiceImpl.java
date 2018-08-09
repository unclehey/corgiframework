package pers.corgiframework.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.corgiframework.dao.domain.AppRule;
import pers.corgiframework.dao.mapper.AppRuleMapper;
import pers.corgiframework.dao.mapper.BaseMapper;
import pers.corgiframework.service.IAppRuleService;

/**
 * Created by syk on 2018/8/1.
 */
@Service
public class AppRuleServiceImpl extends BaseServiceImpl<AppRule> implements IAppRuleService {

    @Autowired
    private AppRuleMapper appRuleMapper;

    @Override
    protected BaseMapper<AppRule> getMapper() {
        return appRuleMapper;
    }
}
