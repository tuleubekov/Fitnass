package com.akay.fitnass.view.custom.calendar

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.akay.fitnass.extension.toLocaleDate
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.temporal.ChronoUnit

class MonthAdapter : RecyclerView.Adapter<MonthVH>(), OnSnapPositionChangeListener {
    private val minDate = DEFAULT_MIN_DATE.toLocaleDate()
    private val maxDate = DEFAULT_MAX_DATE.toLocaleDate()
    private var count: Int = 0

    var onDateChangeListener: ((YearMonth) -> Unit) = {}

    lateinit var snapHelper: SnapHelper
    private lateinit var recycler: RecyclerView

    init {
        setRange(minDate, maxDate)
    }

    fun getCurrentYearMonth(): YearMonth {
        val pos = snapHelper.getSnapPosition(recycler)
        return getYearMonth(pos)
    }

    override fun onSnapPositionChange(position: Int) {
        onDateChangeListener.invoke(getYearMonth(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthVH {
        return MonthVH.create(parent)
    }

    override fun onBindViewHolder(holder: MonthVH, position: Int) {
        val yearMonth = getYearMonth(position)
        holder.bind(yearMonth)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        recycler = recyclerView
    }

    override fun getItemCount(): Int = count

    private fun setRange(minDate: LocalDate, maxDate: LocalDate) {
        val diffMonth = ChronoUnit.MONTHS.between(minDate, maxDate)
        count = diffMonth.toInt() + 1
        notifyDataSetChanged()
    }

    private fun getYearMonth(position: Int): YearMonth {
        return YearMonth.of(getYearByPosition(position), getMonthByPosition(position))
    }

    private fun getMonthByPosition(position: Int): Int {
        val res = (position + minDate.month.value) % 12
        return if (res % 12 == 0) 12 else res
    }

    private fun getYearByPosition(position: Int): Int {
        val yearOffset = (position + (minDate.month.value-1)) / 12
        return yearOffset + minDate.year
    }

    companion object {
        private const val DEFAULT_MIN_DATE = "01.01.1900"
        private const val DEFAULT_MAX_DATE = "01.01.2100"
    }

}