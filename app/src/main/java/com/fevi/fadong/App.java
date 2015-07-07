package com.fevi.fadong;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by 1000742 on 15. 7. 3..
 */
public class App extends Application {

    public static Tracker mTracker;

    @Override
    public void onCreate() {
        super.onCreate();

        parseInit();
    }

    private void parseInit() {
        Parse.initialize(this, "2iC7nc2t8jrm8DFPtV03BT1TiXODEkNy6cUomq95", "N8HSWDLfQqDKQ1d5okDbYuIHJtgg7yjugSdffDo1");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);

            mTracker = analytics.newTracker("UA-64869536-1"); // Replace with actual tracker/property Id
            mTracker.enableExceptionReporting(true);
            mTracker.enableAdvertisingIdCollection(true);
            mTracker.enableAutoActivityTracking(true);
        }
        return mTracker;
    }
}
