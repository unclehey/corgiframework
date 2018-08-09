package pers.corgiframework.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.corgiframework.dao.domain.*;
import pers.corgiframework.dao.mapper.MgrDepartFuncMapper;
import pers.corgiframework.dao.mapper.MgrFuncMapper;
import pers.corgiframework.service.IMgrDepartFuncService;
import pers.corgiframework.service.IMgrFuncService;
import pers.corgiframework.service.IMgrUserDepartService;
import pers.corgiframework.tool.enums.MgrFuncEnum;
import pers.corgiframework.tool.utils.DateTimeUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by syk on 2017/4/2.
 */
@Service
public class MgrFuncServiceImpl implements IMgrFuncService {

    @Autowired
    private MgrFuncMapper mgrFuncMapper;
    @Autowired
    private MgrDepartFuncMapper mgrDepartFuncMapper;
    @Autowired
    private IMgrDepartFuncService mgrDepartFuncService;
    @Autowired
    private IMgrUserDepartService mgrUserDepartService;

    /**
     * 新增一个资源功能
     *
     */
    @Override
    public void insert(String cnName, Integer parentId, String type, String url, String remark, Integer userId, String code, String menuIcon){

        if(!this.checkoutCode(code)){
            return;
        }
        // 获取当前时间
        LocalDateTime currentTime = DateTimeUtil.getNowDateTime();
        MgrFunc mgrFunc = new MgrFunc();
        mgrFunc.setCnName(cnName);
        if(parentId!=0){
            mgrFunc.setParentId(parentId);
        }
        mgrFunc.setType(type);
        if(!"1".equals(type)){
            mgrFunc.setUrl(url);
        }
        mgrFunc.setIsUse("1");
        mgrFunc.setCode(code);
        mgrFunc.setMenuIcon(menuIcon);
        mgrFunc.setCreateTime(currentTime);
        mgrFunc.setUpdateTime(currentTime);
        mgrFunc.setRemark(remark);
        mgrFuncMapper.insertSelective(mgrFunc);

        Integer funcId = mgrFunc.getId();
        List<MgrUserDepart> mgrUserDepartList = mgrUserDepartService.getByUser(userId);
        for(MgrUserDepart mgrUserDepart: mgrUserDepartList){
            Integer departId = mgrUserDepart.getDepartId();
            mgrDepartFuncService.insert(funcId, departId);
        }
    }

    /**
     * 修改资源功能名称
     *
     */
    @Override
    public void update(Integer proId, String cnName, String url, String remark, String type, String code){
        // 获取当前时间
        LocalDateTime currentTime = DateTimeUtil.getNowDateTime();
        MgrFunc mgrFunc = new MgrFunc();
        mgrFunc.setId(proId);
        mgrFunc.setCnName(cnName);
        if(!"1".equals(type)){
            mgrFunc.setUrl(url);
        }else{
            mgrFunc.setUrl("");
        }
        mgrFunc.setType(type);
        mgrFunc.setCode(code);
        mgrFunc.setUpdateTime(currentTime);
        mgrFunc.setRemark(remark);
        mgrFuncMapper.updateByPrimaryKeySelective(mgrFunc);
    }

    /**
     * 作废一条功能资源记录
     *
     */
    @Override
    public void delete(Integer proId){

        //1、删除当前功能
        mgrFuncMapper.deleteByPrimaryKey(proId);

        //2、删除功能部门关联
        mgrDepartFuncMapper.deleteByFuncId(proId);

        //3、通过功能id查询所有子功能信息
        MgrFuncExample example = new MgrFuncExample();
        example.createCriteria().andParentIdEqualTo(proId);
        List<MgrFunc> funcList = mgrFuncMapper.selectByExample(example);

        //4、循环所有子功能并删除
        for(MgrFunc mgrFunc: funcList){
            Integer proIds = mgrFunc.getId();
            this.delete(proIds);
        }
    }

    /**
     * 通过用户id查询用户所拥有的权限
     *
     */
    @Override
    public List<MgrFunc> getByUserId(Integer userId){
        return mgrFuncMapper.getByUserId(userId);
    }

    @Override
    public Map<String, Map<String, Object>> getByUserIdToMap(Integer userId){

        List<MgrFunc> mgrFuncList = mgrFuncMapper.getByUserId(userId);
        Map<String, Map<String, Object>> mgrFuncs = new HashMap<String,Map<String, Object>>();
        for(MgrFunc mgrFunc: mgrFuncList){
            Integer funcId = mgrFunc.getId();
            Integer parentId = mgrFunc.getParentId();
            String code = mgrFunc.getCode();
            String cnName = mgrFunc.getCnName();
            String type = mgrFunc.getType();
            String url = mgrFunc.getUrl();
            String menuIcon = mgrFunc.getMenuIcon();

            Map<String, Object> mgrFuncMap = new HashMap<String, Object>();
            mgrFuncMap.put("funcId", funcId);
            mgrFuncMap.put("parentId", parentId);
            mgrFuncMap.put("code", code);
            mgrFuncMap.put("cnName", cnName);
            mgrFuncMap.put("type", type);
            mgrFuncMap.put("url", url);
            mgrFuncMap.put("menuIcon", menuIcon);

            if("3".equals(type)){
                mgrFuncs.put(code, mgrFuncMap);
            }else{
                mgrFuncs.put(String.valueOf(funcId), mgrFuncMap);
            }
        }
        return mgrFuncs;
    }

    /**
     * 超级用户查询所有权限
     *
     */
    @Override
    public List<MgrFunc> getAll(){
        return mgrFuncMapper.getAll();
    }

    @Override
    public Map<String, Map<String, Object>> getAllToMap(){

        List<MgrFunc> mgrFuncList = mgrFuncMapper.getAll();

        if(!this.isHasBasicFunc()){
            mgrFuncList = this.getBasicFunc();
        }

        Map<String, Map<String, Object>> mgrFuncs = new HashMap<String,Map<String, Object>>();
        for(MgrFunc mgrFunc: mgrFuncList){
            Integer funcId = mgrFunc.getId();
            Integer parentId = mgrFunc.getParentId();
            String code = mgrFunc.getCode();
            String cnName = mgrFunc.getCnName();
            String type = mgrFunc.getType();
            String url = mgrFunc.getUrl();
            String menuIcon = mgrFunc.getMenuIcon();

            Map<String, Object> mgrFuncMap = new HashMap<String, Object>();
            mgrFuncMap.put("funcId", funcId);
            mgrFuncMap.put("parentId", parentId);
            mgrFuncMap.put("code", code);
            mgrFuncMap.put("cnName", cnName);
            mgrFuncMap.put("type", type);
            mgrFuncMap.put("url", url);
            mgrFuncMap.put("menuIcon", menuIcon);

            if("3".equals(type)){
                mgrFuncs.put(code, mgrFuncMap);
            }else{
                mgrFuncs.put(String.valueOf(funcId), mgrFuncMap);
            }
        }
        return mgrFuncs;
    }

    /**
     * 超级管理员查询所有带复选框的权限资源
     *
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getTreeAllCheck(String departId){

        //1、获取所有功能资源
        List<MgrFunc> mgrFuncList = this.getAll();
        Map<Integer,Map<String, Object>> funcMaps = new HashMap<Integer, Map<String, Object>>();

        //2、通过部门id获取所有功能部门关联
        MgrDepartFuncExample example = new MgrDepartFuncExample();
        example.createCriteria().andDepartIdEqualTo(Integer.parseInt(departId));
        List<MgrDepartFunc> funcListByDepart = mgrDepartFuncMapper.selectByExample(example);
        Map<Integer, Integer> funcMapByDepart = new HashMap<Integer, Integer>();

        //3、循环所用部门功能关联并放入map中
        for(MgrDepartFunc mgrDepartFunc: funcListByDepart){
            Integer key = mgrDepartFunc.getFuncId();
            Integer value = mgrDepartFunc.getId();
            funcMapByDepart.put(key, value);
        }

        //4、循环所有功能资源并放入map中
        for(MgrFunc mgrFunc: mgrFuncList){
            Map<String,Object> funcMap = new HashMap<String,Object>();
            String cnName = mgrFunc.getCnName();
            Integer parentIn = mgrFunc.getParentId();
            String type = mgrFunc.getType();
            String url = mgrFunc.getUrl();
            String menuIcon = mgrFunc.getMenuIcon();
            String remark = mgrFunc.getRemark();
            Integer proId = mgrFunc.getId();
            funcMap.put("id", proId);
            funcMap.put("parentId", parentIn);
            funcMap.put("text", cnName);
            funcMap.put("type", type);
            funcMap.put("url", url);
            funcMap.put("menuIcon", menuIcon);
            funcMap.put("remark", remark);
            funcMap.put("color", "#428bca");
            funcMaps.put(proId, funcMap);
        }

        //5、循环所有功能map并放入对应父节点下
        List<Map<String, Object>> funcList = new ArrayList<Map<String, Object>>();
        for(Integer key: funcMaps.keySet()){
            Map<String, Object> funcMap = funcMaps.get(key);
            Object parentId = funcMap.get("parentId");
            if(parentId != null){
                Object nodes = funcMaps.get(parentId).get("nodes");
                if(nodes == null){
                    List<Map<String, Object>> funcNodes = new ArrayList<Map<String, Object>>();
                    funcMaps.get(parentId).put("nodes", funcNodes);
                }
                ((List<Map<String, Object>>)funcMaps.get(parentId).get("nodes")).add(funcMap);
            }else{
                funcList.add(funcMap);
            }

            //5.1、选择对应节点复选框
            if(funcMapByDepart.containsKey(key)){
                Map<String, Boolean> stateMap = new HashMap<String, Boolean>();
                stateMap.put("checked", true);
                funcMap.put("state", stateMap);
            }
        }

        return funcList;
    }

    /**
     * 通过用户id查询用户所拥有的带复选框的树状权限
     *
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getTreeByUserIdCheck(Integer userId, String departId){

        //1、获取用户所在部门的所有功能资源
        List<MgrFunc> mgrFuncList = this.getByUserId(userId);

        //2、通过部门id获取所有功能部门关联
        MgrDepartFuncExample example = new MgrDepartFuncExample();
        example.createCriteria().andDepartIdEqualTo(Integer.parseInt(departId));
        List<MgrDepartFunc> funcListByDepart = mgrDepartFuncMapper.selectByExample(example);
        Map<Integer, Integer> funcMapByDepart = new HashMap<Integer, Integer>();

        //3、循环所用部门功能关联并放入map中
        for(MgrDepartFunc mgrDepartFunc: funcListByDepart){
            Integer key = mgrDepartFunc.getFuncId();
            Integer value = mgrDepartFunc.getId();
            funcMapByDepart.put(key, value);
        }

        //4、循环所有功能资源并放入map中
        Map<Integer,Map<String, Object>> funcMaps = new HashMap<Integer, Map<String, Object>>();
        for(MgrFunc mgrFunc: mgrFuncList){
            Map<String,Object> funcMap = new HashMap<String,Object>();
            String cnName = mgrFunc.getCnName();
            Integer parentIn = mgrFunc.getParentId();
            String type = mgrFunc.getType();
            String url = mgrFunc.getUrl();
            String menuIcon = mgrFunc.getMenuIcon();
            String remark = mgrFunc.getRemark();
            Integer proId = mgrFunc.getId();
            funcMap.put("id", proId);
            funcMap.put("parentId", parentIn);
            funcMap.put("text", cnName);
            funcMap.put("type", type);
            funcMap.put("url", url);
            funcMap.put("menuIcon", menuIcon);
            funcMap.put("remark", remark);
            funcMap.put("color", "#428bca");
            funcMaps.put(proId, funcMap);
        }

        //5、循环所有功能map并放入对应父节点下
        List<Map<String, Object>> funcList = new ArrayList<Map<String, Object>>();
        for(Integer key: funcMaps.keySet()){
            Map<String, Object> funcMap = funcMaps.get(key);
            Object parentId = funcMap.get("parentId");
            if(parentId != null){
                if(funcMaps.containsKey(parentId)){
                    Object nodes = funcMaps.get(parentId).get("nodes");
                    if(nodes == null){
                        List<Map<String, Object>> funcNodes = new ArrayList<Map<String, Object>>();
                        funcMaps.get(parentId).put("nodes", funcNodes);
                    }
                    ((List<Map<String, Object>>)funcMaps.get(parentId).get("nodes")).add(funcMap);
                }else{
                    funcList.add(funcMap);
                }
            }else{
                funcList.add(funcMap);
            }
            //5.1、选择对应节点复选框
            if(funcMapByDepart.containsKey(key)){
                Map<String, Boolean> stateMap = new HashMap<String, Boolean>();
                stateMap.put("checked", true);
                funcMap.put("state", stateMap);
            }
        }

        return funcList;
    }

    /**
     * 超级管理员查询所有权限资源
     *
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getTreeAll(){

        //1、获取所有功能资源
        List<MgrFunc> mgrFuncList = this.getAll();

        //2、循环所有功能资源并放入map中
        Map<Integer,Map<String, Object>> funcMaps = new HashMap<Integer, Map<String, Object>>();
        for(MgrFunc mgrFunc: mgrFuncList){
            Map<String,Object> funcMap = new HashMap<String,Object>();
            String code = mgrFunc.getCode();
            String cnName = mgrFunc.getCnName();
            Integer parentIn = mgrFunc.getParentId();
            String type = mgrFunc.getType();
            String url = mgrFunc.getUrl();
            String menuIcon = mgrFunc.getMenuIcon();
            String remark = mgrFunc.getRemark();
            Integer proId = mgrFunc.getId();
            funcMap.put("id", proId);
            funcMap.put("code", code);
            funcMap.put("parentId", parentIn);
            funcMap.put("text", cnName);
            funcMap.put("type", type);
            funcMap.put("url", url);
            funcMap.put("menuIcon", menuIcon);
            funcMap.put("remark", remark);
            funcMap.put("color", "#428bca");
            funcMaps.put(proId, funcMap);
        }

        //3、循环所有功能map并放入对应父节点下
        List<Map<String, Object>> funcList = new ArrayList<Map<String, Object>>();
        for(Integer key: funcMaps.keySet()){
            Map<String, Object> funcMap = funcMaps.get(key);
            Object parentId = funcMap.get("parentId");
            if(parentId != null){
                Object nodes = funcMaps.get(parentId).get("nodes");
                if(nodes == null){
                    List<Map<String, Object>> funcNodes = new ArrayList<Map<String, Object>>();
                    funcMaps.get(parentId).put("nodes", funcNodes);
                }
                ((List<Map<String, Object>>)funcMaps.get(parentId).get("nodes")).add(funcMap);
            }else{
                funcList.add(funcMap);
            }
        }

        return funcList;
    }

    /**
     * 通过用户id查询用户所拥有的树状权限
     *
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getTreeByUserId(Integer userId){

        //1、获取用户所在部门的所有功能资源
        List<MgrFunc> mgrFuncList = this.getByUserId(userId);

        //2、循环所有功能资源并放入map中
        Map<Integer,Map<String, Object>> funcMaps = new HashMap<Integer, Map<String, Object>>();
        for(MgrFunc mgrFunc: mgrFuncList){
            Map<String,Object> funcMap = new HashMap<String,Object>();
            String code = mgrFunc.getCode();
            String cnName = mgrFunc.getCnName();
            Integer parentIn = mgrFunc.getParentId();
            String type = mgrFunc.getType();
            String url = mgrFunc.getUrl();
            String menuIcon = mgrFunc.getMenuIcon();
            String remark = mgrFunc.getRemark();
            Integer proId = mgrFunc.getId();
            funcMap.put("id", proId);
            funcMap.put("code", code);
            funcMap.put("parentId", parentIn);
            funcMap.put("text", cnName);
            funcMap.put("type", type);
            funcMap.put("url", url);
            funcMap.put("menuIcon", menuIcon);
            funcMap.put("remark", remark);
            funcMap.put("color", "#428bca");
            funcMaps.put(proId, funcMap);
        }

        //3、循环所有功能map并放入对应父节点下
        List<Map<String, Object>> funcList = new ArrayList<Map<String, Object>>();
        for(Integer key: funcMaps.keySet()){
            Map<String, Object> funcMap = funcMaps.get(key);
            Object parentId = funcMap.get("parentId");
            if(parentId != null){
                if(funcMaps.containsKey(parentId)){
                    Object nodes = funcMaps.get(parentId).get("nodes");
                    if(nodes == null){
                        List<Map<String, Object>> funcNodes = new ArrayList<Map<String, Object>>();
                        funcMaps.get(parentId).put("nodes", funcNodes);
                    }
                    ((List<Map<String, Object>>)funcMaps.get(parentId).get("nodes")).add(funcMap);
                }else{
                    funcList.add(funcMap);
                }
            }else{
                funcList.add(funcMap);
            }
        }

        return funcList;
    }

    /**
     * 获取还没有新增的功能资源列表
     *
     */
    @Override
    public List<Map<String, Object>> getFuncs(){
        List<Map<String, Object>> funcList = new ArrayList<Map<String, Object>>();

        List<MgrFunc> mgrFuncList = this.getAll();
        Map<String, Object> mgrFuncMap = new HashMap<String, Object>();
        for(MgrFunc mgrFunc: mgrFuncList){
            String code = mgrFunc.getCode();
            code = (code==null||"".equals(code))?"none":code;
            mgrFuncMap.put(code, "333");
        }

        MgrFuncEnum[] funcs = MgrFuncEnum.values();

        for(int i=0; i<funcs.length; i++){
            String code = funcs[i].getCode();
            String cnName = funcs[i].getCnName();
            String type = funcs[i].getType();
            String url = funcs[i].getUrl();
            String menuIcon = funcs[i].getIcon();
            if(!mgrFuncMap.containsKey(code)){
                Map<String, Object> funcMap = new HashMap<String, Object>();
                funcMap.put("code", code);
                funcMap.put("cnName", cnName);
                funcMap.put("type", type);
                funcMap.put("url", url);
                funcMap.put("menuIcon", menuIcon);
                funcList.add(funcMap);
            }
        }

        return funcList;
    }

    /**
     * 校验传入code是否存在
     *
     */
    private boolean checkoutCode(String code){
        MgrFuncExample example = new MgrFuncExample();
        example.createCriteria().andCodeEqualTo(code);
        List<MgrFunc> funcList = mgrFuncMapper.selectByExample(example);
        if(funcList.size()>0){
            return false;
        }else{
            return true;
        }
    }

    /**
     * 判断当天系统是否有基本权限操作
     *
     */
    private boolean isHasBasicFunc(){
        MgrFuncExample example = new MgrFuncExample();
        List<String> codes = new ArrayList<String>();
        codes.add(MgrFuncEnum.FUNC_1.getCode());
        codes.add(MgrFuncEnum.FUNC_1_1.getCode());
        codes.add(MgrFuncEnum.FUNC_1_1_1.getCode());
        example.createCriteria().andCodeIn(codes);
        List<MgrFunc> mgrFuncs = mgrFuncMapper.selectByExample(example);
        if(mgrFuncs.size()>=3){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 获取预定义的必须权限
     *
     */
    private List<MgrFunc> getBasicFunc(){

        List<MgrFunc> mgrFuncList = new ArrayList<MgrFunc>();

        MgrFunc mgrFunc1 = new MgrFunc();
        mgrFunc1.setId(1);
        mgrFunc1.setCnName(MgrFuncEnum.FUNC_1.getCnName());
        mgrFunc1.setCode(MgrFuncEnum.FUNC_1.getCode());
        mgrFunc1.setType(MgrFuncEnum.FUNC_1.getType());
        mgrFunc1.setMenuIcon(MgrFuncEnum.FUNC_1.getIcon());
        mgrFuncList.add(mgrFunc1);

        MgrFunc mgrFunc2 = new MgrFunc();
        mgrFunc2.setId(2);
        mgrFunc2.setParentId(1);
        mgrFunc2.setCnName(MgrFuncEnum.FUNC_1_1.getCnName());
        mgrFunc2.setCode(MgrFuncEnum.FUNC_1_1.getCode());
        mgrFunc2.setType(MgrFuncEnum.FUNC_1_1.getType());
        mgrFunc2.setUrl(MgrFuncEnum.FUNC_1_1.getUrl());
        mgrFunc2.setMenuIcon(MgrFuncEnum.FUNC_1_1.getIcon());
        mgrFuncList.add(mgrFunc2);

        MgrFunc mgrFunc3 = new MgrFunc();
        mgrFunc3.setId(3);
        mgrFunc3.setParentId(2);
        mgrFunc3.setCnName(MgrFuncEnum.FUNC_1_1_1.getCnName());
        mgrFunc3.setCode(MgrFuncEnum.FUNC_1_1_1.getCode());
        mgrFunc3.setType(MgrFuncEnum.FUNC_1_1_1.getType());
        mgrFuncList.add(mgrFunc3);

        return mgrFuncList;
    }

    @Override
    public MgrFunc selectByPrimaryKey(Integer id) {
        return mgrFuncMapper.selectByPrimaryKey(id);
    }
}
