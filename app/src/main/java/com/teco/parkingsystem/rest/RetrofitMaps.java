package com.teco.parkingsystem.rest;

import com.teco.parkingsystem.Models.Example;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

public interface RetrofitMaps {
 
    /*
     * Retrofit get annotation with our URL
     * And our method that will return us nearby parking lots.
     */
    @GET("api/place/nearbysearch/json?&sensor=true&key=AIzaSyDO_Y_V7nN-ej0-0d4-OC59kEpkFtBax4Q")
    Call<Example> getNearbyPlaces(@Query("type") String type, @Query("location") String location, @Query("radius") int radius);
 
}