package com.teco.parkingsystem;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.teco.parkingsystem.global.CommonUtils;
import com.teco.parkingsystem.global.SharedPref;


import javax.inject.Inject;


public class BaseActivity extends AppCompatActivity {



    @Inject
    SharedPreferences sharedPreferences;



    @Inject
    Gson gson;
    @Inject
    public CommonUtils commonUtils;
    @Inject
    public SharedPref sharedPref;


    public String currentFragment = "";
    public static boolean toViewAllFragment = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((SBApp) getApplication()).getmNetComponent().inject(this);


    }



    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

    /*public void addFragment(Fragment fragment, String fragmentName) {
        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.popBackStack();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.frame, fragment, fragmentName);
        transaction.commit();
    }

    public void replaceFragment(Fragment fragment, String fragmentName) {
        BaseActivity.toViewAllFragment = false;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame, fragment, fragmentName);
        transaction.commit();

    }

    private void replaceFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.frame, fragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    public void replaceFragment(Fragment fragment, String fragmentName, boolean isAddToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.popBackStack();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (isAddToBackStack)
            transaction.addToBackStack(null);
        transaction.replace(R.id.frame, fragment, fragmentName);
        transaction.commit();
    }

    public void replaceFragment(Fragment fragment, String fragmentName, boolean isAddToBackStack, Bundle bundle) {
        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.popBackStack();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        fragment.setArguments(bundle);
        if (isAddToBackStack)
            transaction.addToBackStack(null);
        transaction.replace(R.id.frame, fragment, fragmentName);
        transaction.commit();
    }

    public void replaceFragment(Fragment fragment, String fragmentName, Bundle bundle) {
        commonUtils.hideKeyboard(BaseActivity.this);
        BaseActivity.toViewAllFragment = false;
        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.popBackStack();
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        fragment.setArguments(bundle);
        transaction.replace(R.id.frame, fragment, fragmentName);
        transaction.commit();
//        setTitleOnFragment(fragment);

    }*/

    public void setToolbarTitle(String name) {
        getSupportActionBar().setTitle(name);
    }


}
