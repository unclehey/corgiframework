package pers.corgiframework.tool.utils;

import com.google.common.base.*;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.*;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * guava工具类：强大的Ordering排序器，文件操作
 * Guava工程包含了若干被Google的Java项目广泛依赖的核心库，例如：
 * 1、集合 [collections]
 * 2、缓存 [caching]
 * 3、原生类型支持 [primitives support]
 * 4、并发库 [concurrency libraries]
 * 5、通用注解 [common annotations]
 * 6、字符串处理 [string processing]
 * 7、I/O 等等。
 * Created by syk on 2018/8/14.
 */
public class GuavaUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuavaUtil.class);

    /**
     * 将List转换为特定规则的字符串
     * @param list
     * @param separator
     * @return
     */
    public static String parseListToStr(List<String> list, String separator) {
        return Joiner.on(separator).join(list);
    }

    /**
     * 将特定规则的字符串转换为List
     * 使用 separator 切分字符串并去除空串与空格
     * @param str
     * @param separator
     * @return
     */
    public static List<String> parseStrToList(String str, String separator) {
        return Splitter.on(separator).omitEmptyStrings().trimResults().splitToList(str);
    }

    /**
     * 将Map转换为特定规则的字符串
     * @param map
     * @param separator
     * @param kvSeparator
     * @return
     */
    public static String parseMapToStr(Map<String, String> map, String separator, String kvSeparator) {
        return Joiner.on(separator).withKeyValueSeparator(kvSeparator).join(map);
    }

    /**
     * 将特定规则的字符串转换为Map
     * @param str
     * @param separator
     * @param kvSeparator
     * @return
     */
    public static Map<String, String> parseStrToMap(String str, String separator, String kvSeparator) {
        return Splitter.on(separator).withKeyValueSeparator(kvSeparator).split(str);
    }



    public static void main(String[] args) throws Exception {
        // 普通Collection的创建
        List<String> list = Lists.newArrayList();
        Set<String> set = Sets.newHashSet();
        Map<String, String> map = Maps.newHashMap();
        /**
         * 不变Collection的创建
         * 什么是immutable(不可变)对象
         * 1、在多线程操作下，是线程安全的。
         * 2、所有不可变集合会比可变集合更有效的利用资源。
         * 3、中途不可改变
         */
        ImmutableList<String> iList = ImmutableList.of("a", "b", "c");
        ImmutableSet<String> iSet = ImmutableSet.of("e1", "e2");
        ImmutableMap<String, String> iMap = ImmutableMap.of("k1", "v1", "k2", "v2");
        // 创建一个map包含key为String，value为List类型
        Multimap<String, Integer> listMap = ArrayListMultimap.create();
        listMap.put("aa", 1);
        listMap.put("aa", 2);
        /**
         * 其他的黑科技集合
         * MultiSet: 无序+可重复 count()方法获取单词的次数 增强了可读性+操作简单
         * 创建方式: Multiset<String> set = HashMultiset.create();
         * Multimap: key-value key可以重复
         * 创建方式: Multimap<String, String> teachers = ArrayListMultimap.create();
         * BiMap: 双向Map(Bidirectional Map) 键与值都不能重复
         * 创建方式: BiMap<String, String> biMap = HashBiMap.create();
         * Table: 双键的Map Map--> Table-->rowKey+columnKey+value //和sql中的联合主键有点像
         * 创建方式: Table<String, String, Integer> tables = HashBasedTable.create();
         */

        System.out.println(parseListToStr(iList, "-"));
        System.out.println(parseMapToStr(iMap, ",", "="));
        String strList = "1- 2-3-4- 5- 6 ";
        System.out.println(parseStrToList(strList, "-"));
        String strMap = "xiaoming=11,xiaohong=23";
        System.out.println(parseStrToMap(strMap, ",", "="));
        // 判断匹配结果
        boolean result = CharMatcher.inRange('a', 'z').or(CharMatcher.inRange('A', 'Z')).matches('K');
        System.out.println(result);
        // 判断非空
        String s3 = null;
        System.out.println(Strings.isNullOrEmpty(s3));
        /*// 检查是否为空,不仅仅是字符串类型，其他类型的判断 全部都封装在 Preconditions类里 里面的方法全为静态。
        List<String> list1 = null;
        System.out.println(Preconditions.checkNotNull(list1, "集合为空"));
        int count = -1;
        Preconditions.checkArgument(count > 0, "must be positive: %s", count);*/
        // 计算中间代码的运行时间
        Stopwatch stopwatch = Stopwatch.createStarted();
        for (int i = 0; i < 10; i++) {
        }
        long nanos = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        System.out.println(nanos);

        // 文件操作
        File file = new File("d:/test.txt");
        List<String> fileList = null;
        try {
            fileList = Files.readLines(file, Charsets.UTF_8);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        System.out.println(fileList);

        // 缓存
        LoadingCache<String, String> cacheBuilder = CacheBuilder.newBuilder().build(new CacheLoader<String, String>() {
            @Override
            public String load(String s) throws Exception {
                String strProValue = "hello " + s + "!";
                return strProValue;
            }
        });
        System.out.println(cacheBuilder.get("wen"));
        cacheBuilder.put("begin", "code");
        System.out.println(cacheBuilder.get("begin"));

        Cache<String, String> cache = CacheBuilder.newBuilder().maximumSize(1000).build();
        String resultVal = cache.get("code", () -> {
            String strProValue = "begin " + "code" + "!";
            return strProValue;
        });
        System.out.println("value : " + resultVal);

        Map<String, String> params = Maps.newHashMap();
        params.put("appid", "sads");
        params.put("mch_id", "123456");
        params.put("nonce_str", "sakjdksa");
        params.put("body", "升级个人会员");
        params.put("out_trade_no", "asdas asdsadsad");
        params.put("total_fee", "10000");
        params.put("notify_url", "http://www.baidu.com");
        ImmutableSortedMap<String, String> sortedMap = ImmutableSortedMap.copyOf(params);
        System.out.println("sortedMap：" + sortedMap);

    }

}
