package com.akay.fitnass.view.activities

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle

import com.akay.fitnass.R
import com.akay.fitnass.data.model.ActiveRuns
import com.akay.fitnass.data.model.Runs
import com.akay.fitnass.view.adapters.DayAdapter
import com.akay.fitnass.viewmodel.MainViewModel

import java.util.ArrayList

import io.reactivex.disposables.Disposable

import com.akay.fitnass.service.FitService.Companion.INIT_STATE_COMMAND
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    private var mViewModel: MainViewModel? = null
    private var mAdapter: DayAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        mAdapter = DayAdapter(ArrayList(), { this.onItemClicked(it) }, { this.onItemLongClicked(it) })
        recycler_day!!.adapter = mAdapter
        mViewModel!!.getLiveRunsList().observe(this, Observer<List<Runs>> { this.onRunsListChanged(it!!) })
        mViewModel!!.getLiveActiveRuns().observe(this, Observer<ActiveRuns> { this.onActiveRunsChanged(it) })
    }

    override fun initViewRxObservables() {
        addDisposables(initNewRunsObserver())
    }

    private fun onRunsListChanged(runs: List<Runs>) {
        mAdapter!!.onWorkoutListUpdated(runs)
    }

    private fun onActiveRunsChanged(activeRuns: ActiveRuns?) {
        val isNotNull = activeRuns != null
        onRunsButtonState(isNotNull)
        if (isNotNull) {
            sendCommand(INIT_STATE_COMMAND)
        }
    }

    private fun onRunsButtonState(isNotActiveRuns: Boolean) {
        btn_add!!.isChecked = isNotActiveRuns
    }

    private fun onAddRunsClicked(view: Any) {
        startActivity(TimerActivity.getIntent(this))
    }

    private fun onItemClicked(idRuns: Long) {
        startActivity(DetailActivity.getIntent(this, idRuns))
    }

    private fun onItemLongClicked(runs: Runs) {
        showDeleteRunsDialog(DialogInterface.OnClickListener { _, _ -> mViewModel!!.deleteDayRuns(runs) })
    }

    private fun initNewRunsObserver(): Disposable {
        return clickObserver(btn_add!!).subscribe { this.onAddRunsClicked(it) }
    }

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}
