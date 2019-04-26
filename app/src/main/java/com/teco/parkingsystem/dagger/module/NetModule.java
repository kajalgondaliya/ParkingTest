package com.teco.parkingsystem.dagger.module;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.teco.parkingsystem.global.CommonUtils;
import com.teco.parkingsystem.global.SharedPref;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by agile-01 on 7/6/2016.
 */

@Module(includes = AppModule.class)
public class NetModule {


    public NetModule() {

    }

//    @Provides
//    @Singleton
//    Picasso providePicasso(Application application) {
//        return Picasso.with(application);
//    }


    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }




    @Provides
    @Singleton
    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create();
    }




    @Provides
    @Singleton
    CommonUtils provideCommonUtils(Application application) {
        return new CommonUtils(application);
    }



    @Provides
    @Singleton
    SharedPref providePref(Application application) {
        return new SharedPref(application);
    }




}
