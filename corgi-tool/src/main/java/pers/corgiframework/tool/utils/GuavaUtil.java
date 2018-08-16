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
import java.util.Iterator;
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
         *
         * 不可变对象有很多优点，包括：
         * 当对象被不可信的库调用时，不可变形式是安全的；
         * 不可变对象被多个线程调用时，不存在竞态条件问题
         * 不可变集合不需要考虑变化，因此可以节省时间和空间。所有不可变的集合都比它们的可变形式有更好的内存利用率（分析和测试细节）；
         * 不可变对象因为有固定不变，可以作为常量来安全使用。
         */
        ImmutableList<String> iList = ImmutableList.of("a", "b", "c");
        System.out.println(Lists.reverse(iList));
        System.out.println(Lists.partition(iList, 2));
        ImmutableSet<String> iSet = ImmutableSet.of("e1", "e2");
        ImmutableMap<String, String> iMap = ImmutableMap.of("k1", "v1", "k2", "v2");

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

        /**
         * 通常来说，Guava Cache适用于：
         * 1、你愿意消耗一些内存空间来提升速度。
         * 2、你预料到某些键会被查询一次以上。
         * 3、缓存中存放的数据总量不会超出内存容量。（Guava Cache是单个应用运行时的本地缓存，它不把数据存放到文件或外部服务器）
         */
        LoadingCache<String, String> cacheBuilder = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(new CacheLoader<String, String>() {
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

        // 按字典顺序排序
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

        /**
         * Multiset元素的顺序是无关紧要的：Multiset {a, a, b}和{a, b, a}是相等的”。
         * 这里所说的集合[set]是数学上的概念，Multiset继承自JDK中的Collection接口，而不是Set接口，所以包含重复元素并没有违反原有的接口契约
         * 可以用两种方式看待Multiset：
         * 1、没有元素顺序限制的ArrayList<E>
         * 2、Map<E, Integer>，键为元素，值为计数
         */
        Multiset<String> multiset = HashMultiset.create();
        multiset.add("a");
        multiset.add("b");
        multiset.add("c");
        multiset.add("d");
        multiset.add("a");
        multiset.add("b");
        multiset.add("c");
        multiset.add("b");
        multiset.add("b");
        multiset.add("b");
        System.out.println("Occurrence of 'b' : " + multiset.count("b"));
        System.out.println("Total Size : " + multiset.size());
        // get the distinct elements of the multiset as set
        Set<String> strSet = multiset.elementSet();
        //display the elements of the set
        System.out.println("Set [");
        for (String s : strSet) {
            System.out.println(s);
        }
        System.out.println("]");
        // display all the elements of the multiset using iterator
        Iterator<String> iterator = multiset.iterator();
        System.out.println("MultiSet [");
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
        System.out.println("]");
        // display the distinct elements of the multiset with their occurrence count
        System.out.println("MultiSet [");
        for (Multiset.Entry<String> entry : multiset.entrySet()) {
            System.out.println("Element: " + entry.getElement() + ", Occurrence(s): " + entry.getCount());
        }
        System.out.println("]");
        // remove extra occurrences
        multiset.remove("b", 2);
        // print the occurrence of an element
        System.out.println("Occurence of 'b' : " + multiset.count("b"));

        /**
         * Multimap
         * 创建一个map包含key为String，value为List类型
         */
        Multimap<String, Integer> listMap = ArrayListMultimap.create();
        listMap.put("aa", 1);
        listMap.put("aa", 2);
        listMap.put("aa", 2);
        listMap.put("bb", 3);
        // Multimap.size()返回所有”键-单个值映射”的个数
        System.out.println("Total Key Size : " + listMap.size());
        // Multimap.keySet().size()返回不同键的个数
        System.out.println("Total Difference Key Size : " + listMap.keySet().size());

        /**
         * BiMap实现键值对的双向映射
         */
        BiMap<String, Integer> biMap = HashBiMap.create();
        biMap.put("小明", 25);
        biMap.put("小红", 18);
        biMap.put("小强", 30);
        System.out.println(biMap.inverse().get(18));
        System.out.println(biMap.get("小红"));

        /**
         * RangeMap描述了”不相交的、非空的区间”到特定值的映射
         */
        RangeMap<Integer, String> rangeMap = TreeRangeMap.create();
        rangeMap.put(Range.closed(1, 10), "foo");
        System.out.println(rangeMap);
        rangeMap.put(Range.open(3, 6), "bar");
        System.out.println(rangeMap);
        rangeMap.put(Range.open(10, 20), "foo");
        System.out.println(rangeMap);
        rangeMap.remove(Range.closed(5, 11));
        System.out.println(rangeMap);

    }

}
