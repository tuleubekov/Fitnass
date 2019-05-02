package com.akay.fitnass.ui.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Parcelable;
//import android.os.SystemClock;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.akay.fitnass.data.storage.model.TimerParams;
import com.akay.fitnass.util.DateTimeUtils;

import org.threeten.bp.ZonedDateTime;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
//
//    public long getStart() {
//        return mStart;
//    }
//
//    public long getTimeWhenStopped() {
//        return mTimeWhenStopped;
//    }

    public boolean isPaused() {
        return mPaused;
    }
//
//    public TimerParams getParams() {
//        TimerParams params = new TimerParams();
//        params.setStart(mStart);
//        params.setTws(mTimeWhenStopped);
//        params.setStarted(mStarted);
//        params.setPaused(mPaused);
//        return params;
//    }
//
//    public void setParams(TimerParams params) {
//        this.mStart = params.getStart();
//        this.mTimeWhenStopped = params.getTws();
////        this.mStarted = params.isStarted();
//        this.mPaused = params.isPaused();
//
//        if (mPaused) {
//            mStart = nowMillis() + mTimeWhenStopped;
//            updateView(nowMillis());
//        }
//        updateRunning();
//    }

    public void prepare(Builder builder) {
        mPaused = builder.paused;
        mStart = builder.start;
        mTimeWhenStopped = builder.tws;

        if (mPaused) {
            mStart = nowMillis() + mTimeWhenStopped;
            updateView(nowMillis());
        }
    }

    public void update() {
        updateRunning();
    }

    public void start(long startMs) {
//        mStart = nowMillis() + mTimeWhenStopped;
        mStart = startMs + mTimeWhenStopped;
        mPaused = false;
        startTimer();
    }

    public void pause(long twsMs) {
//        mTimeWhenStopped = mStart - nowMillis();
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

    public static class Builder {
        private boolean paused;
        private long start;
        private long tws;

        public Builder setStart(ZonedDateTime zdtStart) {
            this.start = DateTimeUtils.toMs(zdtStart);
            return this;
        }

        public Builder setTws(ZonedDateTime zdtTws) {
            this.tws = DateTimeUtils.toMs(zdtTws);
            return this;
        }

        public Builder setPaused(boolean paused) {
            this.paused = paused;
            return this;
        }
    }
}
