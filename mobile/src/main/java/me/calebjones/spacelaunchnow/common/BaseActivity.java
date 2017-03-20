package me.calebjones.spacelaunchnow.common;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.realm.Realm;
import me.calebjones.spacelaunchnow.utils.Analytics;

public class BaseActivity extends AppCompatActivity {

    public BaseActivity (String screenName){
        name = screenName;
    }

    private Realm realm;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Analytics.from(this).sendScreenView(name, "Activity destroyed.");
        realm.close();
    }

    @Override
    public void onStart() {
        super.onStart();
        Analytics.from(this).sendScreenView(name, name + " started.");
    }

    @Override
    public void onResume() {
        super.onResume();
        Analytics.from(this).sendScreenView(name, name + " resumed.");
    }

    @Override
    public void onPause() {
        super.onPause();
        Analytics.from(this).notifyGoneBackground();
    }

    public Realm getRealm() {
        return realm;
    }
}
