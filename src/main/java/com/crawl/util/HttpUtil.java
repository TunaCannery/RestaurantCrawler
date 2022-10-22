package com.crawl.util;

import com.squareup.okhttp.*;

import java.io.IOException;

public class HttpUtil {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static OkHttpClient httpClient = new OkHttpClient();

    /**
     * Fetch data from url via GET method.
     *
     * @param url the url you want to get from
     * @return the response body as string
     */
    public static String get(String url) {

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            LogUtil.printOnConsole(String.format("Fetching data from %s fails, please check it out.", url));
            return null;
        }

    }

    /**
     * store data to url via POST method.
     *
     * @param url  the url you want to POST TO
     * @param json
     * @return the response body as string
     */

    public static String post(String url, String json) {

        long start = System.currentTimeMillis();

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            LogUtil.printOnConsole(String.format("Posting data to %s fails,please check it out. It cost %dms", url, System.currentTimeMillis()-start));
            return null;
        }

    }
}
