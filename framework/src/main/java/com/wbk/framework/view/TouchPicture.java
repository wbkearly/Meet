package com.wbk.framework.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.wbk.framework.R;

public class TouchPicture extends View {

    // 背景
    private Bitmap mBgBitmap;

    // 背景画笔
    private Paint mBgPaint;

    // 空白块
    private Bitmap mBlankBitmap;

    // 空白块画笔
    private Paint mBlankPaint;

    // 移动方块
    private Bitmap mMoveBitmap;

    // 移动画笔
    private Paint mMovePaint;

    // View的宽高
    private int mWidth;
    private int mHeight;

    private int CARD_SIZE = 200;
    private int LINE_W = 0;
    private int LINE_H = 0;

    private int MOVE_X = 200;

    private int errorValue = 10;

    private boolean canMove = false;

    private OnViewResultListener viewResultListener;

    public TouchPicture(Context context) {
        super(context);
        init();
    }

    public TouchPicture(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TouchPicture(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public TouchPicture(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mBgPaint = new Paint();
        mBlankPaint = new Paint();
        mMovePaint = new Paint();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBg(canvas);
        drawBlankCard(canvas);
        drawMoveCard(canvas);
    }

    private void drawMoveCard(Canvas canvas) {
        // 截取空白块坐标的bitmap图像
        mMoveBitmap = Bitmap.createBitmap(mBgBitmap, LINE_W, LINE_H, CARD_SIZE, CARD_SIZE);
        canvas.drawBitmap(mMoveBitmap, MOVE_X, LINE_H, mMovePaint);
    }

    private void drawBlankCard(Canvas canvas) {
        mBlankBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_blank_card);
        // 计算值
        CARD_SIZE = mBlankBitmap.getWidth();
        LINE_W = mWidth / 3 * 2;
        LINE_H = (mHeight - CARD_SIZE) / 2;
        canvas.drawBitmap(mBlankBitmap, LINE_W, LINE_H, mBlankPaint);
    }

    private void drawBg(Canvas canvas) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_bg);
        mBgBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas bgCanvas = new Canvas(mBgBitmap);
        bgCanvas.drawBitmap(bitmap, null, new Rect(0, 0, mWidth, mHeight), mBgPaint);
        canvas.drawBitmap(mBgBitmap, null, new Rect(0, 0, mWidth, mHeight), mBgPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if ((event.getX() > MOVE_X) && (event.getX() < MOVE_X + CARD_SIZE) &&
                        (event.getY() > LINE_H) && (event.getY() < LINE_H + CARD_SIZE)) {
                    canMove = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (canMove) {
                    if (event.getX() > 0 && event.getX() < mWidth - CARD_SIZE) {
                        MOVE_X = (int) event.getX();
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if ((MOVE_X > LINE_W - errorValue) && (MOVE_X < LINE_W + errorValue)) {
                    if (viewResultListener != null) {
                        viewResultListener.onResult();
                    }
                }
                MOVE_X = 200;
                invalidate();
                canMove = false;
                break;
            default:
                break;
        }
        return true;
    }

    public void setViewResultListener(OnViewResultListener viewResultListener) {
        this.viewResultListener = viewResultListener;
    }

    public interface OnViewResultListener {
        void onResult();
    }
}
