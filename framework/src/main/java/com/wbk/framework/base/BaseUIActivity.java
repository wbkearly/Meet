package com.wbk.framework.base;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.wbk.framework.utils.SystemUI;

public class BaseUIActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemUI.fixSystemUI(this);
    }
}
