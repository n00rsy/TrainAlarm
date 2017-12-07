package com.noorsy.lightbulb.trainalarm;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Locale;

public class MonitoringActivity extends AppCompatActivity implements OnMapReadyCallback {

    //private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mMap;
    String d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        /*
        Intent intent = new Intent(this, MonitoringService.class);
        startService(intent);
        */
        //set destination text
        Intent intent = getIntent();
        d =intent.getStringExtra("DESTINATION");
        TextView destinationText = (TextView)findViewById(R.id.destination_text);
        destinationText.setText("Traveling to: "+d);


        final Button cancelButton = (Button)findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                backDialog();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Util u = new Util();
        LatLng location = u.getLatLngFromString(d, this, Locale.getDefault());

        /*
        Address address = null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            address = geocoder.getFromLocationName(d, 1).get(0);
        }
        catch (IOException e){
            Log.d("TAG", e.toString());
        }

        //double lat =address.getLatitude();
        //double lng =address.getLongitude();
        LatLng location = new LatLng(address.getLatitude(), address.getLongitude());
        Log.d("TAG",location.toString());
*/
        mMap.addMarker(new MarkerOptions().position(location).title("Destination"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        //map.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
    }

    @Override
    public void onBackPressed()
    {
        backDialog();
    }

    @Override
    public void onDestroy(){
        Intent serviceIntent = new Intent(MonitoringActivity.this, MonitoringService.class);
        stopService(serviceIntent);
        Log.d("TAG","MONITORING ACTIVITY ONDESTROY METHOD CALLED");
        super.onDestroy();

    }

    void backDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(MonitoringActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Are you sure you want to exit?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Stay",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Exit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        stopService(new Intent(MonitoringActivity.this, MonitoringService.class));
                        Intent i = new Intent(MonitoringActivity.this, MainActivity.class);
                        MonitoringActivity.this.startActivity(i);
                    }
                });
        alertDialog.show();
    }
}
