package tn.isi.ussef.mytracker.Activity;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import tn.isi.ussef.mytracker.R;
import tn.isi.ussef.mytracker.Utils.GlobalInfo;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference dbRef;
    private LatLng currentPlace;
    private String lastOnlineDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        dbRef = FirebaseDatabase.getInstance().getReference();

        Bundle b = getIntent().getExtras();
        String phoneNb = b.get("PhoneNumber").toString();
        loadLocation(phoneNb);

    }

    void loadLocation(String phoneNumber){
        dbRef.child("Users").child(phoneNumber).child("Location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();
                if (td == null) {
                    currentPlace = new LatLng(-33.867, 151.206);
                    lastOnlineDate = "This is not correct !";

                }else {
                double lat = Double.parseDouble(td.get("lat").toString());
                    double lag = Double.parseDouble(td.get("log").toString());
                    currentPlace = new LatLng(lat, lag);
                    lastOnlineDate = td.get("LastOnlineDate").toString();
                    currentPlace = new LatLng(lat, lag);
                    lastOnlineDate = td.get("LastOnlineDate").toString();
                }
                loadMap();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        GlobalInfo globalInfo = new GlobalInfo(this);
        globalInfo.LoadData();
    }
    void loadMap(){
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        mMap.addMarker(new MarkerOptions().position(currentPlace).title(currentPlace.toString()+"@" + lastOnlineDate));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPlace, 150));
    }
}
