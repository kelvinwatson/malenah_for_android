package com.watsonlogic.malenah.malenah3;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.util.Log;


public class FetchAllDataService extends Service {
    private String filterStr = "com.watsonlogic.malenah.malenah3.providers";
    private String jsonResponse = null;
    private URL url;
    private HttpURLConnection urlConnection;

    public FetchAllDataService() {
    }



    /*TODO: Retreive data from database and broadcast it back to MainActivity*/
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId){
        try {
            Runnable r = new Runnable() {   //MUST place service code in thread(req'd for Service class)
                @Override
                public void run() {
                    //TODO: retrieve provider data database
                    //http://developer.android.com/reference/java/net/HttpURLConnection.html
                    //http://stackoverflow.com/questions/8376072/whats-the-readstream-method-i-just-can-not-find-it-anywhere
                    try {
                        url = new URL("http://malenah-api-prod.appspot.com/provider");
                        Log.d("FETCH","calling http://malenah-api-prod.appspot.com/provider");
                        urlConnection = (HttpURLConnection)url.openConnection();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        BufferedReader inputStream = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        String jsonResponse = inputStream.readLine();
                        Log.d("FETCH (JSON)", jsonResponse);
                        Intent intent = new Intent();
                        intent.setAction(filterStr);
                        intent.putExtra("providers", jsonResponse);
                        sendBroadcast(intent);
                    } catch (IOException e) {
                        Log.e("FETCH (JSON)", "error reading stream, internet connection maybe lost");
                        //int status = urlConnection.getResponseCode();
                        //InputStream error = urlConnection.getErrorStream();
                    } finally {
                        urlConnection.disconnect();
                    }
                }
            };
            Thread getDefaultEventAllBeersThread = new Thread(r);
            getDefaultEventAllBeersThread.start();
            return Service.START_STICKY;
        } catch(Exception e){
            e.printStackTrace();
            return Service.START_NOT_STICKY;
        }
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
