package com.wbk.framework.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;

import com.wbk.framework.utils.LogUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileHelper {

    private static volatile FileHelper mInstance = null;
    private final SimpleDateFormat mSimpleDateFormat;
    public static final int CAMERA_REQUEST_CODE = 1004;
    public static final int ALBUM_REQUEST_CODE = 1005;
    private File mTempFile;
    private Uri mImageUri;

    private FileHelper() {
        mSimpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CHINA);
    }

    public static FileHelper getInstance() {
        if (mInstance == null) {
            synchronized (FileHelper.class) {
                if (mInstance == null) {
                    mInstance = new FileHelper();
                }
            }
        }
        return mInstance;
    }

    public void toCamera(Activity activity) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String filename = mSimpleDateFormat.format(new Date());
        mTempFile = new File(Environment.getExternalStorageDirectory(), filename + ".jpg");
        // 兼容android 7.0
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mImageUri = Uri.fromFile(mTempFile);
        } else {
            // 利用FileProvider
            mImageUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", mTempFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        LogUtils.i("imageUri" + mImageUri);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        activity.startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    public void toAlbum(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent, ALBUM_REQUEST_CODE);
    }

    public File getTempFile() {
        return mTempFile;
    }

    public String getRealPathFromUri(Context context, Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        CursorLoader cursorLoader = new CursorLoader(context, uri, projection, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        if (cursor == null){
            return null;
        }
        int index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        if (index == -1) {
            return null;
        }
        return cursor.getString(index);
    }
}
