import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pers.corgiframework.multithread.ITask;
import pers.corgiframework.multithread.MultiThreadUtil;
import pers.corgiframework.multithread.ResultBean;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by syk on 2017/8/18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring.xml"})
public class JunitTest {
    final static Logger LOGGER = LoggerFactory.getLogger(JunitTest.class);

    @Resource(name = "smsTask")
    private ITask<ResultBean<String>, Integer> task;

    @Test
    public void test(){
        try {
            // 需要多线程处理的大量数据list
            List<Integer> data = Lists.newArrayList();
            for(int i = 0; i < 100000; i ++){
                data.add(i + 1);
            }
            // 创建多线程处理任务
            MultiThreadUtil<Integer> threadUtil = new MultiThreadUtil<>(4);
            // 辅助参数
            Map<String, Object> params = Maps.newHashMap();
            params.put("param", "multiThread");
            // 执行多线程处理，并返回处理结果
            ResultBean<List<ResultBean<String>>> resultBean = threadUtil.execute(data, params, task);
            System.out.println("处理完成 " + resultBean.getMsg());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

    public static void main(String[] args) throws Exception {
        class CodingTask implements Runnable {
            private final int employeeId;
            public CodingTask(int employeeId) {
                this.employeeId = employeeId;
            }

            @Override
            public void run() {
                LOGGER.info("Employee " + employeeId + " started writing code.");
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
                LOGGER.info("Employee " + employeeId + " finished writing code.");
            }
        }


        // 线程池
        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<Future<?>> taskResults = Lists.newLinkedList();
        for (int i = 0; i < 10; i++) {
            taskResults.add(executor.submit(new CodingTask(i)));
        }
        LOGGER.info("10 tasks distribute success.");
        for (Future<?> taskResult : taskResults) {
            taskResult.get();
        }
        LOGGER.info("All tasks finished.");
        executor.shutdown();
    }

}