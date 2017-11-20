package me.calebjones.spacelaunchnow.widget.launchcard;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.jobs.UpdateLaunchCardJob;
import timber.log.Timber;

import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;
import static android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID;

public class LaunchCardConfigurationActivity extends Activity {

    @BindView(R.id.okButton)
    Button okButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_launch_card_config_activity);
        setResult(RESULT_CANCELED);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.okButton)
    public void onButtonClicked() {
        showAppWidget();
    }

    private void showAppWidget() {

        int mAppWidgetId = INVALID_APPWIDGET_ID;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(EXTRA_APPWIDGET_ID,
                    INVALID_APPWIDGET_ID);
            UpdateLaunchCardJob.runJobImmediately(mAppWidgetId);
            setResult(RESULT_OK);
            finish();
        }
        if (mAppWidgetId == INVALID_APPWIDGET_ID) {
            Timber.i("I am invalid");
            finish();
        }

    }
}
