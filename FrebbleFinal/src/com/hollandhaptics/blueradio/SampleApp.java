/**
 * File=SampleApp.java
 * Skeleton for App
 */
package com.hollandhaptics.blueradio;

import android.app.Application;
import android.content.res.Configuration;

public class SampleApp extends Application {
    private final String TAG = "BRSPTERM." + this.getClass().getSimpleName();
   
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
	super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
	super.onLowMemory();
    }

    @Override
    public void onCreate() {
	super.onCreate();
    }

    @Override
    public void onTerminate() {
	super.onTerminate();
    }
       
}
