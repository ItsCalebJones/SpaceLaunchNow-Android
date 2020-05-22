package me.calebjones.spacelaunchnow.ui.intro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import me.calebjones.spacelaunchnow.R;


public class OnboardingActivity extends AhoyOnboarderActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.BaseAppTheme);



        AhoyOnboarderCard introCard = new AhoyOnboarderCard(getString(R.string.app_name),
                getString(R.string.intro_card_description),
                R.drawable.intro_slide_one);
        AhoyOnboarderCard supporterCard = new AhoyOnboarderCard(getString(R.string.supporter_title),
                getString(R.string.supporter_card_description),
                R.drawable.ic_support);
        AhoyOnboarderCard vehicleCard = new AhoyOnboarderCard(getString(R.string.vehicle_card_title),
                getString(R.string.vehicle_card_description),
                R.drawable.intro_slide_three);
        AhoyOnboarderCard notifyCard = new AhoyOnboarderCard(getString(R.string.notification_card),
                getString(R.string.notification_card_description),
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

        setFinishButtonTitle(R.string.done);
        showNavigationControls(false);

        List<Integer> colorList = new ArrayList<>();
        colorList.add(R.color.slide_one_background);
        colorList.add(R.color.slide_two_background);
        colorList.add(R.color.slide_three_background);
        colorList.add(R.color.slide_four_background);

        setColorBackground(colorList);
//        setImageBackground(R.drawable.intro_slide_background);
        setOnboardPages(pages);

    }

    @Override
    public void onFinishButtonPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
