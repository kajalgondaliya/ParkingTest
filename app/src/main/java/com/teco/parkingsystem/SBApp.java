package com.teco.parkingsystem;

import android.app.Application;
import android.content.Context;

import com.teco.parkingsystem.dagger.component.DaggerNetComponent;
import com.teco.parkingsystem.dagger.component.NetComponent;
import com.teco.parkingsystem.dagger.module.AppModule;
import com.teco.parkingsystem.dagger.module.NetModule;


public class SBApp extends Application {
    public static boolean isServiceRunning;
    public NetComponent mNetComponent;

    private static SBApp mInstance;

    public NetComponent getmNetComponent() {
        return mNetComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        mInstance = this;

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

    }

    private void init() {
        mNetComponent = DaggerNetComponent.builder()
                .appModule(new AppModule(this))
                .netModule(new NetModule())
                .build();



    }

    public static synchronized SBApp getInstance() {
        return mInstance;
    }


}
