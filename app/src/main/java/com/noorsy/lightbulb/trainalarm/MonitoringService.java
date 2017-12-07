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
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import android.Manifest;

import java.util.Locale;

/**
 * Created by Noor Syed on 7/30/2017.
 */


public class MonitoringService extends IntentService  {

    public MonitoringService() {
        super("MonitoringService");
    }

    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;

    //String d;
    SharedPreferences prefs;
    public boolean arrived = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationManager mLocationManager = null;
    private LocationCallback mLocationCallback;
    Location mLastLocation;
    Location destination;
    double stationRadiusMeters;
    float [] dist = new float[1];
    String TAG="TAG";

    @Override
    public void onCreate(){
        super.onCreate();
        String TAG = "TAG";
        Log.d(TAG, "onCreate");
        
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.d(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.d(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }


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
                stationRadiusMeters = stationRadius*1609.344;
                arrived = false;


            //Log.d("TAG", d);
        }
        catch (Error e) {
            // Restore interrupt status.
            Thread.currentThread().interrupt();
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, startId, startId);
        String d =intent.getStringExtra("DESTINATION");
        Util u = new Util();

        LatLng location = u.getLatLngFromString(d,this,Locale.getDefault());
        destination = new Location(LocationManager.GPS_PROVIDER);
        destination.setLatitude(location.latitude);
        destination.setLongitude(location.longitude);

        Log.d("TAG", "MONITORING SERVICE ONSTARTCOMMAND METHOD:"+location.toString());
        return START_STICKY;
        
    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    private class LocationListener implements android.location.LocationListener
    {
        String TAG="TAG";
        Location mLastLocation;

        public LocationListener(String provider)
        {
            Log.d(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            Log.d(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            /*
            double lat1 = (double)mLastLocation.getLatitude() ;
            double lng1 = ((double)mLastLocation.getLongitude());
            double lat2 = ((double)destination.getLatitude());
            double lng2 = ((double)destination.getLongitude());
            */
            //float [] dist = new float[1];
            Location.distanceBetween((double)mLastLocation.getLatitude(),
                    (double)mLastLocation.getLongitude(),
                    (double)destination.getLatitude(),
                    (double)destination.getLongitude(), dist);

            if(dist[0]<=stationRadiusMeters){
                Log.d("TAG","ARRIVED, SOUNDING ALARM");
            }
            Log.d("TAG","DISTANCE TO DESTINATION: "+dist[0]);
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.d(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.d(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.d(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public void onDestroy(){
        Log.d(TAG, "MONITORING SERVICE DESTROYED");
    }
}

