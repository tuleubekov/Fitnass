package com.akay.fitnass.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

import org.threeten.bp.ZonedDateTime

@Entity
data class Lap(
        @PrimaryKey(autoGenerate = true)
        var id: Long? = null,
        var time: ZonedDateTime
)
