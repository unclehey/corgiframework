package pers.corgiframework.tool.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * 签名工具类
 * Created by syk on 2017/8/23.
 */
public class SignUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(SignUtil.class);

    /**
     * MD5加密
     * @param strSrc
     * @return
     */
    public static String md5(String strSrc) {
        MessageDigest md = null;
        String strDes = null;
        try {
            byte[] bt = strSrc.getBytes("utf-8");
            md = MessageDigest.getInstance("MD5");
            md.update(bt);
            strDes = bytes2Hex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (UnsupportedEncodingException e1) {
            LOGGER.error(e1.getMessage(), e1);
        }
        return strDes;
    }

    /**
     * SHA-1加密
     * @param strSrc
     * @return
     */
    public static String sha1(String strSrc) {
        MessageDigest md = null;
        String strDes = null;
        try {
            byte[] bt = strSrc.getBytes("utf-8");
            md = MessageDigest.getInstance("SHA");
            md.update(bt);
            strDes = bytes2Hex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (UnsupportedEncodingException e1) {
            LOGGER.error(e1.getMessage(), e1);
        }
        return strDes;
    }

    /**
     * SHA-256加密
     * @param strSrc
     * @return
     */
    public static String sha256(String strSrc) {
        MessageDigest md = null;
        String strDes = null;
        try {
            byte[] bt = strSrc.getBytes("utf-8");
            md = MessageDigest.getInstance("SHA-256");
            md.update(bt);
            strDes = bytes2Hex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (UnsupportedEncodingException e1) {
            LOGGER.error(e1.getMessage(), e1);
        }
        return strDes;
    }

    /**
     * 验证签名（微信公众号）
     * @param signature
     * @param timestamp
     * @param nonce
     * @return
     */
    public static boolean checkSignature(String signature, String timestamp, String nonce) {
        String token = PropertiesUtil.getString("wechat_token");
        String[] arr = new String[]{token, timestamp, nonce};
        // 将token、timestamp、nonce三个参数进行字典序排序
        Arrays.sort(arr);
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            content.append(arr[i]);
        }
        MessageDigest md = null;
        String tmpStr = null;

        try {
            md = MessageDigest.getInstance("SHA-1");
            // 将三个参数字符串拼接成一个字符串进行sha1加密
            byte[] digest = md.digest(content.toString().getBytes());
            tmpStr = byteToStr(digest);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e.getMessage(), e);
        }
        content = null;
        // 将sha1加密后的字符串可与signature对比，标识该请求来源于微信
        return tmpStr != null ? tmpStr.equalsIgnoreCase(signature) : false;
    }

    private static String bytes2Hex(byte[] bts) {
        StringBuffer des = new StringBuffer("");
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = Integer.toHexString(bts[i] & 0xFF);
            if (tmp.length() == 1) {
                des.append("0");
            }
            des.append(tmp);
        }
        return des.toString();
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param byteArray
     * @return
     */
    private static String byteToStr(byte[] byteArray) {
        StringBuffer strDigest = new StringBuffer("");
        for (int i = 0; i < byteArray.length; i++) {
            strDigest.append(byteToHexStr(byteArray[i]));
        }
        return strDigest.toString();
    }

    /**
     * 将字节转换为十六进制字符串
     *
     * @param mByte
     * @return
     */
    private static String byteToHexStr(byte mByte) {
        char[] Digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = Digit[mByte & 0X0F];

        String s = new String(tempArr);
        return s;
    }

}
