package com.masbie.weather;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by AnangHanafi on 19/10/2016.
 */
public class MyLocListener implements LocationListener {

    @Override
    public void onLocationChanged(Location location) {
        if (location!=null){
            Log.e("Latitude :"," "+location.getLatitude());
            Log.e("Longitude :"," "+location.getLongitude());

        }
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
}
