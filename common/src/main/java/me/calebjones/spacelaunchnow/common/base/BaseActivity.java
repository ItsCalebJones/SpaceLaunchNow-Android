package me.calebjones.spacelaunchnow.common.base;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import com.jaredrummler.cyanea.app.CyaneaAppCompatActivity;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.Locale;

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
    protected void attachBaseContext(Context newBase) {
        Context context;
        if (!Prefs.getBoolean("locale_changer", true)){
            context = Utils.changeLang(newBase, "en-US");
        } else {
            context = Utils.changeLang(newBase,  Locale.getDefault().getLanguage());
        }
        super.attachBaseContext(context);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
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
