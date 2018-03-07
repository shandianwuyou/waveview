package com.my.waveview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by zhaopeng on 2018/2/28.
 */

public class WaveView extends View {

    int mWaveLength;
    int mDuration;
    int mColor;
    int mImage;
    int originY;
    boolean isRise;
    int mWaveHeight;

    int mWidth = 0;
    int mHeight = 0;
    int dx = 0;

    Bitmap mBitmap;

    Path mPath;
    Paint mPaint;

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WaveView);

        mWaveLength = (int) a.getDimension(R.styleable.WaveView_wave_length, 400);
        mDuration = (int) a.getDimension(R.styleable.WaveView_wave_time, 2000);
        mColor = a.getColor(R.styleable.WaveView_wave_color, Color.parseColor("#00ff00"));
        mImage = a.getResourceId(R.styleable.WaveView_float_image, 0);
        originY = (int) a.getDimension(R.styleable.WaveView_originY, 400);
        isRise = a.getBoolean(R.styleable.WaveView_rise, false);
        mWaveHeight = (int) a.getDimension(R.styleable.WaveView_wave_height, 200);
        a.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(mColor);

        mPath = new Path();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        if(mImage > 0){
            mBitmap = BitmapFactory.decodeResource(getResources(), mImage, options);
            mBitmap = getCircleBitmap(mBitmap);
        }else{
            mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
        }
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        if(bitmap == null){
            return null;
        }

        try{
            Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(circleBitmap);
            int roundX = Math.min(bitmap.getWidth(), bitmap.getHeight());
            RectF rectF = new RectF(0, 0, roundX, roundX);
            Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);

//        canvas.drawARGB(0, 0, 0, 0);
            canvas.drawRoundRect(rectF, roundX / 2f, roundX / 2f, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rectF, paint);
            return circleBitmap;
        }catch (Exception e){
            return bitmap;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        setPath();
        canvas.drawPath(mPath, mPaint);
    }

    private void setPath() {
        mPath.reset();
        mPath.moveTo(-mWaveLength + dx, originY);
        int halfWaveLength = mWaveLength / 2;

        for(int i = -mWaveLength; i < mWidth; i += mWaveLength){
            mPath.rQuadTo(halfWaveLength / 2, -mWaveHeight, halfWaveLength, 0);
            mPath.rQuadTo(halfWaveLength / 2, mWaveHeight, halfWaveLength, 0);
        }

        mPath.lineTo(mWidth, mHeight);
        mPath.lineTo(0, mHeight);
        mPath.close();
    }

    public void startAnim(){
        ValueAnimator va = ValueAnimator.ofFloat(0f, 1f);
        va.setDuration(mDuration);
        va.setInterpolator(new LinearInterpolator());
        va.setRepeatCount(ValueAnimator.INFINITE);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                dx = (int) ((float)animation.getAnimatedValue() * mWaveLength);
                postInvalidate();
            }
        });
        va.start();
    }


}
