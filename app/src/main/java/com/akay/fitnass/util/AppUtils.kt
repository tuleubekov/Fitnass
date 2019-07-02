package com.akay.fitnass.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

object AppUtils {
    private const val VIBRATE_DURATION_MS = 500L

    val isAfterO: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    fun vibrate(ctx: Context) {
        val v = ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (!v.hasVibrator()) {
            return
        }

        if (isAfterO) {
            v.vibrate(VibrationEffect.createOneShot(VIBRATE_DURATION_MS, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            v.vibrate(VIBRATE_DURATION_MS) //deprecated in API 26
        }
    }
}
