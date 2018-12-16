package org.nam;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

public class MyApp extends Application {

    private static MyApp instance;
    public MyApp() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static Context getInstance() {
        return instance;
    }
}
