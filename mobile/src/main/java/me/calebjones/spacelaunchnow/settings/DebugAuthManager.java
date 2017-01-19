package me.calebjones.spacelaunchnow.settings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DebugAuthManager {

    public static boolean getAuthResult(CharSequence input){
        Date date = Calendar.getInstance().getTime();
        DateFormat formatter = new SimpleDateFormat("ddMMyyyy");
        String today = formatter.format(date);

        if (today.equals(input.toString())){
            return true;
        } else {
            return false;
        }
    }
}
