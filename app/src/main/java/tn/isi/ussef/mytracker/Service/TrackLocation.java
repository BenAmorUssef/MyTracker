package tn.isi.ussef.mytracker.Service;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by Ussef on 3/30/2017.
 */

public class TrackLocation implements LocationListener {
    public static Location location;
    public static boolean isRunning = false;
    public TrackLocation(){
        isRunning = true;
    }

    @Override
    public void onLocationChanged(Location loc) {
        location = loc;
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
