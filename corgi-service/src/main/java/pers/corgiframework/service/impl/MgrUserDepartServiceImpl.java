package pers.corgiframework.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.corgiframework.dao.domain.*;
import pers.corgiframework.dao.mapper.MgrDepartMapper;
import pers.corgiframework.dao.mapper.MgrUserDepartMapper;
import pers.corgiframework.dao.mapper.MgrUserMapper;
import pers.corgiframework.service.IMgrDepartService;
import pers.corgiframework.service.IMgrUserDepartService;
import pers.corgiframework.tool.utils.DateTimeUtil;
import pers.corgiframework.tool.utils.JsonUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by syk on 2017/4/2.
 */
@Service
public class MgrUserDepartServiceImpl implements IMgrUserDepartService {

    @Autowired
    private MgrUserMapper mgrUserMapper;
    @Autowired
    private MgrUserDepartMapper mgrUserDepartMapper;
    @Autowired
    private IMgrDepartService mgrDepartService;
    @Autowired
    private MgrDepartMapper mgrDepartMapper;

    /**
     * 关联一个部门和用户
     *
     */
    @Override
    public void insert(Integer userId, Integer departId){
        // 获取当前时间
        LocalDateTime currentTime = DateTimeUtil.getNowDateTime();
        MgrUserDepart mgrUserDepart = new MgrUserDepart();
        mgrUserDepart.setDepartId(departId);
        mgrUserDepart.setUserId(userId);
        mgrUserDepart.setCreateTime(currentTime);
        mgrUserDepart.setUpdateTime(currentTime);
        mgrUserDepart.setRemark("给部门分配人员");
        mgrUserDepartMapper.insertSelective(mgrUserDepart);
    }

    /**
     * 根据部门id删除一条关联
     *
     */
    @Override
    public void deleteByDepart(String departId){

        List<MgrUserDepart> userDepartList = this.getByDepart(departId);
        for(MgrUserDepart mgrUserDepart: userDepartList){

            Integer proId = mgrUserDepart.getId();
            mgrUserDepartMapper.deleteByPrimaryKey(proId);
        }
    }

    /**
     * 通过部门id获取所有关联
     *
     */
    @Override
    public List<MgrUserDepart> getByDepart(String departId){

        MgrUserDepartExample example = new MgrUserDepartExample();
        example.createCriteria().andDepartIdEqualTo(Integer.parseInt(departId));
        List<MgrUserDepart> mgrUserList = mgrUserDepartMapper.selectByExample(example);
        return mgrUserList;
    }

    /**
     * 通过部门id获取所有关联
     *
     */
    @Override
    public List<MgrUserDepart> getByUser(Integer userId){

        MgrUserDepartExample example = new MgrUserDepartExample();
        example.createCriteria().andUserIdEqualTo(Integer.valueOf(userId));
        List<MgrUserDepart> mgrUserDepartList = mgrUserDepartMapper.selectByExample(example);
        return mgrUserDepartList;
    }

    /**
     * 超级管理员获取所有用户
     *
     */
    @Override
    public List<MgrUser> getUser(){
        MgrUserExample example = new MgrUserExample();
        example.createCriteria().andAccountNotEqualTo("admin");
        return mgrUserMapper.selectByExample(example);
    }

    /**
     * 通过登录用户id获取所属部门及子部门的所有用户
     *
     */
    @Override
    public List<MgrUser> getUserByUserId(String userId){
        List<MgrUserDepart> mgrUserDepartList = this.getByUser(Integer.parseInt(userId));
        List<MgrDepart> mgrDepartList = new ArrayList<MgrDepart>();
        for(MgrUserDepart mgrUserDepart: mgrUserDepartList){
            Integer departId = mgrUserDepart.getDepartId();
            mgrDepartList.addAll(mgrDepartService.getAllByProId(departId));
        }

        List<Integer> departIdList = new ArrayList<Integer>();
        for(MgrDepart mgrDepart: mgrDepartList){
            Integer departId = mgrDepart.getId();
            departIdList.add(departId);
        }

        MgrUserDepartExample mgrUserDepartExample = new MgrUserDepartExample();
        mgrUserDepartExample.createCriteria().andDepartIdIn(departIdList);
        List<MgrUserDepart> userDepartList = mgrUserDepartMapper.selectByExample(mgrUserDepartExample);

        List<Integer> userIdList = new ArrayList<Integer>();
        for(MgrUserDepart mgrUserDepart: userDepartList){
            Integer userIdResult = mgrUserDepart.getUserId();
            userIdList.add(userIdResult);
        }

        MgrUserExample mgrUserExample = new MgrUserExample();
        mgrUserExample.createCriteria().andIdIn(userIdList);
        return mgrUserMapper.selectByExample(mgrUserExample);
    }

    /**
     * 通过登录用户id获取所属部门及子部门的所有用户
     *
     */
    @Override
    public List<Map<String,Object>> getDepartUserByUserId(String userId){

        List<MgrUserDepart> mgrUserDepartList = this.getByUser(Integer.parseInt(userId));
        List<Map<String, Object>> departUserList = new ArrayList<Map<String, Object>>();

        for(MgrUserDepart mgrUserDepart: mgrUserDepartList){
            Integer departId = mgrUserDepart.getDepartId();
            this.getAllUserByParentDepartId(departUserList, departId);
        }

        Map<Object, Map<String, Object>> departUserMap = new HashMap<Object, Map<String, Object>>();
        for(Map<String, Object> departUser: departUserList){
            Object account = departUser.get("account");
            Object departName = departUser.get("departName");
            Object departId = departUser.get("departId");
            if(departUserMap.containsKey(account)
                    && !(departUserMap.get(account).get("departId")+"").contains(departId+"")){
                Map<String, Object> theOneInMap = departUserMap.get(account);
                Object theDepartName = theOneInMap.get("departName");
                theDepartName = theDepartName+","+departName;
                Object theDepartId = theOneInMap.get("departId");
                theDepartId = theDepartId+","+departId;
                theOneInMap.put("departName", theDepartName);
                theOneInMap.put("departId", theDepartId);
            }else{
                departUserMap.put(account, departUser);
            }
        }

        departUserList.clear();

        for (Object key : departUserMap.keySet()) {
            departUserList.add(departUserMap.get(key));
        }

        return departUserList;
    }

    /**
     * 通过登录用户id获取所属部门及子部门的所有用户
     *
     */
    @Override
    public List<Map<String,Object>> getAllDepartUserByUserId(){

        List<Map<String, Object>> departUserList = mgrUserDepartMapper.selectAllUserByDepart();

        Map<Object, Map<String, Object>> departUserMap = new HashMap<Object, Map<String, Object>>();
        for(Map<String, Object> departUser: departUserList){
            Object account = departUser.get("account");
            Object departName = departUser.get("departName");
            if(departUserMap.containsKey(account)){
                Map<String, Object> theOneInMap = departUserMap.get(account);
                Object theDepartName = theOneInMap.get("departName");
                theDepartName = theDepartName+","+departName;
                theOneInMap.put("departName", theDepartName);
            }else{
                departUserMap.put(account, departUser);
            }
        }

        departUserList.clear();

        for (Object key : departUserMap.keySet()) {
            departUserList.add(departUserMap.get(key));
        }

        return departUserList;
    }


    /**
     * 通过部门id获取本部门及子部门的所有用户
     *
     */
    @Override
    public List<MgrUser> getUserByDepart(String departId){
        return mgrUserDepartMapper.selectByDepart(Integer.parseInt(departId));
    }

    /**
     * 给某个部门分配用户
     *
     */
    @SuppressWarnings("unchecked")
    @Override
    public void giveUser(String userIds, String departId){

        //1、获取所有的用户id
        List<String> userIdsList = JsonUtil.readValue(userIds, List.class);

        //2、通过部门id获取所有部门用户关联
        List<MgrUserDepart> userList = this.getByDepart(departId);

        Map<Integer, Integer> userMap = new HashMap<Integer, Integer>();
        Map<Integer, Integer> userMapNew = new HashMap<Integer, Integer>();

        //3、循环用户部门关联列表并放入map中
        for(MgrUserDepart mgrUserDepart: userList){
            Integer key = mgrUserDepart.getUserId();
            Integer value = mgrUserDepart.getId();
            userMap.put(key, value);
        }

        //4、循环传入用户列表如果没有则新增，并把关联列表放入map中
        for(String userId: userIdsList){
            userMapNew.put(Integer.parseInt(userId), 33);
            if(userMap.get(Integer.parseInt(userId)) == null){
                this.insert(Integer.parseInt(userId), Integer.parseInt(departId));
            }
        }

        //5、循环部门用户关联列表如果传入用户列表中没有，则删除
        for(MgrUserDepart mgrUserDepart: userList){
            Integer key = mgrUserDepart.getUserId();
            Integer value = mgrUserDepart.getId();
            if(userMapNew.get(key) == null){
                mgrUserDepartMapper.deleteByPrimaryKey(value);
            }
        }
    }


    private void getAllUserByParentDepartId(List<Map<String, Object>> list, Integer departId){
        List<Map<String, Object>> listSon = mgrUserDepartMapper.selectUserByDepart(departId);
        list.addAll(listSon);

        MgrDepartExample  example = new MgrDepartExample();
        example.createCriteria().andParentIdEqualTo(departId);
        List<MgrDepart> mgrDepartList = mgrDepartMapper.selectByExample(example);
        for(int i=0; i<mgrDepartList.size(); i++){
            MgrDepart mgrDepart = mgrDepartList.get(i);
            Integer departIdSon = mgrDepart.getId();
            this.getAllUserByParentDepartId(list, departIdSon);
        }
    }

}
