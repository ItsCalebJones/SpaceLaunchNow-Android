package me.calebjones.spacelaunchnow.wear.utils;

import android.text.format.DateFormat;
import android.widget.ImageView;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.calebjones.spacelaunchnow.wear.R;


public class Utils {

    public static SimpleDateFormat getSimpleDateFormatForUI(String pattern) {
        String format = DateFormat.getBestDateTimePattern(Locale.getDefault(), pattern);
        return new SimpleDateFormat(format, Locale.getDefault());
    }

    public static void setCategoryIcon(ImageView imageView, String type) {
        if (type != null) {
            switch (type) {
                case "Earth Science":
                    imageView.setImageResource(R.drawable.ic_earth_white);
                    break;
                case "Planetary Science":
                    imageView.setImageResource(R.drawable.ic_planetary_white);
                    break;
                case "Astrophysics":
                    imageView.setImageResource(R.drawable.ic_astrophysics_white);
                    break;
                case "Heliophysics":
                    imageView.setImageResource(R.drawable.ic_heliophysics_alt_white);
                    break;
                case "Human Exploration":
                    imageView.setImageResource(R.drawable.ic_human_explore_white);
                    break;
                case "Robotic Exploration":
                    imageView.setImageResource(R.drawable.ic_robotic_explore_white);
                    break;
                case "Government/Top Secret":
                    imageView.setImageResource(R.drawable.ic_top_secret_white);
                    break;
                case "Tourism":
                    imageView.setImageResource(R.drawable.ic_tourism_white);
                    break;
                case "Unknown":
                    imageView.setImageResource(R.drawable.ic_unknown_white);
                    break;
                case "Communications":
                    imageView.setImageResource(R.drawable.ic_satellite_white);
                    break;
                case "Resupply":
                    imageView.setImageResource(R.drawable.ic_resupply_white);
                    break;
                default:
                    imageView.setImageResource(R.drawable.ic_unknown_white);
                    break;
            }
        } else {
            imageView.setImageResource(R.drawable.ic_unknown_white);
        }
    }
}
