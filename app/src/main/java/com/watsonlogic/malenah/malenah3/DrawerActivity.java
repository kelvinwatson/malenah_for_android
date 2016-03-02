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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class DrawerActivity extends AppCompatActivity
        implements  MyFavoritesFragment.OnFragmentInteractionListener,
        FindServicesFragment.OnFragmentInteractionListener,
        LogoutFragment.OnFragmentInteractionListener,
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        android.location.LocationListener  {

    private ArrayList<RowItem> items = new ArrayList<RowItem>();
    public ArrayList<RowItem> physicians = new ArrayList<RowItem>();
    public ArrayList<RowItem> nurses = new ArrayList<RowItem>();
    public ArrayList<RowItem> chiropractors = new ArrayList<RowItem>();
    private ArrayList<RowItem> favorites = new ArrayList<RowItem>();
    private User user = null;
    public String allProvidersStr = new String();
    public String userInfoStr = new String();
    private LocationManager locationManager;
    private Location location = null;
    private Context context;
    private JSONObject userObj = null;

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

        //TODO: currently uses dummy data, but should be using user and items from
        // database data. This data originates from MainActivity's call to a service, which broadcasts it
        // back to MainActivity, and MainActivity then passes the data as an intent to this (DrawerActivity)'s onStart
        // method, which extracts the data and saves it to variables in this class. So, we should be
        // using that data, passing user and items array lists that were retrieved in this
        // activity's onStart() method*/

        //DUMMY DATA
        //user.setId(1325);
        //user.name("Kelvin");
        //user.setLastName("Watson");
        //user.setFavorites(null);

        /*RowItem r0 = new RowItem("physicians",
                0,
                "http://images.onlysimchas.com.s3.amazonaws.com/uploads/2015/09/doctor.png?crop=faces&w=&fit=crop",
                "Amir",
                "Nassim",
                "MD",
                "Neurosurgery",
                "AN and Associates!",
                "12",
                "ABC Street",
                "Portland",
                "OR",
                "United States",
                "97201",
                "good with brains!",
                45.5171,
                -122.6811,
                0.9,
                false);
        RowItem r1 = new RowItem("physicians",
                1,
                "http://simon-cen.com/dev/5204/SJGH/Images/Doctors/woman_doctor_02.png",
                "Olivia",
                "Benson",
                "MD",
                "Family Practice",
                "Benson Inc.",
                33,
                8913,
                "DEF Boulevard",
                "Portland",
                "OR",
                "United States",
                "97201",
                "Currently accepting new patients!",
                45.5128,
                -122.6853,
                1.5,
                true);
        RowItem r2 = new RowItem("physicians",
                2,
                "http://www.constantinebrown.com/wp-content/uploads/2013/11/photo_21418_20120211.jpg",
                "William",
                "Bose",
                "MD",
                "Pediatrician",
                "Bose Health!",
                111,
                2222,
                "GHIJ Ave",
                "Portland",
                "OR",
                "United States",
                "97201",
                "No longer accepting new patients",
                45.517539,
                -122.679578,
                9.2,
                true);*/
        /* Send the data to MapsActivity */
        //TODO: May need to empty out the items arraylist as it seems to append data without
        //regard to whether the item already exists in the arraylist
        /*if(!items.isEmpty()){
            items.clear(); //unsure if this is needed when finally using real data
        }
        items.add(r0); items.add(r1); items.add(r2);*/
        mapsIntent.putExtra("user", (Serializable) user);
        if(v.getId()==R.id.physicians) {
            mapsIntent.putExtra("items", physicians);
        } else if(v.getId()==R.id.nurses){
            mapsIntent.putExtra("items",nurses);
        }else if(v.getId()==R.id.chiropractors){
            mapsIntent.putExtra("items",chiropractors);
        }
        mapsIntent.putExtra("location", location);
        Log.d("DRAWER", "items=" + physicians.toString());
        Log.d("DRAWER", "items=" + nurses.toString());
        /*for(int i=0; i<items.size(); i++){
            Log.d("DrawerActivity", "fName=" + items.get(i).getFirstName());
            Log.d("DrawerActivity", "lName=" + items.get(i).getLastName());
            Log.d("DrawerActivity", "isFave=" + items.get(i).isFavourited());
        }*/
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
    }

    private void parseProviders(String allProvidersStr){
        JSONArray allProvidersJSONArr = null;
        try{
            allProvidersJSONArr = new JSONArray(allProvidersStr);
            Log.d("DRAWER,allProJSONArr=",allProvidersJSONArr.toString());
        } catch(JSONException e){
            e.printStackTrace();
        }
        System.out.println("DRAWER,allProJSONArrLen" + allProvidersJSONArr.length());
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

    //TODO: Compare favorites array and physicians/nurses/chiropractors array
    //TODO: Set favorited to the physicians/nurses/chiropractors array
    private void setFavoriteProviders(){
        for(RowItem f : favorites){
            for(RowItem p : physicians){
                //if equal, set favorited to true in physicians array
            }
            for(RowItem n : nurses){
                //if equal, set favorited to true in physicians array

            }
            for(RowItem c : chiropractors){
                //if equal, set favorited to true in physicians array

            }
        }
    }

    private void parseUser(String userInfoStr){
        try{
            userObj = new JSONObject(userInfoStr);
            long key = userObj.getLong("key");
            String userId = userObj.getString("user_id");
            String email = userObj.getString("email");
            String faves = userObj.getString("favorites");
            JSONArray favoriteProvidersArr = new JSONArray(faves);

            Log.d("DRAWER parseU",""+key);
            Log.d("DRAWER parseU",userId);
            Log.d("DRAWER parseU",""+email);
            Log.d("DRAWER parseU",faves);
            Log.d("DRAWER parseU",""+favoriteProvidersArr);

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
            Log.d("DRAWER parseU",""+favorites);


            //TODO: parse favorites private ArrayList<RowItem> favorites

            //TODO: call user constructor'
        } catch(JSONException e){
            e.printStackTrace();
        }
    }

    protected void getDataFromMain(){
        /*TODO: Get all data as a string from MainActivity, parse to items array, and save data */
        final Bundle b = getIntent().getExtras(); //retrieve data on first onStart only
        if(b!=null){
            allProvidersStr=(String)b.getString("allProviders");
            userInfoStr=(String)b.getString("userInfo");
            Log.d("DRAWER GOTS",allProvidersStr);
            Log.d("DRAWER GOTS",userInfoStr);

            parseProviders(allProvidersStr);
            parseUser(userInfoStr);

            /*JSONArray allProvidersJSONArr = null;
            try{
                allProvidersJSONArr = new JSONArray(allProvidersStr);
                Log.d("DRAWER,allProJSONArr=",allProvidersJSONArr.toString());
            } catch(JSONException e){
                e.printStackTrace();
            }
            System.out.println("DRAWER,allProJSONArrLen"+allProvidersJSONArr.length());
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
            }*/
        } else{
            return;
        }

    }


    protected void locationDone(Location location) {
        this.location = location;
        Log.d("DrawerActivity(locDone)", location.getLatitude()+" "+location.getLongitude());
        if(location != null){
            onLocationChanged(location);
        }
    }

    //http://stackoverflow.com/questions/15997079/getlastknownlocation-always-return-null-after-i-re-install-the-apk-file-via-ecli
    protected void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            Log.d("LOCATION","PERMISSION NOT SET!");
            return;
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gpsEnabled||networkEnabled) {
            if(networkEnabled){
                Log.d("LOCATION", "NETWORK Enabled");
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, this);
                if(locationManager!=null){
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
            if(location==null && gpsEnabled){
                Log.d("LOCATION", "GPS Enabled");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
                if(locationManager!=null){
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }
            if (location != null) {
                Log.d("LOCATION", "Using last known location!");
            } else { //get location from GPS, or using ip-api.com/json or use Portland as location fallback
                location = new Location("");
                Log.d("LOCATION", "Using ip-api.com/json!");
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
                //TODO: if(above call fail put in the portland lat lng
            }
        } else {
            Log.d("LOCATION","Neither NETWORK_PROVIDER nor GPS enabled!"); //unlikely due to mainActivity's check
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

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        /*TODO: Set data to be sent with the fragment */
        Fragment f = null;

        /*Test items*/
        /*RowItem r0 = new RowItem("physicians",
                0,
                "http://images.onlysimchas.com.s3.amazonaws.com/uploads/2015/09/doctor.png?crop=faces&w=&fit=crop",
                "Amir",
                "Nassim",
                "MD",
                "Neurosurgery",
                "Some Organization",
                12,
                3456,
                "ABC Street",
                "Portland",
                "OR",
                "United States",
                "97201",
                "good with brains!",
                45.6,
                -123,
                0.9,
                false);
        RowItem r1 = new RowItem("physicians",
                1,
                "http://simon-cen.com/dev/5204/SJGH/Images/Doctors/woman_doctor_02.png",
                "Olivia",
                "Benson",
                "MD",
                "Family Practice",
                "An Organization!",
                33,
                8913,
                "DEF Boulevard",
                "Portland",
                "OR",
                "United States",
                "97201",
                "Currently accepting new patients!",
                45.8,
                -122.3,
                1.5,
                true);
        RowItem r2 = new RowItem("physicians",
                2,
                "http://www.constantinebrown.com/wp-content/uploads/2013/11/photo_21418_20120211.jpg",
                "William",
                "Bose",
                "MD",
                "Pediatrician",
                "Reliable Inc!",
                111,
                2222,
                "GHIJ Ave",
                "Portland",
                "OR",
                "United States",
                "97201",
                "No longer accepting new patients",
                45.6,
                -122.7,
                9.2,
                true);*/
        switch(position){
            case 0: {
                mTitle = "Find Services";
                Bundle bundle = new Bundle();
                //TEST: passing in arraylist of data to maps fragment
                /*ArrayList<RowItem> testList = new ArrayList<RowItem>();
                testList.add(r0);
                testList.add(r1);
                testList.add(r2);*/
                //bundle.putParcelableArrayList("testArrayList",testList);
                f = FindServicesFragment.newInstance("", "");//load fragment 0
                f.setArguments(bundle);
                break;
            }case 1: {
                mTitle = "My Favorites";
                Bundle bundle = new Bundle();
                //(WORKS) TEST: bundle.putString("somePrettyKey", "someBeautifulValue");
                //(WORKS) TEST: Passing in array list to favorites fragment
                /*ArrayList<RowItem> testList = new ArrayList<RowItem>();
                testList.add(r0);
                testList.add(r1);
                bundle.putParcelableArrayList("testArrayList", testList);*/
                f = MyFavoritesFragment.newInstance("", "");//load fragment 0
                f.setArguments(bundle);
                break;
            }case 2: {
                mTitle = "Logout";
                f = LogoutFragment.newInstance("", "");//load fragment 0
                break;
            }default:{
                mTitle = "Find Services";
                f = FindServicesFragment.newInstance("", "");//load fragment 0
                break;
            }
        }
        fragmentManager.beginTransaction().replace(R.id.container, f).commit();
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
