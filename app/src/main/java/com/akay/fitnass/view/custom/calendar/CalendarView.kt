package com.akay.fitnass.view.custom.calendar

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.akay.fitnass.R
import kotlinx.android.synthetic.main.view_calendar.view.*
import org.threeten.bp.YearMonth

class CalendarView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    init {
        inflate(context, R.layout.view_calendar, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        val adapter = MonthAdapter()
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(vCalendarHrv)
        adapter.snapHelper = snapHelper

        vCalendarHrv.attachSnapHelperWithListener(snapHelper, adapter)
        vCalendarHrv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        vCalendarHrv.adapter = adapter
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val width = right - left
        val height = bottom - top
        vCalendarHrv.layout(0, 0, width, height)
    }

    fun setDateChangeListener(listener: (YearMonth) -> Unit) {
        val adapter = vCalendarHrv.adapter as MonthAdapter
        adapter.onDateChangeListener = listener
    }

    fun getCurrentMonthName(): YearMonth {
        val adapter = vCalendarHrv.adapter as MonthAdapter
        return adapter.getCurrentYearMonth()
    }
}

private fun RecyclerView.attachSnapHelperWithListener(
        snapHelper: SnapHelper,
        onSnapPositionChangeListener: OnSnapPositionChangeListener) {

    snapHelper.attachToRecyclerView(this)
    val snapOnScrollListener = SnapOnScrollListener(snapHelper, onSnapPositionChangeListener)
    addOnScrollListener(snapOnScrollListener)
}