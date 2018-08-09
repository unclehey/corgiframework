package pers.corgiframework.tool.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 配置文件读取工具类
 * Created by syk on 2018/7/24.
 */
public class PropertiesUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesUtil.class);
    private static Properties properties;

    public static synchronized String getString(String key) {
        if (properties == null) {
            properties = loadProperties();
        }
        return properties.getProperty(key);
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        String location = "classpath:config.properties";
        Resource resource = resourceLoader.getResource(location);

        InputStream is = null;
        BufferedReader br = null;
        try {
            is = resource.getInputStream();
            br = new BufferedReader(new InputStreamReader(is, "utf-8"));
            props.load(br);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        return props;
    }
}
