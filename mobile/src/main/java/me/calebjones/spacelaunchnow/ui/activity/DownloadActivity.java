package me.calebjones.spacelaunchnow.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;
import timber.log.Timber;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.content.services.LaunchDataService;
import me.calebjones.spacelaunchnow.utils.SnackbarHandler;
import me.calebjones.spacelaunchnow.utils.Utils;


public class DownloadActivity extends AppCompatActivity {

    @BindView(R.id.start_download)
    AppCompatButton downloadButton;
    @BindView(R.id.download_background)
    AppCompatButton backgroundButton;
    @BindView(R.id.progress_download)
    ProgressBar progressView;
    @BindView(R.id.titles)
    TextView titles;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout view;
    @BindView(R.id.circularFillableLoaders)
    CircularFillableLoaders circularFillableLoaders;

    private boolean isLaunchReceiverRegistered = false;
    private boolean isErrorReceiverRegistered = false;
    private boolean downloading = false;
    private int progress = 5;

    @OnClick(R.id.download_background) void finishBackground() {
        ListPreferences.getInstance(this).setFirstBoot(false);
        startActivity(new Intent(this, MainActivity.class));
    }

    @OnClick(R.id.start_download) void startDownload() {
        downloading = true;
        downloadButton.setVisibility(View.GONE);
        progressView.setVisibility(View.VISIBLE);
        backgroundButton.setVisibility(View.VISIBLE);
        progressView.setIndeterminate(true);
        progress = 0;
        circularFillableLoaders.setProgress(0);
        titles.setText("Step 1 of 5: Loading upcoming launches.");

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
        isLaunchReceiverRegistered = true;
        isErrorReceiverRegistered = true;

        Intent launchIntent = new Intent(this, LaunchDataService.class);
        launchIntent.setAction(Strings.ACTION_GET_ALL_WIFI);
        this.startService(launchIntent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        ButterKnife.bind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
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
                .setDescription("This update brought a huge overhaul to the data storage of launches." +
                        " The easiest way to migrate is essentially a wipe and install, that being said welcome to the new version!")
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
        if (isLaunchReceiverRegistered) {
            unregisterReceiver(launchReceiver);
            isLaunchReceiverRegistered = false;
        }
        if (isErrorReceiverRegistered) {
            unregisterReceiver(errorReceiver);
            isErrorReceiverRegistered = false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("status", titles.getText().toString());
        outState.putBoolean("downloading", downloading);
        outState.putInt("progress", progress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        titles.setText(savedInstanceState.getString("status"));
        if (savedInstanceState.getBoolean("downloading")){
            downloading = true;
            progress = savedInstanceState.getInt("progress");
            circularFillableLoaders.setProgress(progress);

            downloadButton.setVisibility(View.GONE);
            progressView.setVisibility(View.VISIBLE);
            backgroundButton.setVisibility(View.VISIBLE);
            progressView.setIndeterminate(true);

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
            isLaunchReceiverRegistered = true;
            isErrorReceiverRegistered = true;
        }
    }

    private final BroadcastReceiver launchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            Timber.v("Received: %s", intent.getAction());
            if (intent.getAction().equals(Strings.ACTION_SUCCESS_UP_LAUNCHES)) {
                progress = 20;
                circularFillableLoaders.setProgress(20);
                titles.setText("Step 2 of 5: Loading historical launches.");
            } else if (intent.getAction().equals(Strings.ACTION_SUCCESS_PREV_LAUNCHES)) {
                progress = 40;
                circularFillableLoaders.setProgress(40);
                titles.setText("Step 3 of 5: Loading vehicle information.");
            } else if (intent.getAction().equals(Strings.ACTION_SUCCESS_VEHICLE_DETAILS)) {
                progress = 60;
                circularFillableLoaders.setProgress(60);
                titles.setText("Step 4 of 5: Loading extra vehicle details.");
            } else if (intent.getAction().equals(Strings.ACTION_SUCCESS_VEHICLES)) {
                progress = 80;
                circularFillableLoaders.setProgress(80);
                titles.setText("Step 5 of 5: Loading mission data.");
            } else if (intent.getAction().equals(Strings.ACTION_SUCCESS_MISSIONS)) {
                progress = 100;
                circularFillableLoaders.setProgress(100);
                downloading = false;
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
                }, 2000);
            }
        }
    };

    private final BroadcastReceiver errorReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Timber.v("Received: %s", intent.getAction());
            if (intent.getAction().contains("FAILURE")){
                SnackbarHandler.showErrorSnackbar(context, view, intent);
                titles.setText("Try again?");
                unregisterReceiver(launchReceiver);
                unregisterReceiver(errorReceiver);
                progressView.setVisibility(View.GONE);
                circularFillableLoaders.setProgress(95);
                downloadButton.setVisibility(View.VISIBLE);
            }
            downloading = false;
        }
    };

}
