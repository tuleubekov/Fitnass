package com.akay.fitnass.view.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Parcelable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.akay.fitnass.util.DateTimeUtils;
import com.akay.fitnass.util.Logger;

public class Timer extends AppCompatTextView {
    private static final String KEY_SUPER_STATE = "com.akay.fitnass.ui.custom.SUPER_STATE";
    private static final String KEY_PROGRESS_STATE = "com.akay.fitnass.ui.custom.PROGRESS_STATE";
    private static final long TIMER_INTERVAL = 10L;

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

    public void setUp(final boolean isPaused, final long start, final long tws) {
        this.mPaused = isPaused;
        this.mStart = start < 0 ? 0 : start;
        this.mTimeWhenStopped = tws < 0 ? 0 : tws;

        Logger.e("Timer setUp(): paused: " + isPaused + ", started: " + mStarted + ", mStart: " + mStart + ", tws: " + mTimeWhenStopped);

        if (mPaused) {
//            mStart = nowMillis() + mTimeWhenStopped;
//            updateView(nowMillis());
        } else {
            mStarted = true;
        }
        updateRunning();
    }

    public void start(long startMs) {
        mStart = startMs + mTimeWhenStopped;
        mPaused = false;
        startTimer();
    }

    public void pause(long twsMs) {
        mTimeWhenStopped = mStart - twsMs;
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
            updateView(nowMillis());
            postDelayed(mTickRunnable, TIMER_INTERVAL);
        }
    }

    private void updateRunning() {
        boolean running = mVisible && mStarted && !mPaused && isShown();
        if (running != mRunning) {
            if (running) {
                updateView(nowMillis());
                postDelayed(mTickRunnable, TIMER_INTERVAL);
            } else {
                removeCallbacks(mTickRunnable);
            }
            mRunning = running;
        }
    }

    private synchronized void updateView(long now) {
        long ms = now - mStart;
        setText(DateTimeUtils.msToStrFormat(ms));
    }

    private synchronized long nowMillis() {
        return DateTimeUtils.nowMs();
    }
}
