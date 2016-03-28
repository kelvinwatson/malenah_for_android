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
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

import java.util.LinkedHashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements LocationListener, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    static final int SET_LOCATION_REQUEST = 1;  // The request code
    private static final int RC_SIGN_IN = 9001;
    final Context context = this;
    private LocationManager locationManager;
    private boolean gpsEnabled;
    private boolean networkEnabled;
    private DataReceiver dataReceiver;
    private String providers;
    private String userInfo;
    private Button startButton;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startButton = (Button) findViewById(R.id.startButton); //get reference to button
        startButton.setVisibility(View.GONE);
        loadIcon();
        configureGoogleLogin();
    }

    public void configureGoogleLogin() {
        // Configure sign-in to request
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        // Access Google Sign-In API
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Customize sign-in button.
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        findViewById(R.id.sign_in_button).setOnClickListener(this);
    }

    public void loadIcon() {
        ImageView titleIcon = (ImageView) findViewById(R.id.titleIcon); //get reference to rowIcon
        Picasso.with(context)
                .load(R.drawable.malenah_icon)
                .fit()
                .centerInside()
                .into(titleIcon);
    }


    private void promptUserToEnableConnection(){
        DialogInterface.OnClickListener p = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        };
        DialogInterface.OnClickListener n = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                MainActivity.this.finish();
            }
        };
        generateDialog("Unable to connect",
                "A network connection is required. Please turn on mobile network or WiFi in Settings.",
                "Settings", "Cancel", p, n);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startButton.setEnabled(false); //disable start button until all data retrieved
        if (!isFullyConnected(getApplicationContext())) { //check connection
            Log.d("NETWORK", "not connected");
            promptUserToEnableConnection();
            return;
        } else { //internet connected
            getData();
            Log.d("NETWORK", "connected");
            if (!enableLocation()) { //check Location
                return;
            }
        }
        mGoogleApiClient.connect();
    }

    protected void getData() {
        /* Register receiver for data */
        IntentFilter filter = new IntentFilter("com.watsonlogic.malenah.malenah3.providers");
        dataReceiver = new DataReceiver();
        registerReceiver(dataReceiver, filter);
        /* Start the service to fetch data */
        Intent fetchIntent = new Intent(this, FetchAllDataService.class);
        startService(fetchIntent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            // ...
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public void launchDrawerActivity(View v) {
        Intent i = new Intent(MainActivity.this, DrawerActivity.class);
        i.putExtra("allProviders", providers);
        i.putExtra("user", userInfo);
        startActivity(i);
    }

    //Source: http://stackoverflow.com/questions/28168867/check-internet-status-from-the-main-activity
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public boolean isConnectedMobile(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    public boolean isConnectedWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public boolean isFullyConnected(Context context) {
        if (isNetworkAvailable(context)) {
            Log.d("NETWORK", "available");
            if (isConnectedMobile(context)) {
                Log.d("NETWORK", "mobile connected");
                return true;
            }
            if (isConnectedWifi(context)) {
                Log.d("NETWORK", "wifi connected");
                return true;
            }
        }
        return false;
    }

    private boolean checkLocationPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
        if ((Build.VERSION.SDK_INT >= 23) &&
                (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            return false;
        }
        if (!checkLocationPermission()) {
            Log.d("LOCATION (permission)", "permission denied!");
            return false;
        }
        return true;
    }

    protected boolean enableLocation() {
        if (!checkLocationPermissions()){ //SHORT CIRCUIT DISABLED PERMISSIONS
            return false;
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); //check GPS status
        networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER); // check network status
        if (!gpsEnabled && !networkEnabled) {
            promptUserToEnableLocation(); //GPS disabled, ask user to enable
            return false;
        } else if (gpsEnabled) {
            try{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
            } catch(SecurityException e){
                e.printStackTrace();
            }
            Log.i("LOCATION (GPS)", LocationManager.GPS_PROVIDER);
        } else if (networkEnabled) {
            try {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, this);
            }catch(SecurityException e){
                e.printStackTrace();
            }
            Log.i("LOCATION (provider)", LocationManager.NETWORK_PROVIDER);
        }
        return true;
    }

    private void promptUserToEnableLocation(){
        DialogInterface.OnClickListener p = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), SET_LOCATION_REQUEST);
            }
        };
        DialogInterface.OnClickListener n = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { //do nothing
            }
        };
        generateDialog("GPS Disabled", "Location services must be enabled. Enable now?", "Yes", "No", p, n);
    }

    private void generateDialog(String title, String msg, String pos, String neg,
                                DialogInterface.OnClickListener diop, DialogInterface.OnClickListener dion){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(msg).setCancelable(false);
        dialog.setPositiveButton(pos, diop);
        dialog.setNegativeButton(neg, dion);
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == SET_LOCATION_REQUEST) {
            switch (requestCode) {
                case 1:
                    break;
            }
        }
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    public void verifyTokenDone(String userInformation) {
        userInfo = userInformation;
        Log.d("VerifyToken done", userInfo);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            signInButton.setVisibility(View.GONE);
            GoogleSignInAccount acct = result.getSignInAccount();

            String idToken = acct.getIdToken();
            String email = acct.getEmail();
            String name = acct.getDisplayName();

            Map<String, String> postParams = new LinkedHashMap<>();
            postParams.put("id_token", idToken); //pass to App Engine Python API
            postParams.put("email", email);
            postParams.put("name", name);

            //Log.d("VerifyTokenAsyncTask", "execute async task from MainActivity()");
            new VerifyTokenAsyncTask(MainActivity.this, postParams).execute();

            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();

            startButton.setVisibility(View.VISIBLE);
            startButton.setEnabled(true); //disable start button until logged in and all data retrieved
        } else {
            // Sign in failed
            startButton.setEnabled(false);
            Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG).show();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(dataReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class DataReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            providers = intent.getStringExtra("providers");
        }
    }
}
