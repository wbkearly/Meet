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

import com.google.gson.Gson;
import com.wbk.framework.base.BaseUIActivity;
import com.wbk.framework.bmob.BmobManager;
import com.wbk.framework.bmob.IMUser;
import com.wbk.framework.entity.Constants;
import com.wbk.framework.gson.TokenBean;
import com.wbk.framework.helper.GlideHelper;
import com.wbk.framework.manager.DialogManager;
import com.wbk.framework.manager.HttpManager;
import com.wbk.framework.utils.SpUtils;
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
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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

    private Disposable mDisposable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        requestPermission();
    }

    /**
     * ????????????
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
        // ???????????????
        checkMainTab(0);

        // ??????TOKEN
        checkToken();
    }

    private void checkToken() {
        // ??????token ??????????????????
        // ???????????? ???????????? ??????
        String token = SpUtils.getInstance().getString(Constants.SP_TOKEN, "");
        if (!TextUtils.isEmpty(token)) {
            // ????????????????????????????????????
            startCloudService();
        } else {
            String tokenPortrait = BmobManager.getInstance().getUser().getTokenPortrait();
            String tokenNickname = BmobManager.getInstance().getUser().getTokenNickname();
            if (!TextUtils.isEmpty(tokenPortrait) && !TextUtils.isEmpty(tokenNickname)) {
                // ??????token
                createToken();
            } else {
                // ???????????????????????????
                createUploadDialog();
            }
        }
    }

    public void startCloudService() {
        startService(new Intent(this, CloudService.class));
    }

    private void createToken() {

        HashMap<String, String> map = new HashMap<>();
        map.put("userId", BmobManager.getInstance().getUser().getObjectId());
        map.put("name", BmobManager.getInstance().getUser().getNickname());
        map.put("portraitUrl", BmobManager.getInstance().getUser().getTokenPortrait());

        // ??????OkHttp ??????Token
        mDisposable = Observable.create((ObservableOnSubscribe<String>) emitter -> {
            // ??????????????????
            String json = HttpManager.getInstance().postCloudToken(map);
            emitter.onNext(json);
            emitter.onComplete();
        }).subscribeOn(Schedulers.newThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(this::parsingCloudToken);
    }

    private void parsingCloudToken(String s) {
        TokenBean tokenBean = new Gson().fromJson(s, TokenBean.class);
        if (tokenBean.getCode() == 200) {
            if (!TextUtils.isEmpty(tokenBean.getToken())) {
                // ??????Token
                SpUtils.getInstance().putString(Constants.SP_TOKEN, tokenBean.getToken());
                startCloudService();
            }
        }
    }

    private void createUploadDialog() {
        DialogView uploadView = DialogManager.getInstance().initView(this, R.layout.dialog_first_upload);
        // ????????????????????????
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
     * ?????????????????????
     * @param index
     * 0 - ??????
     * 1 - ??????
     * 2 - ??????
     * 3 - ??????
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
     * ????????????
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
                // ????????????
                checkToken();
                // ????????????????????????
                ImageView ivPortrait = mMeFragment.getView().findViewById(R.id.iv_me_portrait);
                TextView tvNickname = mMeFragment.getView().findViewById(R.id.tv_nickname);
                IMUser user = BmobManager.getInstance().getUser();
                GlideHelper.loadUrl(this, user.getPortrait(), ivPortrait);
                tvNickname.setText(user.getNickname());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity??????????????????RxJava??????
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }
}