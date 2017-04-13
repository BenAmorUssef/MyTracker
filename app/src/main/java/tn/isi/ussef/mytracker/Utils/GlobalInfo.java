package tn.isi.ussef.mytracker.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import tn.isi.ussef.mytracker.Activity.LoginActivity;

/**
 * Created by Ussef on 3/29/2017.
 */

public class GlobalInfo {

    Context context;
    SharedPreferences ShredRef;
    public static String PhoneNumber;
    public static Map<String,String> Trackers = new HashMap<String, String>();

    public  GlobalInfo(Context context){
        this.context=context;
        ShredRef = context.getSharedPreferences("PhoneNumber",Context.MODE_PRIVATE);
    }

    public static void UpdatesUser(String userphone){

        DateFormat df =new SimpleDateFormat("yyyy/MM/dd HH:MM:ss");
        Date date = new Date();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(userphone).child("Updates").setValue(df.format(date).toString());

    }

    public void SaveData(){
        String MyTrackersList = "" ;
        for (Map.Entry  m:GlobalInfo.Trackers.entrySet()){
            if (MyTrackersList.length()==0)
                MyTrackersList=m.getKey() + "%" + m.getValue();
            else
                MyTrackersList =MyTrackersList+ "%" + m.getKey() + "%" + m.getValue();

        }

        if (MyTrackersList.length()==0)
            MyTrackersList="empty";


        SharedPreferences.Editor editor = ShredRef.edit();
        editor.putString("MyTrackers",MyTrackersList);
        editor.putString("PhoneNumber",PhoneNumber);
        editor.apply();
    }

    public void LoadData(){
        Trackers.clear();
        PhoneNumber = ShredRef.getString("PhoneNumber","empty");
        String MyTrackersList= ShredRef.getString("MyTrackers","empty");
        if (!MyTrackersList.equals("empty")){
            String[] users=MyTrackersList.split("%");
            for (int i=0;i<users.length;i=i+2){
                Trackers.put(users[i],users[i+1]);
            }
        }


        if (PhoneNumber.equals("empty")){

            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

    }
}
