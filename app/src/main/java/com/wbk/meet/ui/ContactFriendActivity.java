package com.wbk.meet.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wbk.framework.base.BaseBackActivity;
import com.wbk.framework.bmob.BmobManager;
import com.wbk.framework.bmob.IMUser;
import com.wbk.framework.utils.CommonUtil;
import com.wbk.framework.utils.LogUtil;
import com.wbk.meet.R;
import com.wbk.meet.adapter.AddFriendAdapter;
import com.wbk.meet.mdoel.AddFriendModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ContactFriendActivity extends BaseBackActivity {

    private RecyclerView mRvContact;

    private Map<String, String> mContactMap = new HashMap<>();

    private AddFriendAdapter mAddFriendAdapter;

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
        mAddFriendAdapter = new AddFriendAdapter(this, mList);
        mRvContact.setAdapter(mAddFriendAdapter);
        mAddFriendAdapter.setOnClickListener(new AddFriendAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {

            }
        });
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
                            if (CommonUtil.isNotEmpty(object)) {
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
            LogUtil.i("name:" + name + ", phone:" + phone);
            phone = phone.replace(" ", "").replace("-", "");
            mContactMap.put(name, phone);
        }
    }

    private void addContent(IMUser imUser, String name, String phone) {
        AddFriendModel model = new AddFriendModel();
        model.setType(AddFriendAdapter.TYPE_CONTENT);
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
