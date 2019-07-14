/*
 * Copyright (C) 2015 The Android Open Source Project
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
package com.akay.fitnass.view.custom.fit_calendar

import com.android.internal.widget.PagerAdapter

import android.R
import android.R.attr
import android.annotation.IdRes
import android.annotation.LayoutRes
import android.annotation.NonNull
import android.annotation.Nullable
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleMonthView.OnDayClickListener

import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.viewpager.widget.PagerAdapter

import java.util.Calendar

import android.R.attr.colorControlHighlight

/**
 * An adapter for a list of [android.widget.SimpleMonthView] items.
 */
class DayPickerAdapter(context: Context, @param:LayoutRes private val mLayoutResId: Int,
                                @param:IdRes private val mCalendarViewId: Int) : PagerAdapter() {
    private val mMinDate = Calendar.getInstance()
    private val mMaxDate = Calendar.getInstance()
    private val mItems = SparseArray<ViewHolder>()
    private val mInflater: LayoutInflater
    private var mSelectedDay: Calendar? = null
    private var mMonthTextAppearance: Int = 0
    var dayOfWeekTextAppearance: Int = 0
    var dayTextAppearance: Int = 0
    private var mCalendarTextColor: ColorStateList? = null
    private var mDaySelectorColor: ColorStateList? = null
    private val mDayHighlightColor: ColorStateList?
    private var mOnDaySelectedListener: OnDaySelectedListener? = null
    private var mCount: Int = 0
    /**
     * Sets the first day of the week.
     *
     * @param weekStart which day the week should start on, valid values are
     * [Calendar.SUNDAY] through [Calendar.SATURDAY]
     */
    // Update displayed views.
    var firstDayOfWeek: Int = 0
        set(weekStart) {
            field = weekStart
            val count = mItems.size()
            for (i in 0 until count) {
                val monthView = mItems.valueAt(i).calendar
                monthView!!.setFirstDayOfWeek(weekStart)
            }
        }
    private val mOnDayClickListener = object : OnDayClickListener() {
        fun onDayClick(view: SimpleMonthView, day: Calendar?) {
            if (day != null && isCalendarInRange(day)) {
                setSelectedDay(day)
                if (mOnDaySelectedListener != null) {
                    mOnDaySelectedListener!!.onDaySelected(this@DayPickerAdapter, day)
                }
            }
        }

        fun onNavigationClick(view: SimpleMonthView, direction: Int, animate: Boolean) {
            if (mOnDaySelectedListener != null) {
                mOnDaySelectedListener!!.onNavigationClick(this@DayPickerAdapter, direction, animate)
            }
        }
    }

    init {
        mInflater = LayoutInflater.from(context)
        val ta = context.obtainStyledAttributes(intArrayOf(com.android.internal.R.attr.colorControlHighlight))
        mDayHighlightColor = ta.getColorStateList(0)
        ta.recycle()
    }

    fun setRange(min: Calendar, max: Calendar) {
        mMinDate.timeInMillis = min.timeInMillis
        mMaxDate.timeInMillis = max.timeInMillis
        val diffYear = mMaxDate.get(Calendar.YEAR) - mMinDate.get(Calendar.YEAR)
        val diffMonth = mMaxDate.get(Calendar.MONTH) - mMinDate.get(Calendar.MONTH)
        mCount = diffMonth + MONTHS_IN_YEAR * diffYear + 1
        // Positions are now invalid, clear everything and start over.
        notifyDataSetChanged()
    }

    /**
     * Sets the selected day.
     *
     * @param day the selected day
     */
    fun setSelectedDay(day: Calendar?) {
        val oldPosition = getPositionForDay(mSelectedDay)
        val newPosition = getPositionForDay(day)
        // Clear the old position if necessary.
        if (oldPosition != newPosition && oldPosition >= 0) {
            val oldMonthView = mItems.get(oldPosition, null)
            if (oldMonthView != null) {
                oldMonthView.calendar!!.setSelectedDay(-1)
            }
        }
        // Set the new position.
        if (newPosition >= 0) {
            val newMonthView = mItems.get(newPosition, null)
            if (newMonthView != null) {
                val dayOfMonth = day!!.get(Calendar.DAY_OF_MONTH)
                newMonthView.calendar!!.setSelectedDay(dayOfMonth)
            }
        }
        mSelectedDay = day
    }

    /**
     * Sets the listener to call when the user selects a day.
     *
     * @param listener The listener to call.
     */
    fun setOnDaySelectedListener(listener: OnDaySelectedListener) {
        mOnDaySelectedListener = listener
    }

    fun setCalendarTextColor(calendarTextColor: ColorStateList) {
        mCalendarTextColor = calendarTextColor
    }

    fun setDaySelectorColor(selectorColor: ColorStateList) {
        mDaySelectorColor = selectorColor
    }

    fun setMonthTextAppearance(resId: Int) {
        mMonthTextAppearance = resId
    }

    override fun getCount(): Int {
        return mCount
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        val holder = `object` as ViewHolder
        return view === holder.container
    }

    private fun getMonthForPosition(position: Int): Int {
        return position % MONTHS_IN_YEAR + mMinDate.get(Calendar.MONTH)
    }

    private fun getYearForPosition(position: Int): Int {
        return position / MONTHS_IN_YEAR + mMinDate.get(Calendar.YEAR)
    }

    private fun getPositionForDay(day: Calendar?): Int {
        if (day == null) {
            return -1
        }
        val yearOffset = day.get(Calendar.YEAR) - mMinDate.get(Calendar.YEAR)
        val monthOffset = day.get(Calendar.MONTH) - mMinDate.get(Calendar.MONTH)
        return yearOffset * MONTHS_IN_YEAR + monthOffset
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = mInflater.inflate(mLayoutResId, container, false)
        val v = itemView.findViewById<View>(mCalendarViewId) as SimpleMonthView
        v.setOnDayClickListener(mOnDayClickListener)
        v.setMonthTextAppearance(mMonthTextAppearance)
        v.setDayOfWeekTextAppearance(dayOfWeekTextAppearance)
        v.setDayTextAppearance(dayTextAppearance)
        if (mDaySelectorColor != null) {
            v.setDaySelectorColor(mDaySelectorColor)
        }
        if (mDayHighlightColor != null) {
            v.setDayHighlightColor(mDayHighlightColor)
        }
        if (mCalendarTextColor != null) {
            v.setMonthTextColor(mCalendarTextColor)
            v.setDayOfWeekTextColor(mCalendarTextColor)
            v.setDayTextColor(mCalendarTextColor)
        }
        val month = getMonthForPosition(position)
        val year = getYearForPosition(position)
        val selectedDay: Int
        if (mSelectedDay != null && mSelectedDay!!.get(Calendar.MONTH) == month) {
            selectedDay = mSelectedDay!!.get(Calendar.DAY_OF_MONTH)
        } else {
            selectedDay = -1
        }
        val enabledDayRangeStart: Int
        if (mMinDate.get(Calendar.MONTH) == month && mMinDate.get(Calendar.YEAR) == year) {
            enabledDayRangeStart = mMinDate.get(Calendar.DAY_OF_MONTH)
        } else {
            enabledDayRangeStart = 1
        }
        val enabledDayRangeEnd: Int
        if (mMaxDate.get(Calendar.MONTH) == month && mMaxDate.get(Calendar.YEAR) == year) {
            enabledDayRangeEnd = mMaxDate.get(Calendar.DAY_OF_MONTH)
        } else {
            enabledDayRangeEnd = 31
        }
        v.setMonthParams(selectedDay, month, year, firstDayOfWeek,
                enabledDayRangeStart, enabledDayRangeEnd)
        v.setPrevEnabled(position > 0)
        v.setNextEnabled(position < mCount - 1)
        val holder = ViewHolder(position, itemView, v)
        mItems.put(position, holder)
        container.addView(itemView)
        return holder
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val holder = `object` as ViewHolder
        container.removeView(holder.container)
        mItems.remove(position)
    }

    override fun getItemPosition(`object`: Any): Int {
        val holder = `object` as ViewHolder
        return holder.position
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val v = mItems.get(position).calendar
        return if (v != null) {
            v!!.getTitle()
        } else null
    }

    private fun isCalendarInRange(value: Calendar): Boolean {
        return value.compareTo(mMinDate) >= 0 && value.compareTo(mMaxDate) <= 0
    }

    private class ViewHolder(val position: Int, val container: View, val calendar: SimpleMonthView?)
    interface OnDaySelectedListener {
        fun onDaySelected(view: DayPickerAdapter, day: Calendar?)
        fun onNavigationClick(view: DayPickerAdapter, direction: Int, animate: Boolean)
    }

    companion object {
        private val MONTHS_IN_YEAR = 12
    }
}