package com.teco.parkingsystem;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.gson.Gson;
import com.teco.parkingsystem.global.CommonUtils;
import com.teco.parkingsystem.global.SharedPref;


import javax.inject.Inject;


public abstract class AbstractFragment extends Fragment {

    @Inject
    protected
    CommonUtils commonUtils;

    @Inject
    public Gson gson;
    @Inject
    public
    SharedPref sharedPref;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((SBApp) getActivity().getApplication()).getmNetComponent().inject(this);
    }

   /* public void addFragment(Fragment fragment, String fragmentName) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStack();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        transaction.add(R.id.frame, fragment, fragmentName);
        transaction.commit();
    }

    public void replaceFragment(Fragment fragment, String fragmentName, boolean isAddToBackStack) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//        fragmentManager.popBackStack();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        if (isAddToBackStack)
            transaction.addToBackStack(fragmentName);
        transaction.replace(R.id.frame, fragment, fragmentName);
        transaction.commit();
    }

    public void replaceFragment(Fragment fragment, String fragmentName, boolean isAddToBackStack, Bundle bundle) {
        commonUtils.hideKeyboard(getActivity());
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
       // fragmentManager.popBackStack();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        fragment.setArguments(bundle);
        if (isAddToBackStack)
            transaction.addToBackStack(null);
        transaction.replace(R.id.frame, fragment, fragmentName);
        transaction.commit();
    }

    public void replaceFragment(Fragment fragment, String fragmentName, Bundle bundle) {
        BaseActivity.toViewAllFragment = true;
        commonUtils.hideKeyboard(getActivity());
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//        fragmentManager.popBackStack();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        fragment.setArguments(bundle);
        transaction.replace(R.id.frame, fragment, fragmentName);
        transaction.commit();
    }*/
}
