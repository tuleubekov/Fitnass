package com.akay.fitnass.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

import org.threeten.bp.ZonedDateTime

@Entity
data class Lap(
        @PrimaryKey(autoGenerate = true)
        var id: Long? = null,
        var time: ZonedDateTime
)
