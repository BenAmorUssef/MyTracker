package tn.isi.ussef.mytracker.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import tn.isi.ussef.mytracker.R;
import tn.isi.ussef.mytracker.Utils.GlobalInfo;
import tn.isi.ussef.mytracker.Utils.Utils;

public class LoginActivity extends AppCompatActivity {
    EditText EDTNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EDTNumber  = (EditText) findViewById(R.id.EDTNumber);
    }

    public void BuNext(View view) {
        GlobalInfo.PhoneNumber = Utils.FormatPhoneNumber(EDTNumber.getText().toString());
        GlobalInfo.UpdatesUser(GlobalInfo.PhoneNumber);
        finish();
        Intent i = new Intent(this, TrackingActivity.class);
        startActivity(i);
    }
}
