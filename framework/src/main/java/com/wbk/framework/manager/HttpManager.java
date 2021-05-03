package com.wbk.framework.manager;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpManager {

    private static final String tokenUrl = "https://api-cn.ronghub.com/user/getToken.json";

    private static final String CLOUD_KEY = "25wehl3u213fw";

    private static final String CLOUD_SECRET = "4esY0ldtiOFBrD";

    private static volatile HttpManager mInstance = null;

    private OkHttpClient mOkHttpClient;

    private HttpManager() {
        mOkHttpClient = new OkHttpClient();
    }

    public static HttpManager getInstance() {
        if (mInstance == null) {
            synchronized (HttpManager.class) {
                if (mInstance == null) {
                    mInstance = new HttpManager();
                }
            }
        }
        return mInstance;
    }

    public String postCloudToken(HashMap<String, String> map) {
        String Timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String Nonce = String.valueOf(Math.floor(Math.random() * 100000));
        // TODO
//        String Signature = SHA1.sha1(CLOUD_SECRET + Nonce + Timestamp);
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : map.keySet()) {
            builder.add(key, map.get(key));
        }
        RequestBody requestBody = builder.build();
        // 添加Header
        Request request = new Request.Builder()
                .url(tokenUrl)
                .addHeader("Timestamp", Timestamp)
                .addHeader("App-Key", CLOUD_KEY)
                .addHeader("Nonce", Nonce)
              // TODO  .addHeader("Signature", Signature)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(requestBody)
                .build();
        try {
            return mOkHttpClient.newCall(request).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
