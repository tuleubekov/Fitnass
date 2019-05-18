package com.akay.fitnass.util;

import android.content.Context;
import android.content.Intent;

import com.akay.fitnass.service.FitService;

public class IntentBuilder {
    public static final String MS_ACTION_KEY = "com.akay.fitnass.util.intent.MS_ACTION_KEY";

    private Context mContext;
    private Class<?> mClass;
    private String mCommandAction;
    private long mMillis;

    public IntentBuilder(Context context) {
        this.mContext = context;
    }

    public IntentBuilder setCommand(String command) {
        this.mCommandAction = command;
        return this;
    }

    public IntentBuilder setMillis(long ms) {
        this.mMillis = ms;
        return this;
    }

    public IntentBuilder setTo(final Class<?> clazz) {
        this.mClass = clazz;
        return this;
    }

    public IntentBuilder toService() {
        return setTo(FitService.class);
    }

    public Intent build() {
        Intent intent = new Intent(mContext, mClass);
        intent.setAction(mCommandAction);
        intent.putExtra(MS_ACTION_KEY, mMillis);
        return intent;
    }
}
