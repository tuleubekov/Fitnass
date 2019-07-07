package com.akay.fitnass.view.activities

import android.app.ActivityManager
import android.content.Context
import android.content.DialogInterface
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.view.View

import com.akay.fitnass.R
import com.akay.fitnass.service.FitService
import com.akay.fitnass.util.IntentBuilder
import com.jakewharton.rxbinding2.view.RxView

import java.util.concurrent.TimeUnit

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseActivity : AppCompatActivity() {
    private var mDisposables: CompositeDisposable? = null

    override fun onStart() {
        super.onStart()
        mDisposables = CompositeDisposable()
        initViewRxObservables()
    }

    override fun onStop() {
        super.onStop()
        if (!mDisposables!!.isDisposed) {
            mDisposables!!.dispose()
        }
    }

    protected fun addDisposables(d: Disposable) {
        mDisposables!!.add(d)
    }

    protected fun clickObserver(view: View): Observable<Any> {
        return RxView.clicks(view).throttleFirst(SKIP_DURATION, TimeUnit.SECONDS)
    }

    protected fun longCLickObserver(view: View): Observable<Any> {
        return RxView.longClicks(view).throttleFirst(SKIP_DURATION, TimeUnit.SECONDS)
    }

    protected open fun initViewRxObservables() {
        // Stub
    }

    protected fun sendCommand(command: String) {
        val intent = IntentBuilder(this)
                .toService()
                .setCommand(command)
                .build()

        if (!isServiceRunningInForeground()) {
            ContextCompat.startForegroundService(this, intent)
        } else {
            startService(intent)
        }
    }

    private fun isServiceRunningInForeground(): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager ?: return false

        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (FitService::class.java.name == service.service.className) {
                return service.foreground
            }
        }
        return false
    }

    fun showDeleteRunsDialog(dialogPositiveClickListener: DialogInterface.OnClickListener) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        builder.setTitle("Удаление")
        builder.setMessage("Вы хотите удалить запись?")

        builder.setPositiveButton("Да", dialogPositiveClickListener)
        builder.setNegativeButton("Отмена") { dialog, id -> dialog.cancel() }

        val dialog = builder.create()
        dialog.show()
    }

    companion object {
        private const val SKIP_DURATION = 1L
    }

}
