package com.akay.fitnass.util

import android.content.Context
import android.content.Intent

import com.akay.fitnass.service.FitService

class IntentBuilder(private val mContext: Context) {
    private var mClass: Class<*>? = null
    private var mCommandAction: String? = null
    private var mMillis: Long = 0

    fun setCommand(command: String): IntentBuilder {
        this.mCommandAction = command
        return this
    }

    fun setMillis(ms: Long): IntentBuilder {
        this.mMillis = ms
        return this
    }

    fun setTo(clazz: Class<*>): IntentBuilder {
        this.mClass = clazz
        return this
    }

    fun toService(): IntentBuilder {
        return setTo(FitService::class.java)
    }

    fun build(): Intent {
        val intent = Intent(mContext, mClass)
        intent.action = mCommandAction
        intent.putExtra(MS_ACTION_KEY, mMillis)
        return intent
    }

    companion object {
        private const val MS_ACTION_KEY = "com.akay.fitnass.util.intent.MS_ACTION_KEY"
    }
}
