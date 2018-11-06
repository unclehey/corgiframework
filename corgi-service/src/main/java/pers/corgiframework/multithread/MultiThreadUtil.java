package pers.corgiframework.multithread;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.corgiframework.dao.model.BisPrompt;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 多线程工具类
 * Created by UncleHey on 2018.10.19.
 */
public class MultiThreadUtil<T> {
    private static Logger logger = LoggerFactory.getLogger(MultiThreadUtil.class);

    // 线程个数
    private int threadCount;
    // 线程池管理器
    private CompletionService<BisPrompt> pool = null;

    public MultiThreadUtil(int threadCount) {
        if (threadCount > 0) {
            this.threadCount = threadCount;
        } else {
            // 默认值为5
            this.threadCount = 5;
        }
    }

    /**
     * 多线程分批执行list中的任务
     * @param data 线程处理的大数据量list
     * @param params 处理数据的辅助参数
     * @param task 具体执行业务的接口
     * @return BisPrompt
     */
    public BisPrompt execute(List<T> data, Map<String, Object> params, ITask<BisPrompt<String>, T> task) {
        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        // 根据线程池初始化线程池管理器
        pool = new ExecutorCompletionService<>(executor);
        // 开始时间（ms）
        long l = System.currentTimeMillis();
        // 数据量大小
        int length = data.size();
        // 每个线程处理的数据个数
        int taskCount = length / threadCount;
        // 划分每个线程调用的数据
        for (int i = 0; i < threadCount; i++) {
            // 每个线程任务数据list
            List<T> subData;
            if (i == (threadCount - 1)) {
                subData = data.subList(i * taskCount, length);
            } else {
                subData = data.subList(i * taskCount, (i + 1) * taskCount);
            }
            // 将数据分配给各个线程
            HandleCallable execute = new HandleCallable<>(String.valueOf(i), subData, params, task);
            // 将线程加入到线程池
            pool.submit(execute);
        }

        // 总的返回结果集
        List<BisPrompt<String>> result = Lists.newArrayList();
        for (int i = 0; i < threadCount; i++) {
            // 每个线程处理结果集
            BisPrompt<List<BisPrompt<String>>> threadResult;
            try {
                threadResult = pool.take().get();
                result.addAll(threadResult.getBisObj());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        // 关闭线程池
        executor.shutdownNow();
        // 执行结束时间
        long end_l = System.currentTimeMillis();
        logger.info("总耗时：{}ms", (end_l - l));
        BisPrompt bisPrompt = new BisPrompt();
        bisPrompt.setBisObj(result);
        return bisPrompt;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

}
