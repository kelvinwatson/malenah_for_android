package com.watsonlogic.malenah.malenah3;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchReviewsAsyncTask extends AsyncTask<Long, Void, String> {
    private Long providerId;
    private String urlStr;
    private String jsonArrStr = new String();
    private JSONArray jsonArr = new JSONArray();
    private Context context;
    private URL url;
    private HttpURLConnection urlConnection;
    private long primitive;
    /**
     * Overridden constructor
     */
    public FetchReviewsAsyncTask(Context context, long providerId) {
        this.context = context;
        this.providerId = new Long(providerId);
    }

    /**
     * Default constructor
     */
    public FetchReviewsAsyncTask() {
    }

    @Override
    protected String doInBackground(Long... i) {
        try {
            primitive = providerId.longValue();
            urlStr = "http://malenah-android.appspot.com/provider/"+primitive+"/review";
            url = new URL(urlStr);
            urlConnection = (HttpURLConnection)url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            jsonArrStr = inputStream.readLine();
            Log.d("FETCHREV(JSONStr)", jsonArrStr);
            jsonArr = new JSONArray(jsonArrStr);
        } catch(JSONException e){
            Log.e("FETCHREV","JSONException");
        } catch (IOException e) {
            Log.e("FETCHREV", "error reading stream, internet connection maybe lost");
            //int status = urlConnection.getResponseCode();
            //InputStream error = urlConnection.getErrorStream();
        }
        return "done";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d("FETCHREV", "Asynctask done");
        //Intent i = new Intent(context, ProfileActivity.class);
        //i.putExtra("Reviews", jsonArrStr);
        //context.startActivity(i);
        ((ProfileActivity)this.context).fetchReviewsDone(jsonArr);
    }


}
