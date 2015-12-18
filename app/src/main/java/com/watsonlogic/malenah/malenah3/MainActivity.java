package com.watsonlogic.malenah.malenah3;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    final Context context = this;
    private LocationManager locationManager;
    private boolean GPSEnabled;
    private boolean networkEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Source: http://stackoverflow.com/questions/15997079/getlastknownlocation-always-return-null-after-i-re-install-the-apk-file-via-ecli
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        GPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); //check GPS status
        networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER); // check network statu
        Log.d("LOCATION (provider)", LocationManager.NETWORK_PROVIDER);
        if (!GPSEnabled || !networkEnabled){
            Log.d("LOCATION (GPS)", "disabled, ask user to enable!");
            //show dialog to allow user to enable location settings
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle("GPS Disabled");
            dialog.setMessage("Location services must be enabled. Enable now?").setCancelable(false);

            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                }
            });

            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //nothing to do
                }
            });
            dialog.show();
            return;
            //TODO: disable login btn and disallow user to proceed
            //TODO: alert user with toast or dialog, on user network/GPS enabled, call onCreate again
        }
        Log.d("LOCATION (network)", "enabled!");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        if (checkLocationPermission()) {
            Log.d("LOCATION (permission)", "user granted permission!");
        } else {
            Log.d("LOCATION (permission)", "permission denied!");
        }

        /*TODO: Call function to launch service to fetch data (see getData()) by calling startService(FetchAllDataService)*/

    }

    public boolean checkLocationPermission() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    /*TODO: Broadcast receiver class to receive the data broadcasted from FetchAllDataService */
    public class Receiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            /*TODO: Launch Drawer Activity once allData (incl. user details) string (a JSON string) received from FetchAllDataService*/
            /*TODO: (see MyReceiver extends BroadcastReceiver onReceive method in MainActivity */
            /*TODO: save received broadcasted data into String allData using intent.getStringExtras*/
            /*TODO: Pass in the above allData string to DrawerActivity using Intent drawerActivity = newIntent(this,drawerActivity.class)*/
            /*TODO: then call startActivity(drawerActivity)*/
            /* Notice that startActivity is only called once all the data has been retrieved from database */
        }
    }

    public void launchDrawerActivity(View v) {
        startActivity(new Intent(this, DrawerActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
}
