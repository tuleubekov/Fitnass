package com.akay.fitnass.view.activities;

import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

abstract class BaseActivity extends AppCompatActivity {
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

    protected void initViewRxObservables() {
        // Stub
    }

}
