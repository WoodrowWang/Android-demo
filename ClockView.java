package com.example.wl.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.Calendar;

/**
 * Created by wl on 4/21/18.
 */

public class ClockView extends View {

    private static final int PAINT_CIRCLE_COLOR = Color.BLACK;
    private static final int PAINT_CIRCLE_WIDTH = 5;

    private static final int HOUR_AND_MINUTE_PTR_COLOR = Color.BLACK;
    private static final int SECOND_PTR_COLOR = Color.RED;
    private static final float HOUR_PTR_WIDTH = 8;
    private static final float MINUTE_AND_SECOND_PTR_WIDTH = 6;

    private Paint mPaintPtr;
    private Paint mPaintCircle;
    private Ptr mHourPtr, mMinutePtr, mSecondPtr;
    private float mHeight, mWidth;
    private float radius;
    private Calendar mCalendar;
    private ClockHandler mClockHandler;

    public ClockView(Context context) {
        this(context, null);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mHourPtr = new Ptr(HOUR_AND_MINUTE_PTR_COLOR, HOUR_PTR_WIDTH);
        mMinutePtr = new Ptr(HOUR_AND_MINUTE_PTR_COLOR, MINUTE_AND_SECOND_PTR_WIDTH);
        mSecondPtr = new Ptr(SECOND_PTR_COLOR, MINUTE_AND_SECOND_PTR_WIDTH);

        mPaintCircle = new Paint();
        mPaintCircle.setColor(PAINT_CIRCLE_COLOR);
        mPaintCircle.setStrokeWidth(PAINT_CIRCLE_WIDTH);
        mPaintCircle.setStyle(Paint.Style.STROKE);
        mPaintCircle.setAntiAlias(true);

        mPaintPtr = new Paint();
        mPaintPtr.setAntiAlias(true);
        mPaintPtr.setStyle(Paint.Style.FILL);
        mPaintPtr.setStrokeCap(Paint.Cap.ROUND);

        mClockHandler = new ClockHandler(new WeakReference<>(this));

        mCalendar = Calendar.getInstance();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        radius = (mWidth < mHeight ? mWidth : mHeight) * 0.5f - PAINT_CIRCLE_WIDTH;
        mHourPtr.setLength(radius * 0.6f);
        mMinutePtr.setLength(radius * 0.75f);
        mSecondPtr.setLength(radius * 0.9f);

        mClockHandler.sendEmptyMessageDelayed(1, 1000 - (mCalendar.get(Calendar.MILLISECOND)));
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Log.i("------------>", "onDraw: ");
        canvas.drawCircle(mWidth * 0.5f, mHeight * 0.5f, radius, mPaintCircle);
        canvas.save();
        for (int i = 0; i < 12; i++) {
            canvas.rotate(30, mWidth * 0.5f, mHeight * 0.5f);
            canvas.drawLine(mWidth * 0.5f, mHeight * 0.5f - radius, mWidth * 0.5f, mHeight * 0.5f - radius + mHeight * 0.025f, mPaintCircle);
            if (i % 3 == 2) {
                canvas.drawLine(mWidth * 0.5f, mHeight * 0.5f - radius, mWidth * 0.5f, mHeight * 0.5f - radius + mHeight * 0.05f, mPaintCircle);
            }
        }
        canvas.restore();

        mCalendar.setTimeInMillis(System.currentTimeMillis());
        mHourPtr.setValue(mCalendar.get(Calendar.HOUR));
        mMinutePtr.setValue(mCalendar.get(Calendar.MINUTE));
        mSecondPtr.setValue(mCalendar.get(Calendar.SECOND));
        Log.i("------------>", "onDraw: " + mCalendar.get(Calendar.MILLISECOND));

        drawPtr(canvas, mHourPtr, mHourPtr.getValue() * 30 + mMinutePtr.getValue() * 0.5f + mSecondPtr.getValue() / 60 * 0.5f);
        canvas.drawCircle(mWidth * 0.5f, mHeight * 0.5f, radius * 0.1f, mPaintPtr);
        drawPtr(canvas, mMinutePtr, mMinutePtr.getValue() * 6 + mSecondPtr.getValue() * 0.1f);
        drawPtr(canvas, mSecondPtr, mSecondPtr.getValue() * 6);
        canvas.drawCircle(mWidth * 0.5f, mHeight * 0.5f, radius * 0.0618f, mPaintPtr);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mClockHandler.removeCallbacksAndMessages(null);
    }

    private void drawPtr(Canvas canvas, Ptr ptr, float degrees) {
        mPaintPtr.setColor(ptr.getColor());
        mPaintPtr.setStrokeWidth(ptr.getWidth());
        canvas.save();
        canvas.rotate(degrees, mWidth * 0.5f, mHeight * 0.5f);
        canvas.drawLine(mWidth * 0.5f, mHeight * 0.5f, mWidth * 0.5f, mHeight * 0.5f - ptr.getLength(), mPaintPtr);
        canvas.restore();
    }

    private static class ClockHandler extends Handler {
        WeakReference<ClockView> mReference;

        private ClockHandler(WeakReference<ClockView> reference) {
            mReference = reference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mReference.get() == null) {
                return;
            }
            if (msg.what == 1) {
                mReference.get().postInvalidate();
                sendEmptyMessageDelayed(1, 1000);
            }
        }
    }

    private static class Ptr {
        private int color;
        private int value;
        private float length;
        private float width;

        private Ptr(int color, float width) {
            this.color = color;
            this.width = width;
            this.value = 0;
            this.length = 0;
        }

        private int getColor() {
            return color;
        }

        private int getValue() {
            return value;
        }

        private void setValue(int value) {
            this.value = value;
        }

        private float getLength() {
            return length;
        }

        private void setLength(float length) {
            this.length = length;
        }

        private float getWidth() {
            return width;
        }
    }
}
