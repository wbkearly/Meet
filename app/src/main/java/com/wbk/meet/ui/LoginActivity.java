package com.wbk.meet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wbk.framework.base.BaseUIActivity;
import com.wbk.framework.bmob.BmobManager;
import com.wbk.framework.bmob.IMUser;
import com.wbk.framework.entity.Constants;
import com.wbk.framework.manager.DialogManager;
import com.wbk.framework.utils.SpUtil;
import com.wbk.framework.view.DialogView;
import com.wbk.framework.view.LoadingView;
import com.wbk.framework.view.TouchPicture;
import com.wbk.meet.MainActivity;
import com.wbk.meet.R;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;

/**
 * 登录页
 */
public class LoginActivity extends BaseUIActivity implements View.OnClickListener {

    /**
     * 点击发送 弹出图片验证码
     * 发送 按钮不可点击 开始倒计时； 倒计时结束 文字变为发送
     * 通过手机号码和验证码登录
     * 登录成功 获取本地对象
     */
    private EditText mEtPhone;
    private EditText mEtCode;
    private Button mBtnSendCode;
    private Button mBtnLogin;
    private DialogView mCodeView;
    private LoadingView mLoadingView;
    private TouchPicture mPicture;

    private static final int H_TIME = 1001;
    private static int TIME = 60;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case H_TIME:
                    TIME--;
                    mBtnSendCode.setText(TIME + "s");
                    if (TIME > 0) {
                        mHandler.sendEmptyMessageDelayed(H_TIME, 1000);
                    } else {
                        mBtnSendCode.setEnabled(true);
                        mBtnSendCode.setText(getString(R.string.text_login_send));
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {

        initDialogView();
        mEtPhone = findViewById(R.id.et_phone);
        mEtCode = findViewById(R.id.et_code);
        mBtnSendCode = findViewById(R.id.btn_send_code);
        mBtnLogin = findViewById(R.id.btn_login);
        mBtnSendCode.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);

        String phone = SpUtil.getInstance().getString(Constants.SP_PHONE, "");
        if (!TextUtils.isEmpty(phone)) {
            mEtPhone.setText(phone);
        }
    }

    private void initDialogView() {
        mCodeView = DialogManager.getInstance().initView(this, R.layout.dialog_code_view);
        mLoadingView = new LoadingView(this);
        mPicture = mCodeView.findViewById(R.id.picture);
        mPicture.setViewResultListener(new TouchPicture.OnViewResultListener() {
            @Override
            public void onResult() {
                DialogManager.getInstance().hide(mCodeView);
                sendSMS();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_code:
                mCodeView.show();
                break;
            case R.id.btn_login:
                login();
                break;
            default:
                break;
        }
    }

    private void login() {
        String phone = mEtPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, getString(R.string.text_login_phone_is_empty),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        String code = mEtCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, getString(R.string.text_login_code_is_empty),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mLoadingView.show("正在登录...");
        BmobManager.getInstance().signOrLoginByMobilePhone(phone, code, new LogInListener<IMUser>() {
            @Override
            public void done(IMUser user, BmobException e) {
                mLoadingView.hide();
                if (e == null) {
                    // 登录成功
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    // 把手机号码保存下来
                    SpUtil.getInstance().putString(Constants.SP_PHONE, phone);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "ERROR:" + e.toString(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 发送短信验证码
     */
    private void sendSMS() {
        String phone = mEtPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, getString(R.string.text_login_phone_is_empty),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        BmobManager.getInstance().requestSMS(phone, new QueryListener<Integer>() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null) {
                    mBtnSendCode.setEnabled(false);
                    mHandler.sendEmptyMessage(H_TIME);
                    Toast.makeText(LoginActivity.this, "短信验证码发送成功",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "短信验证码发送失败",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
