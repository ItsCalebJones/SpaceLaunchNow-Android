package me.calebjones.spacelaunchnow.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.realm.Realm;
import timber.log.Timber;

public class BaseActivity extends AppCompatActivity {

    private Realm realm;

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
