package com.akay.fitnass.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

import org.threeten.bp.ZonedDateTime

@Entity(tableName = "active_runs")
data class ActiveRuns(
        @PrimaryKey
        var id: Long? = null,
        var dateTime: ZonedDateTime,
        var isPaused: Boolean = false,
        var laps: MutableList<Lap>,
        var start: ZonedDateTime? = null,
        var tws: ZonedDateTime? = null
) {



    companion object {
        @Ignore
        val ID = 1L
    }
}
