package com.snowble.android.verticalstepper.sample;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class SampleApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
