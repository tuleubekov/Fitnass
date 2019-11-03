package com.akay.fitnass.view.custom.calendar

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.akay.fitnass.extension.toLocaleDate
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.temporal.ChronoUnit

class MonthAdapter : RecyclerView.Adapter<MonthVH>() {
    private val minDate = DEFAULT_MIN_DATE.toLocaleDate()
    private val maxDate = DEFAULT_MAX_DATE.toLocaleDate()
    private val viewPool = RecyclerView.RecycledViewPool()
    private var count: Int = 0

    init {
        setRange(minDate, maxDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthVH {
        return MonthVH.create(parent)
    }

    private fun setRange(minDate: LocalDate, maxDate: LocalDate) {
        val diffMonth = ChronoUnit.MONTHS.between(minDate, maxDate)
        count = diffMonth.toInt() + 1
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MonthVH, position: Int) {
        val yearMonth = getYearMonth(position)
        holder.bind(yearMonth)
    }

    override fun getItemCount(): Int = count

    private fun getYearMonth(position: Int): YearMonth {
        val ym = YearMonth.of(getYearByPosition(position), getMonthByPosition(position))
        Log.e("______", "getYearMonth y= ${getYearByPosition(position)}, m.v= ${getMonthByPosition(position)}, pos= $position, m= ${ym.month}")
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