import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pers.corgiframework.dao.model.BisPrompt;
import pers.corgiframework.multithread.ITask;
import pers.corgiframework.multithread.MultiThreadUtil;

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

    // 此处如果有多个任务实现类，可以按需引用
    @Resource(name = "smsTask")
    private ITask<BisPrompt<String>, Integer> task;

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
            BisPrompt<List<BisPrompt<String>>> bisPrompt = threadUtil.execute(data, params, task);
            System.out.println("处理完成 " + bisPrompt.getBisMsg());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

    public static void main(String[] args) throws Exception {
        class MyThreadPrint implements Runnable {
            private String name;
            private Object prev;
            private Object self;

            public MyThreadPrint(String name, Object prev, Object self) {
                this.name = name;
                this.prev = prev;
                this.self = self;
            }

            @Override
            public void run() {
                int count = 10;
                while (count > 0) {
                    synchronized (prev) {
                        synchronized (self) {
                            System.out.print(name);
                            count--;
                            self.notify();
                        }
                        try {
                            prev.wait();
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage(), e);
                        }
                    }
                }
            }
        }

        Object a = new Object();
        Object b = new Object();
        Object c = new Object();
        MyThreadPrint pa = new MyThreadPrint("A", c, a);
        MyThreadPrint pb = new MyThreadPrint("B", a, b);
        MyThreadPrint pc = new MyThreadPrint("C", b, c);

        new Thread(pa).start();
        Thread.sleep(100);  //确保按顺序A、B、C执行
        new Thread(pb).start();
        Thread.sleep(100);
        new Thread(pc).start();
        Thread.sleep(100);

    }

}