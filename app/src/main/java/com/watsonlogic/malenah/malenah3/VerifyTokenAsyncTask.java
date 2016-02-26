package com.watsonlogic.malenah.malenah3;

import android.content.Context;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VerifyTokenAsyncTask extends AsyncTask<Void,Void,String> {
    private String TAG = "VerifyTokenAsyncTask";
    private Context context;
    Map<String, String> postParams;

    public VerifyTokenAsyncTask(Context context, Map<String, String> postParams) {
        this.context = context;
        this.postParams = postParams;
    }

    public VerifyTokenAsyncTask() {
    }

    public byte[] generatePostData() {
        Log.d(TAG, "in generatePostData()");
        Log.d(TAG, postParams.toString());
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
        Log.d(TAG, "in doInbackground");

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
                //System.out.print((char)c);
                response += (char) c;
            }
            Log.d(TAG+" response", response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    protected void onPostExecute(String resp) {
        super.onPostExecute(resp);
        //parse the string
        JSONObject jResp = null;
        /*try {
            jResp = new JSONObject(resp);
            if (jResp.has("key")) {
                ((MainActivity) this.context).postReviewDone(jResp, true);
            } else {
                ((ProfileActivity) this.context).postReviewDone(null, false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }
}
