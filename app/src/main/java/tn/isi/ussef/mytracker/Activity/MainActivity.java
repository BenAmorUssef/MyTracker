package tn.isi.ussef.mytracker.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tn.isi.ussef.mytracker.Adapter.TrackerAdapter;
import tn.isi.ussef.mytracker.Model.TrackerItem;
import tn.isi.ussef.mytracker.R;
import tn.isi.ussef.mytracker.Service.MyService;
import tn.isi.ussef.mytracker.Utils.GlobalInfo;
import tn.isi.ussef.mytracker.Service.TrackLocation;
import tn.isi.ussef.mytracker.Utils.Utils;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    ArrayList<TrackerItem> listTrackersData = new ArrayList<TrackerItem>();
    TrackerAdapter myadapter;
    ListView lsTrackers;
    DatabaseReference dbRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GlobalInfo  globalInfo = new GlobalInfo(this);
        globalInfo.LoadData();
        dbRef = FirebaseDatabase.getInstance().getReference();
        CheckUserPermsions();

        myadapter = new TrackerAdapter(listTrackersData, this, new TrackerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(TrackerItem item) {
                GlobalInfo.UpdatesUser(item.getPhoneNumber());
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("PhoneNumber", item.getPhoneNumber());
                startActivity(intent);
            }
        });
        lsTrackers = (ListView)findViewById(R.id.main_list);
        lsTrackers.setAdapter(myadapter);//initial with data

    }
    @Override
    public void onResume(){
        super.onResume();
        refresh();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.addtracker:
                Toast.makeText(this,"Add Tracker",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, TrackingActivity.class);
                startActivity(intent);
                return true;
            case R.id.help:
                Toast.makeText(this,"Help page",Toast.LENGTH_LONG).show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    void CheckUserPermsions(){
        if ( Build.VERSION.SDK_INT >= 23){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED  ){
                requestPermissions(new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION },
                        REQUEST_CODE_ASK_PERMISSIONS);
                return ;
            }
        }
        StartServices();
    }

    //get acces to location permsion
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    StartServices();
                } else {
                    // Permission Denied
                    Toast.makeText( this,"Allow access to contact list." , Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void refresh(){
        listTrackersData.clear();
        dbRef.child("Users").child(GlobalInfo.PhoneNumber).
                child("Finders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();

                listTrackersData.clear();
                if (td == null)  //no one allow you to find him
                {
                    listTrackersData.add(new TrackerItem("NoTicket", "no_desc"));
                    myadapter.notifyDataSetChanged();
                    return;
                }
                // List<Object> values = td.values();


                // get all contact to list
                ArrayList<TrackerItem> list_contact = new ArrayList<TrackerItem>();
                Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                while (cursor.moveToNext()) {

                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    list_contact.add(new TrackerItem(name, Utils.FormatPhoneNumber(phoneNumber)));
                }


                // if the name is save chane his text
                // case who find me
                String tinfo;
                for (  String Numbers : td.keySet()) {
                    for (TrackerItem cs : list_contact) {

                        //IsFound = SettingSaved.WhoIFindIN.get(cs.Detals);  // for case who i could find list
                        if (cs.PhoneNumber.length() > 0)
                            if (Numbers.contains(cs.PhoneNumber)) {
                                listTrackersData.add(new TrackerItem(cs.UserName, cs.PhoneNumber));
                                break;
                            }
                    }
                }
                myadapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    void StartServices(){
        // Start Track
        if(!TrackLocation.isRunning){
            TrackLocation locationListener = new TrackLocation();
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 35000, 10, locationListener);

        }

        if(!MyService.isRunning){
            Intent intent = new Intent(this,MyService.class);
            startService(intent);
        }
    }
}
