package me.calebjones.spacelaunchnow.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.Scanner;

import de.hdodenhof.circleimageview.CircleImageView;
import me.calebjones.spacelaunchnow.LaunchApplication;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.DatabaseManager;
import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.content.models.LaunchVehicle;
import me.calebjones.spacelaunchnow.ui.fragment.PayloadDetail;
import me.calebjones.spacelaunchnow.ui.fragment.SummaryDetail;


public class LaunchDetail extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener {

    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AppBarLayout appBarLayout;
    private ImageView detail_profile_backdrop;
    private CircleImageView detail_profile_image;
    private TextView detail_rocket, detail_mission_location;
    private int mMaxScrollSize;

    private String URL = "https://launchlibrary.net/1.1/launch/%s";

    public String response;
    public Launch launch;

    //TODO need to setTheme based on NightMode
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch_detail);

        //Setup Views
        tabLayout = (TabLayout) findViewById(R.id.detail_tabs);
        viewPager  = (ViewPager) findViewById(R.id.detail_viewpager);
        appBarLayout = (AppBarLayout) findViewById(R.id.detail_appbar);
        detail_profile_image = (CircleImageView) findViewById(R.id.detail_profile_image);
        detail_profile_backdrop = (ImageView) findViewById(R.id.detail_profile_backdrop);
        detail_rocket = (TextView) findViewById(R.id.detail_rocket);
        detail_mission_location = (TextView) findViewById(R.id.detail_mission_location);

        //Grab information from Intent
        Intent mIntent = getIntent();
        launch = (Launch)mIntent.getSerializableExtra("launch");
        getLaunchVehicle(launch);

        //Assign the title and mission locaiton data
        detail_rocket.setText(launch.getName());

        findProfileLogo();

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        appBarLayout.addOnOffsetChangedListener(this);
        mMaxScrollSize = appBarLayout.getTotalScrollRange();

        viewPager.setAdapter(new TabsAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
    }

    private void findProfileLogo() {

        //Default location, mission is unknown.
        String location = "Unknown Location";
        String mission = "Unknown Mission";
        String locationCountryCode = null;
        String rocketAgency = "";

        if (launch.getRocket().getAgencies().size() > 0){
            for (int i = 0; i < launch.getRocket().getAgencies().size(); i++ ){
                rocketAgency = rocketAgency + launch.getRocket().getAgencies().get(i).getAbbrev() + " ";
            }
        }

        //This checks to see if a location is available
        if (launch.getLocation().getName() != null) {

            //Check to see if a countrycode is available
            if (launch.getLocation().getPads().size() > 0 && launch.getLocation().getPads().
                    get(0).getAgencies().size() > 0) {
                locationCountryCode = launch.getLocation().getPads().
                        get(0).getAgencies().get(0).getCountryCode();

                Log.v(LaunchApplication.TAG, "LaunchDetail - CountryCode length: " +
                        String.valueOf(locationCountryCode.length()));

                //Go through various CountryCodes and assign flag.
                if (locationCountryCode.length() == 3) {

                    if (locationCountryCode.contains("USA")) {
                        //Check for SpaceX/Boeing/ULA/NASA
                        if (launch.getLocation().getPads().
                                get(0).getAgencies().get(0).getAbbrev().contains("SpX")){
                            //Apply SpaceX Logo
                            applyProfileLogo("http://i.imgur.com/3BqROn0.jpg");
                        } else if (launch.getLocation().getPads().
                                get(0).getAgencies().get(0).getAbbrev() == "BA" && launch.getRocket().getAgencies().get(0).getCountryCode() == "UKR"){
                            //Apply Yuzhnoye Logo
                            applyProfileLogo("https://i.imgur.com/KnDMy7l.png");
                        } else if (rocketAgency.contains("ULA")){
                            //Apply ULA Logo
                            applyProfileLogo("https://i.imgur.com/rh7JAa3.png");
                        } else {
                            //Else Apply USA flag
                            detail_profile_image.setImageResource(R.drawable.usa_flag);
                        }
                    } else if (locationCountryCode.contains("RUS")) {
                        //Apply Russia Logo
                        applyProfileLogo("https://i.imgur.com/eafUB5i.png");
                    } else if (locationCountryCode.contains("CHN")) {
                        applyProfileLogo("http://i.imgur.com/dIsaknI.png");
                    } else if (locationCountryCode.contains("IND")) {
                        applyProfileLogo("https://i.imgur.com/Caj9kpG.png");
                    } else if (locationCountryCode.contains("JPN")){
                        applyProfileLogo("https://i.imgur.com/QOdDYa5.png");
                    }

                } else if (launch.getLocation().getPads().
                        get(0).getAgencies().get(0).getAbbrev() == "ASA"){
                    //Apply Arianespace Logo
                    applyProfileLogo("https://i.imgur.com/yffq0aI.jpg");
                }
                location = (launch.getLocation().getName().substring(launch
                        .getLocation().getName().indexOf(", ") + 2));
            }
        }
        //Assigns the result of the two above checks.
        detail_mission_location.setText(location);
    }

    private void applyProfileLogo(String url){
        Log.d(LaunchApplication.TAG, "LaunchDetail - Loading Profile Image url: " + url);

        Picasso.with(this)
                .load(url)
                .placeholder(R.drawable.icon_international)
                .error(R.drawable.icon_international)
                .into(detail_profile_image);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    private void getLaunchVehicle(Launch result) {
        DatabaseManager databaseManager = new DatabaseManager(this);
        LaunchVehicle launchVehicle = databaseManager.getLaunchVehicle(result.getRocket()
                .getName());
        if (launchVehicle != null && launchVehicle.getImageURL().length() > 0){
            Glide.with(this)
                    .load(launchVehicle
                            .getImageURL())
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .crossFade()
                    .into(detail_profile_backdrop);
            Log.d(LaunchApplication.TAG, "LaunchDetail - " + launchVehicle.getLVName() + " "
                    + launchVehicle.getImageURL());
        }
    }

    public void setData(String data){
        response = data;
        Log.v(LaunchApplication.TAG, "LaunchDetail - " + response);
        Scanner scanner = new Scanner(response);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            // process the line
            Log.v(LaunchApplication.TAG, "LaunchDetail - " + line);
        }
        scanner.close();
    }

    public Launch getLaunch(){
        return launch;
    }

    public static void start(Context c) {
        c.startActivity(new Intent(c, LaunchDetail.class));
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int percentage = (Math.abs(i)) * 100 / mMaxScrollSize;

        if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false;
            detail_profile_image.animate().scaleY(0).scaleX(0).setDuration(200).start();
        }

        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;

            detail_profile_image.animate()
                    .scaleY(1).scaleX(1)
                    .start();
        }
    }

    class TabsAdapter extends FragmentPagerAdapter {
        public TabsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int i) {
            switch(i) {
                case 0: return SummaryDetail.newInstance();
                case 1: return PayloadDetail.newInstance();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0: return "Details";
                case 1: return "Mission";
            }
            return "";
        }
    }

}