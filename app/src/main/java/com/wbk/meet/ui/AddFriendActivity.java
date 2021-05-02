package com.wbk.meet.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.wbk.framework.base.BaseActivity;
import com.wbk.framework.bmob.BmobManager;
import com.wbk.framework.bmob.IMUser;
import com.wbk.framework.utils.CommonUtil;
import com.wbk.framework.utils.LogUtil;
import com.wbk.meet.R;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class AddFriendActivity extends BaseActivity implements View.OnClickListener {


    private LinearLayout mLlToContact;
    private EditText mEtPhone;
    private ImageView mIvSearch;
    private RecyclerView mRvSearchResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriend);
        initViews();
    }

    private void initViews() {
        mLlToContact = findViewById(R.id.ll_to_contact);
        mEtPhone = findViewById(R.id.et_phone);
        mIvSearch = findViewById(R.id.iv_search);
        mRvSearchResult = findViewById(R.id.rv_search_result);

        mLlToContact.setOnClickListener(this);
        mIvSearch.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_to_contact:

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
        BmobManager.getInstance().queryUserByPhone(phone, new FindListener<IMUser>() {
            @Override
            public void done(List<IMUser> object, BmobException e) {
                if (CommonUtil.isEmpty(object)) {
                    IMUser imUser = object.get(0);
                    LogUtil.i("IMUser:" + imUser);
                }
            }
        });
    }
}
