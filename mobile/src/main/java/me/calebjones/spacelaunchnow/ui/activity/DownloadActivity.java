package me.calebjones.spacelaunchnow.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import me.calebjones.spacelaunchnow.MainActivity;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.content.services.LaunchDataService;
import me.calebjones.spacelaunchnow.utils.Connectivity;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;

public class DownloadActivity extends AppCompatActivity {

    @Bind(R.id.start_download)
    AppCompatButton button;
    @Bind(R.id.progress_download)
    ProgressBar progressView;
    @Bind(R.id.titles)
    TextView titles;
    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout view;
    @Bind(R.id.circularFillableLoaders)
    CircularFillableLoaders circularFillableLoaders;

    private boolean upComplete = false;
    private boolean previousComplete = false;
    private boolean vehiclesDetailsComplete = false;
    private boolean vehiclesComplete = false;
    private boolean missionsComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        ButterKnife.bind(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter launchFilter = new IntentFilter();
        launchFilter.addAction(Strings.ACTION_SUCCESS_UP_LAUNCHES);
        launchFilter.addAction(Strings.ACTION_SUCCESS_PREV_LAUNCHES);
        launchFilter.addAction(Strings.ACTION_SUCCESS_VEHICLE_DETAILS);
        launchFilter.addAction(Strings.ACTION_SUCCESS_VEHICLES);
        launchFilter.addAction(Strings.ACTION_SUCCESS_MISSIONS);

        IntentFilter errorFilter = new IntentFilter();
        errorFilter.addAction(Strings.ACTION_FAILURE_UP_LAUNCHES);
        errorFilter.addAction(Strings.ACTION_FAILURE_PREV_LAUNCHES);
        errorFilter.addAction(Strings.ACTION_FAILURE_VEHICLE_DETAILS);
        errorFilter.addAction(Strings.ACTION_FAILURE_VEHICLES);
        errorFilter.addAction(Strings.ACTION_FAILURE_MISSIONS);

        registerReceiver(launchReceiver, launchFilter);
        registerReceiver(errorReceiver, errorFilter);

        int version = SwitchPreferences.getInstance(this).getVersionCode();
        if (version <= 87 && version > 0){
            showRealmMigrationDialog();
            SwitchPreferences.getInstance(this).setVersionCode(Utils.getVersionCode(this));
        }
    }

    private void showRealmMigrationDialog() {
        new MaterialStyledDialog(this)
                .withIconAnimation(false)
                .withDialogAnimation(true)
                .setIcon(new IconicsDrawable(this).icon(MaterialDesignIconic.Icon.gmi_info).color(Color.WHITE))
                .setTitle("Whats New? " + Utils.getVersionName(this))
                .setDescription("This update brought a huge overhaul to the data storage of launches. The easiest way to migrate is essentially a wipe and install, that being said welcome to the new version!")
                .setScrollable(true)
                .setNegative("Okay!", new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(launchReceiver);
        unregisterReceiver(errorReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("status", titles.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        titles.setText(savedInstanceState.getString("status"));
    }

    @OnClick(R.id.start_download)
    public void startDownload() {
        button.setVisibility(View.GONE);
        progressView.setVisibility(View.VISIBLE);
        progressView.setIndeterminate(true);
        circularFillableLoaders.setProgress(0);
        titles.setText("Step 1 of 5: Loading upcoming launches.");

        Intent launchIntent = new Intent(this, LaunchDataService.class);
        launchIntent.setAction(Strings.ACTION_GET_ALL_WIFI);
        this.startService(launchIntent);
    }

    private final BroadcastReceiver launchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            Timber.v("Received: %s", intent.getAction());
            if (intent.getAction().equals(Strings.ACTION_SUCCESS_UP_LAUNCHES)) {
                circularFillableLoaders.setProgress(20);
                upComplete = true;
                titles.setText("Step 2 of 5: Loading historical launches.");
            } else if (intent.getAction().equals(Strings.ACTION_SUCCESS_PREV_LAUNCHES)) {
                circularFillableLoaders.setProgress(40);
                previousComplete = true;
                titles.setText("Step 3 of 5: Loading vehicle information.");
            } else if (intent.getAction().equals(Strings.ACTION_SUCCESS_VEHICLE_DETAILS)) {
                circularFillableLoaders.setProgress(60);
                vehiclesDetailsComplete = true;
                titles.setText("Step 4 of 5: Loading extra vehicle details.");
            } else if (intent.getAction().equals(Strings.ACTION_SUCCESS_VEHICLES)) {
                circularFillableLoaders.setProgress(80);
                vehiclesComplete = true;
                titles.setText("Step 5 of 5: Loading mission data.");
            } else if (intent.getAction().equals(Strings.ACTION_SUCCESS_MISSIONS)) {
                circularFillableLoaders.setProgress(100);
                missionsComplete = true;
                titles.setText("Complete!");
                progressView.setVisibility(View.GONE);
                ListPreferences.getInstance(context).setFirstBoot(false);
                final Intent mainIntent = new Intent(context, MainActivity.class);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(mainIntent);
                    }
                }, 1500);


            }
        }
    };

    private final BroadcastReceiver errorReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Timber.v("Received: %s", intent.getAction());
            String error;
            if (intent.getAction().contains("FAILURE")){
                if (Connectivity.isConnected(context)) {
                    error = intent.getStringExtra("error");
                    Crashlytics.logException(new Throwable(error));
                } else {
                    error = "Connection timed out, check network connectivity?";
                }
                showSnackbar(error);
                titles.setText("Try again?");
                progressView.setVisibility(View.GONE);
                button.setVisibility(View.VISIBLE);
            }
        }
    };

    private void showSnackbar(String error) {
       Snackbar.make(view, "Error - " + error, Snackbar.LENGTH_SHORT).show();
    }
}
