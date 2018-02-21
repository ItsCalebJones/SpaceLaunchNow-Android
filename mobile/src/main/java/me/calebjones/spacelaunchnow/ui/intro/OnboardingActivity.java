package me.calebjones.spacelaunchnow.ui.intro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;

import java.util.ArrayList;
import java.util.List;

import me.calebjones.spacelaunchnow.R;


public class OnboardingActivity extends AhoyOnboarderActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        AhoyOnboarderCard introCard = new AhoyOnboarderCard("Space Launch Now",
                "Keep up to date on all your favorite orbital launches, missions, and launch vehicles.",
                R.drawable.intro_slide_one);
        AhoyOnboarderCard supporterCard = new AhoyOnboarderCard("Become a Supporter",
                "Exclusive Widgets, Wear Complications, Remove Ads and more.",
                R.drawable.ic_support);
        AhoyOnboarderCard vehicleCard = new AhoyOnboarderCard("Find Launch Vehicles",
                "Get to know the vehicles that have taken us to orbit.",
                R.drawable.intro_slide_three);
        AhoyOnboarderCard notifyCard = new AhoyOnboarderCard("Notification for Launches",
                "Get notifications for the launches you care about.",
                R.drawable.intro_slide_two);


        introCard.setBackgroundColor(R.color.slide_one);
        vehicleCard.setBackgroundColor(R.color.slide_two);
        supporterCard.setBackgroundColor(R.color.slide_three);
        notifyCard.setBackgroundColor(R.color.slide_four);

        List<AhoyOnboarderCard> pages = new ArrayList<>();

        pages.add(introCard);
        pages.add(vehicleCard);
        pages.add(supporterCard);
        pages.add(notifyCard);

        for (AhoyOnboarderCard page : pages) {
            page.setTitleColor(R.color.material_typography_primary_text_color_light);
            page.setDescriptionColor(R.color.material_typography_secondary_text_color_light);
        }

        setFinishButtonTitle("Done");
        showNavigationControls(false);

        List<Integer> colorList = new ArrayList<>();
        colorList.add(R.color.slide_one_background);
        colorList.add(R.color.slide_two_background);
        colorList.add(R.color.slide_three_background);
        colorList.add(R.color.slide_four_background);

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
