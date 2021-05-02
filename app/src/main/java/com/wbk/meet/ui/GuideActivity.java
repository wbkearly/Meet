package com.wbk.meet.ui;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.wbk.framework.base.BasePageAdapter;
import com.wbk.framework.base.BaseUIActivity;
import com.wbk.framework.manager.MediaPlayerManager;
import com.wbk.framework.utils.AnimUtil;
import com.wbk.meet.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 引导页
 */
public class GuideActivity extends BaseUIActivity implements View.OnClickListener {

    private ImageView mIvMusicSwitch;
    private TextView mTvGuideSkip;
    private ImageView mIvPoint1;
    private ImageView mIvPoint2;
    private ImageView mIvPoint3;
    private ViewPager mViewPager;

    private View view1;
    private View view2;
    private View view3;
    private List<View> mPageList = new ArrayList<>();
    private BasePageAdapter mPageAdapter;

    private MediaPlayerManager mGuideMusicManager;

    private ObjectAnimator mAnimator;

    /**
     * ViewPager
     * 小圆点
     * 歌曲
     * 动画旋转
     * 跳转
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initView();
    }

    private void initView() {
        mIvMusicSwitch = findViewById(R.id.iv_music_switch);
        mTvGuideSkip = findViewById(R.id.tv_guide_skip);
        mIvPoint1 = findViewById(R.id.iv_point_1);
        mIvPoint2 = findViewById(R.id.iv_point_2);
        mIvPoint3 = findViewById(R.id.iv_point_3);
        mViewPager = findViewById(R.id.viewpager);

        mIvMusicSwitch.setOnClickListener(this);
        mTvGuideSkip.setOnClickListener(this);

        view1 = View.inflate(this, R.layout.layout_pager_guide_1, null);
        view2 = View.inflate(this, R.layout.layout_pager_guide_2, null);
        view3 = View.inflate(this, R.layout.layout_pager_guide_3, null);
        mPageList.add(view1);
        mPageList.add(view2);
        mPageList.add(view3);

        // 预加载
        mViewPager.setOffscreenPageLimit(mPageList.size());

        mPageAdapter = new BasePageAdapter(mPageList);
        mViewPager.setAdapter(mPageAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectPoint(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // 歌曲播放逻辑
        startMusic();
    }

    private void startMusic() {
        mGuideMusicManager = new MediaPlayerManager();
        mGuideMusicManager.setLooping(true);
        AssetFileDescriptor fileDescriptor = getResources().openRawResourceFd(R.raw.guide);
        mGuideMusicManager.startPlay(fileDescriptor);

        // 旋转动画
        mAnimator = AnimUtil.rotation(mIvMusicSwitch);
        mAnimator.start();
    }

    private void selectPoint(int position) {
        switch (position) {
            case 0:
                mIvPoint1.setImageResource(R.drawable.ic_point_p);
                mIvPoint2.setImageResource(R.drawable.ic_point);
                mIvPoint3.setImageResource(R.drawable.ic_point);
                break;
            case 1:
                mIvPoint1.setImageResource(R.drawable.ic_point);
                mIvPoint2.setImageResource(R.drawable.ic_point_p);
                mIvPoint3.setImageResource(R.drawable.ic_point);
                break;
            case 2:
                mIvPoint1.setImageResource(R.drawable.ic_point);
                mIvPoint2.setImageResource(R.drawable.ic_point);
                mIvPoint3.setImageResource(R.drawable.ic_point_p);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_music_switch:
                if (mGuideMusicManager.mMediaStatus == MediaPlayerManager.MEDIA_STATUS_PAUSE) {
                    mAnimator.start();
                    mGuideMusicManager.continuePlay();
                } else if (mGuideMusicManager.mMediaStatus == MediaPlayerManager.MEDIA_STATUS_PLAY) {
                    mAnimator.pause();
                    mGuideMusicManager.pausePlay();
                }
                break;
            case R.id.tv_guide_skip:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGuideMusicManager.stopPlay();
    }
}
