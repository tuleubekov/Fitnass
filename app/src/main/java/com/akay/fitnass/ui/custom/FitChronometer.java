package com.akay.fitnass.ui.custom;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.akay.fitnass.R;

import java.util.Locale;

public class FitChronometer extends AppCompatTextView {
    private Handler mHandler = new Handler();
    private long millisTime, startTime, buffer, updateTime;
    private int minutes, seconds, millis;
    private Runnable mRunnable = this::run;

    public FitChronometer(Context context) {
        super(context);
    }

    public FitChronometer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FitChronometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void run() {
        millisTime = SystemClock.uptimeMillis() - startTime;
        updateTime = buffer + millisTime;
        seconds = (int) updateTime / 100;
        minutes = seconds / 60;
        seconds = seconds % 60;
        millis = (int) (updateTime % 1000) / 10;
        setText(setFormat(minutes, seconds, millis));
        mHandler.postDelayed(mRunnable, 0);
    }

    public void start() {
        startTime = SystemClock.uptimeMillis();
        mHandler.postDelayed(mRunnable, 0);
    }

    public void pause() {
        buffer += millisTime;
        mHandler.removeCallbacks(mRunnable);
    }

    public void reset() {
        clear();
        setText(R.string.text_chronometer_initial);
    }

    private void clear() {
        millisTime = 0L;
        startTime = 0L;
        updateTime = 0L;
        buffer = 0L;
        minutes = 0;
        seconds = 0;
        millis = 0;
    }

    private String setFormat(int min, int sec, int ms) {
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", min, sec, ms);
    }
}
