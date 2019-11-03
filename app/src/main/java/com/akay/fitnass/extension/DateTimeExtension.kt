package com.akay.fitnass.extension

import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.format.DateTimeFormatter

private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

val LocalDate.yearMonth: YearMonth
    get() = YearMonth.of(year, month)

val YearMonth.next: YearMonth
    get() = this.plusMonths(1)

val YearMonth.previous: YearMonth
    get() = this.minusMonths(1)

val YearMonth.firstDay: LocalDate
    get() = this.atDay(1)

val LocalDate.firstDay: LocalDate
    get() = this.withDayOfMonth(1)

fun LocalDate.isToday(): Boolean
        = LocalDate.now() == this

fun String.toLocaleDate(): LocalDate = LocalDate.parse(this, formatter)