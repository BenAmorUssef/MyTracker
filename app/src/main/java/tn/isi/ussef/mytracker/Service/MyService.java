package tn.isi.ussef.mytracker.Service;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import tn.isi.ussef.mytracker.Utils.GlobalInfo;

/**
 * Created by Ussef on 3/30/2017.
 */

public class MyService extends IntentService {
    public static boolean isRunning = false;
    DatabaseReference dbRef;
    LocationManager mLocationManager;

    public MyService() {
        super("MyService");
        isRunning = true;
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Toast.makeText(this, getLastKnownLocation().toString(), Toast.LENGTH_SHORT).show();
        TrackLocation.location = getLastKnownLocation();
        dbRef.child("Users").child(GlobalInfo.PhoneNumber).child("Updates").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dbRef.child("Users").child(GlobalInfo.PhoneNumber).child("Location").child("lat").setValue(TrackLocation.location.getLatitude());
                dbRef.child("Users").child(GlobalInfo.PhoneNumber).child("Location").child("log").setValue(TrackLocation.location.getLongitude());

                DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:MM:ss");
                Date date = new Date();

                dbRef.child("Users").child(GlobalInfo.PhoneNumber).child("Location").child("LastOnlineDate").setValue(df.format(date).toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return new Location("empty");
            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

}
