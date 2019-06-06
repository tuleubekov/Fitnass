package com.akay.fitnass.view.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.akay.fitnass.service.FitService;
import com.akay.fitnass.util.IntentBuilder;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

abstract class BaseActivity extends AppCompatActivity {
    private static final long SKIP_DURATION = 1L;
    private CompositeDisposable mDisposables;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDisposables = new CompositeDisposable();
        initViewRxObservables();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!mDisposables.isDisposed()) {
            mDisposables.dispose();
        }
    }

    protected void addDisposables(final Disposable d) {
        mDisposables.add(d);
    }

    protected Observable<Object> clickObserver(final View view) {
        return RxView.clicks(view).throttleFirst(SKIP_DURATION, TimeUnit.SECONDS);
    }

    protected Observable<Object> longCLickObserver(final View view) {
        return RxView.longClicks(view).throttleFirst(SKIP_DURATION, TimeUnit.SECONDS);
    }

    protected void initViewRxObservables() {
        // Stub
    }

    protected void sendCommand(String command) {
        Intent intent = new IntentBuilder(this)
                .toService()
                .setCommand(command)
                .build();

        if (!isServiceRunningInForeground()) {
            ContextCompat.startForegroundService(this, intent);
        } else {
            startService(intent);
        }
    }

    protected boolean isServiceRunningInForeground() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) return false;

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (FitService.class.getName().equals(service.service.getClassName())) {
                return service.foreground;
            }
        }
        return false;
    }

}
