package com.watsonlogic.malenah.malenah3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*TODO: Service to fetch data (see getData()) by calling startService(FetchAllDataService)*/


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
