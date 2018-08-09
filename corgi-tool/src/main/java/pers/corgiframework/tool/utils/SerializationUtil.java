package pers.corgiframework.tool.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * 序列化工具类
 * Created by syk on 2017/8/18.
 */
public class SerializationUtil {

    final static Logger LOGGER = LoggerFactory.getLogger(SerializationUtil.class);

    public static byte[] serialize(Object value) {
        if (value == null) {
            throw new NullPointerException("Can't serialize null");
        }
        byte[] rv = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream os = null;
        try {
            bos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bos);
            os.writeObject(value);
            os.close();
            bos.close();
            rv = bos.toByteArray();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            try {
                if (null != bos) {
                    bos.close();
                }
                if (null != os) {
                    bos.close();
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return rv;
    }

    public static Object deserialize(byte[] in) {
        Object rv = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream is = null;
        try {
            if (null != in) {
                bis = new ByteArrayInputStream(in);
                is = new ObjectInputStream(bis);
                rv = is.readObject();
                is.close();
                bis.close();
            }
        } catch (IOException e) {
            LOGGER.error("Caught IOException decoding %d bytes of data", in == null ? 0 : in.length, e);
        } catch (ClassNotFoundException e) {
            LOGGER.error("Caught CNFE decoding %d bytes of data", in == null ? 0 : in.length, e);
        } finally {
            try {
                if (null != bis) {
                    bis.close();
                }
                if (null != is) {
                    is.close();
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return rv;
    }

}
