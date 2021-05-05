package com.wbk.meet.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.wbk.framework.bmob.BmobManager;
import com.wbk.framework.cloud.CloudManager;
import com.wbk.framework.db.LitePalHelper;
import com.wbk.framework.db.NewFriend;
import com.wbk.framework.entity.Constants;
import com.wbk.framework.gson.TextBean;
import com.wbk.framework.utils.CommonUtils;
import com.wbk.framework.utils.LogUtils;
import com.wbk.framework.utils.SpUtils;

import org.litepal.LitePal;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;

public class CloudService extends Service {

    private Disposable mDisposable;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        linkCloudServer();
    }

    /**
     * 连接云服务
     */
    private void linkCloudServer() {
        String token = SpUtils.getInstance().getString(Constants.SP_TOKEN, "");
        LogUtils.i("token:" + token);
        CloudManager.getInstance().connect(token);
        CloudManager.getInstance().setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageWrapperListener() {
            /**
             * 接收实时或者离线消息。
             * 注意:
             * 1. 针对接收离线消息时，服务端会将 200 条消息打成一个包发到客户端，客户端对这包数据进行解析。
             * 2. hasPackage 标识是否还有剩余的消息包，left 标识这包消息解析完逐条抛送给 App 层后，剩余多少条。
             * 如何判断离线消息收完：
             * 1. hasPackage 和 left 都为 0；
             * 2. hasPackage 为 0 标识当前正在接收最后一包（200条）消息，left 为 0 标识最后一包的最后一条消息也已接收完毕。
             *
             * @param message    接收到的消息对象
             * @param left       每个数据包数据逐条上抛后，还剩余的条数
             * @param hasPackage 是否在服务端还存在未下发的消息包
             * @param offline    消息是否离线消息
             * @return 是否处理消息。 如果 App 处理了此消息，返回 true; 否则返回 false 由 SDK 处理。
             */
            @Override
            public boolean onReceived(final Message message, final int left, boolean hasPackage, boolean offline) {
                LogUtils.i("message:" + message);
                String objectName = message.getObjectName();
                if (objectName.equals(CloudManager.MESSAGE_TEXT_NAME)) {
                    // 获取消息主体
                    TextMessage textMessage = (TextMessage) message.getContent();
                    String content = textMessage.getContent();
                    LogUtils.i("content:" + content);
                    TextBean textBean = new Gson().fromJson(content, TextBean.class);
                    if (textBean.getType().equals(CloudManager.TYPE_TEXT)) {

                    } else if (textBean.getType().equals(CloudManager.TYPE_ADD_FRIEND)) {
                        // 存入本地数据库
                        // 过滤重复数据
                        mDisposable = Observable.create((ObservableOnSubscribe<List<NewFriend>>) emitter -> {
                            emitter.onNext(LitePalHelper.getInstance().queryNewFriend());
                            emitter.onComplete();
                        }).subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(newFriends -> {
                                    boolean repeat = false;
                                    if (CommonUtils.isNotEmpty(newFriends)) {
                                        for (NewFriend newFriend : newFriends) {
                                            if (message.getSenderUserId().equals(newFriend.getUserId())) {
                                                repeat = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (!repeat) {
                                        LitePalHelper.getInstance().saveNewFriend(textBean.getMsg(), message.getSenderUserId());
                                    }
                                });
                    } else if (textBean.getType().equals(CloudManager.TYPE_AGREE_FRIEND)) {
                        BmobManager.getInstance().addFriend(message.getSenderUserId(), new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    // TODO 刷新好友列表
                                }
                            }
                        });
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }
}
