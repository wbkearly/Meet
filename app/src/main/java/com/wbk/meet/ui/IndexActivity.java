package com.wbk.meet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wbk.framework.base.BaseActivity;
import com.wbk.framework.bmob.BmobManager;
import com.wbk.framework.entity.Constants;
import com.wbk.framework.utils.SpUtils;
import com.wbk.meet.MainActivity;
import com.wbk.meet.R;

/**
 * 启动页
 */
public class IndexActivity extends BaseActivity {

    /**
     * 1 启动页全屏
     * 2 延迟进入主页
     * 3 根据逻辑 进入主页还是登录页
     * 4.适配刘海屏
     */

    private static final int SKIP_MAIN = 1000;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case SKIP_MAIN:
                    startMain();
                    break;
            }
            return false;
        }
    });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        mHandler.sendEmptyMessageDelayed(SKIP_MAIN, 2 * 1000);
    }

    /**
     * 进入主页
     */
    private void startMain() {
        // 判断App是否第一次启动
        Boolean isFirstRun = SpUtils.getInstance().getBoolean(Constants.SP_IS_FIRST_RUN, true);
        Intent intent = new Intent();
        if (isFirstRun) {
            // 跳转引导页
            intent.setClass(this, GuideActivity.class);
            SpUtils.getInstance().putBoolean(Constants.SP_IS_FIRST_RUN, false);
        } else {
            // 判断是否曾经登录
            String token = SpUtils.getInstance().getString(Constants.SP_TOKEN, "");
            if (TextUtils.isEmpty(token)) {
                // 判断Bmob是否登录
                if (BmobManager.getInstance().isLogin()) {
                    intent.setClass(this, MainActivity.class);
                } else {
                    intent.setClass(this, LoginActivity.class);
                }
            } else {
                intent.setClass(this, MainActivity.class);
            }
        }
        startActivity(intent);
        finish();
    }

    /**
     * 冷启动
     * 第一次安装加载应用
     * 启动后显示空白窗口
     * 启动创建应用进程
     *
     * 创建App对象
     * 启动主线程
     * 创建应用入口/LAUNCH
     * 填充ViewGroup中的View
     * 绘制View measure->layout->draw
     *
     * 优化
     * 视图优化---主题透明 启动图片
     * 代码优化---优化 Application  布局优化 不需要繁琐布局  阻塞UI  加载Bitmap/大图 其他占用主线程操作
     *
     * 检测App Activity启动时间
     * shell: adb shell am start -S -W com.wbk.meet/com.wbk.meet.ui.IndexActivity
     * log: TAG = displayed
     * */
}
