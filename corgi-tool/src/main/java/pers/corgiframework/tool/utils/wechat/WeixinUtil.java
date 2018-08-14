package pers.corgiframework.tool.utils.wechat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import pers.corgiframework.tool.utils.PropertiesUtil;
import pers.corgiframework.tool.utils.XmlUtil;
import pers.corgiframework.tool.utils.wechat.pojo.AccessToken;
import pers.corgiframework.tool.utils.wechat.pojo.JsApiTicket;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.security.KeyStore;
import java.util.Formatter;
import java.util.Map;
import java.util.UUID;

/**
 * Created by syk on 2017/3/13.
 */
public class WeixinUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeixinUtil.class);
    // 从配置文件获取参数
    private static final String ACCESS_TOKEN_URL = PropertiesUtil.getString("access_token_url");
    private static final String JSAPI_TICKET_URL = PropertiesUtil.getString("public_jsapi_ticket_url");
    private static final String AUTH_OPENID_URL = PropertiesUtil.getString("public_auth_openid_url");

    /**
     * 发起https请求并获取结果
     * @param requestUrl 请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param outputStr 提交的数据
     */
    public static Map<Object, Object> httpRequest(String requestUrl, String requestMethod, String outputStr) {
        Map<Object, Object> map = null;
        StringBuffer buffer = new StringBuffer();
        try {
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            TrustManager[] tm = { new MyX509TrustManager() };
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();

            URL url = new URL(requestUrl);
            HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
            httpUrlConn.setSSLSocketFactory(ssf);

            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            httpUrlConn.setRequestMethod(requestMethod);

            if("GET".equalsIgnoreCase(requestMethod)){
                httpUrlConn.connect();
            }
            // 当有数据需要提交时
            if(null != outputStr){
                OutputStream os = httpUrlConn.getOutputStream();
                // 注意编码格式，防止中文乱码
                os.write(outputStr.getBytes("utf-8"));
                os.close();
            }
            // 将返回的输入流转换成字符串
            InputStream is = httpUrlConn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);

            String str = null;
            while((str = br.readLine()) != null){
                buffer.append(str);
            }
            br.close();
            isr.close();
            // 释放资源
            is.close();
            httpUrlConn.disconnect();
            ObjectMapper mapper = new ObjectMapper();
            map = mapper.readValue(buffer.toString(), Map.class);
        } catch (ConnectException ce) {
            LOGGER.error("Weixin server connection timed out.");
            LOGGER.error(ce.getMessage(), ce);
        } catch (Exception e) {
            LOGGER.error("https request error:{}", e);
            LOGGER.error(e.getMessage(), e);
        }
        return map;
    }

    /**
     * 获取access_token
     * @param appId
     * @param appSecret
     * @return
     */
    public static AccessToken getAccessToken(String appId, String appSecret){
        AccessToken accessToken = null;
        String requestUrl = ACCESS_TOKEN_URL.replace("APPID", appId).replace("APPSECRET", appSecret);
        Map<Object, Object> map = httpRequest(requestUrl, "GET", null);
        //如果请求成功
        if(null != map){
            try {
                accessToken = new AccessToken();
                accessToken.setToken(map.get("access_token").toString());
                accessToken.setExpiresIn(Integer.parseInt(map.get("expires_in").toString()));
            } catch (Exception e) {
                LOGGER.error(e.getMessage(),e);
            }
        }
        return accessToken;
    }

    /**
     * 获取JsApiTicket
     * @param appId
     * @param appSecret
     * @return
     */
    public static JsApiTicket getJsTicket(String appId, String appSecret) {
        JsApiTicket jat = null;
        //获取token
        AccessToken at = getAccessToken(appId, appSecret);
        String requestUrl = JSAPI_TICKET_URL.replace("ACCESS_TOKEN", at.getToken());
        Map<Object, Object> map = WeixinUtil.httpRequest(requestUrl, "GET", null);
        //如果请求成功
        if(null != map){
            try {
                jat = new JsApiTicket();
                jat.setTicket(map.get("ticket").toString());
                jat.setExpiresIn(Integer.parseInt(map.get("expires_in").toString()));
            } catch (Exception e) {
                LOGGER.error(e.getMessage(),e);
            }
        }
        return jat;
    }

    /**
     * 网页授权获取open_id
     * @param appId
     * @param appSecret
     * @param code 微信返回
     * @return
     */
    public static String getOpenId(String appId, String appSecret, String code) {
        String open_id = null;
        try {
            if(StringUtils.isNotBlank(code)){
                String requestUrl = AUTH_OPENID_URL.replace("APPID", appId).replace("SECRET", appSecret).replace("CODE", code);
                Map<Object, Object> map = WeixinUtil.httpRequest(requestUrl, "GET", null);
                if (null != map && null != map.get("openid")) {
                    open_id = map.get("openid").toString();
                }
            }
        } catch (Exception e) {
            LOGGER.info(e.getMessage(), e);
        }
        return open_id;
    }

    /**
     *获取微信昵称时过滤存在的乱码
     */
    public static String doFilter(String str) {
        StringBuffer str_Result = new StringBuffer("");
        String str_OneStr = "";
        for (int z = 0; z < str.length(); z++) {
            str_OneStr = str.substring(z, z + 1);
            if (str_OneStr.matches("[\u4e00-\u9fa5]+") || str_OneStr.matches("[\\x00-\\x7F]+")) {
                str_Result.append(str_OneStr);
            }
        }
        return str_Result.toString();
    }

    /**
     * 将字节数组转换为十六进制字符串
     * @param hash
     * @return
     */
    public static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    /**
     * JS-SDK使用权限生成签名的随机串
     */
    public static String createNonceStr() {
        return UUID.randomUUID().toString();
    }

    /**
     * JS-SDK使用权限生成签名的时间戳
     */
    public static String createTimestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }

    /**
     * 请求带证书信息的接口
     * @param httpsUrl
     * @param method
     * @param params
     * @param mchId
     * @return
     * @throws Exception
     */
    public static Map<String,String> httpsRequestSSL(String httpsUrl, String method, String params, String mchId) throws Exception{
        //        ----------------------------以下为httpclient4.3.4版本写法-------------------------------------------------
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        String location = "classpath:wx_pay.p12";
        Resource resource = resourceLoader.getResource(location);
        InputStream instream = null;
        try {
            instream = resource.getInputStream();
            keyStore.load(instream, mchId.toCharArray());
        } finally {
            instream.close();
        }
        String reponseStr = "";
        Map<String, String> map = Maps.newHashMap();
        // Trust own CA and all self-signed certs
        SSLContext sslcontext = SSLContexts.custom()
                .loadKeyMaterial(keyStore, mchId.toCharArray())
                .build();
        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext,
                new String[]{"TLSv1"},
                null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

        try {
            CloseableHttpResponse response = null;
            if ("post".equalsIgnoreCase(method)) {
                HttpPost httpPost = new HttpPost(httpsUrl);
                httpPost.setEntity(new StringEntity(params, "utf-8"));
                response = httpclient.execute(httpPost);
            } else {
                HttpGet httpget = new HttpGet(httpsUrl);
                response = httpclient.execute(httpget);
            }
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent(), "utf-8"));
                    String text = "";
                    while ((text = bufferedReader.readLine()) != null) {
                        reponseStr += text;
                    }
                }
                EntityUtils.consume(entity);
                map = XmlUtil.xmlToMap(reponseStr);
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
        return map;
    }

}
