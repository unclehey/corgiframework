package pers.corgiframework.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pers.corgiframework.dao.domain.AppBanner;
import pers.corgiframework.dao.mapper.AppBannerMapper;
import pers.corgiframework.dao.mapper.BaseMapper;
import pers.corgiframework.service.IAppBannerService;
import pers.corgiframework.tool.utils.PropertiesUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by syk on 2018/8/1.
 */
@Service
public class AppBannerServiceImpl extends BaseServiceImpl<AppBanner> implements IAppBannerService {
    private final String BANNER_URL = PropertiesUtil.getString("banner_url");

    @Autowired
    private AppBannerMapper appBannerMapper;

    @Override
    protected BaseMapper<AppBanner> getMapper() {
        return appBannerMapper;
    }

    @Override
    public List<AppBanner> selectAppBannersByCondition(Map<String, Object> map) {
        List<AppBanner> appBanners = appBannerMapper.selectListByCondition(map);
        if (!CollectionUtils.isEmpty(appBanners)) {
            for (AppBanner appBanner : appBanners) {
                appBanner.setPicUrl(BANNER_URL + appBanner.getPicUrl());
            }
        }
        return appBanners;
    }
}
