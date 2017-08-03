package com.noorsy.lightbulb.trainalarm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.app.Service;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import android.Manifest;

import java.util.Locale;

/**
 * Created by Noor Syed on 7/30/2017.
 */


public class MonitoringService extends IntentService implements LocationListener  {

    public MonitoringService() {
        super("MonitoringService");
    }

    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;

    String d;
    SharedPreferences prefs;
    public boolean arrived = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationManager mLocationManager = null;
    Location mLastLocation;

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            Log.d("TAG", "LOCATION SERVICE ACTIVATED");
            prefs  = PreferenceManager.getDefaultSharedPreferences(this);
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

                String r = prefs.getString("ringetone_preference", "");
                Boolean vibrate = prefs.getBoolean("vibrate_preference", true);
                Boolean batterySaver = prefs.getBoolean("battery_saver_preference", false);
                float stationRadius = Float.parseFloat(prefs.getString("station_radius_preference", "1"));
                Integer updateInterval = Integer.parseInt(prefs.getString("update_interval_preference", "10"));

                updateInterval = updateInterval * 1000;
                arrived = false;

            while (!arrived){
                Log.d("TAG","CHECKING LOCATION");

                Thread.sleep(updateInterval);
            }
            //Log.d("TAG", d);
        }
        catch (InterruptedException e) {
            // Restore interrupt status.
            Thread.currentThread().interrupt();
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, startId, startId);
        d =intent.getStringExtra("DESTINATION");
        Util u = new Util();
        LatLng location = u.getLatLngFromString(d,this,Locale.getDefault());
        Log.d("TAG", "MONITORING SERVICE ONSTARTCOMMAND METHOD:"+location.toString());
        return START_STICKY;
    }

    @Override
    public void onLocationChanged(Location location)     {
        // do stuff here with location object
    }

}

