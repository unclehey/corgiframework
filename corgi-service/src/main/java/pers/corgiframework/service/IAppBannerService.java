package pers.corgiframework.service;

import pers.corgiframework.dao.domain.AppBanner;

import java.util.List;
import java.util.Map;

/**
 * Created by syk on 2018/8/1.
 */
public interface IAppBannerService extends IBaseService<AppBanner> {
    List<AppBanner> selectAppBannersByCondition(Map<String, Object> map);
}
