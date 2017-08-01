package com.noorsy.lightbulb.trainalarm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Locale;

/**
 * Created by Noor Syed on 7/30/2017.
 */
public class MonitoringService extends IntentService {

    public MonitoringService() {
        super("MonitoringService");
    }

    String d;
    SharedPreferences prefs;
    public boolean arrived = false;

    @Override
    protected void onHandleIntent(Intent intent) {
        // Normally we would do some work here, like download a file.
        // For our sample, we just sleep for 5 seconds.
        try {
            Log.d("TAG", "LOCATION SERVICE ACTIVATED");
            prefs  = PreferenceManager.getDefaultSharedPreferences(this);

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
        Log.d("TAG","MONITORING SERVICE: "+d);
        return START_STICKY;
    }

}
