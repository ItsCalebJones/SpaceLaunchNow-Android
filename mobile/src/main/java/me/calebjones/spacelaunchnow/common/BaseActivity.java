package me.calebjones.spacelaunchnow.common;

import android.app.ActivityManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;

import io.realm.Realm;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.utils.analytics.Analytics;

public class BaseActivity extends AppCompatActivity {

    public BaseActivity(){}

    public BaseActivity (String screenName){
        name = screenName;
    }

    private Realm realm;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = getTheme();
            theme.resolveAttribute(R.attr.recentBarColor, typedValue, true);
            int color = typedValue.data;

            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
            ActivityManager.TaskDescription td = new ActivityManager.TaskDescription(null, bm, color);

            setTaskDescription(td);
            if (bm != null) {
                bm.recycle();
            }
        }
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
