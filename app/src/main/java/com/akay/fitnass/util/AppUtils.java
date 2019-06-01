package com.akay.fitnass.util;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class AppUtils {
    private static final long VIBRATE_DURATION_MS = 500L;

    public static void vibrate(final Context ctx) {
        Vibrator v = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
        if (v == null) return;

        if (isAfterO()) {
            v.vibrate(VibrationEffect.createOneShot(VIBRATE_DURATION_MS, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(VIBRATE_DURATION_MS); //deprecated in API 26
        }
    }

    public static boolean isAfterO() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }
}
