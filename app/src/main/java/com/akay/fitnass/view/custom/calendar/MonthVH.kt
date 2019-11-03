package com.akay.fitnass.view.custom.calendar

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.akay.fitnass.R
import com.akay.fitnass.extension.inflate
import kotlinx.android.synthetic.main.view_calendar_item_month.view.*
import org.threeten.bp.YearMonth

class MonthVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(yearMonth: YearMonth) {
        itemView.vMonthView.setDate(yearMonth)
    }

    fun bind(dateStr: String) {
        itemView.vMonthView.setDate(dateStr)
    }

    companion object {
        fun create(parent: ViewGroup): MonthVH {
            val view = parent.inflate(R.layout.view_calendar_item_month)
            return MonthVH(view)
        }
    }
}