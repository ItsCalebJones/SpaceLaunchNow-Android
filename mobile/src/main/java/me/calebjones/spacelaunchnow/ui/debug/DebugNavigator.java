package me.calebjones.spacelaunchnow.ui.debug;

import androidx.appcompat.app.AppCompatActivity;

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
