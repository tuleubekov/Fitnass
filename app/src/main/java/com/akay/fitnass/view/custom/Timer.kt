package com.akay.fitnass.view.custom

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.view.View

import com.akay.fitnass.util.DateTimes

import com.akay.fitnass.util.DateTimes.nowMillis

class Timer : AppCompatTextView {

    private var mVisible: Boolean = false
    private var mStarted: Boolean = false
    private var mRunning: Boolean = false
    private var mPaused: Boolean = false
    private var mStart: Long = 0
    private var mTimeWhenStopped: Long = 0

    private val mTickRunnable = Runnable { this.run() }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putBoolean(KEY_STARTED_STATE, mStarted)
        bundle.putBoolean(KEY_PAUSED_STATE, mPaused)
        bundle.putLong(KEY_START_STATE, mStart)
        bundle.putLong(KEY_TWS_STATE, mTimeWhenStopped)
        bundle.putParcelable(KEY_SUPER_STATE, super.onSaveInstanceState())
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        var viewState: Parcelable? = state
        if (state is Bundle) {
            val bState = viewState as Bundle?
            viewState = bState!!.getParcelable(KEY_SUPER_STATE)
            mStarted = bState.getBoolean(KEY_STARTED_STATE)
            mPaused = bState.getBoolean(KEY_PAUSED_STATE)
            mStart = bState.getLong(KEY_START_STATE)
            mTimeWhenStopped = bState.getLong(KEY_TWS_STATE)
        }
        super.onRestoreInstanceState(viewState)
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        mVisible = visibility == View.VISIBLE

        when (visibility) {
            View.VISIBLE -> onVisible()
            View.GONE -> pauseTimer()
            View.INVISIBLE -> return
        }

        updateRunning()
    }

    fun setUp(isPaused: Boolean, start: Long, tws: Long) {
        this.mPaused = isPaused
        this.mStart = start - mTimeWhenStopped
        this.mTimeWhenStopped = tws

        if (mPaused) {
            mTimeWhenStopped = mStart - tws
        } else {
            mStarted = true
        }
        updateRunning()
    }

    fun setUpPause(start: Long, tws: Long) {
        mPaused = true
        mStarted = false
        mTimeWhenStopped = tws
        mStart = nowMillis() + mTimeWhenStopped
        updateView(nowMillis())
    }

    fun setUpStart(start: Long, tws: Long) {
        mPaused = false
        mStarted = true
        mStart = start
        mTimeWhenStopped = tws
        updateRunning()
    }

    fun start(startMs: Long) {
        mStart = startMs
        mPaused = false
        startTimer()
    }

    fun pause(twsMs: Long) {
        mTimeWhenStopped = twsMs
        mPaused = true
        pauseTimer()
    }

    fun reset() {
        initTimer()
    }

    private fun init() {
        setupFont()
        initTimer()
    }

    private fun setupFont() {
        if (!isInEditMode) {
            val tf = Typeface.createFromAsset(context.assets, "fonts/roboto-thin.ttf")
            typeface = tf
        }
    }

    private fun initTimer() {
        clear()
        updateView(0L)
    }

    private fun startTimer() {
        mStarted = true
        updateRunning()
    }

    private fun pauseTimer() {
        mStarted = false
        updateRunning()
    }

    private fun clear() {
        mStart = 0
        mTimeWhenStopped = 0
        mStarted = false
        mPaused = true
    }

    private fun onVisible() {
        if (!mStarted && !mPaused) {
            startTimer()
        }
    }

    private fun run() {
        if (mRunning) {
            updateView(nowMillis())
            postDelayed(mTickRunnable, TIMER_INTERVAL)
        }
    }

    private fun updateRunning() {
        val running = mVisible && mStarted && !mPaused && isShown
        if (running != mRunning) {
            if (running) {
                updateView(nowMillis())
                postDelayed(mTickRunnable, TIMER_INTERVAL)
            } else {
                removeCallbacks(mTickRunnable)
            }
            mRunning = running
        }
    }

    @Synchronized
    private fun updateView(now: Long) {
        val ms = now - mStart
        text = DateTimes.msToStrFormat(ms)
    }

    companion object {
        private const val KEY_SUPER_STATE = "com.akay.fitnass.ui.custom.SUPER_STATE"
        private const val KEY_STARTED_STATE = "com.akay.fitnass.ui.custom.STARTED_STATE"
        private const val KEY_PAUSED_STATE = "com.akay.fitnass.ui.custom.PAUSED_STATE"
        private const val KEY_START_STATE = "com.akay.fitnass.ui.custom.START_STATE"
        private const val KEY_TWS_STATE = "com.akay.fitnass.ui.custom.TWS_STATE"
        private const val TIMER_INTERVAL = 10L
    }
}
