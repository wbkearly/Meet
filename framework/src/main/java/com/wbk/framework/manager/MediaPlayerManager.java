package com.wbk.framework.manager;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.wbk.framework.utils.LogUtil;

import java.io.IOException;

public class MediaPlayerManager {

    // 播放
    public static final int MEDIA_STATUS_PLAY = 0;
    // 暂停
    public static final int MEDIA_STATUS_PAUSE = 1;
    // 停止
    public static final int MEDIA_STATUS_STOP = 2;

    public int mMediaStatus = MEDIA_STATUS_STOP;

    private MediaPlayer mMediaPlayer;

    private static final int H_PROGRESS = 1000;

    private OnMusicProgressListener mMusicProgressListener;

    /**
     * 开始播放 则开始循环计算时长
     * 将进度计算结果对外抛出
     */
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case H_PROGRESS:
                    if (mMusicProgressListener != null) {
                        int currentPosition = getCurrentPosition();
                        int pos = (int) (((float) currentPosition) / ((float) getDuration()) * 100);
                        mMusicProgressListener.onProgress(currentPosition, pos);
                        mHandler.sendEmptyMessageDelayed(H_PROGRESS, 1000);
                    };
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    public MediaPlayerManager() {
        mMediaPlayer = new MediaPlayer();
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void startPlay(AssetFileDescriptor path) {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(path.getFileDescriptor(), path.getStartOffset(), path.getLength());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            mMediaStatus = MEDIA_STATUS_PLAY;
            mHandler.sendEmptyMessage(H_PROGRESS);
        } catch (IOException e) {
            LogUtil.e(e.toString());
            e.printStackTrace();
        }
    }

    public void pausePlay() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mMediaStatus = MEDIA_STATUS_PAUSE;
            mHandler.removeMessages(H_PROGRESS);
        }
    }

    public void continuePlay() {
        mMediaPlayer.start();
        mMediaStatus = MEDIA_STATUS_PLAY;
        mHandler.sendEmptyMessage(H_PROGRESS);

    }

    public void stopPlay() {
        mMediaPlayer.stop();
        mMediaStatus = MEDIA_STATUS_STOP;
        mHandler.removeMessages(H_PROGRESS);
    }

    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    public void seekTo(int ms) {
        mMediaPlayer.seekTo(ms);
    }

    public void setLooping(boolean isLooping) {
        mMediaPlayer.setLooping(isLooping);
    }

    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    public void onCompletionListener(MediaPlayer.OnCompletionListener listener) {
        mMediaPlayer.setOnCompletionListener(listener);
    }

    public void onErrorListener(MediaPlayer.OnErrorListener listener) {
        mMediaPlayer.setOnErrorListener(listener);
    }

    public void setOnProgressListener(OnMusicProgressListener listener) {
        mMusicProgressListener = listener;
    }

    public interface OnMusicProgressListener {
        void onProgress(int progress, int pos);
    }
}
