package org.nam;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatDelegate;

import com.facebook.share.Share;

import org.nam.contract.Contract;

import java.util.Map;

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
