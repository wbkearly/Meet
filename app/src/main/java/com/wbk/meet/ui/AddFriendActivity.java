package com.wbk.meet.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wbk.framework.adapter.CommonAdapter;
import com.wbk.framework.adapter.CommonViewHolder;
import com.wbk.framework.base.BaseBackActivity;
import com.wbk.framework.bmob.BmobManager;
import com.wbk.framework.bmob.IMUser;
import com.wbk.framework.utils.CommonUtils;
import com.wbk.meet.R;
import com.wbk.meet.model.AddFriendModel;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class AddFriendActivity extends BaseBackActivity implements View.OnClickListener {

    // 标题
    public static final int TYPE_TITLE = 0;
    // 内容
    public static final int TYPE_CONTENT = 1;

    private LinearLayout mLlToContact;
    private EditText mEtPhone;
    private ImageView mIvSearch;
    private RecyclerView mRvSearchResult;
    private View mIncludeEmptyView;

    private CommonAdapter<AddFriendModel> mAddFriendAdapter;
    private List<AddFriendModel> mList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriend);
        initViews();
    }

    private void initViews() {
        mLlToContact = findViewById(R.id.ll_to_contact);
        mEtPhone = findViewById(R.id.et_phone);
        mIvSearch = findViewById(R.id.iv_search);
        mRvSearchResult = findViewById(R.id.rv_search_result);
        mIncludeEmptyView = findViewById(R.id.include_empty_view);

        mLlToContact.setOnClickListener(this);
        mIvSearch.setOnClickListener(this);

        // 列表实现
        mRvSearchResult.setLayoutManager(new LinearLayoutManager(this));
        mRvSearchResult.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAddFriendAdapter = new CommonAdapter<>(mList, new CommonAdapter.OnBindMultiTypeDataListener<AddFriendModel>() {
            @Override
            public int getItemType(int position) {
                return mList.get(position).getType();
            }

            @Override
            public void onBindViewHolder(AddFriendModel model, CommonViewHolder viewHolder, int type, int position) {
                if (type == TYPE_TITLE) {
                    viewHolder.setText(R.id.tv_title, model.getTitle());
                } else if (model.getType() == TYPE_CONTENT) {
                    viewHolder.setImageUrl(AddFriendActivity.this, R.id.iv_portrait, model.getPortrait());
                    viewHolder.setImageResource(R.id.iv_gender, model.isGender() ? R.drawable.img_boy_icon : R.drawable.img_girl_icon);
                    viewHolder.setText(R.id.tv_nickname, model.getNickname());
                    viewHolder.setText(R.id.tv_age, model.getAge() + "岁");
                    viewHolder.setText(R.id.tv_desc, model.getDesc());

                    if (model.isContact()) {
                        viewHolder.setVisibility(R.id.tv_contact_name, View.GONE);
                        viewHolder.setText(R.id.tv_contact_name, model.getContactName());
                        viewHolder.setText(R.id.tv_contact_phone, model.getContactPhone());
                    }

                    // 点击事件
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UserInfoActivity.startActivity(AddFriendActivity.this,
                                    model.getUserId());
                        }
                    });
                }
            }

            @Override
            public int getLayoutId(int type) {
                if (type == TYPE_TITLE) {
                    return R.layout.layout_search_title_item;
                } else if(type == TYPE_CONTENT) {
                    return R.layout.layout_search_user_item;
                }
                return 0;
            }
        });
        mRvSearchResult.setAdapter(mAddFriendAdapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_to_contact:
                // 处理权限
                if (checkPermission(Manifest.permission.READ_CONTACTS)) {
                    startActivity(new Intent(this, ContactFriendActivity.class));
                } else {
                    requestPermission(new String[] {Manifest.permission.READ_CONTACTS});
                }
                break;
            case R.id.iv_search:
                queryUserByPhone();
                break;
        }
    }

    private void queryUserByPhone() {
        String phone = mEtPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, getString(R.string.text_login_phone_is_empty),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        // 过滤自己
        String phoneNumber = BmobManager.getInstance().getUser().getMobilePhoneNumber();
        if (phone.equals(phoneNumber)) {
            Toast.makeText(this, "不能添加自己",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        BmobManager.getInstance().queryUserByPhone(phone, new FindListener<IMUser>() {
            @Override
            public void done(List<IMUser> object, BmobException e) {
                if (e == null) {
                    if (CommonUtils.isNotEmpty(object)) {
                        IMUser imUser = object.get(0);
                        mIncludeEmptyView.setVisibility(View.GONE);
                        mRvSearchResult.setVisibility(View.VISIBLE);
                        // 每次清空
                        mList.clear();
                        addTitle("查询结果");
                        addContent(imUser);
                        mAddFriendAdapter.notifyDataSetChanged();

                        // 推荐
                        pushUser();
                    } else {
                        // 显示空数据
                        mIncludeEmptyView.setVisibility(View.VISIBLE);
                        mRvSearchResult.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    /**
     * 推荐好友
     */
    private void pushUser() {
        // 查询所有好友 取100
        BmobManager.getInstance().queryAllUsers(new FindListener<IMUser>() {
            @Override
            public void done(List<IMUser> object, BmobException e) {
                if (e == null) {
                    if (CommonUtils.isNotEmpty(object)) {
                        addTitle("推荐好友");
                        int num = Math.min(object.size(), 100);
                        for (int i = 0; i < num; i++) {
                            String phoneNumber = BmobManager.getInstance().getUser().getMobilePhoneNumber();
                            if (object.get(i).getMobilePhoneNumber().equals(phoneNumber)) {
                                continue;
                            }
                            addContent(object.get(i));
                        }
                        mAddFriendAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void addTitle(String title) {
        AddFriendModel model = new AddFriendModel();
        model.setType(TYPE_TITLE);
        model.setTitle(title);
        mList.add(model);
    }

    private void addContent(IMUser imUser) {
        AddFriendModel model = new AddFriendModel();
        model.setType(TYPE_CONTENT);
        model.setUserId(imUser.getObjectId());
        model.setPortrait(imUser.getPortrait());
        model.setGender(imUser.isGender());
        model.setAge(imUser.getAge());
        model.setNickname(imUser.getNickname());
        model.setDesc(imUser.getDescription());
        mList.add(model);
    }
}
