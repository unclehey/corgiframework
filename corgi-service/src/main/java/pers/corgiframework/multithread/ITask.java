package pers.corgiframework.multithread;

import java.util.Map;

/**
 * 任务处理接口
 * 具体业务逻辑可实现该接口
 * T 返回值类型
 * E 传入值类型
 * Created by UncleHey on 2018.10.19.
 */
public interface ITask<T, E> {

    /**
     * 任务执行方法接口
     * @param e 传入对象
     * @param params 其他辅助参数
     * @return T 返回值类型
     */
    T execute(E e, Map<String, Object> params);
}
