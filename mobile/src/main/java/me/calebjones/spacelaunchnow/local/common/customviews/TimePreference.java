package me.calebjones.spacelaunchnow.local.common.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimePreference extends DialogPreference {
    private int lastHour=0;
    private int lastMinute=0;
    private TimePicker picker=null;

    public static int getHour(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[0]));
    }

    public static int getMinute(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[1]));
    }

    public TimePreference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);

        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
    }

    @Override
    protected View onCreateDialogView() {
        picker=new TimePicker(getContext());

        return(picker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);

        picker.setCurrentHour(lastHour);
        picker.setCurrentMinute(lastMinute);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            lastHour= picker.getCurrentHour();
            lastMinute= picker.getCurrentMinute();

            setSummary(getSummary());

            String lastMinuteString = String.valueOf(lastMinute);
            String lastHourString;
            if (lastHour > 0 && lastHour < 10){
                lastHourString = "0" + String.valueOf(lastHour);
            } else {
                lastHourString = String.valueOf(lastHour);
            }
            String time = lastHourString + ":" + (lastMinuteString.length() == 1 ? "0" + lastMinuteString : lastMinuteString);

            if (callChangeListener(time)) {
                persistString(time);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return(a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {

        String time;
        String defaultValueStr = (defaultValue != null) ? defaultValue.toString() : "00:00";
        if (restoreValue)
            time = getPersistedString(defaultValueStr);
        else {
            time = defaultValueStr;
            if (shouldPersist())
                persistString(defaultValueStr);
        }

        lastHour=getHour(time);
        lastMinute=getMinute(time);

        setSummary(getSummary());
    }

    @Override
    public CharSequence getSummary() {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, lastHour);
        cal.set(Calendar.MINUTE, lastMinute);
        DateFormat sdf = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);

        return sdf.format(cal.getTime());
    }
}