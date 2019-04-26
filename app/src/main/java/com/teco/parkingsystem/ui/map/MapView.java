package com.teco.parkingsystem.ui.map;


import com.teco.parkingsystem.Models.Result;
import com.teco.parkingsystem.global.BaseView;

import java.util.ArrayList;


public interface MapView extends BaseView {

    void clearMap();

    void setMapData(double lat, double lng, String place, String vicinity);

    void setAdapter(ArrayList<Result> results);

    void setFlag(boolean value);

    void setUpSearchList(ArrayList<String> searchList);

    void setMapError(String status, double latitude, double longitude);
}
