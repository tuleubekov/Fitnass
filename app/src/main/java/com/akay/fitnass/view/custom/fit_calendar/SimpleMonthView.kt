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

import android.annotation.TargetApi
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.Paint.Style
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.TextPaint
import android.text.format.DateFormat
import android.util.AttributeSet
import android.util.IntArray
import android.util.StateSet
import android.view.MotionEvent
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction
import androidx.annotation.RequiresApi
import com.android.internal.R
import com.android.internal.widget.ExploreByTouchHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * A calendar-like view displaying a specified month and the appropriate selectable day numbers
 * within the specified month.
 */
class SimpleMonthView : View {
    private val mMonthPaint = TextPaint()
    private val mDayOfWeekPaint = TextPaint()
    private val mDayPaint = TextPaint()
    private val mDaySelectorPaint = Paint()
    private val mDayHighlightPaint = Paint()
    private val mCalendar = Calendar.getInstance()
    private val mDayOfWeekLabelCalendar = Calendar.getInstance()
    private val mTouchHelper: MonthViewTouchHelper
    private val mTitleFormatter: SimpleDateFormat
    private val mDayOfWeekFormatter: SimpleDateFormat
    // Desired dimensions.
    private val mDesiredMonthHeight: Int
    private val mDesiredDayOfWeekHeight: Int
    private val mDesiredDayHeight: Int
    private val mDesiredCellWidth: Int
    private val mDesiredDaySelectorRadius: Int
    // Next/previous drawables.
    private val mPrevDrawable: Drawable?
    private val mNextDrawable: Drawable?
    private val mPrevHitArea: Rect?
    private val mNextHitArea: Rect?
    private val mPrevContentDesc: CharSequence
    private val mNextContentDesc: CharSequence
    private var mTitle: CharSequence? = null
    private var mMonth: Int = 0
    private var mYear: Int = 0
    // Dimensions as laid out.
    private var mMonthHeight: Int = 0
    private var mDayOfWeekHeight: Int = 0
    private var mDayHeight: Int = 0
    private var mCellWidth: Int = 0
    private var mDaySelectorRadius: Int = 0
    private var mPaddedWidth: Int = 0
    private var mPaddedHeight: Int = 0
    /** The day of month for the selected day, or -1 if no day is selected.  */
    private var mActivatedDay = -1
    /**
     * The day of month for today, or -1 if the today is not in the current
     * month.
     */
    private var mToday = DEFAULT_SELECTED_DAY
    /** The first day of the week (ex. Calendar.SUNDAY).  */
    private var mWeekStart = DEFAULT_WEEK_START
    /** The number of days (ex. 28) in the current month.  */
    private var mDaysInMonth: Int = 0
    /**
     * The day of week (ex. Calendar.SUNDAY) for the first day of the current
     * month.
     */
    private var mDayOfWeekStart: Int = 0
    /** The day of month for the first (inclusive) enabled day.  */
    private var mEnabledDayStart = 1
    /** The day of month for the last (inclusive) enabled day.  */
    private var mEnabledDayEnd = 31
    /** The number of week rows needed to display the current month.  */
    private val mNumWeeks = MAX_WEEKS_IN_MONTH
    /** Optional listener for handling day click actions.  */
    private var mOnDayClickListener: OnDayClickListener? = null
    private var mDayTextColor: ColorStateList? = null
    private var mTouchedItem = -1
    private var mPrevEnabled: Boolean = false
    private var mNextEnabled: Boolean = false
    val title: CharSequence
        get() {
            if (mTitle == null) {
                mTitle = mTitleFormatter.format(mCalendar.time)
            }
            return mTitle
        }

    constructor(context: Context): super(context, null)

    constructor(context: Context, attr: AttributeSet): super(context, attr, R.attr.datePickerStyle)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int): super(context, attr, defStyleAttr, 0)

    init {
        val res = context.resources
        mDesiredMonthHeight = res.getDimensionPixelSize(R.dimen.date_picker_month_height)
        mDesiredDayOfWeekHeight = res.getDimensionPixelSize(R.dimen.date_picker_day_of_week_height)
        mDesiredDayHeight = res.getDimensionPixelSize(R.dimen.date_picker_day_height)
        mDesiredCellWidth = res.getDimensionPixelSize(R.dimen.date_picker_day_width)
        mDesiredDaySelectorRadius = res.getDimensionPixelSize(R.dimen.date_picker_day_selector_radius)
        mPrevDrawable = context.getDrawable(R.drawable.ic_chevron_left)
        mNextDrawable = context.getDrawable(R.drawable.ic_chevron_right)
        mPrevHitArea = if (mPrevDrawable != null) Rect() else null
        mNextHitArea = if (mNextDrawable != null) Rect() else null
        mPrevContentDesc = res.getText(R.string.date_picker_prev_month_button)
        mNextContentDesc = res.getText(R.string.date_picker_next_month_button)
        // Set up accessibility components.
        mTouchHelper = MonthViewTouchHelper(this)
        setAccessibilityDelegate(mTouchHelper)
        importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        val locale = res.configuration.locale
        val titleFormat = DateFormat.getBestDateTimePattern(locale, DEFAULT_TITLE_FORMAT)
        mTitleFormatter = SimpleDateFormat(titleFormat, locale)
        mDayOfWeekFormatter = SimpleDateFormat(DAY_OF_WEEK_FORMAT, locale)
        isClickable = true
        initPaints(res)
    }

    fun setNextEnabled(enabled: Boolean) {
        mNextEnabled = enabled
        mTouchHelper.invalidateRoot()
        invalidate()
    }

    fun setPrevEnabled(enabled: Boolean) {
        mPrevEnabled = enabled
        mTouchHelper.invalidateRoot()
        invalidate()
    }

    /**
     * Applies the specified text appearance resource to a paint, returning the
     * text color if one is set in the text appearance.
     *
     * @param p the paint to modify
     * @param resId the resource ID of the text appearance
     * @return the text color, if available
     */
    private fun applyTextAppearance(p: Paint, resId: Int): ColorStateList? {
        val ta = context.obtainStyledAttributes(null, R.styleable.TextAppearance, 0, resId)
        val fontFamily = ta.getString(R.styleable.TextAppearance_fontFamily)
        if (fontFamily != null) {
            p.typeface = Typeface.create(fontFamily, 0)
        }
        p.textSize = ta.getDimensionPixelSize(R.styleable.TextAppearance_textSize, p.textSize.toInt()).toFloat()
        val textColor = ta.getColorStateList(R.styleable.TextAppearance_textColor)
        if (textColor != null) {
            val enabledColor = textColor!!.getColorForState(View.ENABLED_STATE_SET, 0)
            p.color = enabledColor
        }
        ta.recycle()
        return textColor
    }

    fun setMonthTextAppearance(resId: Int) {
        val monthColor = applyTextAppearance(mMonthPaint, resId)
        if (monthColor != null) {
            mPrevDrawable?.setTintList(monthColor)
            mNextDrawable?.setTintList(monthColor)
        }
        invalidate()
    }

    fun setDayOfWeekTextAppearance(resId: Int) {
        applyTextAppearance(mDayOfWeekPaint, resId)
        invalidate()
    }

    fun setDayTextAppearance(resId: Int) {
        val textColor = applyTextAppearance(mDayPaint, resId)
        if (textColor != null) {
            mDayTextColor = textColor
        }
        invalidate()
    }

    /**
     * Sets up the text and style properties for painting.
     */
    private fun initPaints(res: Resources) {
        val monthTypeface = res.getString(R.string.date_picker_month_typeface)
        val dayOfWeekTypeface = res.getString(R.string.date_picker_day_of_week_typeface)
        val dayTypeface = res.getString(R.string.date_picker_day_typeface)
        val monthTextSize = res.getDimensionPixelSize(
                R.dimen.date_picker_month_text_size)
        val dayOfWeekTextSize = res.getDimensionPixelSize(
                R.dimen.date_picker_day_of_week_text_size)
        val dayTextSize = res.getDimensionPixelSize(
                R.dimen.date_picker_day_text_size)
        mMonthPaint.isAntiAlias = true
        mMonthPaint.textSize = monthTextSize.toFloat()
        mMonthPaint.typeface = Typeface.create(monthTypeface, 0)
        mMonthPaint.textAlign = Align.CENTER
        mMonthPaint.style = Style.FILL
        mDayOfWeekPaint.isAntiAlias = true
        mDayOfWeekPaint.textSize = dayOfWeekTextSize.toFloat()
        mDayOfWeekPaint.typeface = Typeface.create(dayOfWeekTypeface, 0)
        mDayOfWeekPaint.textAlign = Align.CENTER
        mDayOfWeekPaint.style = Style.FILL
        mDaySelectorPaint.isAntiAlias = true
        mDaySelectorPaint.style = Style.FILL
        mDayHighlightPaint.isAntiAlias = true
        mDayHighlightPaint.style = Style.FILL
        mDayPaint.isAntiAlias = true
        mDayPaint.textSize = dayTextSize.toFloat()
        mDayPaint.typeface = Typeface.create(dayTypeface, 0)
        mDayPaint.textAlign = Align.CENTER
        mDayPaint.style = Style.FILL
    }

    fun setMonthTextColor(monthTextColor: ColorStateList) {
        val enabledColor = monthTextColor.getColorForState(View.ENABLED_STATE_SET, 0)
        mMonthPaint.color = enabledColor
        invalidate()
    }

    fun setDayOfWeekTextColor(dayOfWeekTextColor: ColorStateList) {
        val enabledColor = dayOfWeekTextColor.getColorForState(View.ENABLED_STATE_SET, 0)
        mDayOfWeekPaint.color = enabledColor
        invalidate()
    }

    fun setDayTextColor(dayTextColor: ColorStateList) {
        mDayTextColor = dayTextColor
        invalidate()
    }

    fun setDaySelectorColor(dayBackgroundColor: ColorStateList) {
        val activatedColor = dayBackgroundColor.getColorForState(
                StateSet.get(StateSet.VIEW_STATE_ENABLED or StateSet.VIEW_STATE_ACTIVATED), 0)
        mDaySelectorPaint.color = activatedColor
        invalidate()
    }

    fun setDayHighlightColor(dayHighlightColor: ColorStateList) {
        val pressedColor = dayHighlightColor.getColorForState(
                StateSet.get(StateSet.VIEW_STATE_ENABLED or StateSet.VIEW_STATE_PRESSED), 0)
        mDayHighlightPaint.color = pressedColor
        invalidate()
    }

    fun setOnDayClickListener(listener: OnDayClickListener) {
        mOnDayClickListener = listener
    }

    public override fun dispatchHoverEvent(event: MotionEvent): Boolean {
        // First right-of-refusal goes the touch exploration helper.
        return mTouchHelper.dispatchHoverEvent(event) || super.dispatchHoverEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = (event.x + 0.5f).toInt()
        val y = (event.y + 0.5f).toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val touchedItem = getItemAtLocation(x, y)
                if (mTouchedItem != touchedItem) {
                    mTouchedItem = touchedItem
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                val clickedItem = getItemAtLocation(x, y)
                onItemClicked(clickedItem, true)
                // Reset touched day on stream end.
                mTouchedItem = -1
                invalidate()
            }
            // Fall through.
            MotionEvent.ACTION_CANCEL -> {
                mTouchedItem = -1
                invalidate()
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        canvas.translate(paddingLeft.toFloat(), paddingTop.toFloat())
        drawMonth(canvas)
        drawDaysOfWeek(canvas)
        drawDays(canvas)
        drawButtons(canvas)
        canvas.translate((-paddingLeft).toFloat(), (-paddingTop).toFloat())
    }

    private fun drawMonth(canvas: Canvas) {
        val x = mPaddedWidth / 2f
        // Vertically centered within the month header height.
        val lineHeight = mMonthPaint.ascent() + mMonthPaint.descent()
        val y = (mMonthHeight - lineHeight) / 2f
        canvas.drawText(title.toString(), x, y, mMonthPaint)
    }

    private fun drawDaysOfWeek(canvas: Canvas) {
        val p = mDayOfWeekPaint
        val headerHeight = mMonthHeight
        val rowHeight = mDayOfWeekHeight
        val colWidth = mCellWidth
        // Text is vertically centered within the day of week height.
        val halfLineHeight = (p.ascent() + p.descent()) / 2f
        val rowCenter = headerHeight + rowHeight / 2
        for (col in 0 until DAYS_IN_WEEK) {
            val colCenter = colWidth * col + colWidth / 2
            val dayOfWeek = (col + mWeekStart) % DAYS_IN_WEEK
            val label = getDayOfWeekLabel(dayOfWeek)
            canvas.drawText(label, colCenter.toFloat(), rowCenter - halfLineHeight, p)
        }
    }

    private fun getDayOfWeekLabel(dayOfWeek: Int): String {
        mDayOfWeekLabelCalendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)
        return mDayOfWeekFormatter.format(mDayOfWeekLabelCalendar.time)
    }

    /**
     * Draws the month days.
     */
    private fun drawDays(canvas: Canvas) {
        val p = mDayPaint
        val headerHeight = mMonthHeight + mDayOfWeekHeight
        val rowHeight = mDayHeight
        val colWidth = mCellWidth
        // Text is vertically centered within the row height.
        val halfLineHeight = (p.ascent() + p.descent()) / 2f
        var rowCenter = headerHeight + rowHeight / 2
        var day = 1
        var col = findDayOffset()
        while (day <= mDaysInMonth) {
            val colCenter = colWidth * col + colWidth / 2
            var stateMask = 0
            if (day >= mEnabledDayStart && day <= mEnabledDayEnd) {
                stateMask = stateMask or StateSet.VIEW_STATE_ENABLED
            }
            val isDayActivated = mActivatedDay == day
            if (isDayActivated) {
                stateMask = stateMask or StateSet.VIEW_STATE_ACTIVATED
                // Adjust the circle to be centered on the row.
                canvas.drawCircle(colCenter.toFloat(), rowCenter.toFloat(), mDaySelectorRadius.toFloat(), mDaySelectorPaint)
            } else if (mTouchedItem == day) {
                stateMask = stateMask or StateSet.VIEW_STATE_PRESSED
                // Adjust the circle to be centered on the row.
                canvas.drawCircle(colCenter.toFloat(), rowCenter.toFloat(), mDaySelectorRadius.toFloat(), mDayHighlightPaint)
            }
            val isDayToday = mToday == day
            val dayTextColor: Int
            if (isDayToday && !isDayActivated) {
                dayTextColor = mDaySelectorPaint.color
            } else {
                val stateSet = StateSet.get(stateMask)
                dayTextColor = mDayTextColor!!.getColorForState(stateSet, 0)
            }
            p.color = dayTextColor
            canvas.drawText(Integer.toString(day), colCenter.toFloat(), rowCenter - halfLineHeight, p)
            col++
            if (col == DAYS_IN_WEEK) {
                col = 0
                rowCenter += rowHeight
            }
            day++
        }
    }

    private fun drawButtons(canvas: Canvas) {
        if (mPrevEnabled && mPrevDrawable != null) {
            mPrevDrawable.draw(canvas)
        }
        if (mNextEnabled && mNextDrawable != null) {
            mNextDrawable.draw(canvas)
        }
    }

    /**
     * Sets the selected day.
     *
     * @param dayOfMonth the selected day of the month, or `-1` to clear
     * the selection
     */
    fun setSelectedDay(dayOfMonth: Int) {
        mActivatedDay = dayOfMonth
        // Invalidate cached accessibility information.
        mTouchHelper.invalidateRoot()
        invalidate()
    }

    /**
     * Sets the first day of the week.
     *
     * @param weekStart which day the week should start on, valid values are
     * [Calendar.SUNDAY] through [Calendar.SATURDAY]
     */
    fun setFirstDayOfWeek(weekStart: Int) {
        if (isValidDayOfWeek(weekStart)) {
            mWeekStart = weekStart
        } else {
            mWeekStart = mCalendar.firstDayOfWeek
        }
        // Invalidate cached accessibility information.
        mTouchHelper.invalidateRoot()
        invalidate()
    }

    /**
     * Sets all the parameters for displaying this week.
     *
     *
     * Parameters have a default value and will only update if a new value is
     * included, except for focus month, which will always default to no focus
     * month if no value is passed in. The only required parameter is the week
     * start.
     *
     * @param selectedDay the selected day of the month, or -1 for no selection
     * @param month the month
     * @param year the year
     * @param weekStart which day the week should start on, valid values are
     * [Calendar.SUNDAY] through [Calendar.SATURDAY]
     * @param enabledDayStart the first enabled day
     * @param enabledDayEnd the last enabled day
     */
    fun setMonthParams(selectedDay: Int, month: Int, year: Int, weekStart: Int, enabledDayStart: Int,
                       enabledDayEnd: Int) {
        mActivatedDay = selectedDay
        if (isValidMonth(month)) {
            mMonth = month
        }
        mYear = year
        mCalendar.set(Calendar.MONTH, mMonth)
        mCalendar.set(Calendar.YEAR, mYear)
        mCalendar.set(Calendar.DAY_OF_MONTH, 1)
        mDayOfWeekStart = mCalendar.get(Calendar.DAY_OF_WEEK)
        if (isValidDayOfWeek(weekStart)) {
            mWeekStart = weekStart
        } else {
            mWeekStart = mCalendar.firstDayOfWeek
        }
        if (enabledDayStart > 0 && enabledDayEnd < 32) {
            mEnabledDayStart = enabledDayStart
        }
        if (enabledDayEnd > 0 && enabledDayEnd < 32 && enabledDayEnd >= enabledDayStart) {
            mEnabledDayEnd = enabledDayEnd
        }
        // Figure out what day today is.
        val today = Calendar.getInstance()
        mToday = -1
        mDaysInMonth = getDaysInMonth(mMonth, mYear)
        for (i in 0 until mDaysInMonth) {
            val day = i + 1
            if (sameDay(day, today)) {
                mToday = day
            }
        }
        // Invalidate the old title.
        mTitle = null
        // Invalidate cached accessibility information.
        mTouchHelper.invalidateRoot()
    }

    private fun sameDay(day: Int, today: Calendar): Boolean {
        return (mYear == today.get(Calendar.YEAR) && mMonth == today.get(Calendar.MONTH)
                && day == today.get(Calendar.DAY_OF_MONTH))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val preferredHeight = (mDesiredDayHeight * MAX_WEEKS_IN_MONTH
                + mDesiredDayOfWeekHeight + mDesiredMonthHeight
                + paddingTop + paddingBottom)
        val preferredWidth = (mDesiredCellWidth * DAYS_IN_WEEK
                + paddingStart + paddingEnd)
        val resolvedWidth = View.resolveSize(preferredWidth, widthMeasureSpec)
        val resolvedHeight = View.resolveSize(preferredHeight, heightMeasureSpec)
        setMeasuredDimension(resolvedWidth, resolvedHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (!changed) {
            return
        }
        // Let's initialize a completely reasonable number of variables.
        val w = right - left
        val h = bottom - top
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom
        val paddedRight = w - paddingRight
        val paddedBottom = h - paddingBottom
        val paddedWidth = paddedRight - paddingLeft
        val paddedHeight = paddedBottom - paddingTop
        if (paddedWidth == mPaddedWidth || paddedHeight == mPaddedHeight) {
            return
        }
        mPaddedWidth = paddedWidth
        mPaddedHeight = paddedHeight
        // We may have been laid out smaller than our preferred size. If so,
        // scale all dimensions to fit.
        val measuredPaddedHeight = measuredHeight - paddingTop - paddingBottom
        val scaleH = paddedHeight / measuredPaddedHeight.toFloat()
        val monthHeight = (mDesiredMonthHeight * scaleH).toInt()
        val cellWidth = mPaddedWidth / DAYS_IN_WEEK
        mMonthHeight = monthHeight
        mDayOfWeekHeight = (mDesiredDayOfWeekHeight * scaleH).toInt()
        mDayHeight = (mDesiredDayHeight * scaleH).toInt()
        mCellWidth = cellWidth
        // Compute the largest day selector radius that's still within the clip
        // bounds and desired selector radius.
        val maxSelectorWidth = cellWidth / 2 + Math.min(paddingLeft, paddingRight)
        val maxSelectorHeight = mDayHeight / 2 + paddingBottom
        mDaySelectorRadius = Math.min(mDesiredDaySelectorRadius,
                Math.min(maxSelectorWidth, maxSelectorHeight))
        // Vertically center the previous/next drawables within the month
        // header, horizontally center within the day cell, then expand the
        // hit area to ensure it's at least 48x48dp.
        val prevDrawable = mPrevDrawable
        if (prevDrawable != null) {
            val dW = prevDrawable.intrinsicWidth
            val dH = prevDrawable.intrinsicHeight
            val iconTop = (monthHeight - dH) / 2
            val iconLeft = (cellWidth - dW) / 2
            // Button bounds don't include padding, but hit area does.
            prevDrawable.setBounds(iconLeft, iconTop, iconLeft + dW, iconTop + dH)
            mPrevHitArea!!.set(0, 0, paddingLeft + cellWidth, paddingTop + monthHeight)
        }
        val nextDrawable = mNextDrawable
        if (nextDrawable != null) {
            val dW = nextDrawable.intrinsicWidth
            val dH = nextDrawable.intrinsicHeight
            val iconTop = (monthHeight - dH) / 2
            val iconRight = paddedWidth - (cellWidth - dW) / 2
            // Button bounds don't include padding, but hit area does.
            nextDrawable.setBounds(iconRight - dW, iconTop, iconRight, iconTop + dH)
            mNextHitArea!!.set(paddedRight - cellWidth, 0, w, paddingTop + monthHeight)
        }
        // Invalidate cached accessibility information.
        mTouchHelper.invalidateRoot()
    }

    private fun findDayOffset(): Int {
        val offset = mDayOfWeekStart - mWeekStart
        return if (mDayOfWeekStart < mWeekStart) {
            offset + DAYS_IN_WEEK
        } else offset
    }

    /**
     * Calculates the day of the month or item identifier at the specified
     * touch position. Returns the day of the month or -1 if the position
     * wasn't in a valid day.
     *
     * @param x the x position of the touch event
     * @param y the y position of the touch event
     * @return the day of the month at (x, y), an item identifier, or -1 if the
     * position wasn't in a valid day or item
     */
    private fun getItemAtLocation(x: Int, y: Int): Int {
        if (mNextEnabled && mNextDrawable != null && mNextHitArea!!.contains(x, y)) {
            return ITEM_ID_NEXT
        } else if (mPrevEnabled && mPrevDrawable != null && mPrevHitArea!!.contains(x, y)) {
            return ITEM_ID_PREV
        }
        val paddedX = x - paddingLeft
        if (paddedX < 0 || paddedX >= mPaddedWidth) {
            return -1
        }
        val headerHeight = mMonthHeight + mDayOfWeekHeight
        val paddedY = y - paddingTop
        if (paddedY < headerHeight || paddedY >= mPaddedHeight) {
            return -1
        }
        val row = (paddedY - headerHeight) / mDayHeight
        val col = paddedX * DAYS_IN_WEEK / mPaddedWidth
        val index = col + row * DAYS_IN_WEEK
        val day = index + 1 - findDayOffset()
        return if (day < 1 || day > mDaysInMonth) {
            -1
        } else day
    }

    /**
     * Calculates the bounds of the specified day.
     *
     * @param id the day of the month, or an item identifier
     * @param outBounds the rect to populate with bounds
     */
    private fun getBoundsForItem(id: Int, outBounds: Rect): Boolean {
        if (mNextEnabled && id == ITEM_ID_NEXT) {
            if (mNextDrawable != null) {
                outBounds.set(mNextHitArea)
                return true
            }
        } else if (mPrevEnabled && id == ITEM_ID_PREV) {
            if (mPrevDrawable != null) {
                outBounds.set(mPrevHitArea)
                return true
            }
        }
        if (id < 1 || id > mDaysInMonth) {
            return false
        }
        val index = id - 1 + findDayOffset()
        // Compute left edge.
        val col = index % DAYS_IN_WEEK
        val colWidth = mCellWidth
        val left = paddingLeft + col * colWidth
        // Compute top edge.
        val row = index / DAYS_IN_WEEK
        val rowHeight = mDayHeight
        val headerHeight = mMonthHeight + mDayOfWeekHeight
        val top = paddingTop + headerHeight + row * rowHeight
        outBounds.set(left, top, left + colWidth, top + rowHeight)
        return true
    }

    /**
     * Called when an item is clicked.
     *
     * @param id the day number or item identifier
     */
    private fun onItemClicked(id: Int, animate: Boolean): Boolean {
        return onNavigationClicked(id, animate) || onDayClicked(id)
    }

    /**
     * Called when the user clicks on a day. Handles callbacks to the
     * [OnDayClickListener] if one is set.
     *
     * @param day the day that was clicked
     */
    private fun onDayClicked(day: Int): Boolean {
        if (day < 0 || day > mDaysInMonth) {
            return false
        }
        if (mOnDayClickListener != null) {
            val date = Calendar.getInstance()
            date.set(mYear, mMonth, day)
            mOnDayClickListener!!.onDayClick(this, date)
        }
        // This is a no-op if accessibility is turned off.
        mTouchHelper.sendEventForVirtualView(day, AccessibilityEvent.TYPE_VIEW_CLICKED)
        return true
    }

    /**
     * Called when the user clicks on a navigation button. Handles callbacks to
     * the [OnDayClickListener] if one is set.
     *
     * @param id the item identifier
     */
    private fun onNavigationClicked(id: Int, animate: Boolean): Boolean {
        val direction: Int
        if (id == ITEM_ID_NEXT) {
            direction = 1
        } else if (id == ITEM_ID_PREV) {
            direction = -1
        } else {
            return false
        }
        if (mOnDayClickListener != null) {
            mOnDayClickListener!!.onNavigationClick(this, direction, animate)
        }
        // This is a no-op if accessibility is turned off.
        mTouchHelper.sendEventForVirtualView(id, AccessibilityEvent.TYPE_VIEW_CLICKED)
        return true
    }

    /**
     * Provides a virtual view hierarchy for interfacing with an accessibility
     * service.
     */
    private inner class MonthViewTouchHelper(host: View) : ExploreByTouchHelper(host) {
        private val mTempRect = Rect()
        private val mTempCalendar = Calendar.getInstance()
        protected fun getVirtualViewAt(x: Float, y: Float): Int {
            val day = getItemAtLocation((x + 0.5f).toInt(), (y + 0.5f).toInt())
            return if (day >= 0) {
                day
            } else ExploreByTouchHelper.INVALID_ID
        }

        protected fun getVisibleVirtualViews(virtualViewIds: IntArray) {
            if (mNextEnabled && mNextDrawable != null) {
                virtualViewIds.add(ITEM_ID_PREV)
            }
            if (mPrevEnabled && mPrevDrawable != null) {
                virtualViewIds.add(ITEM_ID_NEXT)
            }
            for (day in 1..mDaysInMonth) {
                virtualViewIds.add(day)
            }
        }

        protected fun onPopulateEventForVirtualView(virtualViewId: Int, event: AccessibilityEvent) {
            event.contentDescription = getItemDescription(virtualViewId)
        }

        protected fun onPopulateNodeForVirtualView(virtualViewId: Int, node: AccessibilityNodeInfo) {
            val hasBounds = getBoundsForItem(virtualViewId, mTempRect)
            if (!hasBounds) {
                // The day is invalid, kill the node.
                mTempRect.setEmpty()
                node.contentDescription = ""
                node.setBoundsInParent(mTempRect)
                node.isVisibleToUser = false
                return
            }
            node.text = getItemText(virtualViewId)
            node.contentDescription = getItemDescription(virtualViewId)
            node.setBoundsInParent(mTempRect)
            node.addAction(AccessibilityAction.ACTION_CLICK)
            if (virtualViewId == mActivatedDay) {
                // TODO: This should use activated once that's supported.
                node.isChecked = true
            }
        }

        protected fun onPerformActionForVirtualView(virtualViewId: Int, action: Int,
                                                    arguments: Bundle): Boolean {
            when (action) {
                AccessibilityNodeInfo.ACTION_CLICK -> return onItemClicked(virtualViewId, false)
            }
            return false
        }

        /**
         * Generates a description for a given virtual view.
         *
         * @param id the day or item identifier to generate a description for
         * @return a description of the virtual view
         */
        private fun getItemDescription(id: Int): CharSequence {
            if (id == ITEM_ID_NEXT) {
                return mNextContentDesc
            } else if (id == ITEM_ID_PREV) {
                return mPrevContentDesc
            } else if (id >= 1 && id <= mDaysInMonth) {
                mTempCalendar.set(mYear, mMonth, id)
                return DateFormat.format(DATE_FORMAT, mTempCalendar.timeInMillis)
            }
            return ""
        }

        /**
         * Generates displayed text for a given virtual view.
         *
         * @param id the day or item identifier to generate text for
         * @return the visible text of the virtual view
         */
        private fun getItemText(id: Int): CharSequence? {
            if (id == ITEM_ID_NEXT || id == ITEM_ID_PREV) {
                return null
            } else if (id >= 1 && id <= mDaysInMonth) {
                return Integer.toString(id)
            }
            return null
        }

        companion object {
            private val DATE_FORMAT = "dd MMMM yyyy"
        }
    }

    /**
     * Handles callbacks when the user clicks on a time object.
     */
    interface OnDayClickListener {
        fun onDayClick(view: SimpleMonthView, day: Calendar)
        fun onNavigationClick(view: SimpleMonthView, direction: Int, animate: Boolean)
    }

    companion object {
        private val DAYS_IN_WEEK = 7
        private val MAX_WEEKS_IN_MONTH = 6
        private val DEFAULT_SELECTED_DAY = -1
        private val DEFAULT_WEEK_START = Calendar.SUNDAY
        private val DEFAULT_TITLE_FORMAT = "MMMMy"
        private val DAY_OF_WEEK_FORMAT = "EEEEE"
        /** Virtual view ID for previous button.  */
        private val ITEM_ID_PREV = 0x101
        /** Virtual view ID for next button.  */
        private val ITEM_ID_NEXT = 0x100

        private fun isValidDayOfWeek(day: Int): Boolean {
            return day >= Calendar.SUNDAY && day <= Calendar.SATURDAY
        }

        private fun isValidMonth(month: Int): Boolean {
            return month >= Calendar.JANUARY && month <= Calendar.DECEMBER
        }

        private fun getDaysInMonth(month: Int, year: Int): Int {
            when (month) {
                Calendar.JANUARY, Calendar.MARCH, Calendar.MAY, Calendar.JULY, Calendar.AUGUST, Calendar.OCTOBER, Calendar.DECEMBER -> return 31
                Calendar.APRIL, Calendar.JUNE, Calendar.SEPTEMBER, Calendar.NOVEMBER -> return 30
                Calendar.FEBRUARY -> return if (year % 4 == 0) 29 else 28
                else -> throw IllegalArgumentException("Invalid Month")
            }
        }
    }
}