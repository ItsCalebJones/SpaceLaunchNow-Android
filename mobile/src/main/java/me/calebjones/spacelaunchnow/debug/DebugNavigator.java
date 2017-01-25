package me.calebjones.spacelaunchnow.debug;

import android.support.v7.app.AppCompatActivity;

public class DebugNavigator implements DebugContract.Navigator {

    private AppCompatActivity debugActivity;

    public DebugNavigator(AppCompatActivity activity){
        debugActivity = activity;
    }

    @Override
    public void goHome() {
        debugActivity.onBackPressed();
    }
}
