package com.akay.fitnass.view.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.akay.fitnass.R
import com.akay.fitnass.data.model.Runs
import kotlinx.android.synthetic.main.item_day.view.*
import org.threeten.bp.format.DateTimeFormatter

class DayAdapter(
        private var mWorkouts: List<Runs>?,
        val clickListener: (id: Long) -> Unit,
        val longClickListener: (runs: Runs) -> Unit) : RecyclerView.Adapter<DayAdapter.ViewHolder>() {

    fun onWorkoutListUpdated(workouts: List<Runs>) {
        mWorkouts = workouts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, pos: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_day, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, pos: Int) {
        viewHolder.onBind(mWorkouts!![pos])
    }

    @Synchronized
    override fun getItemCount(): Int {
        return if (mWorkouts == null) 0 else mWorkouts!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun onBind(runs: Runs) {
            itemView.run {
                text_date.text = runs.dateTime.format(formatter)
                text_run_count.text = runs.laps.size.toString()

                setOnClickListener { clickListener(runs.id!!) }

                setOnLongClickListener {
                    longClickListener(runs)
                    return@setOnLongClickListener true
                }
            }
        }
    }

    companion object {
        private const val FORMAT_DATETIME = "dd MMMM yyyy HH:mm"
        private val formatter = DateTimeFormatter.ofPattern(FORMAT_DATETIME)
    }
}
