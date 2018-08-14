package pers.corgiframework.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.corgiframework.dao.domain.MgrDepart;
import pers.corgiframework.dao.domain.MgrDepartExample;
import pers.corgiframework.dao.domain.MgrUserDepart;
import pers.corgiframework.dao.mapper.MgrDepartMapper;
import pers.corgiframework.service.IMgrDepartFuncService;
import pers.corgiframework.service.IMgrDepartService;
import pers.corgiframework.service.IMgrUserDepartService;
import pers.corgiframework.tool.utils.DateTimeUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Created by syk on 2017/4/2.
 */
@Service
public class MgrDepartServiceImpl implements IMgrDepartService {

    @Autowired
    private MgrDepartMapper mgrDepartMapper;
    @Autowired
    private IMgrDepartFuncService mgrDepartFuncService;
    @Autowired
    private IMgrUserDepartService mgrUserDepartService;

    /**
     * 新增一个部门
     *
     */
    @Override
    public void insert(String cnName, Integer parentId, String type, String remark, Integer userId){
        // 获取当前时间
        LocalDateTime currentTime = DateTimeUtil.getNowDateTime();
        MgrDepart mgrDepart = new MgrDepart();
        mgrDepart.setCnName(cnName);
        if(parentId!=0){
            mgrDepart.setParentId(parentId);
        }
        mgrDepart.setType(String.valueOf(Integer.parseInt(type)+1));
        mgrDepart.setOperator(userId);
        mgrDepart.setCreateTime(currentTime);
        mgrDepart.setUpdateTime(currentTime);
        mgrDepart.setRemark(remark);
        mgrDepartMapper.insertSelective(mgrDepart);
    }

    /**
     * 更新部门名称和备注
     *
     */
    public void update(Integer proId, String cnName, String remark){
        // 获取当前时间
        LocalDateTime currentTime = DateTimeUtil.getNowDateTime();
        MgrDepart mgrDepart = new MgrDepart();
        mgrDepart.setId(proId);
        mgrDepart.setCnName(cnName);
        mgrDepart.setUpdateTime(currentTime);
        mgrDepart.setRemark(remark);
        mgrDepartMapper.updateByPrimaryKeySelective(mgrDepart);
    }

    /**
     * 删除一个部门及其相关联的
     *
     */
    @Override
    public void delete(String proId){

        //1、通过部门id查询所有子部门信息
        MgrDepartExample example = new MgrDepartExample();
        example.createCriteria().andParentIdEqualTo(Integer.parseInt(proId));
        List<MgrDepart> funcList = mgrDepartMapper.selectByExample(example);

        //2、循环所有子部门并删除
        for(MgrDepart mgrDepart: funcList){
            Integer proIds = mgrDepart.getId();
            this.delete(String.valueOf(proIds));
        }

        //3、删除当前部门
        mgrDepartMapper.deleteByPrimaryKey(Integer.parseInt(proId));

        //4、删除部门功能关联
        mgrDepartFuncService.deleteByDepart(proId);

        //5、删除用户部门关联
        mgrUserDepartService.deleteByDepart(proId);
    }

    /**
     * 查询当前id的所有子部门
     *
     */
    @Override
    public List<MgrDepart> getByParentId(Integer parentId){
        MgrDepartExample example = new MgrDepartExample();
        example.createCriteria().andParentIdEqualTo(parentId);
        return mgrDepartMapper.selectByExample(example);
    }

    /**
     * 查询当前id的所有子部门
     *
     */
    @Override
    public List<MgrDepart> getAllByProId(Integer proId){

        //1、定义所有部门的集合
        List<MgrDepart> allList = Lists.newArrayList();

        //2、查询当前部门
        MgrDepart mgrDepart = mgrDepartMapper.selectByPrimaryKey(proId);
        allList.add(mgrDepart);

        //3、获取所遇级别的子部门
        this.addAllSonMgrDepart(allList, proId);

        return allList;
    }

    /**
     * 超级管理员查询所有职能部门
     *
     */
    @Override
    public List<MgrDepart> getAll(){
        return mgrDepartMapper.getAll();
    }

    /**
     * 超级管理员查询所有职能部门树
     *
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getTreeAll(){

        //1、定义一个所有职能部门集合
        List<MgrDepart> mgrDepartList = this.getAll();

        //2、定义一个部门map并把部门list转化为map
        Map<Integer,Map<String, Object>> departMaps = Maps.newHashMap();
        for(MgrDepart mgrDepart: mgrDepartList){
            Map<String,Object> departMap = Maps.newHashMap();
            String cnName = mgrDepart.getCnName();
            Integer parentIn = mgrDepart.getParentId();
            String type = mgrDepart.getType();
            String remark = mgrDepart.getRemark();
            Integer proId = mgrDepart.getId();
            departMap.put("id", proId);
            departMap.put("parentId", parentIn);
            departMap.put("text", cnName);
            departMap.put("type", type);
            departMap.put("remark", remark);
            departMap.put("color", "#428bca");
            departMaps.put(proId, departMap);
        }

        //3、定义最顶层的部门列表并把对应的部门放到父部门下
        List<Map<String, Object>> departList = Lists.newArrayList();
        for(Integer key: departMaps.keySet()){
            Map<String, Object> departMap = departMaps.get(key);
            Object parentId = departMap.get("parentId");
            if(parentId != null){
                Object nodes = departMaps.get(parentId).get("nodes");
                if(nodes == null){
                    List<Map<String, Object>> funcNodes = Lists.newArrayList();
                    departMaps.get(parentId).put("nodes", funcNodes);
                }
                ((List<Map<String, Object>>)departMaps.get(parentId).get("nodes")).add(departMap);
            }else{
                departList.add(departMap);
            }
        }

        return departList;
    }

    /**
     * 查询当前用户所属部门及所有子部门
     *
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getTreeByUserId(Integer userId){

        //1、定义一个所有职能部门集合
        List<MgrDepart> mgrDepartList = Lists.newArrayList();

        //2、通过用户id查询所有用户部门关联
        List<MgrUserDepart> mgrUserDepartList = mgrUserDepartService.getByUser(userId);

        //3、通过部门id查询所有部门及子部门
        for(MgrUserDepart mgrUserDepart: mgrUserDepartList){
            Integer departId = mgrUserDepart.getDepartId();
            List<MgrDepart> mgrDeparts = this.getAllByProId(departId);
            mgrDepartList.addAll(mgrDeparts);
        }

        //4、定义一个部门map并把部门list转化为map
        Map<Integer,Map<String, Object>> departMaps = Maps.newHashMap();
        for(MgrDepart mgrDepart: mgrDepartList){
            Map<String,Object> departMap = Maps.newHashMap();
            String cnName = mgrDepart.getCnName();
            Integer parentIn = mgrDepart.getParentId();
            String type = mgrDepart.getType();
            String remark = mgrDepart.getRemark();
            Integer proId = mgrDepart.getId();
            departMap.put("id", proId);
            departMap.put("parentId", parentIn);
            departMap.put("text", cnName);
            departMap.put("type", type);
            departMap.put("remark", remark);
            departMap.put("color", "#428bca");
            departMaps.put(proId, departMap);
        }

        //5、定义最顶层的部门列表并把对应的部门放到父部门下
        List<Map<String, Object>> departList = Lists.newArrayList();
        for(Integer key: departMaps.keySet()){
            Map<String, Object> departMap = departMaps.get(key);
            Object parentId = departMap.get("parentId");
            if(parentId != null){
                if(departMaps.containsKey(parentId)){
                    Object nodes = departMaps.get(parentId).get("nodes");
                    if(nodes == null){
                        List<Map<String, Object>> funcNodes = Lists.newArrayList();
                        departMaps.get(parentId).put("nodes", funcNodes);
                    }
                    ((List<Map<String, Object>>)departMaps.get(parentId).get("nodes")).add(departMap);
                }else{
                    departList.add(departMap);
                }
            }else{
                departList.add(departMap);
            }
        }

        return departList;
    }

    /**
     * 判断当前部门是否是当前用户所创建
     *
     */
    @Override
    public boolean isMyDepart(Integer departId, Integer userId){

        boolean flag = false;

        MgrDepart mgrDepart = mgrDepartMapper.selectByPrimaryKey(departId);
        Integer creater = mgrDepart.getOperator();

        if(creater.equals(userId)){
            flag = true;
        }

        return flag;
    }

    /**
     * 获取当前部门的所有子部门
     *
     */
    private void addAllSonMgrDepart(List<MgrDepart> list, Integer proId){

        //1、通过部门id查询所有下一级子部门
        List<MgrDepart> sonList = this.getByParentId(proId);
        list.addAll(sonList);

        //2、循环子部门添加子部门的子部门
        for(MgrDepart mgrDepart: sonList){
            Integer proIdSon = mgrDepart.getId();
            this.addAllSonMgrDepart(list, proIdSon);
        }
    }

}
