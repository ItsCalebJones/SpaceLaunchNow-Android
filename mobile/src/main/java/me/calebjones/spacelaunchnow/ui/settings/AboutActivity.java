package me.calebjones.spacelaunchnow.ui.settings;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.SupportActivity;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.ui.changelog.ChangelogActivity;
import me.calebjones.spacelaunchnow.ui.intro.OnboardingActivity;
import me.calebjones.spacelaunchnow.ui.supporter.SupporterActivity;
import me.calebjones.spacelaunchnow.utils.Utils;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.Iconics;
import com.mikepenz.iconics.IconicsDrawable;
import com.vansuita.materialabout.builder.AboutBuilder;
import com.vansuita.materialabout.views.AboutView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        loadAbout();
    }

    private void loadAbout() {
        final FrameLayout flHolder = (FrameLayout) this.findViewById(R.id.about);

        AboutBuilder builder = AboutBuilder.with(this)
                .setAppIcon(R.drawable.launcher)
                .setAppName(R.string.app_name)
                .setPhoto(R.drawable.ic_jones_logo)
                .setCover(R.mipmap.profile_cover)
                .setLinksAnimated(true)
                .setName("Caleb Jones")
                .setSubTitle("Amateur Imagineer")
                .setLinksColumnsCount(3)
                .setBrief("Gamer, Nerd, Developer, Tester.")
                .addGooglePlayStoreLink("7111684947714289915")
                .addGitHubLink("itscalebjones")
                .addTwitterLink("spacelaunchnow")
                .addFacebookLink("spacelaunchnow")
                .addWebsiteLink("https://spacelaunchnow.me")
                .addLink(new IconicsDrawable(this)
                        .icon(CommunityMaterial.Icon.cmd_rocket)
                        .sizeDp(24).toBitmap(),
                        "Launch Library",
                        "https://launchlibrary.net")
                .setAppName("Space Launch Now")
                .setAppTitle(Utils.getVersionName(this))
                .addShareAction("Checkout " + R.string.app_name)
                .addUpdateAction()
                .setActionsColumnsCount(2)
                .addChangeLogAction(new Intent(this, ChangelogActivity.class))
                .addIntroduceAction(new Intent(this, OnboardingActivity.class))
                .addDonateAction(new Intent(this, SupporterActivity.class))
                .addAction(new IconicsDrawable(this)
                        .icon(CommunityMaterial.Icon.cmd_discord)
                        .sizeDp(24).toBitmap(),
                        "Discord",
                        "https://discord.gg/WVfzEDW")
                .setWrapScrollView(true)
                .setShowAsCard(true);


        AboutView view = builder.build();

        flHolder.addView(view);
    }
}
