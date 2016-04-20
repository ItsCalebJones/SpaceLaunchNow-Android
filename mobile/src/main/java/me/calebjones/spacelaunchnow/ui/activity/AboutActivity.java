package me.calebjones.spacelaunchnow.ui.activity;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.LibsConfiguration;
import com.mikepenz.aboutlibraries.entity.Library;
import com.mikepenz.aboutlibraries.ui.LibsActivity;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;

public class AboutActivity extends LibsActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        int m_theme;
        final Context context = this;

        ListPreferences sharedPreference = ListPreferences.getInstance(context);

        if (sharedPreference.getNightMode()) {
            m_theme = R.style.AboutLibrariesDark;
        } else {
            m_theme = R.style.AboutLibrariesLight;
        }


        LibsConfiguration.LibsListener libsListener = new LibsConfiguration.LibsListener() {
            @Override
            public void onIconClicked(View v) {
            }

            @Override
            public boolean onLibraryAuthorClicked(View v, Library library) {
                return false;
            }

            @Override
            public boolean onLibraryContentClicked(View v, Library library) {
                return false;
            }

            @Override
            public boolean onLibraryBottomClicked(View v, Library library) {
                return false;
            }

            @Override
            public boolean onExtraClicked(View v, Libs.SpecialButton specialButton) {
                if(specialButton.ordinal() == 1){
                    Intent intent = new Intent(getApplicationContext(), SupportActivity.class);
                    startActivity(intent);
                } else if (specialButton.ordinal() == 2) {
                    customTabCallback();
                }
                return false;
            }

            @Override
            public boolean onIconLongClicked(View v) {
                return false;
            }

            @Override
            public boolean onLibraryAuthorLongClicked(View v, Library library) {
                return false;
            }

            @Override
            public boolean onLibraryContentLongClicked(View v, Library library) {
                return false;
            }

            @Override
            public boolean onLibraryBottomLongClicked(View v, Library library) {
                return false;
            }
        };

        setIntent(new LibsBuilder()
                .withAutoDetect(true)
                .withLicenseShown(true)
                .withVersionShown(true)
                .withListener(libsListener)
                .withActivityTitle("About")
                .withActivityTheme(m_theme)
                .withAboutSpecial2("Support")
                .withAboutSpecial3("Change Log")
                .withFields(R.string.class.getFields())
                .intent(this));
        super.onCreate(savedInstanceState);
    }

    public void customTabCallback(){
        Utils.openCustomTab(this, getApplicationContext(), "https://github.com/caman9119/SpaceLaunchNow/releases");
    }
}
