package pers.corgiframework.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.corgiframework.dao.domain.MgrUser;
import pers.corgiframework.dao.domain.MgrUserExample;
import pers.corgiframework.dao.mapper.BaseMapper;
import pers.corgiframework.dao.mapper.MgrUserMapper;
import pers.corgiframework.service.IMgrUserService;

import java.util.List;

/**
 * Created by syk on 2018/8/1.
 */
@Service
public class MgrUserServiceImpl extends BaseServiceImpl<MgrUser> implements IMgrUserService {

    @Autowired
    private MgrUserMapper mgrUserMapper;

    @Override
    protected BaseMapper<MgrUser> getMapper() {
        return mgrUserMapper;
    }

    @Override
    public MgrUser getMgrUserByExample(MgrUserExample mgrUserExample) {
        MgrUser mgrUser = null;
        List<MgrUser> list = mgrUserMapper.selectByExample(mgrUserExample);
        if(!list.isEmpty()) {
            mgrUser = list.get(0);
        }
        return mgrUser;
    }

    @Override
    public MgrUser selectByAccount(String account) {
        MgrUser mgrUser = null;
        MgrUserExample example = new MgrUserExample();
        example.createCriteria().andAccountEqualTo(account);
        List<MgrUser> list = mgrUserMapper.selectByExample(example);
        if(!list.isEmpty()) {
            mgrUser = list.get(0);
        }
        return mgrUser;
    }

    @Override
    public MgrUser selectByAccountOrEmail(String account, String email) {
        MgrUser mgrUser = null;
        MgrUserExample example = new MgrUserExample();
        MgrUserExample.Criteria criteria = example.createCriteria();
        criteria.andEmailEqualTo(email);
        MgrUserExample.Criteria criteria2 = example.createCriteria();
        criteria2.andAccountEqualTo(account);
        example.or(criteria2);
        List<MgrUser> list = mgrUserMapper.selectByExample(example);
        if(!list.isEmpty()) {
            mgrUser = list.get(0);
        }
        return mgrUser;
    }

}
