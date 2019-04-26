package com.teco.parkingsystem.global;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.teco.parkingsystem.SBApp;
import com.teco.parkingsystem.dagger.component.NetComponent;

import javax.inject.Inject;

public abstract class BaseUseCaseImpl implements BaseUseCase {

    private final BaseView mBaseView;

    @Inject
    public SharedPreferences sharedPreferences;
    @Inject
    public Gson gson;
    /*  @Inject
      public RequestManager requestManager; //glide*/
    @Inject
    public CommonUtils commonUtils;

    @Inject
    public SharedPref sharePref;

    public BaseUseCaseImpl(BaseView baseView) {
        NetComponent injector = SBApp.getInstance().getmNetComponent();
        injector.inject(this);
        this.mBaseView = baseView;
    }



    @Override
    public void showLoader() {
        mBaseView.showLoader();
    }

    @Override
    public void hideLoader() {
        mBaseView.hideLoader();
    }

    @Override
    public Context getContext() {
        return mBaseView.getContextAppp();
    }

    @Override
    public void onBackPress() {
        mBaseView.onBackPress();
    }
}
