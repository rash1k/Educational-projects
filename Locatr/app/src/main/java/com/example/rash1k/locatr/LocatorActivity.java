package com.example.rash1k.locatr;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class LocatorActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fm = getSupportFragmentManager();
        fm.findFragmentById(R.id.fragment);
        /*if(fragment == null){
            fm.beginTransaction().add(R.id.fragment, LocatorFragment.newInstance()).commit();
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (errorCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = GooglePlayServicesUtil
                    .getErrorDialog(errorCode, this, REQUEST_CODE, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {

                            finish();
                        }
                    });
            errorDialog.show();
        }
    }

}
