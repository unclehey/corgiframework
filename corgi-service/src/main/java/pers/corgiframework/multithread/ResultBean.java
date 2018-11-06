package pers.corgiframework.multithread;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * 多线程返回结果统一bean
 * Created by UncleHey on 2018.10.19.
 */
public class ResultBean<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    // 成功状态
    public static final int SUCCESS = 1;
    // 处理中状态
    public static final int PROCESSING = 0;
    // 失败状态
    public static final int FAIL = -1;

    // 描述
    private String msg = "SUCCESS";
    // 状态默认成功
    private int code = SUCCESS;
    // 备注
    private String remark;
    // 返回数据
    private T data;

    public ResultBean() {
        super();
    }

    public ResultBean(T data) {
        super();
        this.data = data;
    }

    /**
     * 使用异常创建结果
     */
    public ResultBean(Throwable e) {
        super();
        this.msg = e.toString();
        this.code = FAIL;
    }

    /**
     * 实例化结果默认成功状态
     * @return ResultBean<T>
     */
    public static <T> ResultBean<T> newInstance() {
        ResultBean<T> instance = new ResultBean<T>();
        // 默认返回信息
        instance.code = SUCCESS;
        instance.msg = "SUCCESS";
        return instance;
    }

    /**
     * 实例化结果默认成功状态和数据
     * @param data
     * @return ResultBean<T>
     */
    public static <T> ResultBean<T> newInstance(T data) {
        ResultBean<T> instance = new ResultBean<T>();
        instance.code = SUCCESS;
        instance.msg = "SUCCESS";
        instance.data = data;
        return instance;
    }

    /**
     * 实例化返回结果
     * @param code
     * @param msg
     * @return ResultBean<T>
     */
    public static <T> ResultBean<T> newInstance(int code, String msg) {
        ResultBean<T> instance = new ResultBean<T>();
        instance.code = code;
        instance.msg = msg;
        return instance;
    }

    /**
     * 实例化返回结果
     * @param code
     * @param msg
     * @param data
     * @return ResultBean<T>
     */
    public static <T> ResultBean<T> newInstance(int code, String msg, T data) {
        ResultBean<T> instance = new ResultBean<T>();
        instance.code = code;
        instance.msg = msg;
        instance.data = data;
        return instance;
    }

    /**
     * 设置返回数据
     * @param data
     * @return ResultBean<T>
     */
    public ResultBean<T> setData(T data){
        this.data = data;
        return this;
    }

    /**
     * 设置结果描述
     * @param msg
     * @return ResultBean<T>
     */
    public ResultBean<T> setMsg(String msg){
        this.msg = msg;
        return this;
    }

    /**
     * 设置状态
     * @param code
     * @return ResultBean<T>
     */
    public ResultBean<T> setCode(int code){
        this.code = code;
        return this;
    }

    /**
     * 设置备注
     * @param remark
     * @return ResultBean<T>
     */
    public ResultBean<T> setRemark(String remark){
        this.remark = remark;
        return this;
    }

    /**
     * 设置成功描述和返回数据
     * @param msg
     * @param data
     * @return ResultBean<T>
     */
    public ResultBean<T> success(String msg, T data){
        this.code = SUCCESS;
        this.data = data;
        this.msg = msg;
        return this;
    }

    /**
     * 设置成功返回结果描述
     * @param msg
     * @return ResultBean<T>
     */
    public ResultBean<T> success(String msg){
        this.code = SUCCESS;
        this.msg = msg;
        return this;
    }

    /**
     * 设置处理中描述和返回数据
     * @param msg
     * @param data
     * @return ResultBean<T>
     */
    public ResultBean<T> processing(String msg, T data){
        this.code = PROCESSING;
        this.data = data;
        this.msg = msg;
        return this;
    }

    /**
     * 设置处理中返回结果描述
     * @param msg
     * @return ResultBean<T>
     */
    public ResultBean<T> processing(String msg){
        this.code = PROCESSING;
        this.msg = msg;
        return this;
    }

    /**
     * 设置失败返回描述和返回数据
     * @param msg
     * @param data
     * @return ResultBean<T>
     */
    public ResultBean<T> fail(String msg, T data){
        this.code = FAIL;
        this.data = data;
        this.msg = msg;
        return this;
    }

    /**
     * 设置失败返回描述
     * @param msg
     * @return ResultBean<T>
     */
    public ResultBean<T> fail(String msg){
        this.code = FAIL;
        this.msg = msg;
        return this;
    }

    public T getData() {
        return data;
    }
    public String getMsg() {
        return msg;
    }
    public int getCode() {
        return code;
    }
    public String getRemark() {
        return remark;
    }

    /**
     * 生成json字符串
     * @return String
     */
    public String json(){
        return JSON.toJSONString(this);
    }

}
