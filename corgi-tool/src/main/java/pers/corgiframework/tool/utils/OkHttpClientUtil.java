package pers.corgiframework.tool.utils;

import com.squareup.okhttp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * HttpClient工具类
 * Created by syk on 2018/7/24.
 */
public class OkHttpClientUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(OkHttpClientUtil.class);
    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");
    private static final MediaType STRING = MediaType.parse("text/x-markdown;charset=utf-8");

    /**
     * get请求
     * @param url
     * @param params
     * @return
     */
    public static String executeGet(String url, String params) {
        String returnMsg = "";
        try {
            Request request = new Request.Builder()
                    .url(url + "?" + params)
                    .build();
            Response response = client.newCall(request).execute();
            returnMsg = response.body().string();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return returnMsg;
    }

    /**
     * post请求发送json数据
     * @param url
     * @param jsonData
     * @return
     */
    public static String executeJsonPost(String url, String jsonData) {
        String returnMsg = "";
        try {
            RequestBody body = RequestBody.create(JSON, jsonData);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            returnMsg = response.body().string();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return returnMsg;
    }

    /**
     * post请求发送json数据(忽略https证书信任)
     * @param url
     * @param jsonData
     * @return
     */
    public static String executeJsonWithNoVerify(String url, String jsonData) {
        String returnMsg = "";
        try {
            RequestBody body = RequestBody.create(JSON, jsonData);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            X509TrustManager xtm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }
                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    X509Certificate[] x509Certificates = new X509Certificate[0];
                    return x509Certificates;
                }
            };
            SSLContext sslContext = null;
            try {
                sslContext = SSLContext.getInstance("SSL");

                sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
            HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            // 忽略https证书信任
            client.setSslSocketFactory(sslContext.getSocketFactory()).setHostnameVerifier(DO_NOT_VERIFY);
            client.setReadTimeout(60, TimeUnit.SECONDS);    // 读取超时时间设置
            client.setConnectTimeout(60, TimeUnit.SECONDS); // 连接超时时间设置
            client.setWriteTimeout(60, TimeUnit.SECONDS);   // 写入超时时间设置
            Response response = client.newCall(request).execute();
            returnMsg = response.body().string();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return returnMsg;
    }

    /**
     * get请求(忽略https证书信任)
     * @param url
     * @return
     */
    public static String executeGetWithNoVerify(String url) {
        String returnMsg = "";
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            X509TrustManager xtm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }
                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    X509Certificate[] x509Certificates = new X509Certificate[0];
                    return x509Certificates;
                }
            };
            SSLContext sslContext = null;
            try {
                sslContext = SSLContext.getInstance("SSL");

                sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
            HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            // 忽略https证书信任
            client.setSslSocketFactory(sslContext.getSocketFactory()).setHostnameVerifier(DO_NOT_VERIFY);
            Response response = client.newCall(request).execute();
            returnMsg = response.body().string();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return returnMsg;
    }

    /**
     * post请求发送String数据
     * @param url
     * @param param
     * @return
     */
    public static String executeStringPost(String url, String param) {
        String returnMsg = "";
        try {
            RequestBody body = RequestBody.create(STRING, param);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            returnMsg = response.body().string();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return returnMsg;
    }

    /**
     * post请求发送header数据
     * @param url
     * @param map
     * @return
     */
    public static String executeHeaderPost(String url, Map<String, Object> map) {
        String returnMsg = "";
        try {
            Headers headers = setHeaders(map);
            Request request = new Request.Builder()
                    .url(url)
                    .headers(headers)
                    .build();
            Response response = client.newCall(request).execute();
            returnMsg = response.body().string();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return returnMsg;
    }

    /**
     * 设置Header头
     * @param headersParams
     * @return
     */
    private static Headers setHeaders(Map<String, Object> headersParams) {
        Headers headers = null;
        Headers.Builder headerBuilder = new Headers.Builder();
        if (headersParams != null) {
            for (String key : headersParams.keySet()) {
                headerBuilder.add(key, headersParams.get(key).toString());
            }
        }
        headers = headerBuilder.build();
        return headers;
    }
}
