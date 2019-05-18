package com.akay.fitnass.util;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateTimes {
    private static boolean showLocal = true;

    public static ZoneId getZoneId() {
        return ZoneId.systemDefault();
    }

    public static ZonedDateTime fromMs(final long ms) {
        Instant instant = Instant.ofEpochMilli(ms);
        return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static long toMs(final ZonedDateTime zdt) {
        if (zdt == null) {
            return 0;
        }
        return zdt.toInstant().toEpochMilli();
    }

    public static long nowMillis() {
        return toMs(ZonedDateTime.now());
    }

    public static String msToStrFormat(long ms) {
        int msView = (int) (ms % 1000L) / 10;
        int seconds = (int) (TimeUnit.MILLISECONDS.toSeconds(ms) % 60);
        int minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(ms) % 60);
        int hours = (int) (TimeUnit.MILLISECONDS.toHours(ms) % 24);
        int days = (int) TimeUnit.MILLISECONDS.toDays(ms);

        return setFormat(days, hours, minutes, seconds, msView);
    }

    private static String setFormat(int days, int hr, int min, int sec, int ms) {
        if (days > 0) {
            return String.format(Locale.getDefault(), "%02d.%02d.%02d.%02d.%02d", days, hr, min, sec, ms);
        } else if (hr > 0) {
            return String.format(Locale.getDefault(), "%02d.%02d.%02d.%02d", hr, min, sec, ms);
        } else {
            return String.format(Locale.getDefault(), "%02d.%02d.%02d", min, sec, ms);
        }
    }
}
