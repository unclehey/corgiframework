package pers.corgiframework.tool.utils;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

/**
 * RSA加解密工具类
 * Created by syk on 2018/7/25.
 */
public class RSAUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(RSAUtil.class);
    /**
     * 默认公钥私钥
     */
    public static final String DEFAULT_PUBLIC_KEY = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAK9MQwZTp6/X4nPd9L0JHy20kSJy+fml6x231itSYg42teK7vwF2l0Ubzfnnme1AoHA2ag8NNwq7U4osmGG2QXcCAwEAAQ==";
    public static final String DEFAULT_PRIVATE_KEY = "MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAr0xDBlOnr9fic930vQkfLbSRInL5+aXrHbfWK1JiDja14ru/AXaXRRvN+eeZ7UCgcDZqDw03CrtTiiyYYbZBdwIDAQABAkB4uAAAP2PfSMB+IoAEHmAGTa8VFjDnp0c+8/bdZqJ4PjhtvfQWz4DxvN1c3krnRJsYS+RHNk29QfxCCdlWmcJBAiEA5Jcyq0Q4X0X4wJ5sHGV+oqwZruB83ynfrMPnpN4r7l8CIQDEUTG2zP3UKHF+P3xx0F2Inh0otO+nhOx6ZQj3DP7T6QIgHxd5qtXinQLVbM2fgtVDPjkOXTznfysg40zaKxCVBR0CIQCqOelMo+V5IHSAzxXeLpncC5YnJPUlodnXq2IhrlAMeQIhANH69ERpV6F4sXnJMjdC5g9UYqTFCccR7SbxuDXSFs8k";
    /**
     * 加密算法RSA
     */
    public static final String KEY_ALGORITHM = "RSA";
    /**
     * 获取公钥的key
     */
    private static final String PUBLIC_KEY = "RSAPublicKey";
    /**
     * 获取私钥的key
     */
    private static final String PRIVATE_KEY = "RSAPrivateKey";
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 53;
    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 64;

    /**
     * 生成密钥对：公钥和私钥
     * @return
     */
    public static Map<String, Object> genKeyPair() {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGen.initialize(512);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            Map<String, Object> keyMap = Maps.newHashMap();
            keyMap.put(PUBLIC_KEY, publicKey);
            keyMap.put(PRIVATE_KEY, privateKey);
            return keyMap;
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取公钥
     * @param keyMap 密钥对
     * @return
     */
    public static String getPublicKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return Base64Util.encode(key.getEncoded());
    }

    /**
     * 获取私钥
     * @param keyMap 密钥对
     * @return
     */
    public static String getPrivateKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return Base64Util.encode(key.getEncoded());
    }

    /**
     * 公钥加密
     * @param data
     * @param publicKey 公钥
     * @return
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey) {
        try {
            byte[] keyBytes = Base64Util.decode(publicKey);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            Key publicK = keyFactory.generatePublic(x509KeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, publicK);
            int inputLen = data.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段加密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }
            byte[] encryptedData = out.toByteArray();
            out.close();
            return encryptedData;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 私钥解密
     * @param encryptedData
     * @param privateKey 私钥
     * @return
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey) {
        try {
            byte[] keyBytes = Base64Util.decode(privateKey);
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateK);
            int inputLen = encryptedData.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            byte[] decryptedData = out.toByteArray();
            out.close();
            return decryptedData;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        // 公钥加密
        String requestMessage = "{\"key\": \"380bd416c0534970b97fde0f929a9459\", \"sign\": \"/KnRin6XvMVk967gyIsGN2FjRpNJqDWCZYtmXeu9RK9eofcQz8PlYLrm16Ld+s/zzjdsH7YthA4OYUConHfO+F2zpnFawu94U/8ptejLv3vImHgNliRqb5qkUOY+Ve7Y0F+VbvGfq35+L4s65AUvW1HEl421VAlT7BTmcp94NCWtvPI4qZkiVLlhbQm+yeCq/VQtUD/tTC5DPJXQIfQOzw==\"}";
        byte[] afterBytes = encryptByPublicKey(requestMessage.getBytes(), DEFAULT_PUBLIC_KEY);
        String afterEncrypt = Base64Util.encode(afterBytes);
        System.out.println(afterEncrypt);

        /*// 私钥解密
        String base64Str = "F0lhPyJegC/lGk2lTIbcw4/zQ/0L1K1qP2qsbN1ruNPlAK027ByNiXNLzwEmh88a2a2c2x5RvZVEjzP5Cfwcog==";
        byte[] bytes = Base64Util.decode(base64Str);
        String beforeEncrypt = new String(decryptByPrivateKey(bytes, DEFAULT_PRIVATE_KEY), "UTF-8");
        System.out.println(beforeEncrypt);*/
    }

}
