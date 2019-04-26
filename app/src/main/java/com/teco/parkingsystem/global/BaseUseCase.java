package com.teco.parkingsystem.global;

import android.content.Context;

public interface BaseUseCase {

    void showLoader();

    void hideLoader();

    Context getContext();

    void onBackPress();
}