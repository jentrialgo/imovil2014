package es.uniovi.imovil.fcrtrainer;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import android.app.Application;

public class FcrTrainerApplication extends Application {
    Tracker tracker = null;

    public synchronized Tracker getTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            tracker = analytics.newTracker(AnalyticsCredentials.TRACKING_ID);
        }

        return tracker;
    }
}
