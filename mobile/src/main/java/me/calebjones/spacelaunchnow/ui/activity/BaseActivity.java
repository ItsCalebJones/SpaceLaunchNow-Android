package me.calebjones.spacelaunchnow.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.realm.Realm;
import timber.log.Timber;

public class BaseActivity extends AppCompatActivity {

    private Realm realm;

//    static {
//        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.v("BaseActivity - onCreate");
        realm = Realm.getDefaultInstance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.v("BaseActivity - onDestroy");
        realm.close();
    }

    public Realm getRealm() {
        return realm;
    }
}
