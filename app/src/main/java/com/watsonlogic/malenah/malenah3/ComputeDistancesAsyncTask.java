package com.watsonlogic.malenah.malenah3;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

/**
 * Update distance attributes in row item objects
 */
public class ComputeDistancesAsyncTask extends AsyncTask<Void,Void,Void> {
    private Context context;
    private ArrayList<RowItem> rowItems;

    public ComputeDistancesAsyncTask(Context context,ArrayList<RowItem> rowItems) {
        Log.d("AsyncTask", "distance constructor");
        this.context = context;
        this.rowItems = rowItems;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.d("AsyncTask", "distance doInBackground");
        //TODO: Compute distance from user to each row item based on database info
        //TODO: Update database distances
        //TODO: Update distance member of each RowItem
        //DUMMY DATA to demonstrate modification of distance member of each row item:
        for(RowItem r : rowItems){
            r.setDistance(r.getDistance()+1);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        ((MapsActivity)this.context).distancesDone(rowItems);
        Log.d("AsyncTask", "distancesDone!");
    }
}
