package com.akay.fitnass.view.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.akay.fitnass.util.DateTimes;

import static com.akay.fitnass.util.DateTimes.nowMillis;

public class Timer extends AppCompatTextView {
    private static final String KEY_SUPER_STATE = "com.akay.fitnass.ui.custom.SUPER_STATE";
    private static final String KEY_STARTED_STATE = "com.akay.fitnass.ui.custom.STARTED_STATE";
    private static final String KEY_PAUSED_STATE = "com.akay.fitnass.ui.custom.PAUSED_STATE";
    private static final String KEY_START_STATE = "com.akay.fitnass.ui.custom.START_STATE";
    private static final String KEY_TWS_STATE = "com.akay.fitnass.ui.custom.TWS_STATE";
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
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_STARTED_STATE, mStarted);
        bundle.putBoolean(KEY_PAUSED_STATE, mPaused);
        bundle.putLong(KEY_START_STATE, mStart);
        bundle.putLong(KEY_TWS_STATE, mTimeWhenStopped);
        bundle.putParcelable(KEY_SUPER_STATE, super.onSaveInstanceState());
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        Parcelable viewState = state;
        if (state instanceof Bundle) {
            Bundle bState = (Bundle) viewState;
            viewState = bState.getParcelable(KEY_SUPER_STATE);
            mStarted = bState.getBoolean(KEY_STARTED_STATE);
            mPaused = bState.getBoolean(KEY_PAUSED_STATE);
            mStart = bState.getLong(KEY_START_STATE);
            mTimeWhenStopped = bState.getLong(KEY_TWS_STATE);
        }
        super.onRestoreInstanceState(viewState);
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
        this.mStart = start - mTimeWhenStopped;
        this.mTimeWhenStopped = tws;

        if (mPaused) {
            mTimeWhenStopped = mStart - tws;
        } else {
            mStarted = true;
        }
        updateRunning();
    }

    public void setUpPause(long start, long tws) {
        mPaused = true;
        mStarted = false;
        mTimeWhenStopped = tws;
        mStart = nowMillis() + mTimeWhenStopped;
        updateView(nowMillis());
    }

    public void setUpStart(long start, long tws) {
        mPaused = false;
        mStarted = true;
        mStart = start;
        mTimeWhenStopped = tws;
        updateRunning();
    }

    public void start(long startMs) {
        mStart = startMs;
        mPaused = false;
        startTimer();
    }

    public void pause(long twsMs) {
        mTimeWhenStopped = twsMs;
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
        setText(DateTimes.msToStrFormat(ms));
    }
}
