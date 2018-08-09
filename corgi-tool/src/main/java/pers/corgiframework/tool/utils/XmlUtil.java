package pers.corgiframework.tool.utils;

import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.*;

/**
 * Xml工具类
 * Created by syk on 2018/7/24.
 */
public class XmlUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlUtil.class);

    /**
     * 将String解析成XML
     * @param xmlData
     * @return
     */
    public static Map<String, Object> parseXml(String xmlData){
        // 将解析结果存储在HashMap中
        Map<String, Object> map = new HashMap<>();
        Document document;
        try {
            document = DocumentHelper.parseText(xmlData);
            // 得到xml根元素
            Element root = document.getRootElement();
            // 得到根元素的所有子节点
            List<Element> elementList = root.elements();
            // 遍历所有子节点
            for (Element e : elementList){
                map.put(e.getName(), e.getText());
            }
        } catch (DocumentException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return map;
    }

    /**
     * xml转map
     * @param xml
     * @return
     */
    public static Map xmlToMap(String xml) {
        try {
            Map map = new HashMap();
            Document document = DocumentHelper.parseText(xml);
            Element nodeElement = document.getRootElement();
            List node = nodeElement.elements();
            for (Iterator it = node.iterator(); it.hasNext(); ) {
                Element elm = (Element) it.next();
                map.put(elm.getName(), elm.getText());
            }
            return map;
        } catch (Exception e) {
            LOGGER.info(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 根据String解析多层XML
     * @param xmlData
     * @return
     */
    public static Map<String, Object> parseXml2Map(String xmlData){
        // 将解析结果存储在HashMap中
        Map<String, Object> map = new HashMap<>();
        Document document;
        try {
            document = DocumentHelper.parseText(xmlData);
            // 得到xml根元素
            Element rootElement  = document.getRootElement();
            element2Map(map, rootElement );
        } catch (DocumentException e) {
            LOGGER.info(e.getMessage(), e);
        }
        return map;
    }

    /**
     * 使用递归调用将多层级xml转为map
     * @param map
     * @param rootElement
     */
    public static void element2Map(Map<String, Object> map, Element rootElement) {
        //获得当前节点的子节点
        List<Element> elements = rootElement.elements();
        if (elements.size() == 0) {
            //没有子节点说明当前节点是叶子节点，直接取值
            map.put(rootElement.getName(), rootElement.getText());
        } else if (elements.size() == 1) {
            //只有一个子节点说明不用考虑list的情况，继续递归
            Map<String, Object> tempMap = new HashMap<>();
            element2Map(tempMap, elements.get(0));
            map.put(rootElement.getName(), tempMap);
        } else {
            //多个子节点的话就要考虑list的情况了，特别是当多个子节点有名称相同的字段时
            Map<String, Object> tempMap = new HashMap<>();
            for (Element element : elements) {
                tempMap.put(element.getName(), null);
            }
            Set<String> keySet = tempMap.keySet();
            for (String string : keySet) {
                Namespace namespace = elements.get(0).getNamespace();
                List<Element> sameElements = rootElement.elements(new QName(string, namespace));
                //如果同名的数目大于1则表示要构建list
                if (sameElements.size() > 1) {
                    List<Map> list = new ArrayList<>();
                    for (Element element : sameElements) {
                        Map<String, Object> sameTempMap = new HashMap<>();
                        element2Map(sameTempMap, element);
                        list.add(sameTempMap);
                    }
                    map.put(string, list);
                } else {
                    //同名的数量不大于1直接递归
                    Map<String, Object> sameTempMap = new HashMap<>();
                    element2Map(sameTempMap, sameElements.get(0));
                    map.put(string, sameTempMap);
                }
            }
        }
    }

    /**
     * 根据流解析XML
     * @param request
     * @return
     */
    public static Map<String, String> parseXml(HttpServletRequest request) {
        // 解析结果存储在HashMap
        Map<String, String> map = new HashMap<>();
        InputStream inputStream;
        try {
            inputStream = request.getInputStream();
            // 读取输入流
            SAXReader reader = new SAXReader();
            // To protect a Java org.dom4j.io.SAXReader from XXE：XML外部实体注入漏洞(XML External Entity Injection，简称 XXE)
            reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
            reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            Document document = reader.read(inputStream);
            // 得到xml根元素
            Element root = document.getRootElement();
            // 得到根元素的所有子节点
            List<Element> elementList = root.elements();
            // 遍历所有子节点
            for (Element e : elementList) {
                map.put(e.getName(), e.getText());
            }
            // 释放资源
            inputStream.close();
        } catch (Exception e) {
            LOGGER.info(e.getMessage(), e);
        }
        return map;
    }

    /**
     * map转成xml
     * @param arr
     * @return
     */
    public static String mapToXml(Map<String, String> arr) {
        String xml = "<xml>";
        Iterator<Map.Entry<String, String>> iter = arr.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = iter.next();
            String key = entry.getKey();
            String val = entry.getValue();
            xml += "<" + key + ">" + val + "</" + key + ">";
        }
        xml += "</xml>";
        return xml;
    }
}
