package me.calebjones.spacelaunchnow.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.LibsConfiguration;
import com.mikepenz.aboutlibraries.entity.Library;
import com.mikepenz.aboutlibraries.ui.LibsActivity;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.utils.Utils;

public class AboutActivity extends LibsActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        int m_theme;

        ListPreferences sharedPreference = ListPreferences.getInstance(this);

        m_theme = R.style.AboutLibrariesLight;

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
                    specialButtonOne();
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
                .intent(this.getApplicationContext()));
        super.onCreate(savedInstanceState);
    }

    private void specialButtonOne() {
        Intent intent = new Intent(this, SupportActivity.class);
        startActivity(intent);
    }

    public void customTabCallback(){
        Utils.openCustomTab(this, this, "https://github.com/caman9119/SpaceLaunchNow/releases");
    }

}
