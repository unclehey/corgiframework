package pers.corgiframework.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.corgiframework.dao.domain.AppVersion;
import pers.corgiframework.dao.mapper.AppVersionMapper;
import pers.corgiframework.dao.mapper.BaseMapper;
import pers.corgiframework.service.IAppVersionService;

/**
 * Created by syk on 2018/8/1.
 */
@Service
public class AppVersionServiceImpl extends BaseServiceImpl<AppVersion> implements IAppVersionService {

    @Autowired
    private AppVersionMapper appVersionMapper;

    @Override
    protected BaseMapper<AppVersion> getMapper() {
        return appVersionMapper;
    }
}
