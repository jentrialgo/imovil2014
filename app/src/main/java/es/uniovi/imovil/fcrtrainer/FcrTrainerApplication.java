package es.uniovi.imovil.fcrtrainer;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;

public class FcrTrainerApplication extends Application {
    Tracker tracker = null;
    private static FcrTrainerApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    @Nullable
    public static Context getAppContext() {
        return sInstance;
    }

    public synchronized Tracker getTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            tracker = analytics.newTracker(AnalyticsCredentials.TRACKING_ID);
        }

        return tracker;
    }

}
