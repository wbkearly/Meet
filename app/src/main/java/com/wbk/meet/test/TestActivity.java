package com.wbk.meet.test;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.wbk.framework.base.BaseActivity;
import com.wbk.framework.bmob.MyData;
import com.wbk.framework.utils.LogUtil;
import com.wbk.meet.R;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 测试专用
 */
public class TestActivity extends BaseActivity implements View.OnClickListener {

    private Button mBtnAdd;
    private Button mBtnDel;
    private Button mBtnMod;
    private Button mBtnQuery;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initView();
    }

    private void initView() {
        mBtnAdd = findViewById(R.id.btn_add);
        mBtnDel = findViewById(R.id.btn_del);
        mBtnMod = findViewById(R.id.btn_mod);
        mBtnQuery = findViewById(R.id.btn_query);

        mBtnAdd.setOnClickListener(this);
        mBtnDel.setOnClickListener(this);
        mBtnMod.setOnClickListener(this);
        mBtnQuery.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                MyData myData = new MyData();
                myData.setName("张三");
                myData.setGender(1);
                myData.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            LogUtil.i("新增成功" + s);
                        }
                    }
                });
                break;
            case R.id.btn_del:
                MyData data = new MyData();
                data.setObjectId("22f1a72fda");
                data.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            LogUtil.i("删除成功");
                        } else {
                            LogUtil.i("删除失败");
                        }
                    }
                });
                break;
            case R.id.btn_mod:
                MyData myData1 = new MyData();
                myData1.setName("李四");
                myData1.update("69bd0ef00f", new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            LogUtil.i("修改成功！");
                        }
                    }
                });
                break;
            case R.id.btn_query:
                BmobQuery<MyData> bmobQuery = new BmobQuery<>();
//                bmobQuery.getObject("95f1b3aeaf", new QueryListener<MyData>() {
//                    @Override
//                    public void done(MyData myData, BmobException e) {
//                        if (e == null) {
//                            LogUtil.i("MyData:" + myData.getName());
//                        } else {
//                            LogUtil.e("error:" + e.toString());
//                        }
//                    }
//                });
                bmobQuery.addWhereEqualTo("name", "张三");
                bmobQuery.findObjects(new FindListener<MyData>() {
                    @Override
                    public void done(List<MyData> object, BmobException e) {
                        if (e == null) {
                            if (object != null && object.size() > 0) {
                                for (MyData data : object) {
                                    LogUtil.i("MyData:name:" + data.getName() + "-" +
                                            "gender:" + data.getGender());
                                }
                            }
                        }
                    }
                });
                break;
            default:
                break;
        }
    }
}
