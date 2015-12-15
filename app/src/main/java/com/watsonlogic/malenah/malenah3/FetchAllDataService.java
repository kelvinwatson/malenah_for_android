package com.watsonlogic.malenah.malenah3;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class FetchAllDataService extends Service {
    public FetchAllDataService() {
    }

    /*TODO: Retreive data from database and broadcast it back to MainActivity*/

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
