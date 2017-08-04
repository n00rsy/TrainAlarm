package com.noorsy.lightbulb.trainalarm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.Manifest;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    Place userPlace;
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("TAG", "Place: " + place.getName());
                userPlace = place;
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });

        final Button buttonStart = (Button)findViewById(R.id.button_start);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("TAG", "START BUTTON PRESSED");
                if (userPlace != null) {

                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                            // No explanation needed, we can request the permission.
                            Log.d("TAG","REQUESTING LOCATION PERMISSION");
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                            // app-defined int constant. The callback method gets the
                            // result of the request.
                    }
                        else {
                        startMonitoring();
                    }

                } else {
                    Snackbar.make(v, "Please select a destination.", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d("TAG", "MAIN ACTIVITY RESUMED");
        //userPlace = null;
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        startMonitoring();

                } else {

                    Snackbar.make(getWindow().getDecorView().getRootView(), "You must enable location services for GPS tracking.", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
                return;
            }

        }
    }

    void startMonitoring(){
        Intent ActivityIntent = new Intent(MainActivity.this, MonitoringActivity.class);
        ActivityIntent.putExtra("DESTINATION", userPlace.getAddress().toString());
        MainActivity.this.startActivity(ActivityIntent);

        Intent ServiceIntent = new Intent(MainActivity.this, MonitoringService.class);
        ServiceIntent.putExtra("DESTINATION", userPlace.getAddress().toString());
        startService(ServiceIntent);
    }
}
