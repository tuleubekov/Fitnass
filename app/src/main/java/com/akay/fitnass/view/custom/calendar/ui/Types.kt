package com.akay.fitnass.view.custom.calendar.ui

import android.view.View
import com.akay.fitnass.view.custom.calendar.model.CalendarDay
import com.akay.fitnass.view.custom.calendar.model.CalendarMonth

open class ViewContainer(val view: View)

interface DayBinder<T : ViewContainer> {
    fun create(view: View): T
    fun bind(container: T, day: CalendarDay)
}

interface MonthHeaderFooterBinder<T : ViewContainer> {
    fun create(view: View): T
    fun bind(container: T, month: CalendarMonth)
}

typealias MonthScrollListener = (CalendarMonth) -> Unit