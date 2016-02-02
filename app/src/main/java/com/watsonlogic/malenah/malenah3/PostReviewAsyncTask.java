package com.watsonlogic.malenah.malenah3;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PostReviewAsyncTask extends AsyncTask<Void,Void,Void> {
    Context context;
    Map<String, String> postParams;

    public PostReviewAsyncTask(Context context, Map<String,String> postParams) {
        Log.d("AsyncTask", "distance constructor");
        this.context = context;
        this.postParams = postParams;
    }

    public PostReviewAsyncTask() {
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.d("POSTREVIEW","in doInbackground");


        //set params
        Map<String,String> dummyParams = new LinkedHashMap<>();
        dummyParams.put("username", "watsokel1234567890");
        dummyParams.put("rating", "4.6");
        dummyParams.put("comment", "Posting a comment from Android application");
        dummyParams.put("provider", "5629499534213120");
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, String> dParam : dummyParams.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            try {
                postData.append(URLEncoder.encode(dParam.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(dParam.getValue()), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        Log.d("POSTREVIEW postData",postData.toString());
        byte[] postDataBytes = null;
        try{
            postDataBytes = postData.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }

        //set headers

        URL url = null;
        HttpURLConnection conn = null;
        try {
            url = new URL("http://malenah-api.appspot.com/review");
            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
        Reader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String response = "";
            for (int c = in.read(); c != -1; c = in.read()){
                //System.out.print((char)c);
                response += (char) c;
            }
            Log.d("POSTREVIEW response=",response);
        } catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
