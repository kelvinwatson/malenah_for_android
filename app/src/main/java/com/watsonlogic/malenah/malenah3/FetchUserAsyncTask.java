package com.watsonlogic.malenah.malenah3;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchUserAsyncTask extends AsyncTask<Long, Void, String> {
    private final String TAG = "fetchUser";
    private Long key;
    private String urlStr;
    private String userInfoStr = new String();
    JSONObject jsonObj = null;
    private Context context;
    private URL url;
    private HttpURLConnection urlConnection;
    private long primitive;
    /**
     * Overridden constructor
     */
    public FetchUserAsyncTask(Context context, long key) {
        this.context = context;
        this.key = new Long(key);
    }

    /**
     * Default constructor
     */
    public FetchUserAsyncTask() {
    }

    @Override
    protected String doInBackground(Long... i) {
        try {
            primitive = key.longValue();
            urlStr = "http://malenah-android.appspot.com/user/"+primitive;
            url = new URL(urlStr);
            urlConnection = (HttpURLConnection)url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            userInfoStr = inputStream.readLine();
            Log.d(TAG, userInfoStr);
        } catch (IOException e) {
            Log.e(TAG, "error reading stream, internet connection maybe lost");
            //int status = urlConnection.getResponseCode();
            //InputStream error = urlConnection.getErrorStream();
        }
        return userInfoStr;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d(TAG, "Asynctask done");
        ((DrawerActivity)this.context).fetchUserDone(s);
    }


}
