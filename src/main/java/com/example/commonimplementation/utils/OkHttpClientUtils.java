package com.example.commonimplementation.utils;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * OkHttpClientUtils
 */
public class OkHttpClientUtils {

    private OkHttpClientUtils(){

    }

    private static Logger logger = LoggerFactory.getLogger(OkHttpClientUtils.class);

    private static OkHttpClient okHttpClient;

    static {
        okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .sslSocketFactory(SSLSocketClient.getSSLSocketFactory())
                .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
                .build();
    }

    /**
     * GET  请求
     *
     * @param url
     * @param paramMap
     * @param headerMap
     * @return
     */
    public static String get(String url, Map<String, String> paramMap, Map<String, String> headerMap) {


        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(url)
                .append("?");

        if (paramMap != null && !paramMap.isEmpty()) {
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                stringBuilder.append(entry.getKey())
                        .append("=")
                        .append(entry.getValue())
                        .append("&");
            }
        }
        if (stringBuilder.lastIndexOf("?") > 0 || stringBuilder.lastIndexOf("&") > 0) {
            stringBuilder.substring(0, stringBuilder.length() - 1);
        }

        Request.Builder builder = new Request.Builder().url(stringBuilder.toString()).get();
        if (headerMap != null && !headerMap.isEmpty()) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        try {
            Response response = okHttpClient.newCall(builder.build()).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (IOException e) {
            logger.error("OKHttpClientUtils  发送GET请求出现异常", e);
        }

        return null;

    }

    public static String get(String url, String params, Map<String, String> headerMap){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(url)
            .append("?")
            .append(params);
        if (stringBuilder.lastIndexOf("?") > 0 || stringBuilder.lastIndexOf("&") > 0) {
            stringBuilder.substring(0, stringBuilder.length() - 1);
        }

        Request.Builder builder = new Request.Builder().url(stringBuilder.toString()).get();
        if (headerMap != null && !headerMap.isEmpty()) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        try {
            Response response = okHttpClient.newCall(builder.build()).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (IOException e) {
            logger.error("OKHttpClientUtils  发送GET请求出现异常", e);
        }

        return null;
    }

    /**
     * POST  请求
     *
     * @param url
     * @param paramMap
     * @param headerMap
     * @return
     */
    public static String post(String url, Map<String, Object> paramMap, Map<String, String> headerMap) {
        FormBody.Builder params=new FormBody.Builder();
        if (paramMap != null && !paramMap.isEmpty()) {
            for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                if(null != entry.getValue()){
                    params.add(entry.getKey(), entry.getValue().toString());
                }
            }
        }

        Request.Builder builder = new Request.Builder().url(url).post(params.build());
        if (headerMap != null && !headerMap.isEmpty()) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        try {
            Response response = okHttpClient.newCall(builder.build()).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (IOException e) {
            logger.error("OKHttpClientUtils  发送POST请求出现异常", e);
        }

        return null;
    }

    public static String post(String url, String json) {

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (IOException e) {
            logger.error("OKHttpClientUtils  发送POST请求出现异常", e);
        }

        return null;
    }
}
