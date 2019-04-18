package com.akay.fitnass.ui.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.akay.fitnass.data.storage.model.TimerParams;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Timer extends AppCompatTextView {
    private static final String KEY_SUPER_STATE = "com.akay.fitnass.ui.custom.SUPER_STATE";
    private static final String KEY_PROGRESS_STATE = "com.akay.fitnass.ui.custom.PROGRESS_STATE";
    private static final long TIMER_INTERVAL = 0L;

    private boolean mVisible;
    private boolean mStarted;
    private boolean mRunning;
    private boolean mPaused;
    private long mStart;
    private long mTimeWhenStopped;

    private Runnable mTickRunnable = this::run;

    public Timer(Context context) {
        super(context);
        init();
    }

    public Timer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Timer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        mVisible = visibility == VISIBLE;

        switch (visibility) {
            case VISIBLE: onVisible(); break;
            case GONE: pauseTimer(); break;
            case INVISIBLE: return;
        }

        updateRunning();
    }

    public boolean isPaused() {
        return mPaused;
    }

    public TimerParams getParams() {
        TimerParams params = new TimerParams();
        params.setStart(mStart);
        params.setTws(mTimeWhenStopped);
        params.setStarted(mStarted);
        params.setPaused(mPaused);
        return params;
    }

    public void setParams(TimerParams params) {
        this.mStart = params.getStart();
        this.mTimeWhenStopped = params.getTws();
        this.mStarted = params.isStarted();
        this.mPaused = params.isPaused();

        if (mPaused) {
            mStart = SystemClock.uptimeMillis() + mTimeWhenStopped;
            updateView(SystemClock.uptimeMillis());
        }
        updateRunning();
    }

    public void start() {
        mStart = SystemClock.uptimeMillis() + mTimeWhenStopped;
        mPaused = false;
        startTimer();
    }

    public void pause() {
        mTimeWhenStopped = mStart - SystemClock.uptimeMillis();
        mPaused = true;
        pauseTimer();
    }

    public void reset() {
        initTimer();
    }

    private void init() {
        setupFont();
        initTimer();
    }

    private void setupFont() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/roboto-thin.ttf");
            setTypeface(tf);
        }
    }

    private void initTimer() {
        clear();
        updateView(0L);
    }

    private void startTimer() {
        mStarted = true;
        updateRunning();
    }

    private void pauseTimer() {
        mStarted = false;
        updateRunning();
    }

    private void clear() {
        mStart = 0;
        mTimeWhenStopped = 0;
        mStarted = false;
        mPaused = true;
    }

    private void onVisible() {
        if (!mStarted && !mPaused) {
            startTimer();
        }
    }

    private void run() {
        if (mRunning) {
            updateView(SystemClock.uptimeMillis());
            postDelayed(mTickRunnable, TIMER_INTERVAL);
        }
    }

    private void updateRunning() {
        boolean running = mVisible && mStarted && !mPaused && isShown();
        if (running != mRunning) {
            if (running) {
                updateView(SystemClock.uptimeMillis());
                postDelayed(mTickRunnable, TIMER_INTERVAL);
            } else {
                removeCallbacks(mTickRunnable);
            }
            mRunning = running;
        }
    }

    private synchronized void updateView(long now) {
        long ms = now - mStart;

        int msView = (int) (ms % 1000L) / 10;
        int seconds = (int) (TimeUnit.MILLISECONDS.toSeconds(ms) % 60);
        int minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(ms) % 60);
        int hours = (int) (TimeUnit.MILLISECONDS.toHours(ms) % 24);
        int days = (int) TimeUnit.MILLISECONDS.toDays(ms);

        String strTime = setFormat(days, hours, minutes, seconds, msView);
        setText(strTime);
    }

    private void staticView(long ms) {
        int msView = (int) (ms % 1000L) / 10;
        int seconds = (int) (TimeUnit.MILLISECONDS.toSeconds(ms) % 60);
        int minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(ms) % 60);
        int hours = (int) (TimeUnit.MILLISECONDS.toHours(ms) % 24);
        int days = (int) TimeUnit.MILLISECONDS.toDays(ms);

        String strTime = setFormat(days, hours, minutes, seconds, msView);
        setText(strTime);
    }

    private String setFormat(int days, int hr, int min, int sec, int ms) {
        if (days > 0) {
            return String.format(Locale.getDefault(), "%02d.%02d.%02d.%02d.%02d", days, hr, min, sec, ms);
        } else if (hr > 0) {
            return String.format(Locale.getDefault(), "%02d.%02d.%02d.%02d", hr, min, sec, ms);
        } else {
            return String.format(Locale.getDefault(), "%02d.%02d.%02d", min, sec, ms);
        }
    }
}
