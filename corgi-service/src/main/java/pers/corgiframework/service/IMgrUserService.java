package pers.corgiframework.service;

import pers.corgiframework.dao.domain.MgrUser;
import pers.corgiframework.dao.domain.MgrUserExample;

/**
 * Created by syk on 2018/8/1.
 */
public interface IMgrUserService extends IBaseService<MgrUser> {
    MgrUser getMgrUserByExample(MgrUserExample mgrUserExample);
    MgrUser selectByAccount(String account);
    MgrUser selectByAccountOrEmail(String account, String email);
}
