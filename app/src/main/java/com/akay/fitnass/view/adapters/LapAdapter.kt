package com.akay.fitnass.view.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.akay.fitnass.R
import com.akay.fitnass.data.model.Lap
import com.akay.fitnass.util.DateTimes.msToStrFormat
import com.akay.fitnass.util.DateTimes.toMs
import kotlinx.android.synthetic.main.item_lap_workout.view.*

class LapAdapter(private var mLaps: MutableList<Lap>?) : RecyclerView.Adapter<LapAdapter.ViewHolder>() {
    private var mRecycler: RecyclerView? = null

    var laps: MutableList<Lap>?
        get() = mLaps
        set(laps) {
            if (laps != null && !laps.isEmpty()) {
                this.mLaps = laps
                notifyDataSetChanged()
                mRecycler!!.smoothScrollToPosition(mLaps!!.size - 1)
            }
        }

    fun addLap(lap: Lap) {
        mLaps!!.add(lap)
        notifyDataSetChanged()
        mRecycler!!.smoothScrollToPosition(mLaps!!.size - 1)
    }

    fun clear() {
        mLaps!!.clear()
        notifyDataSetChanged()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecycler = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        mRecycler = null
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_lap_workout, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.onBind(mLaps!![i], i)
    }

    override fun getItemCount(): Int {
        return if (mLaps == null) 0 else mLaps!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun onBind(lap: Lap, pos: Int) {
            itemView.run {
                text_iterator.text = (pos + 1).toString()
                text_lapTime.text = msToStrFormat(toMs(lap.time))
            }
        }
    }
}
