package com.akay.fitnass.view.custom.calendar

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.akay.fitnass.R
import kotlinx.android.synthetic.main.view_calendar.view.*

class CalendarView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    init {
        inflate(context, R.layout.view_calendar, this)

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(vCalendarHrv)
        vCalendarHrv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        vCalendarHrv.adapter = MonthAdapter()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val width = right - left
        val height = bottom - top
        vCalendarHrv.layout(0, 0, width, height)
    }
}