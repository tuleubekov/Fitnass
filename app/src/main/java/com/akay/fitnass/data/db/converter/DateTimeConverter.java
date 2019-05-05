package com.akay.fitnass.data.db.converter;

import android.arch.persistence.room.TypeConverter;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

public class DateTimeConverter {

    @TypeConverter
    public static ZonedDateTime toDate(final long millis) {
        if (millis < 0) {
            return null;
        }
        Instant instant = Instant.ofEpochMilli(millis);
        return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    @TypeConverter
    public static long toMillis(final ZonedDateTime zdt) {
        if (zdt == null) {
            return -1L;
        }
        return zdt.toInstant().toEpochMilli();
    }
}
