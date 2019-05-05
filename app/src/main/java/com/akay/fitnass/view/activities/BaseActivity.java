package com.akay.fitnass.view.activities;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

abstract class BaseActivity extends AppCompatActivity {
    private static final long SKIP_DURATION = 3L;
    private final CompositeDisposable mDisposables = new CompositeDisposable();

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
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

    protected Observable<View> clickObserver(final View view) {
        return RxView.clicks(view).throttleFirst(SKIP_DURATION, TimeUnit.SECONDS).map((o) -> (View) o);
    }

    protected void initViewRxObservables() {
        // Stub
    }

}
