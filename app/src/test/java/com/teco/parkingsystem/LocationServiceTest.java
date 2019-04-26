package com.teco.parkingsystem;////package com.teco.parkingsystem;

import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.teco.parkingsystem.Service.LocationService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;

import com.google.android.gms.common.api.GoogleApiClient;
import com.teco.parkingsystem.Service.LocationService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import android.location.Location;
import android.os.CountDownTimer;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.teco.parkingsystem.Models.Example;
import com.teco.parkingsystem.Models.Result;
import com.teco.parkingsystem.ui.map.MapPresenter;
import com.teco.parkingsystem.ui.map.MapPresenterImpl;
import com.teco.parkingsystem.ui.map.MapView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class LocationServiceTest {

    MapPresenter presenter;
    MapView mapView;
    double lat, lng, lat1, lng1, lat2, lng2;
    Call<Example> call;
    Example example;
    CountDownTimer timer;
    @Captor
    ArgumentCaptor<Callback<Example>> argumentCapture;
    Response<Example> response;

    @Before
    public void setUp() {
        // MockitoAnnotations.initMocks( this );
        mapView = Mockito.mock(MapView.class);
        lat = 21.1702;
        lng = 72.8311;
        lat1 = 21.1704;
        lng1 = 72.8291;
        lat2 = 21.174716;
        lng2 = 72.829081;
        call = Mockito.mock(Call.class);
        argumentCapture = ArgumentCaptor.forClass(Callback.class);
        example = mock(Example.class);
        response = mock(Response.class);

    }

    @Test
    public void checkNearByApiCallSuccess() {
        // Mockito.doReturn(true).when(apiClient)
        presenter = new MapPresenterImpl(mapView);
        presenter.callPlaceAPI("parking", lat, lng);
        Mockito.doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Callback<Example> callback = invocation.getArgument(0);

                callback.onResponse(response, (Retrofit) any());

                return null;
            }
        }).when(call).enqueue(any(Callback.class));
        Assert.assertNotNull(response);

    }

    @Test
    public void checkNearByApiCallFailure() {
        // Mockito.doReturn(true).when(apiClient)
        presenter = new MapPresenterImpl(mapView);
        presenter.callPlaceAPI("parking", lat, lng);
        Mockito.doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Callback<Example> callback = invocation.getArgument(0);

                callback.onFailure((Throwable) any());

                return null;
            }
        }).when(call).enqueue(any(Callback.class));

    }

    @Test
    public void TestParking() {
        final Location location = new Location("location1");
        location.setLatitude(lat);
        location.setLongitude(lng);

        Location location2 = new Location("location2");
        location2.setLatitude(lat1);
        location2.setLongitude(lat2);

        double distance = location.distanceTo(location2);
        if (distance <= 200) {
            timer = new CountDownTimer(3000, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Location currentDistanceLocation = new Location("current");
                    currentDistanceLocation.setLatitude(lat2);
                    currentDistanceLocation.setLongitude(lng2);
                    double currentDistance = location.distanceTo(currentDistanceLocation);
                    if (currentDistance > 200) {
                        timer.cancel();
//                        isTimerOn = false;
                    }
                }

                @Override
                public void onFinish() {

                }
            };
        }
    }





}
