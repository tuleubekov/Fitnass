package com.akay.fitnass.view.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.akay.fitnass.R
import com.akay.fitnass.data.model.ActiveRuns
import com.akay.fitnass.data.model.Lap
import com.akay.fitnass.service.FitService.Companion.LAP_COMMAND
import com.akay.fitnass.service.FitService.Companion.PAUSE_COMMAND
import com.akay.fitnass.service.FitService.Companion.RESET_COMMAND
import com.akay.fitnass.service.FitService.Companion.SAVE_COMMAND
import com.akay.fitnass.service.FitService.Companion.START_COMMAND
import com.akay.fitnass.util.DateTimes.nowMillis
import com.akay.fitnass.util.DateTimes.toMs
import com.akay.fitnass.view.adapters.LapAdapter
import com.akay.fitnass.viewmodel.TimerViewModel
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_timer.*
import java.util.*

class TimerActivity : BaseActivity() {
    private var mViewModel: TimerViewModel? = null
    private var mAdapter: LapAdapter? = null
    private var mActiveRuns: ActiveRuns? = null
    private var mInitialized: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        mViewModel = ViewModelProviders.of(this).get(TimerViewModel::class.java)
        mViewModel!!.activeRuns.observe(this, Observer<ActiveRuns> { this.onActiveRunsChanged(it) })
        mAdapter = LapAdapter(ArrayList())
        recycler_workout!!.adapter = mAdapter
    }

    override fun onStop() {
        super.onStop()
        mInitialized = false
    }

    override fun initViewRxObservables() {
        addDisposables(initStartPauseObserver())
        addDisposables(initLapSaveObserver())
        addDisposables(initResetObserver())
    }

    private fun onActiveRunsChanged(activeRuns: ActiveRuns?) {
        if (activeRuns == null) {
            return
        }

        if (lapsValid(activeRuns.laps)) {
            mAdapter!!.laps = activeRuns.laps
        }

        mActiveRuns = activeRuns
        uiSetUp(activeRuns.isPaused)
        onTimerActions(activeRuns)

        if (!mInitialized) {
            setUpTimer(activeRuns)
        }
    }

    private fun setUpTimer(activeRuns: ActiveRuns) {
        mInitialized = true
        val isPaused = activeRuns.isPaused
        val start = toMs(activeRuns.start)
        val tws = toMs(activeRuns.tws)

        if (isPaused) {
            chronometer!!.setUpPause(start, tws)
        } else {
            chronometer!!.setUpStart(start, tws)
        }
    }

    private fun onTimerActions(activeRuns: ActiveRuns) {
        if (activeRuns.isPaused) {
            chronometer!!.pause(toMs(activeRuns.tws))
        } else {
            chronometer!!.start(toMs(activeRuns.start))
        }
    }

    private fun onStartPauseClicked(view: Any) {
        mInitialized = true
        if (mActiveRuns == null) {
            chronometer!!.start(nowMillis())
            sendCommand(START_COMMAND)
            return
        }

        val isPaused = !mActiveRuns!!.isPaused
        sendCommand(if (isPaused) PAUSE_COMMAND else START_COMMAND)
    }

    private fun onLapSaveClicked(view: Any) {
        val isPaused = mActiveRuns!!.isPaused
        sendCommand(if (isPaused) SAVE_COMMAND else LAP_COMMAND)
        if (isPaused) {
            finish()
        }
    }

    private fun onResetClicked(view: Any) {
        mInitialized = false
        mActiveRuns = null
        mAdapter!!.clear()
        chronometer!!.reset()
        btn_reset!!.isEnabled = false
        btn_lap_save!!.isEnabled = false
        sendCommand(RESET_COMMAND)
    }

    private fun uiSetUp(isPaused: Boolean) {
        btn_start_pause!!.isChecked = !isPaused
        btn_lap_save!!.isChecked = isPaused
        btn_reset!!.isEnabled = isPaused
        btn_lap_save!!.isChecked = !isPaused
        btn_lap_save!!.isEnabled = !isPaused || mAdapter != null && mAdapter!!.itemCount > 0
    }

    private fun initStartPauseObserver(): Disposable {
        return clickObserver(btn_start_pause!!).subscribe { this.onStartPauseClicked(it) }
    }

    private fun initLapSaveObserver(): Disposable {
        return clickObserver(btn_lap_save!!).subscribe { this.onLapSaveClicked(it) }
    }

    private fun initResetObserver(): Disposable {
        return longCLickObserver(btn_reset!!).subscribe { this.onResetClicked(it) }
    }

    private fun lapsValid(laps: List<Lap>?): Boolean {
        return laps != null && !laps.isEmpty()
    }

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, TimerActivity::class.java)
        }
    }
}
