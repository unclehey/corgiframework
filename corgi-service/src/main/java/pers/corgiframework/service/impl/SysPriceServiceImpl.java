package pers.corgiframework.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.corgiframework.dao.domain.SysPrice;
import pers.corgiframework.dao.mapper.BaseMapper;
import pers.corgiframework.dao.mapper.SysPriceMapper;
import pers.corgiframework.service.ISysPriceService;

/**
 * Created by syk on 2018/8/3.
 */
@Service
public class SysPriceServiceImpl extends BaseServiceImpl<SysPrice> implements ISysPriceService {

    @Autowired
    private SysPriceMapper sysPriceMapper;

    @Override
    protected BaseMapper<SysPrice> getMapper() {
        return sysPriceMapper;
    }
}
