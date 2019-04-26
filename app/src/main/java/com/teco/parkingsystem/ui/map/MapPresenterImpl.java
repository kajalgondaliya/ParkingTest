package com.teco.parkingsystem.ui.map;

import android.location.Location;
import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.teco.parkingsystem.Models.Example;
import com.teco.parkingsystem.Models.Result;
import com.teco.parkingsystem.R;
import com.teco.parkingsystem.SortPlaces;
import com.teco.parkingsystem.global.BaseUseCaseImpl;
import com.teco.parkingsystem.rest.RetrofitMaps;

import java.util.ArrayList;
import java.util.Collections;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;



public class MapPresenterImpl extends BaseUseCaseImpl implements MapPresenter {

    private MapView mapView;
    private int PROXIMITY_RADIUS = 8046; // meter , approx 5miles
    private static final double ASSUMED_INIT_LATLNG_DIFF = 1.0;
    private static final float ACCURACY = 0.01f;

    private ArrayList<String> searchList;

    public MapPresenterImpl(MapView mapView) {
        super(mapView);
        this.mapView = mapView;

    }

    /**
     * call api to get parking lots from latlong
     * @param type
     * @param latitude
     * @param longitude
     */
    @Override
    public void callPlaceAPI(String type, final double latitude, final double longitude) {
        String url = "https://maps.googleapis.com/maps/";
        searchList = new ArrayList<>();

        if(commonUtils.isNetworkAvailable()) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RetrofitMaps service = retrofit.create(RetrofitMaps.class);

            Call<Example> call = service.getNearbyPlaces(type, latitude + "," + longitude, PROXIMITY_RADIUS);

            call.enqueue(new Callback<Example>() {
                @Override
                public void onResponse(final Response<Example> response, Retrofit retrofit) {

                    try {
                        if (response.body().getStatus().equalsIgnoreCase("OK")) {
                            mapView.clearMap();
                            // This loop will go through all the results and add marker on each location.
                            for (int i = 0; i < response.body().getResults().size(); i++) {
                                Double lat = response.body().getResults().get(i).getGeometry().getLocation().getLat();
                                Double lng = response.body().getResults().get(i).getGeometry().getLocation().getLng();
                                String placeName = response.body().getResults().get(i).getName();
                                String vicinity = response.body().getResults().get(i).getVicinity();

                                searchList.add(response.body().getResults().get(i).getName());
                                mapView.setUpSearchList(searchList);

                                mapView.setMapData(lat, lng, placeName, vicinity);
                            }

                            ArrayList<Result> results = (ArrayList<Result>) response.body().getResults();
                            Collections.sort(results, new SortPlaces(new LatLng(latitude, longitude)));
                            mapView.setAdapter(results);
                        } else {

                            mapView.showToast(response.body().getStatus());
                            mapView.setMapError(response.body().getStatus(), latitude , longitude);

                            mapView.clearMap();

                        }

                    } catch (Exception e) {
                        mapView.setFlag(true);
                        mapView.setMapError(e.getMessage(), latitude , longitude);
                        mapView.clearMap();

                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    mapView.hideLoader();
                }
            });
        }else {
            mapView.hideLoader();
            mapView.showToast(getContext().getString(R.string.internet_connection_error));
        }
    }

    /**
     * to draw polygon get bounds of place
     * @param center : latlong of place
     * @return
     */
    @Override
    public ArrayList<LatLng> boundsWithCenterAndLatLngDistance(LatLng center) {

        float latDistanceInMeters = 200;
        float lngDistanceInMeters = 200;

        ArrayList<LatLng> list = new ArrayList<>();
        latDistanceInMeters /= 2;
        lngDistanceInMeters /= 2;
        LatLngBounds.Builder builder = LatLngBounds.builder();
        LatLng east = null, west = null, north = null, south = null;
        float[] distance = new float[1];
        {
            boolean foundMax = false;
            double foundMinLngDiff = 0;
            double assumedLngDiff = ASSUMED_INIT_LATLNG_DIFF;
            do {
                Location.distanceBetween(center.latitude, center.longitude, center.latitude, center.longitude + assumedLngDiff, distance);
                float distanceDiff = distance[0] - lngDistanceInMeters;
                if (distanceDiff < 0) {
                    if (!foundMax) {
                        foundMinLngDiff = assumedLngDiff;
                        assumedLngDiff *= 2;
                    } else {
                        double tmp = assumedLngDiff;
                        assumedLngDiff += (assumedLngDiff - foundMinLngDiff) / 2;
                        foundMinLngDiff = tmp;
                    }
                } else {
                    assumedLngDiff -= (assumedLngDiff - foundMinLngDiff) / 2;
                    foundMax = true;
                }
            } while (Math.abs(distance[0] - lngDistanceInMeters) > lngDistanceInMeters * ACCURACY);
            east = new LatLng(center.latitude, center.longitude + assumedLngDiff);
            builder.include(east);
            west = new LatLng(center.latitude, center.longitude - assumedLngDiff);
            builder.include(west);
        }
        {
            boolean foundMax = false;
            double foundMinLatDiff = 0;
            double assumedLatDiffNorth = ASSUMED_INIT_LATLNG_DIFF;
            do {
                Location.distanceBetween(center.latitude, center.longitude, center.latitude + assumedLatDiffNorth, center.longitude, distance);
                float distanceDiff = distance[0] - latDistanceInMeters;
                if (distanceDiff < 0) {
                    if (!foundMax) {
                        foundMinLatDiff = assumedLatDiffNorth;
                        assumedLatDiffNorth *= 2;
                    } else {
                        double tmp = assumedLatDiffNorth;
                        assumedLatDiffNorth += (assumedLatDiffNorth - foundMinLatDiff) / 2;
                        foundMinLatDiff = tmp;
                    }
                } else {
                    assumedLatDiffNorth -= (assumedLatDiffNorth - foundMinLatDiff) / 2;
                    foundMax = true;
                }
            } while (Math.abs(distance[0] - latDistanceInMeters) > latDistanceInMeters * ACCURACY);
            north = new LatLng(center.latitude + assumedLatDiffNorth, center.longitude);
            builder.include(north);
        }
        {
            boolean foundMax = false;
            double foundMinLatDiff = 0;
            double assumedLatDiffSouth = ASSUMED_INIT_LATLNG_DIFF;
            do {
                Location.distanceBetween(center.latitude, center.longitude, center.latitude - assumedLatDiffSouth, center.longitude, distance);
                float distanceDiff = distance[0] - latDistanceInMeters;
                if (distanceDiff < 0) {
                    if (!foundMax) {
                        foundMinLatDiff = assumedLatDiffSouth;
                        assumedLatDiffSouth *= 2;
                    } else {
                        double tmp = assumedLatDiffSouth;
                        assumedLatDiffSouth += (assumedLatDiffSouth - foundMinLatDiff) / 2;
                        foundMinLatDiff = tmp;
                    }
                } else {
                    assumedLatDiffSouth -= (assumedLatDiffSouth - foundMinLatDiff) / 2;
                    foundMax = true;
                }
            } while (Math.abs(distance[0] - latDistanceInMeters) > latDistanceInMeters * ACCURACY);
            south = new LatLng(center.latitude - assumedLatDiffSouth, center.longitude);
            builder.include(south);
        }

        list.add(east);
        list.add(north);
        list.add(west);
        list.add(south);
        return list;
    }

}
