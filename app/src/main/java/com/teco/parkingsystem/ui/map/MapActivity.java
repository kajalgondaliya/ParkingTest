package com.teco.parkingsystem.ui.map;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.teco.parkingsystem.BaseActivity;
import com.teco.parkingsystem.Database.DatabaseHelper;
import com.teco.parkingsystem.Database.HistoryModel;
import com.teco.parkingsystem.Models.Result;
import com.teco.parkingsystem.R;
import com.teco.parkingsystem.SBApp;
import com.teco.parkingsystem.Service.LocationService;
import com.teco.parkingsystem.SplashScreen;
import com.teco.parkingsystem.adapter.ParkingAdapter;
import com.teco.parkingsystem.adapter.PlaceArrayAdapter;
import com.teco.parkingsystem.global.Conts;
import com.teco.parkingsystem.ui.history.HistoryActivity;
import com.teco.parkingsystem.ui.main.MainActivity;

import java.util.ArrayList;
import java.util.Collections;

public class MapActivity extends BaseActivity implements MapView, OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private MapPresenter mapPresenter;
    private Dialog dialog;
    private GoogleMap mMap;
    double latitude;
    double longitude;
    Marker mCurrLocationMarker;
    ParkingAdapter adapter;
    boolean flagFirst = true;
    boolean isTimerOn = false;
    boolean isParked = false;
    CountDownTimer timer;
    ArrayList<Result> results;
    Location mLastLocation;
    private DatabaseHelper databaseHelper;
    String currentId = "";

    private TextView tvShowList;
    private BottomSheetBehavior mBottomSheetBehaviour;
    private ArrayList<String> searchList = new ArrayList<>();
    private SupportMapFragment mapFragment;
    private ArrayAdapter<String> adapterSearch;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private RecyclerView parkingList;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 15000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    //====== auto complete
    private AutoCompleteTextView autoCompleteTextView;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private GoogleApiClient mGoogleApiClient;
    private LatLng newPlaceLatLng = null;
    //======

    LocationService locationService;
    boolean mBounded;

    /**
     * this receiver is called when latlong gets changed
     */
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                latitude = intent.getExtras().getDouble("latitude");
                longitude = intent.getExtras().getDouble("longitude");
                float[] distanceTra = new float[1];

                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                if (mLastLocation != null) {
                    Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(),
                            latitude, longitude, distanceTra);

                }

                LatLng latLng = new LatLng(latitude, longitude);

                if (flagFirst) {
                    if(newPlaceLatLng!=null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(newPlaceLatLng));
                    }else {
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    }
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                }

                /**
                 * when search any place call place api to get parking lots of that area
                 */
                if (!autoCompleteTextView.getText().toString().equals("")
                        && newPlaceLatLng != null) {
                    if (flagFirst) {
                        flagFirst = false;
                        mapPresenter.callPlaceAPI("parking", newPlaceLatLng.latitude, newPlaceLatLng.longitude);
                    }
                }
                /**
                 *else for call place api to get current near parking lots
                 */
                else {

                    if (distanceTra[0] > 8046 || flagFirst) {
                        flagFirst = false;
                        mapPresenter.callPlaceAPI("parking", latitude, longitude);
                    }
                }
                Location location = new Location("currentLocation");
                location.setLongitude(longitude);
                location.setLatitude(latitude);
                mLastLocation = location;

                sharedPref.setDataInPref(Conts.lastLocLat,latitude+"");
                sharedPref.setDataInPref(Conts.lastLocLong,longitude+"");

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");

                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = mMap.addMarker(markerOptions);

                if (results != null && results.size() > 0 && !isTimerOn) {
                    double lowestDistance = 0;
                    final Location location2 = new Location("nearestParking");
                    final ArrayList<Double> distanceFromCurrent = new ArrayList<>();
                    for (int i = 0; i < results.size(); i++) {
                        location2.setLatitude(results.get(i).getGeometry().getLocation().getLat());
                        location2.setLongitude(results.get(i).getGeometry().getLocation().getLng());
                        double distance = mLastLocation.distanceTo(location2);

                        //to get distance from current latlong
                        distanceFromCurrent.add(distance);

                    }
                    if (!isParked) {
                        lowestDistance = Collections.min(distanceFromCurrent);
                        if (lowestDistance <= 200) {
                            final int index = distanceFromCurrent.indexOf(Collections.min(distanceFromCurrent));
                            timer = new CountDownTimer(180000, 1000) { // this timer indicate parking for particular user
                                public void onTick(long millisUntilFinished) {
                                    isTimerOn = true;

                                    Location currentDistanceLocation = new Location("current");
                                    currentDistanceLocation.setLatitude(results.get(index).getGeometry().getLocation().getLat());
                                    currentDistanceLocation.setLongitude(results.get(index).getGeometry().getLocation().getLng());
                                    double currentDistance = mLastLocation.distanceTo(currentDistanceLocation);
                                    if (currentDistance > 200) {
                                        timer.cancel();
                                        isTimerOn = false;
                                    }
                                }

                                public void onFinish() {
                                    isTimerOn = false;
                                    isParked = true;
                                    //insert parking data of user in database
                                    currentId = databaseHelper.InsertParkingPlace(results.get(index).getName(),
                                            results.get(index).getVicinity(),
                                            String.valueOf(System.currentTimeMillis()),
                                            "",
                                            "");

                                    if (!currentId.equals("-1")) {
                                        dialogMessage(getString(R.string.vehicle_park));
                                    }
                                }
                            }.start();
                        }
                    } else {
                        /**
                         * when current user distance is greater than 200 meter of parking lots this method is use for update history
                         */
                        if (Collections.min(distanceFromCurrent) > 200) {
                            isParked = false;
                            dialogMessage(getString(R.string.vehicle_out_of_park));
                            HistoryModel historyModel = databaseHelper.getHistory(currentId);
                            Long endTime = System.currentTimeMillis();
                            if (historyModel != null) {
                                databaseHelper.updateHistory((long) historyModel.getId(), String.valueOf(endTime),
                                        String.valueOf(endTime - Long.parseLong(historyModel.getStartTime())));
                            } else {
                                databaseHelper.updateHistory(Long.parseLong(currentId), String.valueOf(endTime),
                                        String.valueOf(0));
                            }
                        }
                    }
                }

            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        dialog = commonUtils.createCustomLoader(MapActivity.this, false);
        parkingList = findViewById(R.id.parkingList);


       showLoader();
        View nestedScrollView = (View) findViewById(R.id.nestedScrollView);
        tvShowList = findViewById(R.id.tv_show_list);


        /**
         * for autocomplete textview
         */
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        autoCompleteTextView.setThreshold(3);

        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        autoCompleteTextView.setAdapter(mPlaceArrayAdapter);
        autoCompleteTextView.setOnItemClickListener(mAutocompleteClickListener);



        tvShowList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        mBottomSheetBehaviour = BottomSheetBehavior.from(nestedScrollView);
        //By default set BottomSheet Behavior as Collapsed and Height 100
        mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehaviour.setPeekHeight(100);

        //If you want to handle callback of Sheet Behavior you can use below code
        mBottomSheetBehaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        parkingList.setVisibility(View.GONE);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        parkingList.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        mapPresenter = new MapPresenterImpl(this);
        databaseHelper = new DatabaseHelper(this);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }else {
            enableGPS(mGoogleApiClient);
        }

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * when place searching autocomplete is done this method is use for fetch detail of that place
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            // Hide key Board
            View view2 = MapActivity.this.getCurrentFocus();
            if (view2 != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
            }
        }
    };
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * to get latlong of autocomplete place
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {

            if (!places.getStatus().isSuccess()) {
                //Place query did not complete
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);

            flagFirst = true;
            newPlaceLatLng = place.getLatLng();

            showLoader();

            if(mMap!=null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(newPlaceLatLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            }

        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        //register receiver to get latlong when it changed
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(LocationService.LOCATION_CHANGED));

    }

    @Override
    protected void onPause() {
        //unregister receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    public void showLoader() {
        dialog.show();
    }

    @Override
    public void hideLoader() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context getContextAppp() {
        return this;
    }

    @Override
    public void onBackPress() {
        mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
        super.onBackPressed();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);

        rlp.addRule(RelativeLayout.ALIGN_PARENT_END, 0);
        rlp.addRule(RelativeLayout.ALIGN_END, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rlp.setMargins(40, 40, 40, 140);


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                if (!SBApp.isServiceRunning) {
                    Intent i = new Intent(MapActivity.this, LocationService.class);
                    startService(i);


                }else {
                    Intent myService = new Intent(MapActivity.this, LocationService.class);
                    stopService(myService);
                    startService(myService);
                }
                mMap.setMyLocationEnabled(true);

            }
        } else {
            if (!SBApp.isServiceRunning) {
                Intent i = new Intent(MapActivity.this, LocationService.class);
                startService(i);

            }else {
                Intent myService = new Intent(MapActivity.this, LocationService.class);
                stopService(myService);
                startService(myService);
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(false);

        }

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
               showLoader();
                autoCompleteTextView.setText("");
                newPlaceLatLng = null;
                flagFirst = true;
                return false;
            }
        });
    }

    public void dialogMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Parking System")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            enableGPS(mGoogleApiClient);
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        enableGPS(mGoogleApiClient);
                        if (!SBApp.isServiceRunning) {
                            Intent i = new Intent(MapActivity.this, LocationService.class);
                            startService(i);
                        }

                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

    @Override
    public void clearMap() {
        mMap.clear();
    }

    @Override
    public void setMapData(double lat, double lng, String placeName, String vicinity) {

        hideLoader();
        MarkerOptions markerOptions = new MarkerOptions();
        LatLng latLng = new LatLng(lat, lng);
        // Position of Marker on Map
        markerOptions.position(latLng);
        // Adding Title to the Marker
        markerOptions.title(placeName + " : " + vicinity);
        // Adding Marker to the Camera.

        // move map camera
        if(newPlaceLatLng!=null){
            mMap.moveCamera(CameraUpdateFactory.newLatLng(newPlaceLatLng));
        }else {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude,longitude)));
        }

        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        ArrayList<LatLng> bounds = mapPresenter.boundsWithCenterAndLatLngDistance(latLng);

        mMap.addPolygon(new PolygonOptions()
                .addAll(bounds)
                .strokeColor(Color.RED)
                .fillColor(Color.BLUE));

    }

    @Override
    public void setAdapter(final ArrayList<Result> results) {
        this.results = results;
        adapter = new ParkingAdapter(MapActivity.this, results, new ParkingAdapter.ItemClick() {
            @Override
            public void onItemClick(int pos) {
                LatLng latLng1 = new LatLng(results.get(pos).getGeometry().getLocation().getLat(), results.get(pos).getGeometry().getLocation().getLng());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        parkingList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        parkingList.setAdapter(adapter);

    }

    @Override
    public void setMapError(String error,final double latitude, final double longitude) {

       hideLoader();

    }

    @Override
    public void setFlag(boolean value) {
        flagFirst = value;
    }

    @Override
    public void setUpSearchList(ArrayList<String> searchList) {
        this.searchList = searchList;
        adapterSearch = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, searchList);

    }


    public void enableGPS(GoogleApiClient googleApiClient) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                .checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result
                        .getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be
                        // fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling
                            // startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MapActivity.this, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have
                        // no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }



}

