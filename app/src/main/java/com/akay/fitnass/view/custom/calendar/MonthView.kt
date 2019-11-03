package com.akay.fitnass.view.custom.calendar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.akay.fitnass.R
import com.akay.fitnass.extension.firstDay
import com.akay.fitnass.extension.previous
import com.akay.fitnass.extension.toLocaleDate
import com.akay.fitnass.extension.yearMonth
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import kotlin.math.abs

class MonthView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val weekRectPaint: Paint = Paint()
    private val dayRectPaint: Paint = Paint()
    private val dayTextPaint: Paint = Paint()
    private val weekTextPaint: Paint = Paint()
    private val dayOtherTextPaint: Paint = Paint()
    private val daySelectRectPaint: Paint = Paint()
    private var weekRect: Rect = Rect()

    private lateinit var date: YearMonth
    private lateinit var prevMonth: YearMonth
    private var monthLength: Int = -1
    private var inDayOffset: Int = -1
    private var outDayOffset: Int = -1
    private var selectedDay: Int = -1

    // Desired dimensions.
    private val mDesiredMonthHeight: Int
    private val mDesiredDayOfWeekHeight: Int
    private val mDesiredDayHeight: Int
    private val mDesiredCellWidth: Int
    private val mDesiredDaySelectorRadius: Int

    // Dimensions as laid out.
    private var mMonthHeight: Int = 0
    private var mDayOfWeekHeight: Int = 0
    private var mDayHeight: Int = 0
    private var mCellWidth: Int = 0
    private var mDaySelectorRadius: Int = 0

    private var mPaddedWidth: Int = 0
    private var mPaddedHeight: Int = 0

    init {
        val res = context.resources
        mDesiredMonthHeight = res.getDimensionPixelSize(R.dimen.date_picker_month_height)
        mDesiredDayOfWeekHeight = res.getDimensionPixelSize(R.dimen.date_picker_day_of_week_height)
        mDesiredDayHeight = res.getDimensionPixelSize(R.dimen.date_picker_day_height)
        mDesiredCellWidth = res.getDimensionPixelSize(R.dimen.date_picker_day_width)
        mDesiredDaySelectorRadius = res.getDimensionPixelSize(
            R.dimen.date_picker_day_selector_radius
        )

        initPaints()
    }

    private fun initPaints() {
        weekRectPaint.color = Color.TRANSPARENT

        dayRectPaint.color = Color.parseColor("#333333")
        dayRectPaint.strokeWidth = 5f

        daySelectRectPaint.style = Paint.Style.STROKE
        daySelectRectPaint.color = Color.WHITE
        daySelectRectPaint.strokeWidth = 5f

        weekTextPaint.textAlign = Paint.Align.CENTER
        weekTextPaint.color = Color.WHITE
        weekTextPaint.style = Paint.Style.FILL
        weekTextPaint.textSize = 20f

        dayTextPaint.textAlign = Paint.Align.CENTER
        dayTextPaint.color = Color.WHITE
        dayTextPaint.style = Paint.Style.FILL
        dayTextPaint.textSize = 20f

        dayOtherTextPaint.textAlign = Paint.Align.CENTER
        dayOtherTextPaint.color = Color.parseColor("#FF888888")
        dayOtherTextPaint.style = Paint.Style.FILL
        dayOtherTextPaint.textSize = 20f
    }

    public fun setDate(dateStr: String) {
        date = dateStr.toLocaleDate().yearMonth
        prevMonth = date.previous
        monthLength = date.lengthOfMonth()
        inDayOffset = findDayOffset()
        outDayOffset = CELL_IN_MONTH - (monthLength + inDayOffset)
        invalidate()
    }

    public fun setDate(dateYearMonth: YearMonth) {
        date = dateYearMonth
        prevMonth = date.previous
        monthLength = date.lengthOfMonth()
        inDayOffset = findDayOffset()
        outDayOffset = CELL_IN_MONTH - (monthLength + inDayOffset)
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val cellWidth = measuredWidth / DAYS_IN_WEEK

        val preferredHeight = (mDesiredDayHeight * MAX_WEEKS_IN_MONTH
                + mDesiredDayOfWeekHeight
                + paddingTop + paddingBottom)
        val preferredWidth = (cellWidth * DAYS_IN_WEEK
                + paddingLeft + paddingRight)
        val resolvedWidth = View.resolveSize(preferredWidth, widthMeasureSpec)
        val resolvedHeight = View.resolveSize(preferredHeight, heightMeasureSpec)

        setMeasuredDimension(resolvedWidth, resolvedHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (!changed) { return }

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
        mDayHeight = cellWidth
        mCellWidth = cellWidth
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.run {
            drawColor(Color.parseColor("#181717"))
            drawWeekLabels(this)
            drawDays(this)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = (event.x + 0.5f).toInt()
        val y = (event.y + 0.5f).toInt()
        val action = event.action
        when (action) {
            MotionEvent.ACTION_UP -> {
                val clickedDay: Int = getDayAtLocation(x, y)
                onDayClicked(clickedDay)
                invalidate()
            }
        }
        return true
    }

    private fun onDayClicked(day: Int): Boolean {
        if (!isValidDayOfMonth(day)) {
            return false
        }
        selectedDay = day
        invalidate()
        return true
    }

    private fun getDayAtLocation(x: Int, y: Int): Int {
        val paddedX = x - paddingLeft
        if (paddedX < 0 || paddedX >= mPaddedWidth) {
            return -1
        }

        val headerHeight = mDayOfWeekHeight
        val paddedY = y - paddingTop
        if (paddedY < headerHeight || paddedY >= mPaddedHeight) {
            return -1
        }

        val paddedXRtl: Int = paddedX

        val row = (paddedY - headerHeight) / mDayHeight
        val col = paddedXRtl * DAYS_IN_WEEK / mPaddedWidth
        val index = col + row * DAYS_IN_WEEK
        val day = index + 1 - findDayOffset()

        return day
    }

    private val weekDays = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")

    private fun drawWeekLabels(canvas: Canvas) {
        var currentX = 0
        val currentY = 0

        for (d in 0 until weekDays.size) {
            weekRect = Rect(currentX + 3, currentY,currentX + (mCellWidth - 3), mDesiredDayOfWeekHeight)
            canvas.drawRect(weekRect, weekRectPaint)
            currentX += mCellWidth

            val textBounds = Rect()
            val str = weekDays[d]
            weekTextPaint.getTextBounds(str, 0, str.length, textBounds)
            canvas.drawText(str, weekRect.centerX().toFloat(), (weekRect.centerY() + (textBounds.height() / 2)).toFloat(), weekTextPaint)
        }
    }

    private fun drawDays(canvas: Canvas) {
        var currentX = 0
        var currentY = weekRect.height()

        var actualDayText = 1

        for (i in 1..CELL_IN_MONTH) {
            val state = calcStateDay(i)
            val day = calcDay(state, i, if (state == STATE_ACTUAL_DAY) actualDayText++ else actualDayText)

            val rect = Rect(currentX + 3, currentY + 3, currentX + (mCellWidth-3), currentY + (mCellWidth-3))
            canvas.drawRect(rect, dayRectPaint)

            when(state) {
                STATE_IN_DAY -> drawInDay(canvas, day, rect)
                STATE_ACTUAL_DAY -> drawActualDay(canvas, day, rect)
                STATE_OUT_DAY -> drawOutDay(canvas, day, rect)
            }

            currentX += mCellWidth
            if (i % 7 == 0) {
                currentY += mCellWidth
                currentX = 0
            }
        }
    }

    private fun calcDay(state: Int, cellPos: Int, actualDay: Int): Int {
        return when(state) {
            STATE_IN_DAY -> calcDayForInMonth(cellPos)
            STATE_ACTUAL_DAY -> actualDay
            else -> calcDayForOutMonth(cellPos)
        }
    }

    private fun calcDayForInMonth(cellPos: Int): Int {
        return prevMonth.lengthOfMonth() - (inDayOffset - cellPos)
    }

    private fun calcDayForOutMonth(cellPos: Int): Int {
        return abs((CELL_IN_MONTH - outDayOffset) - cellPos)
    }

    private fun calcStateDay(cellPos: Int): Int {
        return when {
            isInDay(cellPos, inDayOffset) ->
                STATE_IN_DAY
            isCurrentMonthDay(cellPos, inDayOffset, monthLength + inDayOffset) ->
                STATE_ACTUAL_DAY
            else ->
                STATE_OUT_DAY
        }
    }

    private fun isValidDayOfMonth(day: Int): Boolean {
        return day in 1..monthLength
    }

    private fun drawInDay(canvas: Canvas, inDay: Int, dayRect: Rect) {
        canvas.drawDayText(inDay.toString(), dayRect, dayOtherTextPaint)
    }

    private fun drawActualDay(canvas: Canvas, day: Int, dayRect: Rect) {
        dayTextPaint.color = if (isToday(day)) Color.YELLOW else Color.WHITE
        if (selectedDay == day) canvas.drawRect(dayRect, daySelectRectPaint)
        canvas.drawDayText(day.toString(), dayRect, dayTextPaint)
    }

    private fun drawOutDay(canvas: Canvas, outDay: Int, dayRect: Rect) {
        canvas.drawDayText(outDay.toString(), dayRect, dayOtherTextPaint)
    }

    private fun Canvas.drawDayText(dayTxt: String, rect: Rect, textPaint: Paint) {
        val textBounds = Rect()
        textPaint.getTextBounds(dayTxt, 0, dayTxt.length, textBounds)

        val x = rect.centerX() + ((rect.right - rect.centerX()) / 2)
        val y = (rect.centerY() + (textBounds.height() / 2)) - ((rect.bottom - rect.centerY()) / 2)
        drawText(dayTxt, x.toFloat(), y.toFloat(), textPaint)
    }

    private fun isInDay(cellPos: Int, inDayOffset: Int): Boolean {
        return cellPos <= inDayOffset && inDayOffset > 0
    }

    private fun isCurrentMonthDay(cellPos: Int, curMonthStart: Int, curMonthEnd: Int): Boolean {
        return cellPos in (curMonthStart + 1)..curMonthEnd
    }

    private fun isOutDay(cellPos: Int, curMonthEnd: Int): Boolean {
        return cellPos > curMonthEnd
    }

    private fun isToday(day: Int): Boolean {
        return LocalDate.now() == LocalDate.of(date.year, date.month, day)
    }

    private fun findDayOffset(): Int {
        val weekStart = DayOfWeek.MONDAY.value
        val firstDayOfWeek = date.firstDay.dayOfWeek.value
        val offset = firstDayOfWeek - weekStart
        if (firstDayOfWeek < weekStart) {
            return offset + DAYS_IN_WEEK
        }
        return offset
    }

    private fun findRow(cellPos: Int): Int {
        return (cellPos.toDouble() / DAYS_IN_WEEK).ceil()
    }

    private fun findColumn(cellPos: Int): Int {
        val col = cellPos % DAYS_IN_WEEK
        if (col == 0) {
            return DAYS_IN_WEEK
        }
        return col
    }

    private fun Double.ceil(): Int = kotlin.math.ceil(this).toInt()

    companion object {
        private const val DAYS_IN_WEEK = 7
        private const val CELL_IN_MONTH = 42
        private const val MAX_WEEKS_IN_MONTH = 6

        private const val STATE_IN_DAY = -1
        private const val STATE_ACTUAL_DAY = 0
        private const val STATE_OUT_DAY = 1
    }
}