package com.akay.fitnass.util;

import android.util.Log;

import java.util.Locale;

public class Logger {
    private static final String TAG = "LoggerFit";

    public static void e() {
        e("-------");
    }

    public static void e(String message) {
        Log.e(TAG, message);
    }

    public static void e(String tag, String message) {
        Log.e(TAG, concat(tag, message));
    }

    private static String concat(String tag, String message) {
        return String.format(Locale.getDefault(), "%s: %s", tag, message);
    }
}
