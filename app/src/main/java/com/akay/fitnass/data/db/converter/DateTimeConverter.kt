package com.akay.fitnass.data.db.converter

import androidx.room.TypeConverter
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

class DateTimeConverter {

    @TypeConverter
    fun toDate(millis: Long): ZonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())

    @TypeConverter
    fun toMillis(zdt: ZonedDateTime?): Long = zdt?.toInstant()?.toEpochMilli() ?: 0L
}