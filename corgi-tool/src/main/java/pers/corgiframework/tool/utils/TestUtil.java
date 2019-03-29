package pers.corgiframework.tool.utils;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * Created by UncleHey on 2019.3.21.
 */
public class TestUtil {

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
     * 保存函数
     * @param a
     * @return
     */
    public static String store(List<Map<String, String>> a) {
        String text = "";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < a.size(); i++) {
            sb.append(parseMapToStr(a.get(i), ";", "="));
            if (i < a.size() - 1) {
                sb.append("\\n");
            }
        }
        if (null != sb) {
            text = sb.toString();
        }
        return text;
    }

    /**
     * 加载函数
     * @param text
     * @return
     */
    public static List<Map<String, String>> load(String text) {
        List<Map<String, String>> a = Lists.newArrayList();
        List<String> list = parseStrToList(text, "\\n");
        for (String str : list) {
            Map<String, String> params = parseStrToMap(str, ";", "=");
            a.add(params);
        }
        return a;
    }

    public static void main(String[] args) {
        // 测试保存
        List<Map<String, String>> a = Lists.newArrayList();
        Map<String, String> params = Maps.newHashMap();
        params.put("appid", "sads");
        params.put("mch_id", "123456");
        params.put("nonce_str", "sakjdksa");
        params.put("body", "升级个人会员");
        params.put("out_trade_no", "asdasasdsadsad");
        a.add(params);
        params = Maps.newHashMap();
        params.put("total_fee", "10000");
        params.put("notify_url", "http://www.baidu.com");
        a.add(params);
        String result = store(a);
        System.out.println(result);
        // 测试加载
        String text = "nonce_str=sakjdksa;out_trade_no=asdasasdsadsad;appid=sads;mch_id=123456;body=升级个人会员\\ntotal_fee=10000;notify_url=http://www.baidu.com";
        List<Map<String, String>> b = load(text);
        System.out.println(b);
    }

}
