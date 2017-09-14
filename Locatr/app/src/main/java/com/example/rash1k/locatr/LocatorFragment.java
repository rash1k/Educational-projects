package com.example.rash1k.locatr;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;


public class LocatorFragment extends SupportMapFragment {

    private static final String REQUESTING_LOCATION_UPDATES_KEY = "locationUpdatesKey";
    private static final String LOCATION_KEY = "currentLocationKey";
    private static final String GALLERY_ITEM_KEY = "galleryItemKey";
    private GoogleApiClient mGoogleApiClient;
    private Bitmap mMapImage;
    private GalleryItem mMapItem;
    private boolean mRequestingLocationUpdates;
    private Location mCurrentLocation;
    private GoogleMap mGoogleMap;
    private static final int FIND_IMAGE_PERMISSION_CODE = 1;
    private static final String TAG = "LocatorFragment";

    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks =
            new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(@Nullable Bundle bundle) {
                    getActivity().invalidateOptionsMenu();
                }

                @Override
                public void onConnectionSuspended(int i) {

                }
            };

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged: " + location);
            new SearchTask().execute(location);
        }
    };

    public static LocatorFragment newInstance() {
        return new LocatorFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

//        setRetainInstance(true);

//        updateValuesIsBundle(savedInstanceState);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(mConnectionCallbacks).build();
        }
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
            }
        });
    }

    private void updateValuesIsBundle(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState
                        .getBoolean(REQUESTING_LOCATION_UPDATES_KEY);
            }

            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

        }
    }


    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }


    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

   /*    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putParcelable(GALLERY_ITEM_KEY, (Parcelable) mMapItem);
        bundle.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        bundle.putParcelable(LOCATION_KEY, mCurrentLocation);
    }
*/

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_locator, menu);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem searchItem = menu.findItem(R.id.action_locate);
        searchItem.setEnabled(mGoogleApiClient.isConnected());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_locate:
                findLocation();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void requestPermissionAccessFineAndCoarseLocation(LocationRequest request) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        && shouldShowRequestPermissionRationale(
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.permission_explanation);
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION}, FIND_IMAGE_PERMISSION_CODE);
                        }
                    });

                    builder.create().show();
                } else {
                    requestPermissions(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, FIND_IMAGE_PERMISSION_CODE);
                }
            } else {
                mRequestingLocationUpdates = true;
                LocationServices.FusedLocationApi
                        .requestLocationUpdates(mGoogleApiClient, request, mLocationListener);
            }

        } else {
            mRequestingLocationUpdates = true;
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(mGoogleApiClient, request, mLocationListener);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case FIND_IMAGE_PERMISSION_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    mRequestingLocationUpdates = true;
                    findLocation();
                }
        }
    }

    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setNumUpdates(1);
        locationRequest.setInterval(0);
//        locationRequest.setFastestInterval(5000);

        return locationRequest;
    }


    private void startLocationRequest() {
        LocationRequest request = createLocationRequest();

        requestPermissionAccessFineAndCoarseLocation(request);
    }

    private void stopLocationUpdates() {

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationListener);
    }

    private void findLocation() {
        startLocationRequest();

    }

    private void updateUi() {
        if (mGoogleMap == null && mMapImage == null) {
            return;
        }

        LatLng itemPoint = new LatLng(mMapItem.getLat(), mMapItem.getLon());
        LatLng myPoint = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(mMapImage);

        MarkerOptions itemMarker = new MarkerOptions();

        itemMarker.position(itemPoint);
        itemMarker.icon(descriptor);

        MarkerOptions myMarker = new MarkerOptions();
        myMarker.position(myPoint);

        mGoogleMap.clear();
        mGoogleMap.addMarker(itemMarker);
        mGoogleMap.addMarker(myMarker);

        LatLngBounds lngBounds = new LatLngBounds.Builder()
                .include(itemPoint)
                .include(myPoint)
                .build();

        int margin = getResources().getDimensionPixelSize(R.dimen.map_insert_margin);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(lngBounds, margin);

        mGoogleMap.animateCamera(cameraUpdate);
    }

    private class SearchTask extends AsyncTask<Location, Void, Void> {
        private GalleryItem galleryItem;
        private Bitmap bitmap;
        private Location mLocation;


        @Override
        protected Void doInBackground(Location... params) {
            mLocation = params[0];
            FlickFetchr fetchr = new FlickFetchr();
            List<GalleryItem> items = fetchr.searchPhotos(params[0]);

            if (items.size() == 0) {
                return null;
            }

            galleryItem = items.get(0);

            byte[] bytes = fetchr.getUrlBytes(galleryItem.getUrl());

            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mMapItem = galleryItem;
            mMapImage = bitmap;
            mCurrentLocation = mLocation;
            updateUi();
        }

    }
}


