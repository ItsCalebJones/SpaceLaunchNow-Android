package me.calebjones.spacelaunchnow.utils;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import me.calebjones.spacelaunchnow.R;

public class TimeRangePickerDialogCustom extends DialogFragment implements View.OnClickListener {
    TabHost tabs;
    Button setTimeRange;
    TimePicker startTimePicker, endTimePicker;
    OnTimeRangeSelectedListener onTimeRangeSelectedListener;
    boolean is24HourMode;

    public static TimeRangePickerDialogCustom newInstance(OnTimeRangeSelectedListener callback, boolean is24HourMode) {
        TimeRangePickerDialogCustom ret = new TimeRangePickerDialogCustom();
        ret.initialize(callback, is24HourMode);

        return ret;
    }

    public void initialize(OnTimeRangeSelectedListener callback,
                           boolean is24HourMode) {
        onTimeRangeSelectedListener = callback;
        this.is24HourMode = is24HourMode;

    }

    public interface OnTimeRangeSelectedListener {
        void onTimeRangeSelected(int startHour, int startMin, int endHour, int endMin);
    }

    public void setOnTimeRangeSetListener(OnTimeRangeSelectedListener callback) {
        onTimeRangeSelectedListener = callback;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.timerange_picker_dialog, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        tabs = (TabHost) root.findViewById(R.id.tabHost);
        setTimeRange = (Button) root.findViewById(R.id.bSetTimeRange);
        startTimePicker = (TimePicker) root.findViewById(R.id.startTimePicker);
        endTimePicker = (TimePicker) root.findViewById(R.id.endTimePicker);
        setTimeRange.setOnClickListener(this);
        tabs.findViewById(R.id.tabHost);
        tabs.setup();
        TabHost.TabSpec tabpage1 = tabs.newTabSpec("one");
        tabpage1.setContent(R.id.startTimeGroup);
        tabpage1.setIndicator("Start Time");

        TabHost.TabSpec tabpage2 = tabs.newTabSpec("two");
        tabpage2.setContent(R.id.endTimeGroup);
        tabpage2.setIndicator("End Time");

        tabs.addTab(tabpage1);
        tabs.addTab(tabpage2);


        if (android.os.Build.VERSION.SDK_INT >= 23){
            SimpleDateFormat sdf = new SimpleDateFormat("hh:ss a");
            Date sdate = null;
            Date edate = null;
            try {
                sdate = sdf.parse("10:00 PM");
                edate = sdf.parse("07:00 AM");
            } catch (ParseException e) {
            }
            Calendar s = Calendar.getInstance();
            Calendar e = Calendar.getInstance();

            s.setTime(sdate);
            e.setTime(edate);

            startTimePicker.setHour(s.get(Calendar.HOUR_OF_DAY));
            startTimePicker.setMinute(s.get(Calendar.MINUTE));

            endTimePicker.setHour(e.get(Calendar.HOUR_OF_DAY));
            endTimePicker.setMinute(e.get(Calendar.MINUTE));
        } else {
        }
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onStart() {
        super.onStart();

        // safety check
        if (getDialog() == null)
            return;
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bSetTimeRange) {
            dismiss();
            int startHour = startTimePicker.getCurrentHour();
            int startMin = startTimePicker.getCurrentMinute();
            int endHour = endTimePicker.getCurrentHour();
            int endMin = endTimePicker.getCurrentMinute();
            onTimeRangeSelectedListener.onTimeRangeSelected(startHour, startMin, endHour, endMin);
        }
    }
}