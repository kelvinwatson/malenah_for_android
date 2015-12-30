package com.watsonlogic.malenah.malenah3;


import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import java.net.URL;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements LocationListener,OnMapReadyCallback {
    private LocationManager locationManager;
    private String provider;
    private Location location;
    private ListView list;
    private ArrayList<RowItem> rowItems = new ArrayList<RowItem>();
    private URL url;
    private User user = new User();
    private LatLng defaultLatLng = new LatLng(45.52,-122.6819);
    private LatLng userLatLng = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getDataFromFindServicesFragment();
        setUpListView();
    }



    @Override
    public void onMapReady(GoogleMap map) {
        placeUserMarker(map);
        placeItemMarkers(map);
    }

    public void placeUserMarker(GoogleMap map){
        if(location!=null && userLatLng!=null){
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 14));
            map.addMarker(new MarkerOptions()
                .position(userLatLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title("You are here"));

        } else{
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 14));
            map.addMarker(new MarkerOptions()
                .position(defaultLatLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title("You are here"));
        }
    }

    public void placeItemMarkers(GoogleMap map){
        if(rowItems != null && rowItems.size()>0)
            for(RowItem ri : rowItems){
                Log.d("MapsActivity (marker)",ri.getLatitude()+" "+ri.getLongitude());
                map.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        .position(new LatLng(ri.getLatitude(), ri.getLongitude()))
                        .title(ri.getName()));
            }
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

    protected void getDataFromFindServicesFragment(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            try{
                user = (User) bundle.getSerializable("user");
                rowItems = bundle.getParcelableArrayList("items");
                location = bundle.getParcelable("location");
                Log.d("MapsActivity", "user=" + user);
                Log.d("MapsActivity", "rowItems=" + rowItems);
                if(location!=null){
                    userLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                    Log.d("MapsActivity", "location=" + location);
                }
                for (int i = 0; i < rowItems.size(); i++) {
                    Log.d("MapsActivity", "name=" + rowItems.get(i).getName());
                    Log.d("MapsActivity", "isFave=" + rowItems.get(i).isFavourited());
                }
            } catch(Exception e){
                e.printStackTrace();
            }

        }
    }

    protected void setUpListView(){
        ListAdapter adapter = new CustomAdapter(this, rowItems, user);
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RowItem ri = (RowItem) list.getItemAtPosition(position); //clicked item's value
                Toast.makeText(getApplicationContext(), "Position :" + position + "  ListItem : " + ri.getName(), Toast.LENGTH_LONG).show();
            }
        });

    }

//        protected void locationDone(Location location) {
//        this.location = location;
//        Log.d("MapsActivity(locDone)", location.getLatitude() + " " + location.getLongitude());
//        if(location != null){
//            onLocationChanged(location);
//        }
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_maps);
//
//        getDataFromFindServicesFragment();
//        setUpListView();
//
//        setUpMapIfNeeded();
//        setLocation();
//        if(location!=null) {
//            onLocationChanged(location);
//        }
//        placeMarkers();
//    }
//
//    //TODO: Set up markers on map based on rowItems
//    protected void placeMarkers(){
//        mMap.addMarker(new MarkerOptions()
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
//                .position(new LatLng(45.5, -123))
//                .title("BLAH"));
//        if(rowItems != null && rowItems.size()>0)
//            for(RowItem ri : rowItems){
//                Log.d("MapsActivity (marker)",ri.getLatitude()+" "+ri.getLongitude());
//                mMap.addMarker(new MarkerOptions()
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
//                    .position(new LatLng(ri.getLatitude(), ri.getLongitude()))
//                    .title(ri.getName()));
//            }
//    }
//
//
//    protected void setLocation() {
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//            provider = locationManager.getBestProvider(new Criteria(), false);
//            if (provider != null) {
//                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
//                    return;
//                }
//                location = locationManager.getLastKnownLocation(provider);
//                if (location != null) {
//                    Log.d("LOCATION (retrieval)", "Using last known location!");
//                    onLocationChanged(location);
//                } else { //get location using ip-api.com/json or use Portland as location fallback
//                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
//                    location = new Location("");
//                    Log.d("LOCATION (check ptr)", location.toString());
//                    Log.d("LOCATION (retrieval)", "Using ip-api.com/json!");
//                    try {
//                        url = new URL("http://ip-api.com/json");
//                    } catch (MalformedURLException e) {
//                        e.printStackTrace();
//                        setFailSafeLocation();
//                    }
//                    SetLocationFromIPAsyncTask asyncTask = new SetLocationFromIPAsyncTask(MapsActivity.this,url,location); //retrieve and parse JSON
//                    try {
//                        asyncTask.execute(); //set location asynchronously
//                    } catch (Exception e) {
//                        setFailSafeLocation();
//                    }
//                    //TODO: if(above call fail put in the portland lat lng
//                }
//            } else {
//                Log.d("LOCATION (provider)", "provider is null!");
//                setFailSafeLocation();
//            }
//        }
//    }
//
//    protected void locationDone(Location location) {
//        this.location = location;
//        Log.d("MapsActivity(locDone)", location.getLatitude() + " " + location.getLongitude());
//        if(location != null){
//            onLocationChanged(location);
//        }
//        placeMarkers();
//    }
//
//    protected void getDataFromFindServicesFragment(){
//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null) {
//            user = (User) bundle.getSerializable("user");
//            rowItems = bundle.getParcelableArrayList("items");
//            Log.d("MapsActivity", "user=" + user);
//            Log.d("MapsActivity", "rowItems=" + rowItems);
//            for (int i = 0; i < rowItems.size(); i++) {
//                Log.d("MapsActivity", "name=" + rowItems.get(i).getName());
//                Log.d("MapsActivity", "isFave=" + rowItems.get(i).isFavourited());
//            }
//        }
//    }
//
//    protected void setUpListView(){
//        ListAdapter adapter = new CustomAdapter(this, rowItems, user);
//        list = (ListView) findViewById(R.id.list);
//        list.setAdapter(adapter);
//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String itemValue = (String) list.getItemAtPosition(position); //clicked item's value
//                Toast.makeText(getApplicationContext(), "Position :" + position + "  ListItem : " + itemValue, Toast.LENGTH_LONG).show();
//            }
//        });
//
//    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//        setUpMapIfNeeded();
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
//            return;
//        }
//        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, this);
//        }
//        else locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
//    }
//
//    /**
//     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
//     * installed) and the map has not already been instantiated.. This will ensure that we only ever
//     * call {@link #setUpMap()} once when {@link #mMap} is not null.
//     * <p/>
//     * If it isn't installed {@link SupportMapFragment} (and
//     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
//     * install/update the Google Play services APK on their device.
//     * <p/>
//     * A user can return to this FragmentActivity after following the prompt and correctly
//     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
//     * have been completely destroyed during this process (it is likely that it would only be
//     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
//     * method in {@link #onResume()} to guarantee that it will be called.
//     */
//    private void setUpMapIfNeeded() {
//        // Do a null check to confirm that we have not already instantiated the map.
//        if (mMap == null) {
//            // Try to obtain the map from the SupportMapFragment.
//            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
//                    .getMap();
//            // Check if we were successful in obtaining the map.
//            if (mMap != null) {
//                setUpMap();
//            }
//        }
//    }
//
//    /**
//     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
//     * just add a marker near Africa.
//     * <p/>
//     * This should only be called once and when we are sure that {@link #mMap} is not null.
//     */
//    private void setUpMap() {
//        //change zoom level between 2-21
//        if(location==null || (location.getLatitude()==0.0 && location.getLongitude()==0.0)){
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(portlandORLat, portlandORLng), 13));
//            Log.d("MapsActivity animate", "using PDX");
//        } else {
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
//            Log.d("MapsActivity animate", location.getLatitude() + " " + location.getLongitude());
//        }
//
//        //place marker
//        Log.d("MapsActivity","setUpMap(addMarker)");
//        mMap.addMarker(new MarkerOptions()
//            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
//            .position(new LatLng(location.getLatitude(), location.getLongitude()))
//            .title("You are here"));
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        Log.d("onLocationChanged","called");
//        Double lat = location.getLatitude();
//        Double lng = location.getLongitude();
//        mMap.clear();
//        mMap.addMarker(new MarkerOptions()
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
//                .position(new LatLng(lat, lng))
//                .title("You are here"));
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 13));
//        Log.i("LOCATION (latitude)",lat.toString());
//        Log.i("LOCATION (longitude)",lng.toString());
//        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
//        try {
//            List<Address> listAddresses = geocoder.getFromLocation(lat,lng,1);
//            if(listAddresses != null && listAddresses.size()>0){
//                Log.i("LOCATION (place info)",listAddresses.get(0).toString());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//
//    }
//
//    private void setFailSafeLocation(){
//        location.setLatitude(45.5171);
//        location.setLongitude(-122.6819);
//    }
}
