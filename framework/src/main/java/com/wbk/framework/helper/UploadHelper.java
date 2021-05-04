package com.wbk.framework.helper;

import android.text.format.DateFormat;
import android.util.Log;

import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.wbk.framework.base.BaseApp;
import com.wbk.framework.utils.HashUtils;

import java.io.File;
import java.util.Date;

/**
 * 上传工具类 用于上传到阿里OSS存储
 */
public class UploadHelper {

    private static final String TAG = UploadHelper.class.getSimpleName();

    private static final String ENDPOINT = "http://oss-cn-hongkong.aliyuncs.com";
    // 上传的仓库名
    private static final String BUCKET_NAME = "lecollege";

    private static volatile UploadHelper sUploadHelper;

    private UploadHelper() {
    }

    public static UploadHelper getInstance() {
        if (sUploadHelper == null) {
            synchronized (UploadHelper.class) {
                if (sUploadHelper == null) {
                    sUploadHelper = new UploadHelper();
                }
            }
        }
        return sUploadHelper;
    }

    private OSS getClient() {

        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(
                "LTAI4G8zK2BfKxRuRuXP3SFt", "P9gwC6j7AFavO2AuaZzNHYbEBiObRM");

        return new OSSClient(BaseApp.getInstance(), ENDPOINT, credentialProvider);
    }

    /**
     * 上传最终方法，成功返回一个路径
     * @param objKey 上传上去后，在服务器上独立的Key
     * @param path 需要上传的文件路径
     * @return 存储的地址
     */
    private String upload(String objKey, String path) {
        // 构造上传请求。
        PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, objKey, path);
        try {
            // 初始化上传的Client
            OSS client = getClient();
            // 开始同步上传
            PutObjectResult result = client.putObject(request);
            // 得到一个可访问的地址
            String url = client.presignPublicObjectURL(BUCKET_NAME, objKey);
            // 格式化存储
            Log.d(TAG, String.format("PublicObjectURL: %s", url));
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            // 如果有异常则返回空
            return null;
        }
    }

    /**
     * 上传普通图片
     * @param path 本地地址
     * @return 服务器地址
     */
    public String uploadImage(String path) {
        String key = getImageObjKey(path);
        return upload(key, path);
    }

    /**
     * 上传头像
     * @param path 本地地址
     * @return 服务器地址
     */
    public String uploadPortrait(String path) {
        String key = getPortraitObjKey(path);
        return upload(key, path);
    }

    /**
     * 上传音频
     * @param path 本地地址
     * @return 服务器地址
     */
    public String uploadAudio(String path) {
        String key = getAudioObjKey(path);
        return upload(key, path);
    }

    /**
     * 分月存储，避免一个文件夹太多
     * @return yyyyMM
     */
    private String getDateString() {
        return DateFormat.format("yyyyMM", new Date()).toString();
    }

    // image/
    private String getImageObjKey(String path) {
        String fileMD5 = HashUtils.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("image/%s/%s.jpg", dateString, fileMD5);
    }

    // portrait/
    private String getPortraitObjKey(String path) {
        String fileMD5 = HashUtils.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("portrait/%s/%s.jpg", dateString, fileMD5);
    }

    // audio/
    private String getAudioObjKey(String path) {
        String fileMD5 = HashUtils.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("audio/%s/%s.mp3", dateString, fileMD5);
    }

}
