package com.wbk.framework.cloud;

import android.content.Context;
import com.wbk.framework.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

public class CloudManager {

    public static final String TOKEN_URL = "https://api-cn.ronghub.com/user/getToken.json";

    public static final String CLOUD_KEY = "25wehl3u213fw";

    public static final String CLOUD_SECRET = "4esY0ldtiOFBrD";

    // ObjectName
    public static final String MESSAGE_TEXT_NAME = "RC:TxtMsg";
    public static final String MESSAGE_IMAGE_NAME = "RC:ImgMsg";
    public static final String MESSAGE_LOCATION_NAME = "RC:LBSMsg";

    // Msg type
    public static final String TYPE_ADD_FRIEND = "TYPE_ADD_FRIEND";
    public static final String TYPE_AGREE_FRIEND = "TYPE_AGREE_FRIEND";
    public static final String TYPE_TEXT = "TYPE_TEXT";

    private static volatile CloudManager mInstance = null;

    /**
     * 发送消息的结果回调
     */
    private IRongCallback.ISendMessageCallback mISendMessageCallback = new IRongCallback.ISendMessageCallback() {
        /**
         * 消息发送前回调, 回调时消息已存储数据库
         * @param message 已存库的消息体
         */
        @Override
        public void onAttached(Message message) {

        }
        /**
         * 消息发送成功。
         * @param message 发送成功后的消息体
         */
        @Override
        public void onSuccess(Message message) {
            LogUtils.i("sendMessage success");
        }

        /**
         * 消息发送失败
         * @param message   发送失败的消息体
         * @param errorCode 具体的错误
         */
        @Override
        public void onError(Message message, RongIMClient.ErrorCode errorCode) {
            LogUtils.e("sendMessage error:" + errorCode);
        }
    };

    private CloudManager() {
    }

    public static CloudManager getInstance() {
        if (mInstance == null) {
            synchronized (CloudManager.class) {
                if (mInstance == null) {
                    mInstance = new CloudManager();
                }
            }
        }
        return mInstance;
    }

    public void initCloud(Context context) {
        RongIMClient.init(context, CLOUD_KEY);
    }

    /**
     * 连接融云
     */
    public void connect(String token) {

        RongIMClient.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onSuccess(String s) {
                LogUtils.i("链接成功:" + s);
            }

            @Override
            public void onError(RongIMClient.ConnectionErrorCode connectionErrorCode) {
                LogUtils.e("链接失败:" + connectionErrorCode);
            }

            /**
             * 数据库打开
             */
            @Override
            public void onDatabaseOpened(RongIMClient.DatabaseOpenStatus databaseOpenStatus) {

            }
        });
    }

    public void disconnect() {
        RongIMClient.getInstance().disconnect();
    }

    public void logout() {
        RongIMClient.getInstance().logout();
    }

    /**
     * 接收消息的监听器
     */
    public void setOnReceiveMessageListener(RongIMClient.OnReceiveMessageListener listener) {
        RongIMClient.setOnReceiveMessageListener(listener);
    }

    /**
     * 发送文本消息
     * @param msg 消息内容
     * @param targetId 用户id
     */
    public void sendTextMessage(String msg, String targetId) {
        LogUtils.i("发送消息...");
        ConversationType conversationType = ConversationType.PRIVATE;
        TextMessage messageContent = TextMessage.obtain(msg);
        Message message = Message.obtain(targetId, conversationType, messageContent);
        RongIMClient.getInstance().sendMessage(message, null, null, mISendMessageCallback);
    }

    public void sendTextMessage(String msg, String type, String targetId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("msg", msg);
            // 如果没有type则是普通消息
            jsonObject.put("type", type);
            sendTextMessage(jsonObject.toString(), targetId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
