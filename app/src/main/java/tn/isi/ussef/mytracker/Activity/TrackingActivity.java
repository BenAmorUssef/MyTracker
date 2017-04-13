package tn.isi.ussef.mytracker.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

import tn.isi.ussef.mytracker.Model.TrackerItem;
import tn.isi.ussef.mytracker.Adapter.TrackerAdapter;
import tn.isi.ussef.mytracker.R;
import tn.isi.ussef.mytracker.Utils.GlobalInfo;
import tn.isi.ussef.mytracker.Utils.Utils;

public class TrackingActivity extends AppCompatActivity {
    ArrayList<TrackerItem> listTrackersData = new ArrayList<TrackerItem>();
    TrackerAdapter myadapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        myadapter = new TrackerAdapter(listTrackersData, this,  new TrackerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(TrackerItem item) {
                GlobalInfo.Trackers.remove(item.getPhoneNumber());
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                dbRef.child("Users").child(item.getPhoneNumber()).child("Finders").child(GlobalInfo.PhoneNumber).removeValue();

                GlobalInfo  globalInfo = new GlobalInfo(TrackingActivity.this);
                globalInfo.SaveData();

                refresh();

            }
        });

        ListView lsTrackers=(ListView)findViewById(R.id.list_trackers);
        lsTrackers.setAdapter(myadapter);//initial with data
        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contact_list, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.option_menu_search).getActionView();
        SearchManager sm=(SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(sm.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getApplicationContext(),query,Toast.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.goback:
                Toast.makeText(this,"Home",Toast.LENGTH_LONG).show();
                GlobalInfo  globalInfo = new GlobalInfo(this);
                globalInfo.SaveData();
                finish();
                return true;
            case R.id.add:
                Toast.makeText(this,"Add",Toast.LENGTH_LONG).show();
                CheckUserPermsions();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void refresh(){
        listTrackersData.clear();
        for (Map.Entry m:GlobalInfo.Trackers.entrySet()){
            listTrackersData.add(new TrackerItem(m.getValue().toString(), m.getKey().toString()));
        }
        myadapter.notifyDataSetChanged();
    }
    void CheckUserPermsions(){
        if ( Build.VERSION.SDK_INT >= 23){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) !=
                    PackageManager.PERMISSION_GRANTED  ){
                requestPermissions(new String[]{
                                android.Manifest.permission.READ_CONTACTS},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return ;
            }
        }
        PickContact();
    }

    //get acces to location permsion
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PickContact();
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

    void PickContact(){

        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    // Declare
    static final int PICK_CONTACT=1;
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (PICK_CONTACT) :
                if (resultCode == Activity.RESULT_OK) {

                    Uri contactData = data.getData();
                    Cursor c =  getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {

                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                        String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        String cNumber="No number";
                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
                                    null, null);
                            phones.moveToFirst();

                            cNumber = Utils.FormatPhoneNumber(phones.getString(phones.getColumnIndex("data1")));
                            System.out.println("number is:"+cNumber);
                        }
                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        GlobalInfo.Trackers.put(cNumber, name);
                        //update firebase and

                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child("Users").child(cNumber).child("Finders").child(GlobalInfo.PhoneNumber).setValue(true);
                        //update list
                        GlobalInfo  globalInfo = new GlobalInfo(this);
                        globalInfo.SaveData();
                        refresh();
                        //update database
                    }
                }
                break;
        }
    }

}
