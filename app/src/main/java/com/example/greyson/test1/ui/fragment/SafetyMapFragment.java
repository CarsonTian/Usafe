package com.example.greyson.test1.ui.fragment;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import com.example.greyson.test1.R;
import com.example.greyson.test1.entity.SafePlaceRes;
import com.example.greyson.test1.net.WSNetService;
import com.example.greyson.test1.ui.base.BaseFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;

/**
 * This class is used to achieve the map function.
 *  Users can view the safe places and pin a event on the map
 *  @author Greyson, Carson
 *  @version 1.0
 */
public class SafetyMapFragment extends BaseFragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,View.OnClickListener,OnMapReadyCallback {
    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    MapView mapView;

    private LinearLayout mLLSafePlace;
    private LinearLayout mLLSafePin;
    SharedPreferences prefs = null;
    Set<String> latSet = new HashSet<>();
    Set<String> lngSet = new HashSet<>();

            /**
             * This method is used to initialize the map view and request the current location
             * @param inflater
             * @param container
             * @param savedInstanceState
             * @return
             */
            @Override
            protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.frag_safetymap, container, false);

                mLLSafePlace = (LinearLayout) view.findViewById(R.id.ll_safetyplace); // Initialize the layout uesd to call safe places map
                mLLSafePin = (LinearLayout) view.findViewById(R.id.ll_safetypin);     // Initialize the layout used to call pin map

                mapView = (MapView) view.findViewById(R.id.map);                      // Initialize the map view
                mapView.onCreate(savedInstanceState);
                mapView.onResume();
                try {
                    MapsInitializer.initialize(getActivity().getApplicationContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mapView.getMapAsync(this); // Make the map view ready to be used

                // Create the google api client connection
                if (mGoogleApiClient == null) {
                    mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .addApi(LocationServices.API)
                            .build();
        }
        // Create the LocationRequest
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(2000)         // 2 seconds, in milliseconds
                .setFastestInterval(1000); // 1 second, in milliseconds
        return view;
    }

    /**
     * This method is the defult setting of map view
     * @param mMap
     */
    @Override
    public void onMapReady(GoogleMap mMap) {
        googleMap = mMap;
        //Check the permissions
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);                    // Add my location button
        googleMap.getUiSettings().setZoomControlsEnabled(true);  // Add zoom in/out button
        googleMap.getUiSettings().setCompassEnabled(true);       // Add compass button
        googleMap.getUiSettings().setMapToolbarEnabled(true);    // Add map tool bar
        //googleMap.animateCamera(CameraUpdateFactory.zoomBy(13)); // Add the defult zoom value
    }

    /**
     * This map is used to execute other method when google api client connected
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        handleNewLocation();
    }

    /**
     *  This method is used to get the safe places locations from server database and mark them
     */
    private void handleNewLocation() {
        LatLng latLng = getCurrentLocation();                         // Get the latitude and lontitude current location
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));  // Move the camera to the current location
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));      // Add the defult zoom value
        Map<String, String> params = new HashMap<>();                 // Store the params of the URL which is used to request corresponding data from server database
        params.put("", "");
        mRetrofit.create(WSNetService.class)                          // Create a listener to observe the change of the server database and update the local data
                .getSafePlaceData(params)
                .subscribeOn(Schedulers.io())
                .compose(this.<List<SafePlaceRes>>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<SafePlaceRes>>() {
                    @Override
                    public void onCompleted() {}                          // The action if the update is completed

                    @Override
                    public void onError(Throwable e) {}                   // The action if the update is failed

                    @Override
                    public void onNext(List<SafePlaceRes> safePlaceRes) { // The action if the update is running
                        showMarker(safePlaceRes);
                    }
                });
    }

    /**
     * This method is used to mark the locations of the safe places
     * @param safePlaceResList
     */
    private void showMarker(List<SafePlaceRes> safePlaceResList) {
        for (SafePlaceRes sfRes:safePlaceResList) {                                               // Deal with each new location
            Double lat = sfRes.getLatitude();
            Double lng = sfRes.getLongtitude();
            String type = sfRes.getType();
            googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(type));  // Add mark
        }
    }

    /**
     * This method s used to get the current location
     * @return
     */
    private LatLng getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient); // Re-request the current location
        double currentLatitude = location.getLatitude();                                         // Get laititude
        double currentLongitude = location.getLongitude();                                       // Get lontitude
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        return latLng;
    }

    /**
     * This method is used to create listener of the two selection layout
     */
    @Override
    protected void initEvent() {
        mLLSafePlace.setOnClickListener(this);
        mLLSafePin.setOnClickListener(this);
    }

    /**
     * This method is the action of selecting the two layout
     * @param v
     */
    @Override
    public void onClick(View v) {
        mLLSafePlace.setSelected(false);
        mLLSafePin.setSelected(false);
        // The action if one of two layouts is activated
        switch (v.getId()) {
            case R.id.ll_safetyplace:
                mLLSafePlace.setSelected(true);
                initPlaceMap();
                break;
            case R.id.ll_safetypin:
                mLLSafePin.setSelected(true);
                initPinMap();
                break;
        }
    }

    /**
     * This method is used to initialize the map view of safe places
     */
    private void initPlaceMap() {
        googleMap.clear(); // Clear all the marks on the map
        handleNewLocation();
    }

    /**
     * This method is to initialize the map view of pin
     */
    private void initPinMap() {
        googleMap.clear();
        LatLng latLng = getCurrentLocation();
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        prefs = mContext.getSharedPreferences("LatLng",MODE_PRIVATE); // Get the locations of pin from local
        if((prefs.contains("Lat")) && (prefs.contains("Lng")))
        {
            List<String> latPlist = new ArrayList<>(prefs.getStringSet("Lat",null));
            List<String> lngPlist = new ArrayList<>(prefs.getStringSet("Lng",null));
            // Look through all the locations and add mark for them
            if (latPlist.size() == lngPlist.size()) {
                for (int i = 0; i< latPlist.size();i++) {
                    String lat = latPlist.get(i);
                    String lng = lngPlist.get(i);
                    LatLng l =new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
                    googleMap.addMarker(new MarkerOptions().position(l)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                }
            }
        }
        // The defult information of pin when people drop the it.
        Marker pinMarker = googleMap.addMarker(new MarkerOptions().position(latLng).draggable(true)
                .title("New Event Pin").snippet("Drag abd Drop :)")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
        pinMarker.showInfoWindow();
        GoogleMap.OnMarkerDragListener mkDragListener = new GoogleMap.OnMarkerDragListener() {
            /**
             * This method called when a marker starts being dragged.
             * @param marker
             */
            @Override
            public void onMarkerDragStart(Marker marker) {
                marker.setSnippet("Drag me");
                marker.showInfoWindow();
            }

            /**
             * This method called repeatedly while a marker is being dragged.
             * @param marker
             */
            @Override
            public void onMarkerDrag(Marker marker) {
                marker.setSnippet("Drop me");
                marker.showInfoWindow();
            }

            /**
             * This method called when a marker has finished being dragged.
             * @param marker
             */
            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng latLng = marker.getPosition();             // Get the dropped location
                marker.setTitle("Pin Saved");                     // Set the title of new location
                marker.setSnippet("Sorry, can not drag again");
                marker.setDraggable(false);
                marker.showInfoWindow();
                latSet.add(String.valueOf(latLng.latitude));
                lngSet.add(String.valueOf(latLng.longitude));
                prefs.edit().putStringSet("Lat",latSet).commit();  // Add the value to local store with a key
                prefs.edit().putStringSet("Lng",lngSet).commit();  // Add the value to local store with a key
            }

        };
        googleMap.setOnMarkerDragListener(mkDragListener);         // The drop listener

    }

    /**
     * This method provides callbacks that are called when the client is connected or disconnected from the service.
     * @param i
     */
    @Override
    public void onConnectionSuspended(int i) {
    }

    /**
     * This method provides callbacks for scenarios that result in a failed attempt to connect the client to the service.
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /**
     * This method is listener to listen the location change
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        googleMap.clear();
        handleNewLocation();
    }

    /**
     * This method called when the activity will start interacting with the user.
     */
    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();

    }

    /**
     * This method called when the system is about to start resuming a previous activity.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * This method is to create the request for safe places location
     */
    @Override
    protected void initData() {
        //String requestPlace = "df";
        Map<String, String> params = new HashMap<>();
        params.put("", "");
        mRetrofit.create(WSNetService.class)
                .getSafePlaceData(params)
                .subscribeOn(Schedulers.io())
                .compose(this.<List<SafePlaceRes>>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<SafePlaceRes>>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {}

                    @Override
                    public void onNext(List<SafePlaceRes> safePlaceRes) {
                        showMarker(safePlaceRes);
                    }

        });
    }

    /**
     *
     * @param str
     */
    private void handleResult(String str) {
    }

    /**
     * This method is the final call you receive before your activity is destroyed.
     */
    @Override
    protected void destroyView() {

    }
}
