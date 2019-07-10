package com.akay.fitnass.view.activities

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.akay.fitnass.R
import com.akay.fitnass.data.model.Flight
import com.akay.fitnass.view.adapters.CalendarAdapter
import com.akay.fitnass.view.custom.calendar.model.CalendarDay
import com.akay.fitnass.view.custom.calendar.model.CalendarMonth
import com.akay.fitnass.view.custom.calendar.model.DayOwner
import com.akay.fitnass.view.custom.calendar.next
import com.akay.fitnass.view.custom.calendar.previous
import com.akay.fitnass.view.custom.calendar.ui.DayBinder
import com.akay.fitnass.view.custom.calendar.ui.MonthHeaderFooterBinder
import com.akay.fitnass.view.custom.calendar.ui.ViewContainer
import kotlinx.android.synthetic.main.activity_test_calendar.*
import kotlinx.android.synthetic.main.calendar_item_day.view.*
import kotlinx.android.synthetic.main.calendar_item_day_legend.view.*
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.WeekFields
import java.util.*

class TestCalendarActivity : AppCompatActivity() {
    private var selectedDate: LocalDate? = null
    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")

    private val flightsAdapter = CalendarAdapter()
    private val flights = generateFlights().groupBy { it.time.toLocalDate() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_calendar)

        exFiveRv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        exFiveRv.adapter = flightsAdapter
        exFiveRv.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
        flightsAdapter.notifyDataSetChanged()

        val daysOfWeek = daysOfWeekFromLocale()

        val currentMonth = YearMonth.now()
        exFiveCalendar.setup(currentMonth.minusMonths(5000), currentMonth.plusMonths(5000), daysOfWeek.first())
        exFiveCalendar.scrollToMonth(currentMonth)

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val textView = view.exFiveDayText
            val layout = view.exFiveDayLayout
            val flightTopView = view.exFiveDayFlightTop
            val flightBottomView = view.exFiveDayFlightBottom

            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        if (selectedDate != day.date) {
                            val oldDate = selectedDate
                            selectedDate = day.date
                            exFiveCalendar.notifyDateChanged(day.date)
                            oldDate?.let { exFiveCalendar.notifyDateChanged(it) }
                            updateAdapterForDate(day.date)
                        }
                    }
                }
            }
        }

        exFiveCalendar.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)

            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.textView
                val layout = container.layout
                textView.text = day.date.dayOfMonth.toString()

                val flightTopView = container.flightTopView
                val flightBottomView = container.flightBottomView

                flightTopView.background = null
                flightBottomView.background = null

                if (day.owner == DayOwner.THIS_MONTH) {
                    textView.setTextColor(ContextCompat.getColor(this@TestCalendarActivity, R.color.example_5_text_grey))
                    layout.setBackgroundResource(if (selectedDate == day.date) R.drawable.example5_selected_bg else 0)

                    val flights = flights[day.date]
                    if (flights != null) {
                        if (flights.count() == 1) {
                            flightBottomView.setBackgroundColor(ContextCompat.getColor(this@TestCalendarActivity, flights[0].color))
                        } else {
                            flightTopView.setBackgroundColor(ContextCompat.getColor(this@TestCalendarActivity, flights[0].color))
                            flightBottomView.setBackgroundColor(ContextCompat.getColor(this@TestCalendarActivity, flights[1].color))
                        }
                    }
                } else {
                    textView.setTextColor(ContextCompat.getColor(this@TestCalendarActivity, R.color.example_5_text_grey_light))
                    layout.background = null
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = view.legendLayout
        }
        exFiveCalendar.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                // Setup each header day text if we have not done that already.
                if (container.legendLayout.tag == null) {
                    container.legendLayout.tag = month.yearMonth
                    container.legendLayout.children.map { it as TextView }.forEachIndexed { index, tv ->
                        tv.text = daysOfWeek[index].name.take(3)
                        tv.setTextColorRes(R.color.example_5_text_grey)
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                    }
                    month.yearMonth
                }
            }
        }

        exFiveCalendar.monthScrollListener = { month ->
            val title = "${monthTitleFormatter.format(month.yearMonth)} ${month.yearMonth.year}"
            exFiveMonthYearText.text = title

            selectedDate?.let {
                // Clear selection if we scroll to a new month.
                selectedDate = null
                exFiveCalendar.notifyDateChanged(it)
                updateAdapterForDate(null)
            }
        }

        exFiveNextMonthImage.setOnClickListener {
            exFiveCalendar.findFirstVisibleMonth()?.let {
                exFiveCalendar.smoothScrollToMonth(it.yearMonth.next)
            }
        }

        exFivePreviousMonthImage.setOnClickListener {
            exFiveCalendar.findFirstVisibleMonth()?.let {
                exFiveCalendar.smoothScrollToMonth(it.yearMonth.previous)
            }
        }
    }

    private fun updateAdapterForDate(date: LocalDate?) {
        flightsAdapter.flights.clear()
        flightsAdapter.flights.addAll(flights[date].orEmpty())
        flightsAdapter.notifyDataSetChanged()
    }

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, TestCalendarActivity::class.java)
        }
    }
}

private typealias Airport = Flight.Airport

fun generateFlights(): List<Flight> {
    val list = mutableListOf<Flight>()
    val currentMonth = YearMonth.now()

    val currentMonth17 = currentMonth.atDay(17)
    list.add(Flight(currentMonth17.atTime(14, 0), Airport("Lagos", "LOS"), Airport("Abuja", "ABV"), R.color.brown_700))
    list.add(Flight(currentMonth17.atTime(21, 30), Airport("Enugu", "ENU"), Airport("Owerri", "QOW"), R.color.blue_grey_700))

    val currentMonth22 = currentMonth.atDay(22)
    list.add(Flight(currentMonth22.atTime(13, 20), Airport("Ibadan", "IBA"), Airport("Benin", "BNI"), R.color.blue_800))
    list.add(Flight(currentMonth22.atTime(17, 40), Airport("Sokoto", "SKO"), Airport("Ilorin", "ILR"), R.color.red_800))

    list.add(
            Flight(
                    currentMonth.atDay(3).atTime(20, 0),
                    Airport("Makurdi", "MDI"),
                    Airport("Calabar", "CBQ"),
                    R.color.teal_700
            )
    )

    list.add(
            Flight(
                    currentMonth.atDay(12).atTime(18, 15),
                    Airport("Kaduna", "KAD"),
                    Airport("Jos", "JOS"),
                    R.color.cyan_700
            )
    )

    val nextMonth13 = currentMonth.plusMonths(1).atDay(13)
    list.add(Flight(nextMonth13.atTime(7, 30), Airport("Kano", "KAN"), Airport("Akure", "AKR"), R.color.pink_700))
    list.add(Flight(nextMonth13.atTime(10, 50), Airport("Minna", "MXJ"), Airport("Zaria", "ZAR"), R.color.green_700))

    list.add(
            Flight(
                    currentMonth.minusMonths(1).atDay(9).atTime(20, 15),
                    Airport("Asaba", "ABB"),
                    Airport("Port Harcourt", "PHC"),
                    R.color.orange_800
            )
    )

    return list
}

internal inline fun Boolean?.orFalse(): Boolean = this ?: false

internal fun Context.getDrawableCompat(@DrawableRes drawable: Int) = ContextCompat.getDrawable(this, drawable)

internal fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)

internal fun TextView.setTextColorRes(@ColorRes color: Int) = setTextColor(context.getColorCompat(color))

fun daysOfWeekFromLocale(): Array<DayOfWeek> {
    val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
    var daysOfWeek = DayOfWeek.values()
    // Order `daysOfWeek` array so that firstDayOfWeek is at index 0.
    if (firstDayOfWeek != DayOfWeek.MONDAY) {
        val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
        val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
        daysOfWeek = rhs + lhs
    }
    return daysOfWeek
}

val ViewGroup.children: Sequence<View>
    get() = object : Sequence<View> {
        override fun iterator() = this@children.iterator()
    }

operator fun ViewGroup.iterator() = object : MutableIterator<View> {
    private var index = 0
    override fun hasNext() = index < childCount
    override fun next() = getChildAt(index++) ?: throw IndexOutOfBoundsException()
    override fun remove() = removeViewAt(--index)
}