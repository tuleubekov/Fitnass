package com.akay.fitnass.util

import android.util.Log

import java.util.Locale

object Logger {
    private const val TAG = "LoggerFit"

    @JvmOverloads
    fun e(message: String = "-------") {
        Log.e(TAG, message)
    }

    fun e(tag: String, message: String) {
        Log.e(TAG, concat(tag, message))
    }

    private fun concat(tag: String, message: String): String {
        return String.format(Locale.getDefault(), "%s: %s", tag, message)
    }
}
