package com.watsonlogic.malenah.malenah3;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;


public class UpdateUserAsyncTask extends AsyncTask<Void, Void, String> {
    private final String TAG = "UPDATEFAVORITE";
    public MyProfileFragment profileFragment = null;
    Map<String, String> postParams;

    /**
     * Overridden constructor
     */
    public UpdateUserAsyncTask(Map<String, String> postParams) {
        this.postParams = new LinkedHashMap<String, String>(postParams);
    }

    /**
     * Overridden constructor
     */
    public UpdateUserAsyncTask(MyProfileFragment profileFragment, Map<String, String> postParams) {
        this.profileFragment = profileFragment;
        this.postParams = new LinkedHashMap<String, String>(postParams);
    }


    /**
     * Default constructor
     */
    public UpdateUserAsyncTask() {
    }

    public byte[] generatePostData() {
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, String> dParam : postParams.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            try {
                postData.append(URLEncoder.encode(dParam.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(dParam.getValue()), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, postData.toString());
        byte[] postDataBytes = null;
        try {
            postDataBytes = postData.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return postDataBytes;
    }

    @Override
    protected String doInBackground(Void... params) {
        /* Set params */
        byte[] postDataBytes = generatePostData();

        /* Set headers and write */
        URL url = null;
        HttpURLConnection conn = null;
        try {
            url = new URL("http://malenah-android.appspot.com/user");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* Retrieve response */
        Reader in = null;
        String response = new String();
        try {
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            response = "";
            for (int c = in.read(); c != -1; c = in.read()) {
                response += (char) c;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    protected void onPostExecute(String resp) {
        super.onPostExecute(resp);
        Log.d(TAG, resp);
        if (profileFragment != null) { //return result to profileFragment
            if (resp.contains("200")) {
                profileFragment.editProfileDone(true);
            } else {
                profileFragment.editProfileDone(false);
            }
        }
    }
}
