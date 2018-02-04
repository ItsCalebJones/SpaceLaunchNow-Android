package me.calebjones.spacelaunchnow.wear.common;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

import timber.log.Timber;

public class BaseActivity extends WearableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.v("onCreate");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.v("onDestroy");
    }
}
