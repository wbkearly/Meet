package com.wbk.meet.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;

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
import com.wbk.framework.utils.LogUtils;
import com.wbk.meet.R;
import com.wbk.meet.model.AddFriendModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ContactFriendActivity extends BaseBackActivity {

    private RecyclerView mRvContact;

    private Map<String, String> mContactMap = new HashMap<>();

    private CommonAdapter<AddFriendModel> mAddFriendAdapter;

    private List<AddFriendModel> mList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_friend);
        initView();
    }

    private void initView() {
        mRvContact = findViewById(R.id.rv_contact);
        mRvContact.setLayoutManager(new LinearLayoutManager(this));
        mRvContact.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAddFriendAdapter = new CommonAdapter<AddFriendModel>(mList, new CommonAdapter.OnBindDataListener<AddFriendModel>() {
            @Override
            public void onBindViewHolder(AddFriendModel model, CommonViewHolder viewHolder, int type, int position) {
                viewHolder.setImageUrl(ContactFriendActivity.this, R.id.iv_portrait, model.getPortrait());
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
                        UserInfoActivity.startActivity(ContactFriendActivity.this,
                                model.getUserId());
                    }
                });
            }

            @Override
            public int getLayoutId(int type) {
                return R.layout.layout_search_user_item;
            }

        });

        mRvContact.setAdapter(mAddFriendAdapter);
        loadContacts();

        loadUser();
    }

    private void loadUser() {
        if (mContactMap.size() > 0) {
            for (Map.Entry<String, String> entry : mContactMap.entrySet()) {
                BmobManager.getInstance().queryUserByPhone(entry.getValue(), new FindListener<IMUser>() {
                    @Override
                    public void done(List<IMUser> object, BmobException e) {
                        if (e == null) {
                            if (CommonUtils.isNotEmpty(object)) {
                                IMUser imUser = object.get(0);
                                addContent(imUser, entry.getKey(), entry.getValue());
                            }
                        }
                    }
                });
            }
        }
    }

    /**
     * 读取联系人
     */
    private void loadContacts() {
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);
        String name;
        String phone;
        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            LogUtils.i("name:" + name + ", phone:" + phone);
            phone = phone.replace(" ", "").replace("-", "");
            mContactMap.put(name, phone);
        }
    }

    private void addContent(IMUser imUser, String name, String phone) {
        AddFriendModel model = new AddFriendModel();
        model.setType(AddFriendActivity.TYPE_CONTENT);
        model.setUserId(imUser.getObjectId());
        model.setPortrait(imUser.getPortrait());
        model.setGender(imUser.isGender());
        model.setAge(imUser.getAge());
        model.setNickname(imUser.getNickname());
        model.setDesc(imUser.getDescription());

        model.setContact(true);
        model.setContactName(name);
        model.setContactPhone(phone);
        mList.add(model);
        mAddFriendAdapter.notifyDataSetChanged();
    }
}
