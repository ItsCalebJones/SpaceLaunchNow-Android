package me.calebjones.spacelaunchnow.common.base;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.jaredrummler.cyanea.app.CyaneaAppCompatActivity;

import androidx.annotation.ColorRes;
import androidx.core.graphics.drawable.DrawableCompat;
import io.realm.Realm;
import me.calebjones.spacelaunchnow.common.utils.Utils;
import timber.log.Timber;

public class BaseActivity extends CyaneaAppCompatActivity {

    public BaseActivity() {
    }

    public BaseActivity(String screenName) {
        name = screenName;
    }

    private Realm realm;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate");
        realm = Realm.getDefaultInstance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.d("onDestroy");
        realm.close();
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.d("onStart");
        if (realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.d("onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Timber.d("onPause");
    }


    public Realm getRealm() {
        if (realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }
        return realm;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }
}
