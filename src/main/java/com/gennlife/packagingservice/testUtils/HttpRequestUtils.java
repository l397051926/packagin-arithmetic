package com.gennlife.packagingservice.testUtils;

import com.gennlife.packagingservice.arithmetic.utils.JsonAttrUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Map;

public class HttpRequestUtils {
    private static final int CONNECTTIMEOUT = 10000;
    private static Logger logger = LoggerFactory.getLogger(HttpRequestUtils.class);
    private static HttpRequestRetryHandler httpRequestRetryHandler;
    private static Registry<ConnectionSocketFactory> registry;
    private static PoolingHttpClientConnectionManager cm;
    private static boolean initPoolConfig = false;

    public static void initHttpPoolConfig(int maxTotal, int maxPerRoute) {
        httpRequestRetryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception,
                                        int executionCount, HttpContext context) {
                if (executionCount >= 5) {// 如果已经重试了5次，就放弃
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                    return false;
                }
                if (exception instanceof InterruptedIOException) {// 超时
                    return false;
                }
                if (exception instanceof UnknownHostException) {// 目标服务器不可达
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                    return false;
                }
                if (exception instanceof SSLException) {// SSL握手异常
                    return false;
                }

                HttpClientContext clientContext = HttpClientContext
                        .adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
        };
        registry = getConnectionSocketFactoryRegistry();
        cm = getPoolingHttpClientConnectionManager(maxTotal, maxPerRoute, registry);
        initPoolConfig = true;
    }
    public static void destory()
    {
        if(cm!=null) cm.close();
    }
    /**
     * post
     *
     * @param url
     * @param jsonParam
     * @return
     */
    public static String httpPost(String url, String jsonParam, int socketTimeOut, int connectTimeOut) {
        HttpClient httpClient = initHttpClient();
        HttpPost method = new HttpPost(url);
        try {
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeOut).setConnectTimeout(connectTimeOut).build();
            method.setConfig(requestConfig);
            if (null != jsonParam) {
                StringEntity entity = new StringEntity(jsonParam, "utf-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");

                method.setEntity(entity);
            }
            HttpResponse result = httpClient.execute(method);
            url = URLDecoder.decode(url, "UTF-8");
            if (result.getStatusLine().getStatusCode() == 200) {
                String str = "";
                try {
                    str = EntityUtils.toString(result.getEntity(), "utf-8");
                    return str;
                } catch (Exception e) {
                    logger.error("" + url, e);
                }
            }
        } catch (IOException e) {
            logger.error("" + url, e);
        }
        return null;
    }

    public static String httpPost(String url, String jsonParam) {
        return httpPost(url, jsonParam, 60000, CONNECTTIMEOUT);
    }

    public static HttpClient initHttpClient() {
        HttpClient httpClient = null;
        if (initPoolConfig) httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setRetryHandler(httpRequestRetryHandler).build();
        else httpClient = HttpClients.createDefault();
        return httpClient;
    }

    public static String httpGet(String url, int socketTimeOut, int connectTimeOut) {
        HttpClient httpClient = initHttpClient();
        HttpGet method = new HttpGet(url);
        try {
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeOut).setConnectTimeout(connectTimeOut).build();
            method.setConfig(requestConfig);
            HttpResponse result = httpClient.execute(method);
            if (result.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String str = null;
                try {
                    str = EntityUtils.toString(result.getEntity(), "utf-8");
                    return str;
                } catch (Exception e) {
                    logger.error("" + url, e);
                }
            }
        } catch (IOException e) {
            logger.error("" + url, e);
        }
        return null;
    }

    public static String httpGet(String url) {
        return httpGet(url, 10000, CONNECTTIMEOUT);
    }

    public static String httpPost(String url, Map<String, String> form, int socketTimeOut, int connectTimeOut) {
        HttpClient httpClient = initHttpClient();
        HttpPost method = new HttpPost(url);
        MultipartEntity entity = new MultipartEntity();
        try {
            if (form != null) {
                for (Map.Entry<String, String> item : form.entrySet()) {
                    entity.addPart(item.getKey(), new StringBody(item.getValue(), Charset.forName("UTF-8")));
                }
            }
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(3000).setConnectTimeout(CONNECTTIMEOUT).build();
            method.setConfig(requestConfig);
            method.setEntity(entity);
            HttpResponse result = httpClient.execute(method);
            url = URLDecoder.decode(url, "UTF-8");
            if (result.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String str = null;
                try {
                    str = EntityUtils.toString(result.getEntity(), "utf-8");
                    return str;
                } catch (Exception e) {
                    logger.error("" + url, e);
                }
            }
        } catch (IOException e) {
            logger.error("" + url, e);
        }
        return null;
    }

    public static String httpPost(String url, Map<String, String> form) {
        return httpPost(url, form, 10000, CONNECTTIMEOUT);
    }

    private static Registry<ConnectionSocketFactory> getConnectionSocketFactoryRegistry() {
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory
                .getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory
                .getSocketFactory();
        return RegistryBuilder
                .<ConnectionSocketFactory>create().register("http", plainsf)
                .register("https", sslsf).build();
    }

    private static PoolingHttpClientConnectionManager getPoolingHttpClientConnectionManager(int maxTotal, int maxPerRoute, Registry<ConnectionSocketFactory> registry) {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(
                registry);
        // 将最大连接数增加
        cm.setMaxTotal(maxTotal);
        // 将每个路由基础的连接增加
        cm.setDefaultMaxPerRoute(maxPerRoute);
        return cm;
    }

    public static JsonObject getOnePatientDataFormSearch(String url, String patient_sn, String indexName, JsonArray source) {
        JsonObject param = new JsonObject();
        param.addProperty("size", 1);
        param.addProperty("page", 1);
        param.addProperty("hospitalID", "public");
        param.addProperty("indexName", indexName);
        param.add("source", source);
        param.addProperty("query", patient_sn);
        JsonObject patient = JsonAttrUtil.toJsonObject(httpPost(url, JsonAttrUtil.toJsonStr(param)));
        return JsonAttrUtil.getJsonObjectValue("hits.hits._source", patient);
    }

    public static JsonObject getOnePatientAllDataFromSearch(String url, String patient_sn, String indexName) {
        JsonArray source = new JsonArray();
        source.add("");
        return getOnePatientDataFormSearch(url, patient_sn, indexName, source);
    }
}
