package com.watsonlogic.malenah.malenah3;

import android.app.Application;
import com.google.android.gms.common.api.GoogleApiClient;

public class App extends Application {
    private GoogleApiClient mGoogleApiClient;
    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized App getInstance() {
        return mInstance;
    }

    public void setClient(GoogleApiClient client){
        mGoogleApiClient = client;
    }

    public GoogleApiClient getClient(){
        return mGoogleApiClient;
    }
}