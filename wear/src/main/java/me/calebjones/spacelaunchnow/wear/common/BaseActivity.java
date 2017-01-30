package me.calebjones.spacelaunchnow.wear.common;

import android.app.Activity;
import android.os.Bundle;
import timber.log.Timber;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BaseActivity extends Activity {

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
