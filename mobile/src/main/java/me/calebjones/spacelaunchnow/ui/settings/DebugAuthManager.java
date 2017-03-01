package me.calebjones.spacelaunchnow.ui.settings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DebugAuthManager {

    public static boolean getAuthResult(CharSequence input){
        Date date = Calendar.getInstance().getTime();
        DateFormat formatter = new SimpleDateFormat("ddMMyyyy");
        String today = formatter.format(date);

        int factor = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        Integer todayInt = Integer.parseInt(today) * factor;
        Integer inputInt = Integer.parseInt(input.toString());

        return todayInt.equals(inputInt);
    }
}
