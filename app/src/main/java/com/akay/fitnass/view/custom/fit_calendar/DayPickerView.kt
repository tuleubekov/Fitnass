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
package com.akay.fitnass.view.custom.fit_calendar

import com.android.internal.widget.ViewPager
import com.android.internal.R

import android.R
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.MathUtils
import android.view.View
import android.view.View.MeasureSpec

import androidx.viewpager.widget.ViewPager

import java.util.ArrayList
import java.util.Calendar
import java.util.Locale
import libcore.icu.LocaleData

import android.R.attr.firstDayOfWeek
import javax.swing.text.StyleConstants.getForeground
import javax.xml.bind.DatatypeConverter.parseDate

/**
 * This displays a list of months in a calendar format with selectable days.
 */
class DayPickerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.calendarViewStyle, defStyleRes: Int = 0)
    : ViewPager(context, attrs, defStyleAttr, defStyleRes) {

    private val mSelectedDay = Calendar.getInstance()
    private val mMinDate = Calendar.getInstance()
    private val mMaxDate = Calendar.getInstance()
    private val mMatchParentChildren = ArrayList<View>(1)
    private val mAdapter: DayPickerAdapter
    /** Temporary calendar used for date calculations.  */
    private var mTempCalendar: Calendar? = null
    private var mOnDaySelectedListener: OnDaySelectedListener? = null
    var dayOfWeekTextAppearance: Int
        get() = mAdapter.getDayOfWeekTextAppearance()
        set(resId) {
            mAdapter.setDayOfWeekTextAppearance(resId)
        }
    var dayTextAppearance: Int
        get() = mAdapter.getDayTextAppearance()
        set(resId) {
            mAdapter.setDayTextAppearance(resId)
        }
    /**
     * Sets the currently selected date to the specified timestamp. Jumps
     * immediately to the new date. To animate to the new date, use
     * [.setDate].
     *
     * @param timeInMillis the target day in milliseconds
     */
    var date: Long
        get() = mSelectedDay.timeInMillis
        set(timeInMillis) = setDate(timeInMillis, false)
    var firstDayOfWeek: Int
        get() = mAdapter.getFirstDayOfWeek()
        set(firstDayOfWeek) {
            mAdapter.setFirstDayOfWeek(firstDayOfWeek)
        }
    var minDate: Long
        get() = mMinDate.timeInMillis
        set(timeInMillis) {
            mMinDate.timeInMillis = timeInMillis
            onRangeChanged()
        }
    var maxDate: Long
        get() = mMaxDate.timeInMillis
        set(timeInMillis) {
            mMaxDate.timeInMillis = timeInMillis
            onRangeChanged()
        }
    /**
     * Gets the position of the view that is most prominently displayed within the list view.
     */
    val mostVisiblePosition: Int
        get() = currentItem

    init {
        val a = context.obtainStyledAttributes(attrs,
                R.styleable.CalendarView, defStyleAttr, defStyleRes)
        var firstDayOfWeek = a.getInt(R.styleable.CalendarView_firstDayOfWeek,
                LocaleData.get(Locale.getDefault()).firstDayOfWeek)
        var minDate = a.getString(R.styleable.CalendarView_minDate)
        var maxDate = a.getString(R.styleable.CalendarView_maxDate)
        val monthTextAppearanceResId = a.getResourceId(
                R.styleable.CalendarView_monthTextAppearance,
                R.style.TextAppearance_Material_Widget_Calendar_Month)
        val dayOfWeekTextAppearanceResId = a.getResourceId(
                R.styleable.CalendarView_weekDayTextAppearance,
                R.style.TextAppearance_Material_Widget_Calendar_DayOfWeek)
        val dayTextAppearanceResId = a.getResourceId(
                R.styleable.CalendarView_dateTextAppearance,
                R.style.TextAppearance_Material_Widget_Calendar_Day)
        val daySelectorColor = a.getColorStateList(
                R.styleable.CalendarView_daySelectorColor)
        a.recycle()
        // Set up adapter.
        mAdapter = DayPickerAdapter(context,
                R.layout.date_picker_month_item_material, R.id.month_view)
        mAdapter.setMonthTextAppearance(monthTextAppearanceResId)
        mAdapter.setDayOfWeekTextAppearance(dayOfWeekTextAppearanceResId)
        mAdapter.setDayTextAppearance(dayTextAppearanceResId)
        mAdapter.setDaySelectorColor(daySelectorColor)
        adapter = mAdapter
        // Set up min and max dates.
        val tempDate = Calendar.getInstance()
        if (!CalendarView.parseDate(minDate, tempDate)) {
            tempDate.set(DEFAULT_START_YEAR, Calendar.JANUARY, 1)
        }
        val minDateMillis = tempDate.timeInMillis
        if (!CalendarView.parseDate(maxDate, tempDate)) {
            tempDate.set(DEFAULT_END_YEAR, Calendar.DECEMBER, 31)
        }
        val maxDateMillis = tempDate.timeInMillis
        if (maxDateMillis < minDateMillis) {
            throw IllegalArgumentException("maxDate must be >= minDate")
        }
        val setDateMillis = MathUtils.constrain(
                System.currentTimeMillis(), minDateMillis, maxDateMillis)
        firstDayOfWeek = firstDayOfWeek
        minDate = minDateMillis
        maxDate = maxDateMillis
        setDate(setDateMillis, false)
        // Proxy selection callbacks to our own listener.
        mAdapter.setOnDaySelectedListener(object : DayPickerAdapter.OnDaySelectedListener() {
            fun onDaySelected(adapter: DayPickerAdapter, day: Calendar) {
                if (mOnDaySelectedListener != null) {
                    mOnDaySelectedListener!!.onDaySelected(this@DayPickerView, day)
                }
            }

            fun onNavigationClick(view: DayPickerAdapter, direction: Int, animate: Boolean) {
                // ViewPager clamps input values, so we don't need to worry
                // about passing invalid indices.
                val nextItem = currentItem + direction
                setCurrentItem(nextItem, animate)
            }
        })
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        populate()
        // Everything below is mostly copied from FrameLayout.
        var count = childCount
        val measureMatchParentChildren = MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY || MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY
        var maxHeight = 0
        var maxWidth = 0
        var childState = 0
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility != View.GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec)
                val lp = child.layoutParams as ViewPager.LayoutParams
                maxWidth = Math.max(maxWidth, child.measuredWidth)
                maxHeight = Math.max(maxHeight, child.measuredHeight)
                childState = View.combineMeasuredStates(childState, child.measuredState)
                if (measureMatchParentChildren) {
                    if (lp.width == ViewPager.LayoutParams.MATCH_PARENT || lp.height == ViewPager.LayoutParams.MATCH_PARENT) {
                        mMatchParentChildren.add(child)
                    }
                }
            }
        }
        // Account for padding too
        maxWidth += paddingLeft + paddingRight
        maxHeight += paddingTop + paddingBottom
        // Check against our minimum height and width
        maxHeight = Math.max(maxHeight, suggestedMinimumHeight)
        maxWidth = Math.max(maxWidth, suggestedMinimumWidth)
        // Check against our foreground's minimum height and width
        val drawable = foreground
        if (drawable != null) {
            maxHeight = Math.max(maxHeight, drawable.minimumHeight)
            maxWidth = Math.max(maxWidth, drawable.minimumWidth)
        }
        setMeasuredDimension(View.resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                View.resolveSizeAndState(maxHeight, heightMeasureSpec,
                        childState shl View.MEASURED_HEIGHT_STATE_SHIFT))
        count = mMatchParentChildren.size
        if (count > 1) {
            for (i in 0 until count) {
                val child = mMatchParentChildren[i]
                val lp = child.layoutParams as ViewPager.LayoutParams
                val childWidthMeasureSpec: Int
                val childHeightMeasureSpec: Int
                if (lp.width == ViewPager.LayoutParams.MATCH_PARENT) {
                    childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                            measuredWidth - paddingLeft - paddingRight,
                            MeasureSpec.EXACTLY)
                } else {
                    childWidthMeasureSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec,
                            paddingLeft + paddingRight,
                            lp.width)
                }
                if (lp.height == ViewPager.LayoutParams.MATCH_PARENT) {
                    childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                            measuredHeight - paddingTop - paddingBottom,
                            MeasureSpec.EXACTLY)
                } else {
                    childHeightMeasureSpec = ViewGroup.getChildMeasureSpec(heightMeasureSpec,
                            paddingTop + paddingBottom,
                            lp.height)
                }
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
            }
        }
        mMatchParentChildren.clear()
    }

    /**
     * Sets the currently selected date to the specified timestamp. Jumps
     * immediately to the new date, optionally animating the transition.
     *
     * @param timeInMillis the target day in milliseconds
     * @param animate whether to smooth scroll to the new position
     */
    fun setDate(timeInMillis: Long, animate: Boolean) {
        setDate(timeInMillis, animate, true)
    }

    /**
     * Moves to the month containing the specified day, optionally setting the
     * day as selected.
     *
     * @param timeInMillis the target day in milliseconds
     * @param animate whether to smooth scroll to the new position
     * @param setSelected whether to set the specified day as selected
     */
    private fun setDate(timeInMillis: Long, animate: Boolean, setSelected: Boolean) {
        if (setSelected) {
            mSelectedDay.timeInMillis = timeInMillis
        }
        val position = getPositionFromDay(timeInMillis)
        if (position != currentItem) {
            setCurrentItem(position, animate)
        }
        mTempCalendar!!.timeInMillis = timeInMillis
        mAdapter.setSelectedDay(mTempCalendar)
    }

    /**
     * Handles changes to date range.
     */
    fun onRangeChanged() {
        mAdapter.setRange(mMinDate, mMaxDate)
        // Changing the min/max date changes the selection position since we
        // don't really have stable IDs. Jumps immediately to the new position.
        setDate(mSelectedDay.timeInMillis, false, false)
    }

    /**
     * Sets the listener to call when the user selects a day.
     *
     * @param listener The listener to call.
     */
    fun setOnDaySelectedListener(listener: OnDaySelectedListener) {
        mOnDaySelectedListener = listener
    }

    private fun getDiffMonths(start: Calendar, end: Calendar): Int {
        val diffYears = end.get(Calendar.YEAR) - start.get(Calendar.YEAR)
        return end.get(Calendar.MONTH) - start.get(Calendar.MONTH) + 12 * diffYears
    }

    private fun getPositionFromDay(timeInMillis: Long): Int {
        val diffMonthMax = getDiffMonths(mMinDate, mMaxDate)
        val diffMonth = getDiffMonths(mMinDate, getTempCalendarForTime(timeInMillis))
        return MathUtils.constrain(diffMonth, 0, diffMonthMax)
    }

    private fun getTempCalendarForTime(timeInMillis: Long): Calendar {
        if (mTempCalendar == null) {
            mTempCalendar = Calendar.getInstance()
        }
        mTempCalendar!!.timeInMillis = timeInMillis
        return mTempCalendar
    }

    interface OnDaySelectedListener {
        fun onDaySelected(view: DayPickerView, day: Calendar)
    }

    companion object {
        private val DEFAULT_START_YEAR = 1900
        private val DEFAULT_END_YEAR = 2100
    }
}