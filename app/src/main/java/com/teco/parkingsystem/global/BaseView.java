package com.teco.parkingsystem.global;

import android.content.Context;

public interface BaseView {

    void showLoader();

    void hideLoader();

    void showToast(String msg);

    Context getContextAppp();

    void onBackPress();
}
