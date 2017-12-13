package me.calebjones.spacelaunchnow.ui.intro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;

import java.util.ArrayList;
import java.util.List;

import jonathanfinerty.once.Once;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.ui.main.MainActivity;

/**
 * Created by Caleb on 11/16/2017.
 */

public class OnboardingActivity extends AhoyOnboarderActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        AhoyOnboarderCard introCard = new AhoyOnboarderCard("Space Launch Now", "Keep up to date on all your favorite  orbital launches, missions, and launch vehicles.",
                R.drawable.intro_slide_one_foreground);

        AhoyOnboarderCard notifyCard = new AhoyOnboarderCard("Notification for Launches", "Get notifications for upcoming launches and look into the history of spaceflight",
                R.drawable.intro_slide_two_foreground);

        AhoyOnboarderCard vehicleCard = new AhoyOnboarderCard("Find Launch Vehicles", "Get to know the vehicles that have taken us to orbit.",
                R.drawable.intro_slide_four_foreground);

        introCard.setBackgroundColor(R.color.slide_one);
        notifyCard.setBackgroundColor(R.color.slide_two);
        vehicleCard.setBackgroundColor(R.color.slide_three);

        List<AhoyOnboarderCard> pages = new ArrayList<>();

        pages.add(introCard);
        pages.add(notifyCard);
        pages.add(vehicleCard);

        for (AhoyOnboarderCard page : pages) {
            page.setTitleColor(R.color.material_typography_primary_text_color_light);
            page.setDescriptionColor(R.color.material_typography_secondary_text_color_light);
//            page.setIconLayoutParams(500, 500, 0, 0, 0, 0);
        }

        setFinishButtonTitle("Done");
        showNavigationControls(false);

        List<Integer> colorList = new ArrayList<>();
        colorList.add(R.color.slide_one_background);
        colorList.add(R.color.slide_two_background);
        colorList.add(R.color.slide_three_background);

        setColorBackground(colorList);
        setImageBackground(R.drawable.intro_slide_background);
        setOnboardPages(pages);

    }

    @Override
    public void onFinishButtonPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
