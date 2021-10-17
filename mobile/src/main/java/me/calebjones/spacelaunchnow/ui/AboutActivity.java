package me.calebjones.spacelaunchnow.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.FrameLayout;
import android.widget.Toast;


import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.vansuita.materialabout.builder.AboutBuilder;
import com.vansuita.materialabout.views.AboutView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.common.base.BaseActivityOld;
import me.calebjones.spacelaunchnow.common.ui.settings.DebugAuthManager;
import me.calebjones.spacelaunchnow.common.ui.supporter.SupporterActivity;
import me.calebjones.spacelaunchnow.common.utils.Utils;
import me.calebjones.spacelaunchnow.ui.changelog.ChangelogActivity;
import me.calebjones.spacelaunchnow.ui.debug.DebugActivity;
import me.calebjones.spacelaunchnow.ui.intro.OnboardingActivity;

public class AboutActivity extends BaseActivityOld {
    
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.activity_about);
        loadAbout();
    }

    private void loadAbout() {
        final FrameLayout flHolder = (FrameLayout) this.findViewById(R.id.about);

        AboutBuilder builder = AboutBuilder.with(this)
                .setAppName("Space Launch Now")
                .setAppTitle(Utils.getVersionName(this))
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
                .addLink(new IconicsDrawable(this)
                                .icon(FontAwesome.Icon.faw_patreon)
                                .sizeDp(24).toBitmap(),
                        "Patreon",
                        "https://www.patreon.com/spacelaunchnow")
                .addTwitterLink("spacelaunchnow")
                .addFacebookLink("spacelaunchnow")
                .addWebsiteLink("https://spacelaunchnow.me")
                .addLink(new IconicsDrawable(this)
                                .icon(CommunityMaterial.Icon.cmd_discord)
                                .sizeDp(24).toBitmap(),
                        "Discord",
                        "https://discord.gg/WVfzEDW")
                .addAction(new IconicsDrawable(this)
                                .icon(CommunityMaterial.Icon2.cmd_rocket)
                                .sizeDp(24).toBitmap(),
                        "Launch Library",
                        "https://launchlibrary.net")
                .addAction(new IconicsDrawable(this)
                                .icon(CommunityMaterial.Icon.cmd_android_debug_bridge)
                                .sizeDp(24).toBitmap(),
                        "Debug", view -> new MaterialDialog.Builder(context)
                                .title("Enter Support Code")
                                .content("To debug the application - please enter the code from support.")
                                .inputType(InputType.TYPE_CLASS_NUMBER)
                                .inputRangeRes(1,100, R.color.accent)
                                .input("Support Code", null, (dialog, input) -> {
                                    // Do something
                                    if(!input.equals("") && DebugAuthManager.getAuthResult(input)){
                                        goToDebug();
                                    } else {
                                        Toast.makeText(context, "Error - code was invalid.", Toast.LENGTH_LONG).show();

                                    }
                                }).show()
                )
                .addShareAction("Checkout " + R.string.app_name)
                .addUpdateAction()
                .setActionsColumnsCount(2)

                .addAction(new IconicsDrawable(this).icon(CommunityMaterial.Icon.cmd_google_translate).sizeDp(24).toBitmap(),
                        "Translate", "https://goo.gl/forms/Pt4QA5JNznF2MiKu1")
                .addChangeLogAction(new Intent(this, ChangelogActivity.class))
                .addIntroduceAction(new Intent(this, OnboardingActivity.class))
                .addRemoveAdsAction(new Intent(this, SupporterActivity.class))
                .addAction(new IconicsDrawable(this)
                                .icon(CommunityMaterial.Icon.cmd_file_document)
                                .sizeDp(24)
                                .toBitmap(),
                        "Privacy Policy",
                        view -> {
                            try {
                                StringBuilder buf = new StringBuilder();
                                InputStream json = context.getAssets().open("PRIVACY.md");
                                BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));

                                String str;
                                while((str = in.readLine()) != null) {
                                    buf.append(str).append("\n");
                                }

                                in.close();
                                new MaterialDialog.Builder(context)
                                        .title("Privacy Policy")
                                        .content(buf.toString())
                                        .positiveText("Got it.")
                                        .show();
                            } catch (IOException var6) {
                                var6.printStackTrace();
                            }

                        })
                .addAction(new IconicsDrawable(this)
                                .icon(CommunityMaterial.Icon.cmd_file_check)
                                .sizeDp(24)
                                .toBitmap(),
                        "Terms of Use",
                        view -> {
                            try {
                                StringBuilder buf = new StringBuilder();
                                InputStream json = context.getAssets().open("TERMS.md");
                                BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));

                                String str;
                                while((str = in.readLine()) != null) {
                                    buf.append(str).append("\n");
                                }

                                in.close();
                                new MaterialDialog.Builder(context)
                                        .title("Terms of Use")
                                        .content(buf.toString())
                                        .positiveText("Got it.")
                                        .show();
                            } catch (IOException var6) {
                                var6.printStackTrace();
                            }
                        })
                .setWrapScrollView(true)
                .setShowAsCard(true);


        AboutView view = builder.build();

        flHolder.addView(view);
    }

    private void goToDebug() {
        Intent i = new Intent(AboutActivity.this, DebugActivity.class);
        startActivity(i);
    }
}
