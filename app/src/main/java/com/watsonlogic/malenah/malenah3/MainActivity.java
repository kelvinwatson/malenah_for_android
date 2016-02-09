package com.watsonlogic.malenah.malenah3;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity implements LocationListener {
    final Context context = this;
    private LocationManager locationManager;
    private boolean gpsEnabled;
    private boolean networkEnabled;
    static final int SET_LOCATION_REQUEST = 1;  // The request code
    private DataReceiver dataReceiver;
    private String providers;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startButton = (Button)findViewById(R.id.startButton); //get reference to button
        loadIcon();
    }

    public void loadIcon(){
        ImageView titleIcon = (ImageView)findViewById(R.id.titleIcon); //get reference to rowIcon
        Picasso.with(context)
                .load(R.drawable.malenah_icon)
                .fit()
                .centerInside()
                .into(titleIcon);
    }

    @Override
    protected void onStart() {
        super.onStart();

        startButton.setEnabled(false); //disable start button until all data retrieved

        if(!isFullyConnected(getApplicationContext())) { //check connection
            Log.d("NETWORK","not connected");
            enableInternet();
            return;
        }else{ //internet connected
            getData();
            Log.d("NETWORK","connected");
            if (!enableLocation()) { //check Location
                return;
            }
        }
        //practice a post here?
        //Log.d("POSTREVIEW","execute async task from MainActivity()");
        //new PostReviewAsyncTask().execute();
    }

    protected void getData() {
        /* Register receiver for data */
        IntentFilter filter = new IntentFilter("com.watsonlogic.malenah.malenah3.providers");
        dataReceiver = new DataReceiver();
        registerReceiver(dataReceiver, filter);
        /* Start the service to fetch data */
        Intent fetchIntent = new Intent(this, FetchAllDataService.class);
        Log.d("FETCH","Main, starting service to fetch data");
        startService(fetchIntent);
    }

    public class DataReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            /*TODO: Launch Drawer Activity once allData (incl. user details) string (a JSON string) received from FetchAllDataService*/
            Log.d("FETCH", "back in Main, onReceive()");
            providers = intent.getStringExtra("providers");
            Log.d("FETCH","in Main, providers="+providers);
            Intent loginIntent = new Intent(MainActivity.this, DrawerActivity.class);
            loginIntent.putExtra("providers", providers);
            Log.d("FETCH", "Launch button enabled");
            startButton.setEnabled(true);

            /*TODO: (see MyReceiver extends BroadcastReceiver onReceive method in MainActivity */
            /*TODO: save received broadcasted data into String allData using intent.getStringExtras*/
            /*TODO: Pass in the above allData string to DrawerActivity using Intent drawerActivity = newIntent(this,drawerActivity.class)*/
            /*TODO: then call startActivity(drawerActivity)*/
            /* Notice that startActivity is only called once all the data has been retrieved from database */
        }
    }

    public void launchDrawerActivity(View v) {
        Intent i = new Intent(MainActivity.this, DrawerActivity.class);
        i.putExtra("allProviders",providers);
        Log.d("DRAWER", "sending providers from Main to Drawer");
        startActivity(i);
    }


    protected void enableInternet(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("A network connection is required. Please turn on mobile network or WiFi in Settings.")
                .setTitle("Unable to connect")
                .setCancelable(false)
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Intent i = new Intent(Settings.ACTION_SETTINGS);
                                startActivity(new Intent(Settings.ACTION_SETTINGS));
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MainActivity.this.finish();
                            }
                        }
                );
        AlertDialog alert = builder.create();
        alert.show();
    }
    //http://stackoverflow.com/questions/28168867/check-internet-status-from-the-main-activity
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        } else{
            return false;
        }
    }

    public boolean isConnectedMobile(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if(info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }

    public boolean isConnectedWifi(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI){
            return true;
        }
        return false;
    }

    public boolean isFullyConnected(Context context){
        if (isNetworkAvailable(context)){
            Log.d("NETWORK","available");
            if(isConnectedMobile(context)){
                Log.d("NETWORK","mobile connected");
                return true;
            }
            if(isConnectedWifi(context)){
                Log.d("NETWORK","wifi connected");
                return true;
            }
        }
        return false;
    }

    protected boolean enableLocation(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
        if (checkLocationPermission()) {
            Log.d("LOCATION (permission)", "user granted permission!");
        } else {
            Log.d("LOCATION (permission)", "permission denied!");
            return false;
        }
        if  ((Build.VERSION.SDK_INT >= 23) &&
                (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            return false;
        }
        //Source: http://stackoverflow.com/questions/15997079/getlastknownlocation-always-return-null-after-i-re-install-the-apk-file-via-ecli
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); //check GPS status
        networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER); // check network statu
        //Log.d("LOCATION (provider)", LocationManager.NETWORK_PROVIDER);
        if (!gpsEnabled && !networkEnabled){
            Log.d("LOCATION (GPS)", "disabled, ask user to enable!");
            //show dialog to allow user to enable location settings
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle("GPS Disabled");
            dialog.setMessage("Location services must be enabled. Enable now?").setCancelable(false);

            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), SET_LOCATION_REQUEST);
                }
            });

            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) { //do nothing
                    //TODO: disable login btn and disallow user to proceed
                    //TODO: alert user with toast or dialog, on user network/GPS enabled, call onCreate again
                }
            });
            dialog.show();
            return false;
        } else if (gpsEnabled) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
            Log.d("LOCATION (GPS)", LocationManager.GPS_PROVIDER);
            //TODO: in MapsActivity, do the following:
            //if(locationManager!=null){
            //  gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            //  location=gpsLocation;
            //}
        }else if (networkEnabled){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,10,this);
            Log.d("LOCATION (provider)", LocationManager.NETWORK_PROVIDER);
            //TODO: in MapsActivity, do the following:
            //if(locationManager!=null){
            //  networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            //  location=networkLocation;
            //}
        }
        return true;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==SET_LOCATION_REQUEST) {
            switch (requestCode) {
                case 1:
                    break;
            }
        }
    }

    public boolean checkLocationPermission() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

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

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(dataReceiver);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(dataReceiver);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}

