package com.watsonlogic.malenah.malenah3;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.google.android.gms.maps.model.Marker;
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
    private LatLng defaultLatLng = new LatLng(45.52, -122.6819);
    private LatLng userLatLng = null;
    private GoogleMap map;
    private Marker userMarker;
    ListAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getDataFromFindServicesFragment();
        computeDistances();
        setUpListView();
    }

    public void computeDistances(){
        Log.d("AsyncTask","compute distances");
        ComputeDistancesAsyncTask a = new ComputeDistancesAsyncTask(MapsActivity.this, rowItems);
        try{
            a.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        placeUserMarker();
        placeItemMarkers();
    }

    public void placeUserMarker() {
        if (location != null && userLatLng != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 14));
            userMarker = map.addMarker(new MarkerOptions()
                    .position(userLatLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .title("You are here"));

        } else {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 14));
            userMarker = map.addMarker(new MarkerOptions()
                    .position(defaultLatLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .title("You are here"));
        }
    }

    public void placeItemMarkers() {
        if (rowItems != null && rowItems.size() > 0)
            for (RowItem ri : rowItems) {
                Log.d("MapsActivity (marker)", ri.getLatitude() + " " + ri.getLongitude());
                ri.setMapMarker(map.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        .position(new LatLng(ri.getLatitude(), ri.getLongitude()))
                        .title(ri.getFirstName()+ " "+ri.getLastName())));
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

    protected void getDataFromFindServicesFragment() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            try {
                user = (User) bundle.getSerializable("user");
                rowItems = bundle.getParcelableArrayList("items");
                location = bundle.getParcelable("location");
                Log.d("MapsActivity", "user=" + user);
                Log.d("MapsActivity", "rowItems=" + rowItems);
                if (location != null) {
                    userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    Log.d("MapsActivity", "location=" + location);
                }
                for (int i = 0; i < rowItems.size(); i++) {
                    Log.d("MapsActivity", "name=" + rowItems.get(i).getFirstName()+rowItems.get(i).getLastName());
                    Log.d("MapsActivity", "isFave=" + rowItems.get(i).isFavourited());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    protected void updateMapCenter(LatLng l, Marker marker) {
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(l, 14));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(l, 14));
        //TODO: animate camera instead of move
        marker.showInfoWindow();
    }

    protected void setUpListView() {
        adapter = new CustomAdapter(this, rowItems, user);
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RowItem ri = (RowItem) list.getItemAtPosition(position); //clicked item's value
                //Toast.makeText(getApplicationContext(), "Clicked position :" + position + "  ListItem : " + ri.getName(), Toast.LENGTH_LONG).show();
                updateMapCenter(new LatLng(ri.getLatitude(), ri.getLongitude()), ri.getMapMarker());
            }
        });
    }

    public void distancesDone(ArrayList<RowItem> rowItems) {
        Log.d("AsyncTask","Back in MapsActivity");
        this.rowItems = rowItems;
        ((ArrayAdapter) adapter).notifyDataSetChanged();
    }
}