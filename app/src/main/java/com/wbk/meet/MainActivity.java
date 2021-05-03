package com.wbk.meet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.wbk.framework.base.BaseUIActivity;
import com.wbk.framework.bmob.BmobManager;
import com.wbk.framework.bmob.IMUser;
import com.wbk.framework.entity.Constants;
import com.wbk.framework.helper.GlideHelper;
import com.wbk.framework.manager.DialogManager;
import com.wbk.framework.manager.HttpManager;
import com.wbk.framework.utils.SpUtil;
import com.wbk.framework.view.DialogView;
import com.wbk.meet.fragment.ChatFragment;
import com.wbk.meet.fragment.MeFragment;
import com.wbk.meet.fragment.SquareFragment;
import com.wbk.meet.fragment.StarFragment;
import com.wbk.meet.service.CloudService;
import com.wbk.meet.ui.FirstUploadActivity;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class MainActivity extends BaseUIActivity implements View.OnClickListener {

    public static final int UPLOAD_REQUEST_CODE = 1002;

    private FrameLayout mFlMain;
    private ImageView mIvStar;
    private TextView mTvStar;
    private LinearLayout mLlStar;
    private StarFragment mStarFragment;
    private FragmentTransaction mStarTransaction;

    private ImageView mIvSquare;
    private TextView mTvSquare;
    private LinearLayout mLlSquare;
    private SquareFragment mSquareFragment;
    private FragmentTransaction mSquareTransaction;

    private ImageView mIvChat;
    private TextView mTvChat;
    private LinearLayout mLlChat;
    private ChatFragment mChatFragment;
    private FragmentTransaction mChatTransaction;

    private ImageView mIvMe;
    private TextView mTvMe;
    private LinearLayout mLlMe;
    private MeFragment mMeFragment;
    private FragmentTransaction mMeTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        requestPermission();
    }

    /**
     * 请求权限
     */
    private void requestPermission() {
        request(new OnPermissionsResult() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFail(List<String> noPermissions) {

            }
        });
    }

    private void initViews() {
        mFlMain = findViewById(R.id.fl_main);
        mIvStar = findViewById(R.id.iv_star);
        mTvStar = findViewById(R.id.tv_star);
        mLlStar = findViewById(R.id.ll_star);

        mIvSquare = findViewById(R.id.iv_square);
        mTvSquare = findViewById(R.id.tv_square);
        mLlSquare = findViewById(R.id.ll_square);

        mIvChat = findViewById(R.id.iv_chat);
        mTvChat = findViewById(R.id.tv_chat);
        mLlChat = findViewById(R.id.ll_chat);

        mIvMe = findViewById(R.id.iv_me);
        mTvMe = findViewById(R.id.tv_me);
        mLlMe = findViewById(R.id.ll_me);

        mLlStar.setOnClickListener(this);
        mLlSquare.setOnClickListener(this);
        mLlChat.setOnClickListener(this);
        mLlMe.setOnClickListener(this);

        mTvStar.setText(getString(R.string.text_star));
        mTvSquare.setText(getString(R.string.text_square));
        mTvChat.setText(getString(R.string.text_chat));
        mTvMe.setText(getString(R.string.text_me));

        initFragments();
        // 默认选项卡
        checkMainTab(0);

        // 检查TOKEN
        checkToken();
    }

    private void checkToken() {
        // 获取token 需要三个参数
        // 用户地址 头像地址 昵称
        String token = SpUtil.getInstance().getString(Constants.SP_TOKEN, "");
        if (!TextUtils.isEmpty(token)) {
            // 启动云服务去连接融云服务
            startService(new Intent(this, CloudService.class));
        } else {
            String tokenPortrait = BmobManager.getInstance().getUser().getTokenPortrait();
            String tokenNickname = BmobManager.getInstance().getUser().getTokenNickname();
            if (!TextUtils.isEmpty(tokenPortrait) && !TextUtils.isEmpty(tokenNickname)) {
                // 创建token
                createToken();
            } else {
                // 创建上传头像对话框
                createUploadDialog();
            }
        }
    }

    private void createToken() {

        HashMap<String, String> map = new HashMap<>();
        map.put("userId", BmobManager.getInstance().getUser().getObjectId());
        map.put("name", BmobManager.getInstance().getUser().getNickname());
        map.put("portraitUrl", BmobManager.getInstance().getUser().getTokenPortrait());

        // 通过OkHttp 请求Token
        Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(@io.reactivex.annotations.NonNull ObservableEmitter<String> emitter) throws Exception {
                // 执行请求过程
                String json = HttpManager.getInstance().postCloudToken(map);
//                emitter.onNext();
            }
        });
    }

    private void createUploadDialog() {
        DialogView uploadView = DialogManager.getInstance().initView(this, R.layout.dialog_first_upload);
        // 外部点击不能消失
        uploadView.setCancelable(false);
        ImageView ivGoUpload = uploadView.findViewById(R.id.iv_go_upload);
        ivGoUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogManager.getInstance().hide(uploadView);
                FirstUploadActivity.startActivity(MainActivity.this, UPLOAD_REQUEST_CODE);
            }
        });
        DialogManager.getInstance().show(uploadView);
    }

    private void initFragments() {
        if (mStarFragment == null) {
            mStarFragment = new StarFragment();
            mStarTransaction = getSupportFragmentManager().beginTransaction();
            mStarTransaction.add(R.id.fl_main, mStarFragment);
            mStarTransaction.commit();
        }
        if (mSquareFragment == null) {
            mSquareFragment = new SquareFragment();
            mSquareTransaction = getSupportFragmentManager().beginTransaction();
            mSquareTransaction.add(R.id.fl_main, mSquareFragment);
            mSquareTransaction.commit();
        }
        if (mChatFragment == null) {
            mChatFragment = new ChatFragment();
            mChatTransaction = getSupportFragmentManager().beginTransaction();
            mChatTransaction.add(R.id.fl_main, mChatFragment);
            mChatTransaction.commit();
        }
        if (mMeFragment == null) {
            mMeFragment = new MeFragment();
            mMeTransaction = getSupportFragmentManager().beginTransaction();
            mMeTransaction.add(R.id.fl_main, mMeFragment);
            mMeTransaction.commit();
        }
    }

    private void showFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            hideAllFragments(fragmentTransaction);
            fragmentTransaction.show(fragment);
            fragmentTransaction.commit();
        }
    }

    private void hideAllFragments(FragmentTransaction fragmentTransaction) {
        if (mStarFragment != null) {
            fragmentTransaction.hide(mStarFragment);
        }
        if (mSquareFragment != null) {
            fragmentTransaction.hide(mSquareFragment);
        }
        if (mChatFragment != null) {
            fragmentTransaction.hide(mChatFragment);
        }
        if (mMeFragment != null) {
            fragmentTransaction.hide(mMeFragment);
        }
    }

    /**
     * 切换主页选项卡
     * @param index
     * 0 - 星球
     * 1 - 广场
     * 2 - 聊天
     * 3 - 我的
     */
    private void checkMainTab(int index) {
        switch (index) {
            case 0:
                showFragment(mStarFragment);
                mIvStar.setImageResource(R.drawable.img_star_p);
                mIvSquare.setImageResource(R.drawable.img_square);
                mIvChat.setImageResource(R.drawable.img_chat);
                mIvMe.setImageResource(R.drawable.img_me);

                mTvStar.setTextColor(getResources().getColor(R.color.colorAccent));
                mTvSquare.setTextColor(Color.BLACK);
                mTvChat.setTextColor(Color.BLACK);
                mTvMe.setTextColor(Color.BLACK);
                break;
            case 1:
                showFragment(mSquareFragment);
                mIvStar.setImageResource(R.drawable.img_star);
                mIvSquare.setImageResource(R.drawable.img_square_p);
                mIvChat.setImageResource(R.drawable.img_chat);
                mIvMe.setImageResource(R.drawable.img_me);

                mTvStar.setTextColor(Color.BLACK);
                mTvSquare.setTextColor(getResources().getColor(R.color.colorAccent));
                mTvChat.setTextColor(Color.BLACK);
                mTvMe.setTextColor(Color.BLACK);
                break;
            case 2:
                showFragment(mChatFragment);
                mIvStar.setImageResource(R.drawable.img_star);
                mIvSquare.setImageResource(R.drawable.img_square);
                mIvChat.setImageResource(R.drawable.img_chat_p);
                mIvMe.setImageResource(R.drawable.img_me);

                mTvStar.setTextColor(Color.BLACK);
                mTvSquare.setTextColor(Color.BLACK);
                mTvChat.setTextColor(getResources().getColor(R.color.colorAccent));
                mTvMe.setTextColor(Color.BLACK);
                break;
            case 3:
                showFragment(mMeFragment);
                mIvStar.setImageResource(R.drawable.img_star);
                mIvSquare.setImageResource(R.drawable.img_square);
                mIvChat.setImageResource(R.drawable.img_chat);
                mIvMe.setImageResource(R.drawable.img_me_p);

                mTvStar.setTextColor(Color.BLACK);
                mTvSquare.setTextColor(Color.BLACK);
                mTvChat.setTextColor(Color.BLACK);
                mTvMe.setTextColor(getResources().getColor(R.color.colorAccent));
                break;
        }
    }

    /**
     * 防止重叠
     */
    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        if (mStarFragment !=null && fragment instanceof StarFragment) {
            mStarFragment = (StarFragment) fragment;
        }
        if (mSquareFragment !=null && fragment instanceof SquareFragment) {
            mSquareFragment = (SquareFragment) fragment;
        }
        if (mChatFragment !=null && fragment instanceof ChatFragment) {
            mChatFragment = (ChatFragment) fragment;
        }
        if (mMeFragment !=null && fragment instanceof MeFragment) {
            mMeFragment = (MeFragment) fragment;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_star:
                checkMainTab(0);
                break;
            case R.id.ll_square:
                checkMainTab(1);
                break;
            case R.id.ll_chat:
                checkMainTab(2);
                break;
            case R.id.ll_me:
                checkMainTab(3);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == UPLOAD_REQUEST_CODE) {
                // 上传成功
                checkToken();
                // 刷新我的页面信息
                ImageView ivPortrait = mMeFragment.getView().findViewById(R.id.iv_me_portrait);
                TextView tvNickname = mMeFragment.getView().findViewById(R.id.tv_nickname);
                IMUser user = BmobManager.getInstance().getUser();
                GlideHelper.loadUrl(this, user.getPortrait(), ivPortrait);
                tvNickname.setText(user.getNickname());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}