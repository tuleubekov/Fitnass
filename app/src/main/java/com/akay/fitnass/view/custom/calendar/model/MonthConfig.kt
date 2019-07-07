package com.akay.fitnass.view.custom.calendar.model

import com.akay.fitnass.view.custom.calendar.next
import com.akay.fitnass.view.custom.calendar.yearMonth
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.temporal.WeekFields

internal data class MonthConfig(
        val outDateStyle: OutDateStyle,
        val inDateStyle: InDateStyle,
        val maxRowCount: Int,
        val startMonth: YearMonth,
        val endMonth: YearMonth,
        val firstDayOfWeek: DayOfWeek,
        val hasBoundaries: Boolean
) {

    internal val months: List<CalendarMonth> by lazy lazy@{
        return@lazy if (hasBoundaries) {
            generateBoundedMonths(
                    startMonth, endMonth, firstDayOfWeek,
                    maxRowCount, inDateStyle, outDateStyle
            )
        } else {
            generateUnboundedMonths(
                    startMonth, endMonth, firstDayOfWeek,
                    maxRowCount, inDateStyle, outDateStyle
            )
        }
    }

    internal companion object {

        /**
         * A [YearMonth] will have multiple [CalendarMonth] instances if the [maxRowCount] is
         * less than 6. Each [CalendarMonth] will hold just enough [CalendarDay] instances(weekDays)
         * to fit in the [maxRowCount].
         */
        internal fun generateBoundedMonths(
                startMonth: YearMonth,
                endMonth: YearMonth,
                firstDayOfWeek: DayOfWeek,
                maxRowCount: Int,
                inDateStyle: InDateStyle,
                outDateStyle: OutDateStyle
        ): List<CalendarMonth> {
            val months = mutableListOf<CalendarMonth>()
            var currentMonth = startMonth
            while (currentMonth <= endMonth) {
                val generateInDates = when (inDateStyle) {
                    InDateStyle.ALL_MONTHS -> true
                    InDateStyle.FIRST_MONTH -> currentMonth == startMonth
                    InDateStyle.NONE -> false
                }

                val weekDaysGroup =
                        generateWeekDays(currentMonth, firstDayOfWeek, generateInDates, outDateStyle).toMutableList()

                // Group rows by maxRowCount into CalendarMonth classes.
                val calendarMonths = mutableListOf<CalendarMonth>()
                val numberOfSameMonth = weekDaysGroup.size roundDiv maxRowCount
                while (weekDaysGroup.isNotEmpty()) {
                    val monthDays = weekDaysGroup.take(maxRowCount)
                    calendarMonths.add(CalendarMonth(currentMonth, monthDays, calendarMonths.size, numberOfSameMonth))
                    weekDaysGroup.removeAll(monthDays)
                }

                months.addAll(calendarMonths)
                if (currentMonth != endMonth) currentMonth = currentMonth.next else break
            }

            return months
        }

        internal fun generateUnboundedMonths(
                startMonth: YearMonth,
                endMonth: YearMonth,
                firstDayOfWeek: DayOfWeek,
                maxRowCount: Int,
                inDateStyle: InDateStyle,
                outDateStyle: OutDateStyle
        ): List<CalendarMonth> {

            // Generate a flat list of all days in the given month range
            val allDays = mutableListOf<CalendarDay>()
            var currentMonth = startMonth
            while (currentMonth <= endMonth) {

                // If inDates are enabled with boundaries disabled,
                // we show them on the first month only.
                val generateInDates = when (inDateStyle) {
                    InDateStyle.FIRST_MONTH, InDateStyle.ALL_MONTHS -> currentMonth == startMonth
                    InDateStyle.NONE -> false
                }

                allDays.addAll(
                        // We don't generate outDates for any month, they are added manually down below.
                        // This is because if outDates are enabled with boundaries disabled, we show them
                        // on the last month only.
                        generateWeekDays(currentMonth, firstDayOfWeek, generateInDates, OutDateStyle.NONE).flatten()
                )
                if (currentMonth != endMonth) currentMonth = currentMonth.next else break
            }

            // Regroup data into 7 days.
            val allDaysGroup = mutableListOf<List<CalendarDay>>()
            while (allDays.isNotEmpty()) {
                val sevenDays = allDays.take(7)
                allDaysGroup.add(sevenDays)
                allDays.removeAll(sevenDays)
            }

            val calendarMonths = mutableListOf<CalendarMonth>()
            val calMonthsCount = allDaysGroup.size roundDiv maxRowCount
            while (allDaysGroup.isNotEmpty()) {
                val monthWeeks = allDaysGroup.take(maxRowCount).toMutableList()

                // Keep a copy so after adding outDates to the last month, we can still remove
                // it from allDaysGroup and allDaysGroup.isNotEmpty() will be true.
                val monthWeeksCopy = allDaysGroup.take(maxRowCount).toMutableList()

                // Add the outDates for the last row if needed.
                if (monthWeeks.last().size < 7 && outDateStyle == OutDateStyle.END_OF_ROW || outDateStyle == OutDateStyle.END_OF_GRID) {
                    val lastWeek = monthWeeks.last().toMutableList()
                    val outMonth = currentMonth.plusMonths(1)
                    val outDates = (1..7 - lastWeek.size).map {
                        CalendarDay(LocalDate.of(outMonth.year, outMonth.month, it), DayOwner.NEXT_MONTH)
                    }
                    monthWeeks[monthWeeks.lastIndex] = lastWeek + outDates
                }

                // Add the outDates needed to make the number of rows in this index match the desired maxRowCount.
                while (monthWeeks.size < maxRowCount && outDateStyle == OutDateStyle.END_OF_GRID ||
                        // This will be true when we add the first inDates and the last week row in the CalendarMonth is not filled up.
                        monthWeeks.size == maxRowCount && monthWeeks.last().size < 7 && outDateStyle == OutDateStyle.END_OF_GRID
                ) {
                    // Since boundaries are disabled hence months will overflow, if we have maxRowCount
                    // set to 6 and the last index has only one row left with some missing dates in it,
                    // e.g the last row has only one day in it, if we attempt to fill the grid(up to maxRowCount)
                    // with outDates and the next month does not provide enough dates to fill the grid,
                    // we get more outDates from the following month.

                    /*  MON   TUE   WED   THU   FRI   SAT   SUN

                        30    31    01    02    03    04    05  => First outDates start here (month + 1)

                        06    07    08    09    10    11    12

                        13    14    15    16    17    18    19

                        20    21    22    23    24    25    26

                        27    28    29    30    01    02    03  => Second outDates start here (month + 2)

                        04    05    06    07    08    09    10  */

                    val lastDay = monthWeeks.last().last()

                    val lastDayIsEndOfFirstOutDates =
                            lastDay.owner == DayOwner.NEXT_MONTH && lastDay.date == lastDay.date.yearMonth.atEndOfMonth()

                    // Move to the second outDates if we have reached the end of
                    // the first outDates, use the first outDates otherwise.
                    val outMonth = lastDay
                            .date.yearMonth.plusMonths(if (lastDay.owner == DayOwner.THIS_MONTH || lastDayIsEndOfFirstOutDates) 1 else 0)

                    val nextRowDates = (1..7).map {
                        val dayValue =
                                if (lastDay.owner == DayOwner.THIS_MONTH || lastDayIsEndOfFirstOutDates) it else it + lastDay.day

                        CalendarDay(LocalDate.of(outMonth.year, outMonth.month, dayValue), DayOwner.NEXT_MONTH)
                    }

                    if (monthWeeks.last().size < 7) {
                        // Update the last week to 7 days instead of adding a new row.
                        // Handles the case when we've added all the first inDates and the
                        // last week row in the CalendarMonth is not filled up to 7 days.
                        monthWeeks[monthWeeks.lastIndex] = (monthWeeks.last() + nextRowDates).take(7)
                    } else {
                        monthWeeks.add(nextRowDates)
                    }
                }

                calendarMonths.add(
                        // numberOfSameMonth is the total number of all months and
                        // indexInSameMonth is basically this item's index in the entire month list.
                        CalendarMonth(startMonth, monthWeeks, calendarMonths.size, calMonthsCount)
                )
                allDaysGroup.removeAll(monthWeeksCopy)
            }

            return calendarMonths
        }

        /**
         * Generates the necessary number of weeks for a [YearMonth].
         */
        internal fun generateWeekDays(
                yearMonth: YearMonth,
                firstDayOfWeek: DayOfWeek,
                generateInDates: Boolean,
                outDateStyle: OutDateStyle
        ): List<List<CalendarDay>> {
            val year = yearMonth.year
            val month = yearMonth.monthValue

            val thisMonthDays = (1..yearMonth.lengthOfMonth()).map {
                CalendarDay(LocalDate.of(year, month, it), DayOwner.THIS_MONTH)
            }.toMutableList()

            val weekDaysGroup = if (generateInDates) {
                // Group days by week of month so we can add the in dates if necessary.
                val weekOfMonthField = WeekFields.of(firstDayOfWeek, 1).weekOfMonth()
                val groupByWeekOfMonth = thisMonthDays.groupBy { it.date.get(weekOfMonthField) }.values.toMutableList()

                // Add in-dates if necessary
                val firstWeek = groupByWeekOfMonth.first()
                if (firstWeek.size < 7) {
                    val previousMonth = yearMonth.minusMonths(1)
                    val inDates = (1..previousMonth.lengthOfMonth()).toList()
                            .takeLast(7 - firstWeek.size).map {
                                CalendarDay(
                                        LocalDate.of(previousMonth.year, previousMonth.month, it),
                                        DayOwner.PREVIOUS_MONTH
                                )
                            }
                    groupByWeekOfMonth[0] = inDates + firstWeek
                }
                groupByWeekOfMonth
            } else {
                // Group days by 7, first day shown on the month will be day 1.
                val groupBySeven = mutableListOf<List<CalendarDay>>()
                while (thisMonthDays.isNotEmpty()) {
                    val nextRow = thisMonthDays.take(7)
                    groupBySeven.add(nextRow)
                    thisMonthDays.removeAll(nextRow)
                }
                groupBySeven
            }


            if (outDateStyle == OutDateStyle.END_OF_ROW || outDateStyle == OutDateStyle.END_OF_GRID) {
                // Add out-dates for the last row.
                val nextMonth = yearMonth.plusMonths(1)
                val lastWeek = weekDaysGroup.last()
                if (lastWeek.size < 7) {
                    val outDates = (1..7 - lastWeek.size).map {
                        CalendarDay(LocalDate.of(nextMonth.year, nextMonth.month, it), DayOwner.NEXT_MONTH)
                    }
                    weekDaysGroup[weekDaysGroup.lastIndex] = lastWeek + outDates
                }

                // Add more rows to form a 6 x 7 grid
                if (outDateStyle == OutDateStyle.END_OF_GRID) {
                    while (weekDaysGroup.size < 6) {
                        val lastDay = weekDaysGroup.last().last()
                        val nextRowDates = (1..7).map {
                            val dayValue = if (lastDay.owner == DayOwner.THIS_MONTH) it else it + lastDay.day
                            CalendarDay(LocalDate.of(nextMonth.year, nextMonth.month, dayValue), DayOwner.NEXT_MONTH)
                        }
                        weekDaysGroup.add(nextRowDates)
                    }
                }
            }

            return weekDaysGroup
        }
    }
}

/**
 * We want the remainder to be added as the division result.
 * E.g: 5/2 should be 3.
 */
private infix fun Int.roundDiv(other: Int): Int {
    val div = this / other
    val rem = this % other
    // Add the last value dropped from div if rem is not zero
    return if (rem == 0) div else div + 1
}