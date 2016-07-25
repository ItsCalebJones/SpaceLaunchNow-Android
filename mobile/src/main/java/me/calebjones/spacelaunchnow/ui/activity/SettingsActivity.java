package me.calebjones.spacelaunchnow.ui.activity;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jrejaud.wear_socket.WearSocket;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.File;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.ui.fragment.settings.NestedPreferenceFragment;
import me.calebjones.spacelaunchnow.ui.fragment.settings.SettingsFragment;
import me.calebjones.spacelaunchnow.ui.fragment.settings.SettingsFragment.Callback;
import me.calebjones.spacelaunchnow.utils.SnackbarHandler;
import timber.log.Timber;

public class SettingsActivity extends AppCompatActivity implements Callback {
    private static final String NESTED = "NESTED";
    private TextView toolbarTitle;
    private static ListPreferences sharedPreference;
    private Context context;
    private CoordinatorLayout supportCoordinator;
    private static final String BACKGROUND_KEY = "me.calebjones.spacelaunchnow.wear.background";
    private static final int BACKGROUND_NORMAL = 0;
    private static final int BACKGROUND_CUSTOM = 1;
    private static final int BACKGROUND_DYNAMIC = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int m_theme;
        this.context = getApplicationContext();

        sharedPreference = ListPreferences.getInstance(this.context);

        if (sharedPreference.getNightMode()) {
            m_theme = R.style.DarkTheme_NoActionBar;
        } else {
            m_theme = R.style.LightTheme_NoActionBar;
        }
        setTheme(m_theme);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbarTitle = (TextView) findViewById(R.id.title_text);
        supportCoordinator = (CoordinatorLayout) findViewById(R.id.support_coordinator);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }

        toolbarTitle.setText(R.string.action_settings);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.settings_content_frame, new SettingsFragment()).commit();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
            } else if (getFragmentManager().getBackStackEntryCount() == 1) {
                getFragmentManager().popBackStack();
                toolbarTitle.setText(R.string.settings);
            } else {
                getFragmentManager().popBackStack();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else if (getFragmentManager().getBackStackEntryCount() == 1) {
            getFragmentManager().popBackStack();
            this.toolbarTitle.setText(R.string.settings);
        } else {
            getFragmentManager().popBackStack();
        }
    }

    public void onNestedPreferenceSelected(int key) {
        getFragmentManager().beginTransaction().replace(R.id.settings_content_frame, NestedPreferenceFragment.newInstance(key), NESTED).addToBackStack(NESTED).commit();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (resultCode == RESULT_OK) {
            Uri selectedImage = imageReturnedIntent.getData();
            Timber.d("Data Received: %s ", selectedImage);
            String filePath = getRealPathFromURI(selectedImage);
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("filepath", filePath);
            editor.apply();
            Toast.makeText(context, "Saved!", Toast.LENGTH_LONG)
                    .show();
            WearSocket wearSocket = WearSocket.getInstance();
            wearSocket.updateDataItem("/config", BACKGROUND_KEY, BACKGROUND_CUSTOM);
            wearSocket.updateDataItem("/config", "background", getAssetFromFilepath(filePath));
        } else {
            Toast.makeText(context, "Error - Try disabling then re-selecting an image.", Toast.LENGTH_LONG)
                    .show();
        }
    }

    public Asset getAssetFromFilepath(String filepath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return createAssetFromBitmap(BitmapFactory.decodeFile(filepath, options));
    }

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }



    // Convert the image URI to the direct file system path
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }
}
