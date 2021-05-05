package com.wbk.meet.ui;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.wbk.framework.adapter.CommonAdapter;
import com.wbk.framework.adapter.CommonViewHolder;
import com.wbk.framework.base.BaseBackActivity;
import com.wbk.framework.bmob.BmobManager;
import com.wbk.framework.bmob.IMUser;
import com.wbk.framework.cloud.CloudManager;
import com.wbk.framework.db.LitePalHelper;
import com.wbk.framework.db.NewFriend;
import com.wbk.framework.event.EventManager;
import com.wbk.framework.utils.CommonUtils;
import com.wbk.meet.R;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class NewFriendActivity extends BaseBackActivity {

    private View mItemEmptyView;
    private RecyclerView mRvNewFriend;

    private List<NewFriend> mList = new ArrayList<>();
    private CommonAdapter<NewFriend> mNewFriendAdapter;

    private IMUser friend;

    private Disposable mDisposable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);
        initView();
    }


    public void initView() {
        mItemEmptyView = findViewById(R.id.item_empty_view);
        mRvNewFriend = findViewById(R.id.rv_new_friend);

        mRvNewFriend.setLayoutManager(new LinearLayoutManager(this));
        mRvNewFriend.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        mNewFriendAdapter = new CommonAdapter<>(mList, new CommonAdapter.OnBindDataListener<NewFriend>() {
            @Override
            public void onBindViewHolder(NewFriend model, CommonViewHolder viewHolder, int type, int position) {
                BmobManager.getInstance().queryUserById(model.getUserId(), new FindListener<IMUser>() {
                    @Override
                    public void done(List<IMUser> object, BmobException e) {
                        if (e == null) {

                            friend = object.get(0);
                            viewHolder.setImageUrl(NewFriendActivity.this, R.id.iv_portrait, friend.getPortrait());
                            viewHolder.setImageResource(R.id.iv_gender, friend.isGender() ?
                                    R.drawable.img_boy_icon : R.drawable.img_girl_icon);
                            viewHolder.setText(R.id.tv_nickname, friend.getNickname());
                            viewHolder.setText(R.id.tv_age, friend.getAge() + getString(R.string.text_search_age));
                            viewHolder.setText(R.id.tv_desc, friend.getDescription());
                            viewHolder.setText(R.id.tv_msg, model.getMsg());

                            if (model.getResponse() == 0) {
                                viewHolder.getView(R.id.ll_agree).setVisibility(View.GONE);
                                viewHolder.getView(R.id.tv_result).setVisibility(View.VISIBLE);
                                viewHolder.setText(R.id.tv_result, "已同意");
                            } else if (model.getResponse() == 1) {
                                viewHolder.getView(R.id.ll_agree).setVisibility(View.GONE);
                                viewHolder.getView(R.id.tv_result).setVisibility(View.VISIBLE);
                                viewHolder.setText(R.id.tv_result, "已拒绝");
                            }
                        }
                    }
                });
                viewHolder.getView(R.id.ll_yes).setOnClickListener(v -> {
                    // 同意则刷新当前Item
                    // 将好友添加到列表
                    // 通知对方已同意
                    // 对方添加我到好友列表
                    // 刷新好友列表
                    updateItem(position, 0);
                    BmobManager.getInstance().addFriend(friend, new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                // 保存成功 通知对方
                                CloudManager.getInstance().sendTextMessage("",
                                        CloudManager.TYPE_AGREE_FRIEND,
                                        friend.getObjectId());
                                // TODO 刷新好友列表
                                EventManager.post(EventManager.FLAG_UPDATE_FRIEND_LIST);
                            }
                        }
                    });
                });
                viewHolder.getView(R.id.ll_no).setOnClickListener(v -> {
                    updateItem(position, 1);
                });
            }

            @Override
            public int getLayoutId(int type) {
                return R.layout.layout_new_friend_item;
            }
        });
        mRvNewFriend.setAdapter(mNewFriendAdapter);
        queryNewFriend();
    }

    private void updateItem(int position, int status) {
        NewFriend newFriend = mList.get(position);
        LitePalHelper.getInstance().updateNewFriend(newFriend.getUserId(), status);
        // 更新本地数据源
        newFriend.setResponse(status);
        mList.set(position, newFriend);
        mNewFriendAdapter.notifyDataSetChanged();
    }

    /**
     * 查询新朋友
     */
    private void queryNewFriend() {
        // 在子线程获取好友申请列表
        // RxJava 线程调度
        // 在主线程更新
        mDisposable = Observable.create((ObservableOnSubscribe<List<NewFriend>>) emitter -> {
            emitter.onNext(LitePalHelper.getInstance().queryNewFriend());
            emitter.onComplete();
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(newFriends -> {
                    // 更新UI
                    if (CommonUtils.isNotEmpty(newFriends)) {
                        mList.addAll(newFriends);
                        mNewFriendAdapter.notifyDataSetChanged();
                    } else {
                        mItemEmptyView.setVisibility(View.VISIBLE);
                        mRvNewFriend.setVisibility(View.GONE);
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }
}