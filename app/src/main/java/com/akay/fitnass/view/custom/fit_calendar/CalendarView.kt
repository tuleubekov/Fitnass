package com.akay.fitnass.view.custom.fit_calendar

import android.annotation.*
import android.content.Context
import android.content.res.Configuration
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import com.akay.fitnass.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CalendarView : FrameLayout {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {

        val a = context.obtainStyledAttributes(
                attrs, R.styleable.CalendarView, defStyleAttr, defStyleRes)
        val mode = a.getInt(R.styleable.CalendarView_calendarViewMode, MODE_HOLO)
        a.recycle()

        mDelegate = CalendarViewMaterialDelegate(this, context, attrs, defStyleAttr, defStyleRes)
    }

    private var mDelegate: CalendarViewDelegate

    /**
     * Gets the number of weeks to be shown.
     *
     * @return The shown week count.
     *
     * @attr ref android.R.styleable#CalendarView_shownWeekCount
     */
    /**
     * Sets the number of weeks to be shown.
     *
     * @param count The shown week count.
     *
     * @attr ref android.R.styleable#CalendarView_shownWeekCount
     */
    var shownWeekCount: Int
        @Deprecated("No longer used by Material-style CalendarView.")
        get() = mDelegate.shownWeekCount
        @Deprecated("No longer used by Material-style CalendarView.")
        set(count) {
            mDelegate.shownWeekCount = count
        }

    /**
     * Gets the background color for the selected week.
     *
     * @return The week background color.
     *
     * @attr ref android.R.styleable#CalendarView_selectedWeekBackgroundColor
     */
    /**
     * Sets the background color for the selected week.
     *
     * @param color The week background color.
     *
     * @attr ref android.R.styleable#CalendarView_selectedWeekBackgroundColor
     */
    var selectedWeekBackgroundColor: Int
        @ColorInt
        @Deprecated("No longer used by Material-style CalendarView.")
        get() = mDelegate.selectedWeekBackgroundColor
        @Deprecated("No longer used by Material-style CalendarView.")
        set(@ColorInt color) {
            mDelegate.selectedWeekBackgroundColor = color
        }

    /**
     * Gets the color for the dates in the focused month.
     *
     * @return The focused month date color.
     *
     * @attr ref android.R.styleable#CalendarView_focusedMonthDateColor
     */
    /**
     * Sets the color for the dates of the focused month.
     *
     * @param color The focused month date color.
     *
     * @attr ref android.R.styleable#CalendarView_focusedMonthDateColor
     */
    var focusedMonthDateColor: Int
        @ColorInt
        @Deprecated("No longer used by Material-style CalendarView.")
        get() = mDelegate.focusedMonthDateColor
        @Deprecated("No longer used by Material-style CalendarView.")
        set(@ColorInt color) {
            mDelegate.focusedMonthDateColor = color
        }

    /**
     * Gets the color for the dates in a not focused month.
     *
     * @return A not focused month date color.
     *
     * @attr ref android.R.styleable#CalendarView_unfocusedMonthDateColor
     */
    /**
     * Sets the color for the dates of a not focused month.
     *
     * @param color A not focused month date color.
     *
     * @attr ref android.R.styleable#CalendarView_unfocusedMonthDateColor
     */
    var unfocusedMonthDateColor: Int
        @ColorInt
        @Deprecated("No longer used by Material-style CalendarView.")
        get() = mDelegate.unfocusedMonthDateColor
        @Deprecated("No longer used by Material-style CalendarView.")
        set(@ColorInt color) {
            mDelegate.unfocusedMonthDateColor = color
        }

    /**
     * Gets the color for the week numbers.
     *
     * @return The week number color.
     *
     * @attr ref android.R.styleable#CalendarView_weekNumberColor
     */
    /**
     * Sets the color for the week numbers.
     *
     * @param color The week number color.
     *
     * @attr ref android.R.styleable#CalendarView_weekNumberColor
     */
    var weekNumberColor: Int
        @ColorInt
        @Deprecated("No longer used by Material-style CalendarView.")
        get() = mDelegate.weekNumberColor
        @Deprecated("No longer used by Material-style CalendarView.")
        set(@ColorInt color) {
            mDelegate.weekNumberColor = color
        }

    /**
     * Gets the color for the separator line between weeks.
     *
     * @return The week separator color.
     *
     * @attr ref android.R.styleable#CalendarView_weekSeparatorLineColor
     */
    /**
     * Sets the color for the separator line between weeks.
     *
     * @param color The week separator color.
     *
     * @attr ref android.R.styleable#CalendarView_weekSeparatorLineColor
     */
    var weekSeparatorLineColor: Int
        @ColorInt
        @Deprecated("No longer used by Material-style CalendarView.")
        get() = mDelegate.weekSeparatorLineColor
        @Deprecated("No longer used by Material-style CalendarView.")
        set(@ColorInt color) {
            mDelegate.weekSeparatorLineColor = color
        }

    /**
     * Gets the drawable for the vertical bar shown at the beginning and at
     * the end of the selected date.
     *
     * @return The vertical bar drawable.
     */
    val selectedDateVerticalBar: Drawable
        @Deprecated("No longer used by Material-style CalendarView.")
        get() = mDelegate.selectedDateVerticalBar

    /**
     * Gets the text appearance for the week day abbreviation of the calendar header.
     *
     * @return The text appearance resource id.
     *
     * @attr ref android.R.styleable#CalendarView_weekDayTextAppearance
     */
    /**
     * Sets the text appearance for the week day abbreviation of the calendar header.
     *
     * @param resourceId The text appearance resource id.
     *
     * @attr ref android.R.styleable#CalendarView_weekDayTextAppearance
     */
    var weekDayTextAppearance: Int
        @StyleRes get() = mDelegate.weekDayTextAppearance
        set(@StyleRes resourceId) {
            mDelegate.weekDayTextAppearance = resourceId
        }

    /**
     * Gets the text appearance for the calendar dates.
     *
     * @return The text appearance resource id.
     *
     * @attr ref android.R.styleable#CalendarView_dateTextAppearance
     */
    /**
     * Sets the text appearance for the calendar dates.
     *
     * @param resourceId The text appearance resource id.
     *
     * @attr ref android.R.styleable#CalendarView_dateTextAppearance
     */
    var dateTextAppearance: Int
        @StyleRes get() = mDelegate.dateTextAppearance
        set(@StyleRes resourceId) {
            mDelegate.dateTextAppearance = resourceId
        }

    /**
     * Gets the minimal date supported by this [CalendarView] in milliseconds
     * since January 1, 1970 00:00:00 in [TimeZone.getDefault] time
     * zone.
     *
     *
     * Note: The default minimal date is 01/01/1900.
     *
     *
     *
     * @return The minimal supported date.
     *
     * @attr ref android.R.styleable#CalendarView_minDate
     */
    /**
     * Sets the minimal date supported by this [CalendarView] in milliseconds
     * since January 1, 1970 00:00:00 in [TimeZone.getDefault] time
     * zone.
     *
     * @param minDate The minimal supported date.
     *
     * @attr ref android.R.styleable#CalendarView_minDate
     */
    var minDate: Long
        get() = mDelegate.minDate
        set(minDate) {
            mDelegate.minDate = minDate
        }

    /**
     * Gets the maximal date supported by this [CalendarView] in milliseconds
     * since January 1, 1970 00:00:00 in [TimeZone.getDefault] time
     * zone.
     *
     *
     * Note: The default maximal date is 01/01/2100.
     *
     *
     *
     * @return The maximal supported date.
     *
     * @attr ref android.R.styleable#CalendarView_maxDate
     */
    /**
     * Sets the maximal date supported by this [CalendarView] in milliseconds
     * since January 1, 1970 00:00:00 in [TimeZone.getDefault] time
     * zone.
     *
     * @param maxDate The maximal supported date.
     *
     * @attr ref android.R.styleable#CalendarView_maxDate
     */
    var maxDate: Long
        get() = mDelegate.maxDate
        set(maxDate) {
            mDelegate.maxDate = maxDate
        }

    /**
     * Gets whether to show the week number.
     *
     * @return True if showing the week number.
     * @attr ref android.R.styleable#CalendarView_showWeekNumber
     */
    /**
     * Sets whether to show the week number.
     *
     * @param showWeekNumber True to show the week number.
     * @attr ref android.R.styleable#CalendarView_showWeekNumber
     */
    var showWeekNumber: Boolean
        @Deprecated("No longer used by Material-style CalendarView.\n" +
                "     \n" +
                "      ")
        get() = mDelegate.showWeekNumber
        @Deprecated("No longer used by Material-style CalendarView.\n" +
                "     \n" +
                "      ")
        set(showWeekNumber) {
            mDelegate.showWeekNumber = showWeekNumber
        }

    /**
     * Gets the first day of week.
     *
     * @return The first day of the week conforming to the [CalendarView]
     * APIs.
     * @see Calendar.MONDAY
     *
     * @see Calendar.TUESDAY
     *
     * @see Calendar.WEDNESDAY
     *
     * @see Calendar.THURSDAY
     *
     * @see Calendar.FRIDAY
     *
     * @see Calendar.SATURDAY
     *
     * @see Calendar.SUNDAY
     *
     *
     * @attr ref android.R.styleable#CalendarView_firstDayOfWeek
     */
    /**
     * Sets the first day of week.
     *
     * @param firstDayOfWeek The first day of the week conforming to the
     * [CalendarView] APIs.
     * @see Calendar.MONDAY
     *
     * @see Calendar.TUESDAY
     *
     * @see Calendar.WEDNESDAY
     *
     * @see Calendar.THURSDAY
     *
     * @see Calendar.FRIDAY
     *
     * @see Calendar.SATURDAY
     *
     * @see Calendar.SUNDAY
     *
     *
     * @attr ref android.R.styleable#CalendarView_firstDayOfWeek
     */
    var firstDayOfWeek: Int
        get() = mDelegate.firstDayOfWeek
        set(firstDayOfWeek) {
            mDelegate.firstDayOfWeek = firstDayOfWeek
        }

    /**
     * Gets the selected date in milliseconds since January 1, 1970 00:00:00 in
     * [TimeZone.getDefault] time zone.
     *
     * @return The selected date.
     */
    /**
     * Sets the selected date in milliseconds since January 1, 1970 00:00:00 in
     * [TimeZone.getDefault] time zone.
     *
     * @param date The selected date.
     *
     * @throws IllegalArgumentException of the provided date is before the
     * minimal or after the maximal date.
     *
     * @see .setDate
     * @see .setMinDate
     * @see .setMaxDate
     */
    var date: Long
        get() = mDelegate.date
        set(date) {
            mDelegate.date = date
        }

    /**
     * The callback used to indicate the user changes the date.
     */
    interface OnDateChangeListener {

        /**
         * Called upon change of the selected day.
         *
         * @param view The view associated with this listener.
         * @param year The year that was set.
         * @param month The month that was set [0-11].
         * @param dayOfMonth The day of the month that was set.
         */
        fun onSelectedDayChange(@NonNull view: CalendarView, year: Int, month: Int, dayOfMonth: Int)
    }

    init {

        val a = context.obtainStyledAttributes(
                attrs, R.styleable.CalendarView, defStyleAttr, defStyleRes)
        val mode = a.getInt(R.styleable.CalendarView_calendarViewMode, MODE_HOLO)
        a.recycle()

        mDelegate = CalendarViewMaterialDelegate(this, context, attrs, defStyleAttr, defStyleRes)

        when (mode) {
            MODE_MATERIAL -> mDelegate = CalendarViewMaterialDelegate(
                    this, context, attrs, defStyleAttr, defStyleRes)
            else -> throw IllegalArgumentException("invalid calendarViewMode attribute")
        }
    }

    /**
     * Sets the drawable for the vertical bar shown at the beginning and at
     * the end of the selected date.
     *
     * @param resourceId The vertical bar drawable resource id.
     *
     * @attr ref android.R.styleable#CalendarView_selectedDateVerticalBar
     */
    @Deprecated("No longer used by Material-style CalendarView.")
    fun setSelectedDateVerticalBar(@DrawableRes resourceId: Int) {
        mDelegate.setSelectedDateVerticalBar(resourceId)
    }

    /**
     * Sets the drawable for the vertical bar shown at the beginning and at
     * the end of the selected date.
     *
     * @param drawable The vertical bar drawable.
     *
     * @attr ref android.R.styleable#CalendarView_selectedDateVerticalBar
     */
    @Deprecated("No longer used by Material-style CalendarView.")
    fun setSelectedDateVerticalBar(drawable: Drawable) {
        mDelegate.setSelectedDateVerticalBar(drawable)
    }

    /**
     * Sets the listener to be notified upon selected date change.
     *
     * @param listener The listener to be notified.
     */
    fun setOnDateChangeListener(listener: OnDateChangeListener) {
        mDelegate.setOnDateChangeListener(listener)
    }

    /**
     * Sets the selected date in milliseconds since January 1, 1970 00:00:00 in
     * [TimeZone.getDefault] time zone.
     *
     * @param date The date.
     * @param animate Whether to animate the scroll to the current date.
     * @param center Whether to center the current date even if it is already visible.
     *
     * @throws IllegalArgumentException of the provided date is before the
     * minimal or after the maximal date.
     *
     * @see .setMinDate
     * @see .setMaxDate
     */
    fun setDate(date: Long, animate: Boolean, center: Boolean) {
        mDelegate.setDate(date, animate, center)
    }

    /**
     * Retrieves the screen bounds for the specific date in the coordinate system of this
     * view. If the passed date is being currently displayed, this method returns true and
     * the caller can query the fields of the passed [Rect] object. Otherwise the
     * method returns false and does not touch the passed [Rect] object.
     *
     * @hide
     */
    @TestApi
    fun getBoundsForDate(date: Long, outBounds: Rect): Boolean {
        return mDelegate.getBoundsForDate(date, outBounds)
    }

    protected override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mDelegate.onConfigurationChanged(newConfig)
    }

    override fun getAccessibilityClassName(): CharSequence {
        return CalendarView::class.java.name
    }

    /**
     * A delegate interface that defined the public API of the CalendarView. Allows different
     * CalendarView implementations. This would need to be implemented by the CalendarView delegates
     * for the real behavior.
     */
    private interface CalendarViewDelegate {
        var shownWeekCount: Int
        @get:ColorInt
        var selectedWeekBackgroundColor: Int
        @get:ColorInt
        var focusedMonthDateColor: Int
        @get:ColorInt
        var unfocusedMonthDateColor: Int
        @get:ColorInt
        var weekNumberColor: Int
        @get:ColorInt
        var weekSeparatorLineColor: Int
        val selectedDateVerticalBar: Drawable
        @get:StyleRes
        var weekDayTextAppearance: Int
        @get:StyleRes
        var dateTextAppearance: Int
        var minDate: Long
        var maxDate: Long
        var showWeekNumber: Boolean
        var firstDayOfWeek: Int
        var date: Long

        fun setSelectedDateVerticalBar(@DrawableRes resourceId: Int)
        fun setSelectedDateVerticalBar(drawable: Drawable)
        fun setDate(date: Long, animate: Boolean, center: Boolean)

        fun getBoundsForDate(date: Long, outBounds: Rect): Boolean

        fun setOnDateChangeListener(listener: OnDateChangeListener)

        fun onConfigurationChanged(newConfig: Configuration)
    }

    /**
     * An abstract class which can be used as a start for CalendarView implementations
     */
    internal abstract class AbstractCalendarViewDelegate(protected var mDelegator: CalendarView, protected var mContext: Context) : CalendarViewDelegate {
        protected var mCurrentLocale: Locale

        override// Deprecated.
        // Deprecated.
        var shownWeekCount: Int
            get() = 0
            set(count) {}

        override// Deprecated.
        var selectedWeekBackgroundColor: Int
            @ColorInt
            get() = 0
            set(@ColorInt color) {}

        override// Deprecated.
        var focusedMonthDateColor: Int
            @ColorInt
            get() = 0
            set(@ColorInt color) {}

        override// Deprecated.
        var unfocusedMonthDateColor: Int
            @ColorInt
            get() = 0
            set(@ColorInt color) {}

        override// Deprecated.
        // Deprecated.
        var weekNumberColor: Int
            @ColorInt
            get() = 0
            set(@ColorInt color) {}

        override// Deprecated.
        // Deprecated.
        var weekSeparatorLineColor: Int
            @ColorInt
            get() = 0
            set(@ColorInt color) {}

        override// Deprecated.
        val selectedDateVerticalBar: Drawable?
            get() = null

        override// Deprecated.
        // Deprecated.
        var showWeekNumber: Boolean
            get() = false
            set(showWeekNumber) {}

        init {

            // Initialization based on locale
            setCurrentLocale(Locale.getDefault())
        }

        protected fun setCurrentLocale(locale: Locale) {
            if (locale == mCurrentLocale) {
                return
            }
            mCurrentLocale = locale
        }

        override fun setSelectedDateVerticalBar(@DrawableRes resId: Int) {
            // Deprecated.
        }

        override fun setSelectedDateVerticalBar(drawable: Drawable) {
            // Deprecated.
        }

        override fun onConfigurationChanged(newConfig: Configuration) {
            // Nothing to do here, configuration changes are already propagated
            // by ViewGroup.
        }

        companion object {
            /** The default minimal date.  */
            protected val DEFAULT_MIN_DATE = "01/01/1900"

            /** The default maximal date.  */
            protected val DEFAULT_MAX_DATE = "01/01/2100"
        }
    }

    companion object {
        private val LOG_TAG = "CalendarView"

        private val MODE_MATERIAL = 1

        /** String for parsing dates.  */
        private val DATE_FORMAT = "MM/dd/yyyy"

        /** Date format for parsing dates.  */
        private val DATE_FORMATTER = SimpleDateFormat(DATE_FORMAT)

        /**
         * Utility method for the date format used by CalendarView's min/max date.
         *
         * @hide Use only as directed. For internal use only.
         */
        fun parseDate(date: String?, outDate: Calendar): Boolean {
            if (date == null || date.isEmpty()) {
                return false
            }

            try {
                val parsedDate = DATE_FORMATTER.parse(date)
                outDate.time = parsedDate
                return true
            } catch (e: ParseException) {
                Log.w(LOG_TAG, "Date: $date not in format: $DATE_FORMAT")
                return false
            }

        }
    }

}
