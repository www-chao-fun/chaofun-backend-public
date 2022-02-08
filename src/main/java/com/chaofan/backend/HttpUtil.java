package com.chaofan.backend;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.Future;
import static org.asynchttpclient.Dsl.asyncHttpClient;


@Slf4j
public class HttpUtil {

    public static AsyncHttpClient asyncHttpClient = asyncHttpClient();
    static  {
    }
    public static JSONObject doGetJson(String url) {
        try {
            Future<Response> whenResponse = asyncHttpClient.prepareGet(url).execute();
            JSONObject object = JSON.parseObject(whenResponse.get().getResponseBody());

            return object;
        } catch (Exception ex) {
            log.info(ex.getMessage(), ex);
            return null;
        }
    }

    public static InputStream getFile(String fileUrl) {
        Future<Response> whenResponse = asyncHttpClient.prepareGet(fileUrl).execute();
        InputStream targetStream = null;

        try {
            Response response = whenResponse.get();
            if (response.getStatusCode() >= 400) {
                log.error("get File error code {}, {}", response.getStatusCode(), fileUrl);
            }
            targetStream = new ByteArrayInputStream(response.getResponseBodyAsBytes());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return targetStream;
    }

}
