package com.wbk.meet.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wbk.framework.adapter.CommonAdapter;
import com.wbk.framework.adapter.CommonViewHolder;
import com.wbk.framework.base.BaseUIActivity;
import com.wbk.framework.bmob.BmobManager;
import com.wbk.framework.bmob.Friend;
import com.wbk.framework.bmob.IMUser;
import com.wbk.framework.cloud.CloudManager;
import com.wbk.framework.entity.Constants;
import com.wbk.framework.helper.GlideHelper;
import com.wbk.framework.manager.DialogManager;
import com.wbk.framework.utils.CommonUtils;
import com.wbk.framework.view.DialogView;
import com.wbk.meet.R;
import com.wbk.meet.model.UserInfoModel;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends BaseUIActivity implements View.OnClickListener {

    public static void startActivity(Context context, String userId) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        intent.putExtra(Constants.INTENT_USER_ID, userId);
        context.startActivity(intent);
    }

    private DialogView mAddFriendDialogView;
    private EditText mEtMsg;
    private TextView mTvCancel;
    private TextView mTvAddFriend;

    private String userId;
    private RelativeLayout mRlBack;
    private CircleImageView mIvUserPortrait;
    private TextView mTvNickname;
    private TextView mTvDesc;
    private RecyclerView mRvUserInfo;
    private Button mBtnAddFriend;
    private LinearLayout mLlIsFriend;
    private Button mBtnChat;
    private Button mBtnAudioChat;
    private Button mBtnVideoChat;

    private CommonAdapter<UserInfoModel> mUserInfoModelAdapter;
    private List<UserInfoModel> mUserInfoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        initViews();
        initView();
    }

    private void initView() {
        initAddFriendDialog();
        userId = getIntent().getStringExtra(Constants.INTENT_USER_ID);

        mRlBack = findViewById(R.id.rl_back);
        mIvUserPortrait = findViewById(R.id.iv_user_portrait);
        mTvNickname = findViewById(R.id.tv_nickname);
        mTvDesc = findViewById(R.id.tv_desc);
        mRvUserInfo = findViewById(R.id.rv_user_info);
        mBtnAddFriend = findViewById(R.id.btn_add_friend);
        mLlIsFriend = findViewById(R.id.ll_is_friend);
        mBtnChat = findViewById(R.id.btn_chat);
        mBtnAudioChat = findViewById(R.id.btn_audio_chat);
        mBtnVideoChat = findViewById(R.id.btn_video_chat);

        mRlBack.setOnClickListener(this);
        mBtnAddFriend.setOnClickListener(this);
        mBtnChat.setOnClickListener(this);
        mBtnAudioChat.setOnClickListener(this);
        mBtnVideoChat.setOnClickListener(this);

        mRvUserInfo.setLayoutManager(new GridLayoutManager(this, 3));
        mUserInfoModelAdapter = new CommonAdapter<>(mUserInfoList, new CommonAdapter.OnBindDataListener<UserInfoModel>() {
            @Override
            public void onBindViewHolder(UserInfoModel model, CommonViewHolder viewHolder, int type, int position) {
                viewHolder.setText(R.id.tv_title, model.getTitle());
                viewHolder.setText(R.id.tv_content, model.getContent());
                // viewHolder.setBgColor(R.id.ll_bg, model.getBgColor());
                viewHolder.getView(R.id.ll_bg).setBackgroundColor(model.getBgColor());
            }

            @Override
            public int getLayoutId(int type) {
                return R.layout.layout_user_info_item;
            }
        });
        mRvUserInfo.setAdapter(mUserInfoModelAdapter);

        queryUserInfo();

    }

    private DialogView initAddFriendDialog() {

        mAddFriendDialogView = DialogManager.getInstance().initView(this, R.layout.dialog_send_friend);
        mEtMsg = mAddFriendDialogView.findViewById(R.id.et_msg);
        mTvCancel = mAddFriendDialogView.findViewById(R.id.tv_cancel);;
        mTvAddFriend = mAddFriendDialogView.findViewById(R.id.tv_add_friend);;

        mTvCancel.setOnClickListener(this);
        mTvAddFriend.setOnClickListener(this);
        return mAddFriendDialogView;
    }

    private void queryUserInfo() {
        if (TextUtils.isEmpty(userId)) {
            return;
        }
        BmobManager.getInstance().queryUserById(userId, new FindListener<IMUser>() {
            @Override
            public void done(List<IMUser> object, BmobException e) {
                if (e == null) {
                    if (CommonUtils.isNotEmpty(object)) {
                        IMUser imUser = object.get(0);
                        updateUserInfo(imUser);
                    }
                }
            }
        });
        // 判断好友关系
        BmobManager.getInstance().queryAllMyFriends(new FindListener<Friend>() {
            @Override
            public void done(List<Friend> object, BmobException e) {
                if (e == null) {
                    if (CommonUtils.isNotEmpty(object)) {
                        for (Friend friend : object) {
                            // 判断这个对象中的id是否跟我目前userId是否相同
                            if (friend.getFriend().getObjectId().equals(userId)) {
                                // 是好友关系
                                mBtnAddFriend.setVisibility(View.GONE);
                                mLlIsFriend.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }
        });
    }

    private void updateUserInfo(IMUser imUser) {
        GlideHelper.loadUrl(UserInfoActivity.this, imUser.getPortrait(),
                mIvUserPortrait);
        mTvNickname.setText(imUser.getNickname());
        mTvDesc.setText(imUser.getDescription());

        // 性别 年龄 生日 星座 爱好 单身状态
        addUserInfoModel(0x88ad68ff, getString(R.string.text_me_info_gender),
                imUser.isGender() ? getString(R.string.text_me_info_boy) : getString(R.string.text_me_info_girl));
        addUserInfoModel(0x88ff69b4, getString(R.string.text_me_info_age),
                imUser.getAge() + getString(R.string.text_search_age));
        addUserInfoModel(0x88cdaa7d, getString(R.string.text_me_info_birthday),
                imUser.getBirthday());
        addUserInfoModel(0x889aff9a, getString(R.string.text_me_info_constellation),
                imUser.getConstellation());
        addUserInfoModel(0x887ec0ee, getString(R.string.text_me_info_hobby),
                imUser.getHobby());
        addUserInfoModel(0x8800fa9a, getString(R.string.text_me_info_status),
                imUser.getStatus());
        mUserInfoModelAdapter.notifyDataSetChanged();
    }

    private void addUserInfoModel(int color, String title, String content) {
        UserInfoModel userInfoModel = new UserInfoModel();
        userInfoModel.setBgColor(color);
        userInfoModel.setTitle(title);
        userInfoModel.setContent(content);
        mUserInfoList.add(userInfoModel);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add_friend:
                String msg = mEtMsg.getText().toString().trim();
                if (TextUtils.isEmpty(msg)) {
                    msg = "你好，我是" + BmobManager.getInstance().getUser().getNickname();
                }
                CloudManager.getInstance().sendTextMessage(msg, CloudManager.TYPE_ADD_FRIEND, userId);
                DialogManager.getInstance().hide(mAddFriendDialogView);
                break;
            case R.id.tv_cancel:
                DialogManager.getInstance().hide(mAddFriendDialogView);
                break;
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_add_friend:
                DialogManager.getInstance().show(mAddFriendDialogView);
                break;
            case R.id.btn_chat:
                break;
            case R.id.btn_audio_chat:
                break;
            case R.id.btn_video_chat:
                break;
            default:
                break;
        }
    }

    private void initViews() {
        mEtMsg = findViewById(R.id.et_msg);
        mTvCancel = findViewById(R.id.tv_cancel);
        mTvAddFriend = findViewById(R.id.tv_add_friend);
    }
}
