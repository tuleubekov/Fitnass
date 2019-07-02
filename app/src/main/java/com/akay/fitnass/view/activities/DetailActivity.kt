package com.akay.fitnass.view.activities

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.akay.fitnass.R
import com.akay.fitnass.data.model.Lap
import com.akay.fitnass.view.adapters.LapAdapter
import com.akay.fitnass.viewmodel.DetailViewModel
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : BaseActivity() {

    private var mViewModel: DetailViewModel? = null
    private var mAdapter: LapAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        mViewModel = ViewModelProviders.of(this).get(DetailViewModel::class.java)
        val runs = mViewModel!!.getById(getRunsId())
        mAdapter = LapAdapter(runs.laps as MutableList<Lap>)
        recycler_workout!!.adapter = mAdapter
    }

    private fun getRunsId(): Long {
        val intent = intent
        if (intent == null || !intent.hasExtra(RUNS_ID_KEY)) {
            throw IllegalArgumentException("Workout ID is missing!")
        }
        return intent.getLongExtra(RUNS_ID_KEY, -1)
    }

    companion object {
        private const val RUNS_ID_KEY = "com.akay.fitnass.ui.detail.WORKOUT_ID_KEY"

        fun getIntent(context: Context, idWorkout: Long): Intent {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(RUNS_ID_KEY, idWorkout)
            return intent
        }
    }
}
