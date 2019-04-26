package com.teco.parkingsystem.ui.map;


import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public interface MapPresenter {

    void callPlaceAPI(String type, double lat, double lng);

    ArrayList<LatLng> boundsWithCenterAndLatLngDistance(LatLng latLng);

}

