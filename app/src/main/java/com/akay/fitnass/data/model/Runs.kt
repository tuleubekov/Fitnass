package com.akay.fitnass.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.ZonedDateTime

@Entity
data class Runs(
        @PrimaryKey(autoGenerate = true)
        var id: Long? = null,
        var laps: List<Lap>,
        var comment: String? = null,
        var dateTime: ZonedDateTime
)
