package com.teco.parkingsystem.ui.main;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;
import com.teco.parkingsystem.BaseActivity;
import com.teco.parkingsystem.R;
import com.teco.parkingsystem.Service.LocationService;
import com.teco.parkingsystem.global.Conts;
import com.teco.parkingsystem.ui.history.HistoryActivity;
import com.teco.parkingsystem.ui.map.MapActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.img_map)
    TextView imgMap;
    @BindView(R.id.img_history)
    TextView imgHistory;
    @BindView(R.id.img_disable_loc)
    TextView imgDisableLoc;
    @BindView(R.id.switch_toggle)
    Switch switchToggle;
LocationService locationService;
    boolean mBounded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if(sharedPref.getBoolean(Conts.isFictitiouslocation)){
            switchToggle.setChecked(true);

        }else {
            switchToggle.setChecked(false);
        }


        switchToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPref.setBoolean(Conts.isFictitiouslocation,isChecked);

            }
        });

    }
    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBounded = false;
            locationService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBounded = true;
            LocationService.LocalBinder mLocalBinder = (LocationService.LocalBinder)service;
            locationService = mLocalBinder.getServerInstance();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if(mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
    }

    @OnClick({R.id.img_map, R.id.img_history})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_map:
                Intent intentMap = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intentMap);
                break;
            case R.id.img_history:
                Intent intentHistory = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intentHistory);
                break;

        }
    }
}
