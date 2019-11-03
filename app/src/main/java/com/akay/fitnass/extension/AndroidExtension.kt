package com.akay.fitnass.extension

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View
        = LayoutInflater.from(this.context).inflate(layoutRes, this, attachToRoot)

inline fun <reified T: Any> Context.launchActivity(
        options: Bundle? = null,
        init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    startActivity(intent, options)
}

inline fun <reified T: Any> AppCompatActivity.launchActivity(
        requestCode: Int,
        options: Bundle? = null,
        init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    startActivityForResult(intent, requestCode, options)
}

inline fun <reified T: Any> Fragment.launchActivity(
        context: Context,
        options: Bundle? = null,
        init: Intent.() -> Unit = {}
) {
    context.launchActivity<T>(options, init)
}

inline fun <reified T: Any> Fragment.launchActivity(
        activity: AppCompatActivity,
        requestCode: Int,
        options: Bundle? = null,
        init: Intent.() -> Unit = {}
) {
    activity.launchActivity<T>(requestCode, options, init)
}

inline fun AppCompatActivity.finishWithResult(
        resultCode: Int,
        init: Intent.() -> Unit = {}
) {
    val intent = Intent()
    intent.init()
    setResult(resultCode, intent)
    finish()
}

inline fun <reified T: Any> newIntent(context: Context) = Intent(context, T::class.java)