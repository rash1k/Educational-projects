package com.rash1k.adressbook;


import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.google.android.gms.location.LocationServices.FusedLocationApi;

public class LocatorFragment extends SupportMapFragment implements LocationListener/*, ValueEventListener*/ {

    private static final String TAG = "LocatorFragment";
    private static final String REQUEST_ID = "requestId";
    private static final int FIND_IMAGE_PERMISSION_CODE = 1;
    private static final float ZOOM_LEVEL = 12.5F;
    private static final int DISPLACEMENT = 50; // 50 метров
    private static final int UPDATE_INTERVAL = 10000; //10 сек
    private static final int FASTEST_INTERVAL = 5000; // 5 сек

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;

    @Override
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);

        createGoogleApiClient();

        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.getUiSettings().setCompassEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

                if (ActivityCompat.checkSelfPermission(getActivity(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat
                        .checkSelfPermission(getActivity(),
                                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                mMap.setMyLocationEnabled(true);
            }
        });
    }

    private void createGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            findLocationUpdates();
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                        }
                    })
                    .build();
        }
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
//        mDatabaseReference.child("contacts").child(mUser.getId()).addValueEventListener(this);
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            findLocationUpdates();
        }
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
//        mDatabaseReference.child("contacts").removeEventListener(this);
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void findLocationUpdates() {
        LocationRequest request = createLocationRequest();

        requestPermissionAccessFineAndCoarseLocation(request);
    }

    private LocationRequest createLocationRequest() {
        LocationRequest request = new LocationRequest();
       /* request.setInterval(UPDATE_INTERVAL);
        request.setFastestInterval(FASTEST_INTERVAL);*/
        request.setNumUpdates(1);
        request.setSmallestDisplacement(DISPLACEMENT);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return request;
    }

    private void updateUi() {

        if (mMap == null) {
            return;
        }

        LatLng contactPosition = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(contactPosition);

        mMap.clear();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(contactPosition, ZOOM_LEVEL);
        mMap.addMarker(markerOptions);
        mMap.animateCamera(cameraUpdate);
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    private void requestPermissionAccessFineAndCoarseLocation(LocationRequest request) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(),
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        && shouldShowRequestPermissionRationale(
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.permission_explanation);
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(new String[]{
                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, FIND_IMAGE_PERMISSION_CODE);
                        }
                    });
                    builder.create().show();
                } else {
                    requestPermissions(new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION}, FIND_IMAGE_PERMISSION_CODE);
                }
            } else {
//                SettingsFragment.setAccessLocation(getActivity(), true);
                FusedLocationApi
                        .requestLocationUpdates(mGoogleApiClient, request, this);
            }
        } else {
//            SettingsFragment.setAccessLocation(getActivity(), true);
            FusedLocationApi
                    .requestLocationUpdates(mGoogleApiClient, request, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case FIND_IMAGE_PERMISSION_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    findLocationUpdates();
                }
                break;
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        updateUi();
    }


    /*@Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);

        if (user == null) {
            // User is null, error out
            Toast.makeText(getActivity(),
                    "Error: could not fetch user.",
                    Toast.LENGTH_SHORT).show();
        } else {
            String latitude = user.getLatitude();
            String longitude = user.getLongitude();
        }

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }*/

    void test(){
    }
}
