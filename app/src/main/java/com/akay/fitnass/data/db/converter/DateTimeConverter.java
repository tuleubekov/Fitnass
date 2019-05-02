package com.akay.fitnass.data.db.converter;

import android.arch.persistence.room.TypeConverter;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

public class DateTimeConverter {

    @TypeConverter
    public static ZonedDateTime toDate(final long millis) {
        Instant instant = Instant.ofEpochMilli(millis);
        return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    @TypeConverter
    public static long toMillis(final ZonedDateTime zdt) {
        return zdt.toInstant().toEpochMilli();
    }
}
