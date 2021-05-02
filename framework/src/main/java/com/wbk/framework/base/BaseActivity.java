package com.wbk.framework.base;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {

    private String[] mPermissions = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    // 保存未同意权限
    private List<String> mPermissionList = new ArrayList<>();
    // 保存未同意的失败权限
    private List<String> mPermissionNoList = new ArrayList<>();

    private OnPermissionsResult mPermissionsResult;

    private int mRequestCode;

    protected void request(int requestCode, OnPermissionsResult permissionsResult) {
        if (!checkAllPermissions()) {
            requestAllPermissions(requestCode, permissionsResult);
        }
    }

    protected boolean checkPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int check = checkSelfPermission(permission);
            return check == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    protected boolean checkAllPermissions() {
        mPermissionList.clear();
        for (String permission : mPermissions) {
            boolean check = checkPermission(permission);
            if (!check) {
                mPermissionList.add(permission);
            }
        }
        return mPermissionList.size() == 0;
    }

    /**
     * 请求权限
     */
    protected void requestPermission(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    protected void requestAllPermissions(int requestCode, OnPermissionsResult permissionsResult) {
        this.mRequestCode = requestCode;
        this.mPermissionsResult = permissionsResult;
        requestPermission(mPermissionList.toArray(new String[0]), requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == this.mRequestCode) {
            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        mPermissionNoList.add(permissions[i]);
                    }
                }
                if (mPermissionsResult != null) {
                    if (mPermissionNoList.size() == 0) {
                        mPermissionsResult.onSuccess();
                    } else {
                        mPermissionsResult.onFail(mPermissionNoList);
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected interface OnPermissionsResult {
        void onSuccess();

        void onFail(List<String> noPermissions);
    }

    protected boolean checkWindowPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(this);
        }
        return true;
    }

    /**
     * 请求窗口权限
     */
    protected void requestWindowPermissions(int requestCode) {
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, requestCode);
        }
    }
}
