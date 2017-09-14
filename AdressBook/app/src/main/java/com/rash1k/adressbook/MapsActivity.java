package com.rash1k.adressbook;

import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.SupportMapFragment;

public class MapsActivity extends BaseActivity {

    private static final int REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

    }

    @Override
    protected void onResume() {
        super.onResume();

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();

        int errorCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (errorCode != ConnectionResult.SUCCESS) {

            if (apiAvailability.isUserResolvableError(errorCode)) {
                apiAvailability.getErrorDialog(this, errorCode,
                        GoogleApiAvailability.GOOGLE_PLAY_SERVICES_VERSION_CODE,
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                finish();
                            }
                        }).show();
            }

        }


    }


}
