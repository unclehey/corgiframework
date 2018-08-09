package pers.corgiframework.tool.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * AES加解密工具类
 * Created by syk on 2018/7/19.
 */
public class AESUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(AESUtil.class);
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final String KEY_AES = "AES";

    /**
     * 加密
     *
     * @param data 需要加密的内容
     * @param key  加密密钥
     * @return
     */
    public static String encrypt(String data, String key) {
        return doAES(data, key, Cipher.ENCRYPT_MODE);
    }

    /**
     * 解密
     *
     * @param data 待解密内容
     * @param key  解密密钥
     * @return
     */
    public static String decrypt(String data, String key) {
        return doAES(data, key, Cipher.DECRYPT_MODE);
    }

    /**
     * 加解密
     *
     * @param data 待处理数据
     * @param key  密钥
     * @param mode 加解密mode
     * @return
     */
    private static String doAES(String data, String key, int mode) {
        try {
            if (StringUtils.isBlank(data) || StringUtils.isBlank(key)) {
                return null;
            }
            // 判断是加密还是解密：true加密，false解密
            boolean encrypt = mode == Cipher.ENCRYPT_MODE;
            byte[] content;
            if (encrypt) {
                // 获取加密内容的字节数组(这里要设置为utf-8)不然内容中如果有中文和英文混合中文就会解密为乱码
                content = data.getBytes(DEFAULT_CHARSET);
            } else {
                // 将加密并编码后的内容解码成字节数组
                content = Base64Util.decode(data);
            }
            // 1、构造密钥生成器，指定为AES算法,不区分大小写
            KeyGenerator kgen = KeyGenerator.getInstance(KEY_AES);
            // 2、根据ecnodeRules规则初始化密钥生成器 根据传入的字节数组生成一个128位的随机源
            kgen.init(128, new SecureRandom(key.getBytes()));
            // 3、产生原始对称密钥
            SecretKey secretKey = kgen.generateKey();
            // 4、获得原始对称密钥的字节数组
            byte[] enCodeFormat = secretKey.getEncoded();
            // 5、根据字节数组生成AES密钥
            SecretKeySpec keySpec = new SecretKeySpec(enCodeFormat, KEY_AES);
            // 6、根据指定算法AES自成密码器
            Cipher cipher = Cipher.getInstance(KEY_AES);
            // 7、初始化密码器，第一个参数为加密(Encrypt_mode)或者解密(Decrypt_mode)，第二个参数为使用的KEY
            cipher.init(mode, keySpec);
            byte[] result = cipher.doFinal(content);
            if (encrypt) {
                // 将加密后的数据转换为字符串
                return new String(Base64Util.encode(result));
            } else {
                // 将解密后的数据转换为字符串
                return new String(result, DEFAULT_CHARSET);
            }
        } catch (Exception e) {
            LOGGER.error("AES加解密处理异常", e);
        }
        return null;
    }

    public static void main(String[] args) {
        long timestamp = DateTimeUtil.getNowTimestamp();
        String key = StringUtil.genUUID();
        String content = "{\"token\": \"d03e6409-7408-41c2-9e48-e2150c75b912\", \"orderType\": 14, \"packageId\": \"27\", \"body\": \"购买企业会员VIP个数\", \"timestamp\": \"" + timestamp + "\"}";
        System.out.println("加密前：" + content);
        System.out.println("加密密钥和解密密钥：" + key);
        String encrypt = encrypt(content, key);
        System.out.println("加密后：" + encrypt);
        String decrypt = decrypt(encrypt, key);
        System.out.println("解密后：" + decrypt);
    }

}
