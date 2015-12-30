package com.watsonlogic.malenah.malenah3;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Update distance attributes in row item objects
 */
public class ComputeDistancesAsyncTask extends AsyncTask<Void,Void,Void> {
    private Context context;


    public ComputeDistancesAsyncTask(Context context) {
        Log.d("AsyncTask", "constructor");
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.d("AsyncTask", "doInBackground");
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        ((MapsActivity)this.context).distancesDone();
        Log.d("AsyncTask", "distancesDone!");
    }
}
