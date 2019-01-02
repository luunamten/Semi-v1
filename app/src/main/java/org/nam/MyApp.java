package org.nam;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatDelegate;

import com.facebook.share.Share;
import com.google.gson.Gson;

import org.nam.contract.Contract;

import java.util.Map;

public class MyApp extends Application {

    private static MyApp instance;
    //new
    private Gson mGSon;

    public MyApp() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mGSon = new Gson();
    }

    //new
    public static Context getContext() {
        return instance;
    }
    public Gson getGSon(){
        return mGSon;
    }
    public static MyApp getInstancee() {
        return instance;
    }
}
