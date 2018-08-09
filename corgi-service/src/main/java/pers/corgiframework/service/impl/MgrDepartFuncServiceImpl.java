package pers.corgiframework.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.corgiframework.dao.domain.MgrDepartFunc;
import pers.corgiframework.dao.domain.MgrDepartFuncExample;
import pers.corgiframework.dao.mapper.MgrDepartFuncMapper;
import pers.corgiframework.service.IMgrDepartFuncService;
import pers.corgiframework.tool.utils.DateTimeUtil;
import pers.corgiframework.tool.utils.JsonUtil;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by syk on 2017/4/2.
 */
@Service
public class MgrDepartFuncServiceImpl implements IMgrDepartFuncService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MgrDepartFuncServiceImpl.class);

    @Autowired
    private MgrDepartFuncMapper mgrDepartFuncMapper;

    /**
     * 新增一条功能和部门关联记录
     *
     */
    @Override
    public void insert(Integer funcId, Integer departId){
        // 获取当前时间
        LocalDateTime currentTime = DateTimeUtil.getNowDateTime();
        MgrDepartFunc mgrDepartFunc = new MgrDepartFunc();
        mgrDepartFunc.setDepartId(departId);
        mgrDepartFunc.setFuncId(funcId);
        mgrDepartFunc.setCreateTime(currentTime);
        mgrDepartFunc.setUpdateTime(currentTime);
        mgrDepartFunc.setRemark("给部门赋权");
        mgrDepartFuncMapper.insertSelective(mgrDepartFunc);
    }

    /**
     * 通过部门id删除一条功能部门关联
     *
     */
    @Override
    public void deleteByDepart(String departId){

        List<MgrDepartFunc> funcList = this.getByDepart(departId);
        for(MgrDepartFunc mgrDepartFunc: funcList){
            Integer proId = mgrDepartFunc.getId();
            mgrDepartFuncMapper.deleteByPrimaryKey(proId);
        }
    }

    /**
     * 通过功能id删除一条功能部门关联
     *
     */
    @Override
    public void deleteByFunc(String funcId){

        List<MgrDepartFunc> funcList = this.getByFunc(funcId);
        for(MgrDepartFunc mgrDepartFunc: funcList){
            Integer proId = mgrDepartFunc.getId();
            mgrDepartFuncMapper.deleteByPrimaryKey(proId);
        }
    }

    /**
     * 给给定部门赋予功能权限
     *
     */
    @SuppressWarnings("unchecked")
    @Override
    public void givePerm(String funcIds, String departId){
        //1、解析传入的功能列表
        List funcIdsList = JsonUtil.readValue(funcIds, List.class);
        Map<Integer, Integer> funcMap = new HashMap<Integer, Integer>();
        Map<Integer, Integer> funcMapNew = new HashMap<Integer, Integer>();

        //2、获取功能部门关联
        List<MgrDepartFunc> funcList = this.getByDepart(departId);

        //3、循环功能部门关联并放入map中
        for(MgrDepartFunc mgrDepartFunc: funcList){
            Integer key = mgrDepartFunc.getFuncId();
            Integer value = mgrDepartFunc.getId();
            funcMap.put(key, value);
        }

        //4、新增功能部门关联（赋权）
        try{
            for(Object funcId: funcIdsList){
                if(!"0".equals(funcId)){
                    funcMapNew.put(Integer.parseInt(funcId.toString()), 33);
                    if(funcMap.get(funcId) == null){
                        this.insert(Integer.parseInt(funcId.toString()), Integer.parseInt(departId));
                    }
                }
            }
        }catch(Exception e){
            LOGGER.error(e.getMessage(), e);
        }

        //5、删除功能部门关联（删除权限）
        for(MgrDepartFunc mgrDepartFunc: funcList){
            Integer key = mgrDepartFunc.getFuncId();
            Integer value = mgrDepartFunc.getId();
            if(funcMapNew.get(key) == null){
                mgrDepartFuncMapper.deleteByPrimaryKey(value);
            }
        }
    }

    /**
     * 通过部门id获取功能部门关联
     *
     */
    @Override
    public List<MgrDepartFunc> getByDepart(String departId){
        MgrDepartFuncExample example = new MgrDepartFuncExample();
        example.createCriteria().andDepartIdEqualTo(Integer.parseInt(departId));
        List<MgrDepartFunc> funcList = mgrDepartFuncMapper.selectByExample(example);
        return funcList;
    }

    /**
     * 通过功能id获取功能部门关联
     *
     */
    @Override
    public List<MgrDepartFunc> getByFunc(String funcId){
        MgrDepartFuncExample example = new MgrDepartFuncExample();
        example.createCriteria().andFuncIdEqualTo(Integer.parseInt(funcId));
        List<MgrDepartFunc> funcList = mgrDepartFuncMapper.selectByExample(example);
        return funcList;
    }

}
