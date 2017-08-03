package com.noorsy.lightbulb.trainalarm;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.Locale;

/**
 * Created by Noor Syed on 8/2/2017.
 */
public class Util {

    public LatLng getLatLngFromString(String addressName, Context context, Locale locale){
        Geocoder geocoder = new Geocoder(context, locale);
        Address address=null;
        try {
            address = geocoder.getFromLocationName(addressName, 1).get(0);
        }
        catch (IOException e){
            Log.d("TAG", e.toString());
        }
        LatLng coordinates = new LatLng(address.getLatitude(), address.getLongitude());
        return  coordinates;
    }
}
