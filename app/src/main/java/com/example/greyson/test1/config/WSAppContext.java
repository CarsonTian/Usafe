package com.example.greyson.test1.config;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.example.greyson.test1.net.NetWorkApi;

import retrofit2.Retrofit;

/**
 * Created by greyson on 28/3/17.
 */

public class WSAppContext extends Application{
    private static WSAppContext instance;
    private Context mContext;
    private Resources mResources;
    private Retrofit mRetrofit;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        init();
    }

    private void init() {
        mContext = WSAppContext.getInstance().getApplicationContext();
        mResources = mContext.getResources();
        mRetrofit = NetWorkApi.getInstance().gradleRetrofit(this);
    }

    public static synchronized WSAppContext getInstance() {
        return instance;
    }

    public synchronized Context getAppContext() {
        return mContext;
    }

    public synchronized Resources getmResources() {
        return mResources;
    }

    public synchronized Retrofit getRetrofit() {
        return mRetrofit;
    }

}
