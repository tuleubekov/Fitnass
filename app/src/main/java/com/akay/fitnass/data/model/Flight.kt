package com.akay.fitnass.data.model

import androidx.annotation.ColorRes
import org.threeten.bp.LocalDateTime

data class Flight(val time: LocalDateTime, val departure: Airport, val destination: Airport, @ColorRes val color: Int) {
    data class Airport(val city: String, val code: String)
}