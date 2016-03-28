package com.watsonlogic.malenah.malenah3;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class DrawerActivity extends AppCompatActivity implements
        FindServicesFragment.OnFragmentInteractionListener,
        MyFavoritesFragment.OnFragmentInteractionListener,
        MyProfileFragment.OnFragmentInteractionListener,
        LogoutFragment.OnFragmentInteractionListener,
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        android.location.LocationListener,
        GoogleApiClient.OnConnectionFailedListener {

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public ArrayList<RowItem> physicians = new ArrayList<RowItem>();
    public ArrayList<RowItem> nurses = new ArrayList<RowItem>();
    public ArrayList<RowItem> chiropractors = new ArrayList<RowItem>();
    private ArrayList<RowItem> favorites = new ArrayList<RowItem>();
    private User user = null;
    public String allProvidersStr = new String();
    public String userInfoStr = new String();
    private LocationManager locationManager;
    private Location location = null;
    private JSONObject userObj = null;
    private GoogleApiClient mGoogleApiClient;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;


    public void launchMapsActivity(View v){
        Intent mapsIntent = new Intent(this,MapsActivity.class);
        mapsIntent.putExtra("user", user);
        if(v.getId()==R.id.physicians) {
            mapsIntent.putExtra("items", physicians);
        } else if(v.getId()==R.id.nurses){
            mapsIntent.putExtra("items",nurses);
        }else if(v.getId()==R.id.chiropractors){
            mapsIntent.putExtra("items",chiropractors);
        }
        mapsIntent.putExtra("location", location);
        startActivity(mapsIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        getLocation();

        getDataFromMain();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void parseProviders(String allProvidersStr){
        JSONArray allProvidersJSONArr = null;
        try{
            allProvidersJSONArr = new JSONArray(allProvidersStr);
        } catch(JSONException e){
            e.printStackTrace();
        }
        for(int i=0,len=allProvidersJSONArr.length(); i<len; i++){
            JSONObject obj = null;
            try{
                obj = allProvidersJSONArr.getJSONObject(i);
                JSONArray spA = obj.getJSONArray("specializations");
                ArrayList<String> spL = new ArrayList<String>();
                for(int k=0,le=spA.length();k<le;k++){
                    spL.add(((JSONObject)spA.get(k)).getString("name"));
                }
                RowItem ri = new RowItem(
                        obj.getString("category"),
                        obj.getLong("key"),
                        obj.getString("icon_url"),
                        obj.getString("first_name"),
                        obj.getString("last_name"),
                        obj.getString("designation"),
                        spL,
                        obj.getString("organization"),
                        obj.getString("building"),
                        obj.getString("street"),
                        obj.getString("city"),
                        obj.getString("state"),
                        obj.getString("country"),
                        obj.getString("zipcode"),
                        obj.getString("notes"),
                        obj.getDouble("latitude"),
                        obj.getDouble("longitude"),
                        0.0,
                        false
                );
                if(obj.getString("category").equals("Physician")){
                    physicians.add(ri);
                } else if(obj.getString("category").equals("Nurse Practitioner") || obj.getString("category").equals("Nurse")){
                    nurses.add(ri);
                } else if(obj.getString("category").equals("Chiropractor")){
                    chiropractors.add(ri);
                }
            } catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

    private void setFavoriteProviders(){
        for(RowItem f : favorites){
            for(RowItem p : physicians){
                if(f.getId()==p.getId()){      //set favorited to true in physicians array
                    p.setFavourited(true);
                }
            }
            for(RowItem n : nurses){
                if(f.getId()==n.getId()){       //set favorited to true in nurses array
                    n.setFavourited(true);
                }
            }
            for(RowItem c : chiropractors){
                if(f.getId()==c.getId()){           //set favorited to true in chiropractor array
                    c.setFavourited(true);
                }
            }
        }
    }

    private void debugPrint(){
        for(RowItem p : physicians){
            Log.d("DRAWER",p.getFirstName()+" "+p.isFavourited());
        }
        for(RowItem n : nurses){
            Log.d("DRAWER",n.getFirstName()+" "+n.isFavourited());
        }
        for(RowItem c : chiropractors){
            Log.d("DRAWER",c.getFirstName()+" "+c.isFavourited());
        }
    }

    private void parseUser(String userInfoStr){
        try{
            userObj = new JSONObject(userInfoStr);
            long key = userObj.getLong("key");
            String userId = userObj.getString("user_id");
            String name = userObj.getString("name");
            String email = userObj.getString("email");
            String faves = userObj.getString("favorites");
            JSONArray favoriteProvidersArr = new JSONArray(faves);

            favorites.clear();

            for(int i=0,len=favoriteProvidersArr.length(); i<len; i++){
                JSONObject obj = null;
                obj = favoriteProvidersArr.getJSONObject(i);
                JSONArray spA = obj.getJSONArray("specializations");
                ArrayList<String> spL = new ArrayList<String>();
                for (int k = 0, le = spA.length(); k < le; k++) {
                    spL.add(((JSONObject) spA.get(k)).getString("name"));
                }
                RowItem ri = new RowItem(
                        obj.getString("category"),
                        obj.getLong("key"),
                        obj.getString("icon_url"),
                        obj.getString("first_name"),
                        obj.getString("last_name"),
                        obj.getString("designation"),
                        spL,
                        obj.getString("organization"),
                        obj.getString("building"),
                        obj.getString("street"),
                        obj.getString("city"),
                        obj.getString("state"),
                        obj.getString("country"),
                        obj.getString("zipcode"),
                        obj.getString("notes"),
                        obj.getDouble("latitude"),
                        obj.getDouble("longitude"),
                        0.0,
                        true
                );
                favorites.add(ri);
            }
            user = new User(key,userId,email,name,favorites);//Create user object
        } catch(JSONException e){
            e.printStackTrace();
        }
    }

    protected void getDataFromMain(){
        final Bundle b = getIntent().getExtras(); //retrieve data on first onStart only
        if(b!=null){
            allProvidersStr=(String)b.getString("allProviders");
            userInfoStr=(String)b.getString("user");
            parseProviders(allProvidersStr);
            parseUser(userInfoStr);
            setFavoriteProviders();
        } else{
            return;
        }

    }


    protected void fetchUserDone(String userInfoStr) {
        parseUser(userInfoStr);
    }


    protected void locationDone(Location location) {
        this.location = location;
        Log.i("DrawerActivity(locDone)", location.getLatitude() + " " + location.getLongitude());
        if(location != null){
            onLocationChanged(location);
        }
    }

    //http://stackoverflow.com/questions/15997079/getlastknownlocation-always-return-null-after-i-re-install-the-apk-file-via-ecli
    protected void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            Log.i("LOCATION","PERMISSION NOT SET!");
            return;
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gpsEnabled||networkEnabled) {
            if(networkEnabled){
                Log.i("LOCATION", "NETWORK Enabled");
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, this);
                if(locationManager!=null){
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
            if(location==null && gpsEnabled){
                Log.i("LOCATION", "GPS Enabled");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
                if(locationManager!=null){
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }
            if (location != null) {
                Log.i("LOCATION", "Using last known location!");
            } else { //get location from GPS, or using ip-api.com/json or use Portland as location fallback
                location = new Location("");
                Log.i("LOCATION", "Using ip-api.com/json!");
                URL url = null;
                try {
                    url = new URL("http://ip-api.com/json");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    setFailSafeLocation();
                }
                SetLocationFromIPAsyncTask asyncTask = new SetLocationFromIPAsyncTask(DrawerActivity.this,url,location); //retrieve and parse JSON
                try {
                    asyncTask.execute(); //set location asynchronously
                } catch (Exception e) {
                    setFailSafeLocation();
                }
            }
        } else {
            Log.i("LOCATION","Neither NETWORK_PROVIDER nor GPS enabled!"); //unlikely due to mainActivity's check
            setFailSafeLocation();
        }
    }

    private void setFailSafeLocation(){
        location.setLatitude(45.5171);
        location.setLongitude(-122.6819);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> postParams = new LinkedHashMap<>();
        postParams.put("post_action", "get_user");
        postParams.put("user_id", user.getUserId());
        new FetchUserAsyncTask(DrawerActivity.this, postParams).execute();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment f = null;
        switch(position){
            case 0: {
                mTitle = "Find Services";
                Bundle bundle = new Bundle();
                f = FindServicesFragment.newInstance("", "");//load fragment 0
                f.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.container, f).commit();
                break;
            }case 1: {
                mTitle = "My Favorites";
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("favorites", favorites);
                f = MyFavoritesFragment.newInstance("", "");
                f.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.container, f).commit();
                break;
            }case 2: {
                mTitle = "My Profile";
                Bundle bundle = new Bundle();
                bundle.putSerializable("user",user);
                f = MyProfileFragment.newInstance("", "");
                f.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.container, f).commit();
                break;
            }case 3: {
                mTitle = "Logout";
                googleSignOut();
                onDestroy();
                break;
            }default:{
                mTitle = "Find Services";
                f = FindServicesFragment.newInstance("", "");//load fragment 0
                fragmentManager.beginTransaction().replace(R.id.container, f).commit();
            }
        }
    }


    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.drawer, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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
    public void onFragmentInteraction(Uri uri) {

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

    public void googleSignOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_drawer, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((DrawerActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
