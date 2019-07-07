package com.akay.fitnass.view.custom.calendar.ui

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.akay.fitnass.view.custom.calendar.CalendarView
import com.akay.fitnass.view.custom.calendar.model.CalendarDay
import com.akay.fitnass.view.custom.calendar.model.ScrollMode
import org.threeten.bp.YearMonth


internal class CalendarLayoutManager(private val calView: CalendarView, @RecyclerView.Orientation orientation: Int) :
        LinearLayoutManager(calView.context, orientation, false) {

    private val adapter: CalendarAdapter
        get() = calView.adapter as CalendarAdapter

    private val context: Context
        get() = calView.context

    fun scrollToMonth(month: YearMonth) {
        scrollToPosition(adapter.getAdapterPosition(month))
        calView.post { adapter.notifyMonthScrollListenerIfNeeded() }
    }

    fun smoothScrollToMonth(month: YearMonth) {
        val position = adapter.getAdapterPosition(month)
        if (position != -1) {
            startSmoothScroll(CalendarSmoothScroller(position, null))
        }
    }

    fun smoothScrollToDay(day: CalendarDay) {
        val position = adapter.getAdapterPosition(day)
        if (position != -1) {
            startSmoothScroll(CalendarSmoothScroller(position, day))
        }
    }

    fun scrollToDay(day: CalendarDay) {
        val monthPosition = adapter.getAdapterPosition(day)
        scrollToPosition(monthPosition)
        if (calView.scrollMode == ScrollMode.PAGED) return
        calView.post {
            if (monthPosition != -1) {
                // We already scrolled to this position so findViewHolder should not return null.
                val viewHolder = calView.findViewHolderForAdapterPosition(monthPosition) as MonthViewHolder
                val offset = calculateOffset(day, viewHolder.itemView)
                scrollToPositionWithOffset(monthPosition, -offset)
                calView.post { adapter.notifyMonthScrollListenerIfNeeded() }
            }
        }
    }

    private fun calculateOffset(day: CalendarDay, itemView: View): Int {
        val dayView = itemView.findViewById<View?>(day.date.hashCode()) ?: return 0
        val rect = Rect()
        dayView.getDrawingRect(rect)
        (itemView as ViewGroup).offsetDescendantRectToMyCoords(dayView, rect)
        return if (calView.isVertical) rect.top + calView.monthMarginTop else rect.left + calView.monthMarginStart
    }

    private inner class CalendarSmoothScroller(position: Int, val day: CalendarDay?) :
            LinearSmoothScroller(context) {

        init {
            targetPosition = position
        }

        override fun getVerticalSnapPreference(): Int {
            return SNAP_TO_START
        }

        override fun getHorizontalSnapPreference(): Int {
            return SNAP_TO_START
        }

        override fun calculateDyToMakeVisible(view: View, snapPreference: Int): Int {
            val dy = super.calculateDyToMakeVisible(view, snapPreference)
            if (day == null) {
                return dy
            }
            val offset = calculateOffset(day, view)
            return dy - offset
        }

        override fun calculateDxToMakeVisible(view: View, snapPreference: Int): Int {
            val dx = super.calculateDxToMakeVisible(view, snapPreference)
            if (day == null) {
                return dx
            }
            val offset = calculateOffset(day, view)
            return dx - offset
        }
    }
}