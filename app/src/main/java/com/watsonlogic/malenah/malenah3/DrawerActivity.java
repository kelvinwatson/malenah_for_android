package com.watsonlogic.malenah.malenah3;

import android.app.Activity;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DrawerActivity extends AppCompatActivity
        implements  MyFavoritesFragment.OnFragmentInteractionListener,
                    FindServicesFragment.OnFragmentInteractionListener,
                    LogoutFragment.OnFragmentInteractionListener,
                    NavigationDrawerFragment.NavigationDrawerCallbacks {

    private ArrayList<RowItem> items = new ArrayList<RowItem>();
    private User user = new User();

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
        user.setId(1325);
        user.setFirstName("Kelvin");
        user.setLastName("Watson");
        user.setFavorites(null);

        RowItem r0 = new RowItem("physicians",
                0,
                "http://images.onlysimchas.com.s3.amazonaws.com/uploads/2015/09/doctor.png?crop=faces&w=&fit=crop",
                "Amir Nassim and Associates",
                "MD",
                "Neurosurgery",
                "good with brains!",
                12,
                3456,
                "ABC Street",
                "Portland",
                "OR",
                "United States",
                "97201",
                "good with brains!",
                0.9,
                false);
        RowItem r1 = new RowItem("physicians",
                1,
                "http://simon-cen.com/dev/5204/SJGH/Images/Doctors/woman_doctor_02.png",
                "Sandra Anderson",
                "MD",
                "Family Practice",
                "Celebrating 10 years in practice!",
                33,
                8913,
                "DEF Boulevard",
                "Portland",
                "OR",
                "United States",
                "97201",
                "Currently accepting new patients!",
                1.5,
                true);
        RowItem r2 = new RowItem("physicians",
                2,
                "http://www.constantinebrown.com/wp-content/uploads/2013/11/photo_21418_20120211.jpg",
                "William Bose",
                "MD",
                "Pediatrician",
                "Reliable and caring!",
                111,
                2222,
                "GHIJ Ave",
                "Portland",
                "OR",
                "United States",
                "97201",
                "No longer accepting new patients",
                9.2,
                true);
        items.add(r0); items.add(r1); items.add(r2);
        mapsIntent.putExtra("user", (Serializable) user);
        mapsIntent.putExtra("items", items);
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

    }

    @Override
    protected void onStart() {
        super.onStart();
        /*TODO: Get all data as a string from MainActivity, parse to items array, and save data */
        /*TODO: Parse user data to user object and save */
        final Bundle bundle = getIntent().getExtras(); //retrieve data on first onStart only
        if(bundle != null) {
            String allDataStr = bundle.getString("allData");
            try {
                JSONObject allDataJSON = new JSONObject(allDataStr);
                //items = parseData(allDataJSON, "data");
                //user = parseUser(allDataJSON, "user");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else return; //bundle null , e.g. for subsequent onStarts(), no data is recv'd
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        /*TODO: Set data to be sent with the fragment */
        Fragment f = null;

        /*Test items*/
        RowItem r0 = new RowItem("physicians",
                0,
                "http://images.onlysimchas.com.s3.amazonaws.com/uploads/2015/09/doctor.png?crop=faces&w=&fit=crop",
                "Amir Nassim and Associates",
                "MD",
                "Neurosurgery",
                "good with brains!",
                12,
                3456,
                "ABC Street",
                "Portland",
                "OR",
                "United States",
                "97201",
                "good with brains!",
                0.9,
                false);
        RowItem r1 = new RowItem("physicians",
                1,
                "http://simon-cen.com/dev/5204/SJGH/Images/Doctors/woman_doctor_02.png",
                "Sandra Anderson",
                "MD",
                "Family Practice",
                "Celebrating 10 years in practice!",
                33,
                8913,
                "DEF Boulevard",
                "Portland",
                "OR",
                "United States",
                "97201",
                "Currently accepting new patients!",
                1.5,
                true);
        RowItem r2 = new RowItem("physicians",
                2,
                "http://www.constantinebrown.com/wp-content/uploads/2013/11/photo_21418_20120211.jpg",
                "William Bose",
                "MD",
                "Pediatrician",
                "Reliable and caring!",
                111,
                2222,
                "GHIJ Ave",
                "Portland",
                "OR",
                "United States",
                "97201",
                "No longer accepting new patients",
                9.2,
                true);
        switch(position){
            case 0: {
                mTitle = "Find Services";
                Bundle bundle = new Bundle();
                //TEST: passing in arraylist of data to maps fragment
                ArrayList<RowItem> testList = new ArrayList<RowItem>();
                testList.add(r0);
                testList.add(r1);
                testList.add(r2);
                //bundle.putParcelableArrayList("testArrayList",testList);
                f = FindServicesFragment.newInstance("", "");//load fragment 0
                f.setArguments(bundle);
                break;
            }case 1: {
                mTitle = "My Favorites";
                Bundle bundle = new Bundle();
                //(WORKS) TEST: bundle.putString("somePrettyKey", "someBeautifulValue");
                //(WORKS) TEST: Passing in array list to favorites fragment
                ArrayList<RowItem> testList = new ArrayList<RowItem>();
                testList.add(r0);
                testList.add(r1);
                bundle.putParcelableArrayList("testArrayList", testList);
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
