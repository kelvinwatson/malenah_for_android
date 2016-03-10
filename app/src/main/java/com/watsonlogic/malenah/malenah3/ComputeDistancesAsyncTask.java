package com.watsonlogic.malenah.malenah3;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

/**
 * Update distance attributes in row item objects
 */
public class ComputeDistancesAsyncTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    private ArrayList<RowItem> rowItems;

    public ComputeDistancesAsyncTask(Context context, ArrayList<RowItem> rowItems) {
        this.context = context;
        this.rowItems = rowItems;
    }

    @Override
    protected Void doInBackground(Void... params) {
        for (RowItem r : rowItems) {
            r.setDistance(r.getDistance() + 1);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        ((MapsActivity) this.context).distancesDone(rowItems);
    }
}
