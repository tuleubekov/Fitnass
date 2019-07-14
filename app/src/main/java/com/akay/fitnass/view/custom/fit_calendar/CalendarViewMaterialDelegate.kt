package com.akay.fitnass.view.custom.fit_calendar

/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context
import android.graphics.Rect
import android.icu.util.Calendar
import android.util.AttributeSet
import android.widget.DayPickerView.OnDaySelectedListener
import androidx.annotation.StyleRes

internal class CalendarViewMaterialDelegate(delegator: CalendarView, context: Context, attrs: AttributeSet,
                                            defStyleAttr: Int, defStyleRes: Int) : CalendarView.AbstractCalendarViewDelegate(delegator, context) {
    private val mDayPickerView: DayPickerView

    private var mOnDateChangeListener: CalendarView.OnDateChangeListener? = null

    override var weekDayTextAppearance: Int
        @StyleRes
        get() = mDayPickerView.getDayOfWeekTextAppearance()
        set(@StyleRes resId) {
            mDayPickerView.setDayOfWeekTextAppearance(resId)
        }

    override var dateTextAppearance: Int
        @StyleRes
        get() = mDayPickerView.getDayTextAppearance()
        set(@StyleRes resId) {
            mDayPickerView.setDayTextAppearance(resId)
        }

    override var minDate: Long
        get() = mDayPickerView.getMinDate()
        set(minDate) {
            mDayPickerView.setMinDate(minDate)
        }

    override var maxDate: Long
        get() = mDayPickerView.getMaxDate()
        set(maxDate) {
            mDayPickerView.setMaxDate(maxDate)
        }

    override var firstDayOfWeek: Int
        get() = mDayPickerView.getFirstDayOfWeek()
        set(firstDayOfWeek) {
            mDayPickerView.setFirstDayOfWeek(firstDayOfWeek)
        }

    override var date: Long
        get() = mDayPickerView.getDate()
        set(date) {
            mDayPickerView.setDate(date, true)
        }

    private val mOnDaySelectedListener = object : OnDaySelectedListener() {
        fun onDaySelected(view: DayPickerView, day: Calendar) {
            if (mOnDateChangeListener != null) {
                val year = day.get(Calendar.YEAR)
                val month = day.get(Calendar.MONTH)
                val dayOfMonth = day.get(Calendar.DAY_OF_MONTH)
                mOnDateChangeListener!!.onSelectedDayChange(mDelegator, year, month, dayOfMonth)
            }
        }
    }

    init {

        mDayPickerView = DayPickerView(context, attrs, defStyleAttr, defStyleRes)
        mDayPickerView.setOnDaySelectedListener(mOnDaySelectedListener)

        delegator.addView(mDayPickerView)
    }

    override fun setDate(date: Long, animate: Boolean, center: Boolean) {
        mDayPickerView.setDate(date, animate)
    }

    override fun setOnDateChangeListener(listener: CalendarView.OnDateChangeListener) {
        mOnDateChangeListener = listener
    }

    override fun getBoundsForDate(date: Long, outBounds: Rect): Boolean {
        val result = mDayPickerView.getBoundsForDate(date, outBounds)
        if (result) {
            // Found the date in the current picker. Now need to offset vertically to return correct
            // bounds in the coordinate system of the entire layout
            val dayPickerPositionOnScreen = IntArray(2)
            val delegatorPositionOnScreen = IntArray(2)
            mDayPickerView.getLocationOnScreen(dayPickerPositionOnScreen)
            mDelegator.getLocationOnScreen(delegatorPositionOnScreen)
            val extraVerticalOffset = dayPickerPositionOnScreen[1] - delegatorPositionOnScreen[1]
            outBounds.top += extraVerticalOffset
            outBounds.bottom += extraVerticalOffset
            return true
        }
        return false
    }
}
