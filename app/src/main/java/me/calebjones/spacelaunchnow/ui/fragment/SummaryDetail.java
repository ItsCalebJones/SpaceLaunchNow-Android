package me.calebjones.spacelaunchnow.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.ui.activity.LaunchDetail;

public class SummaryDetail extends Fragment {

    private SharedPreferences sharedPref;
    private static SharedPreference sharedPreference;
    private Context context;

    public static Launch detailLaunch;
    private TextView launch_date_title, date, launch_window_start, launch_window_end;

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        this.context = getContext();

        sharedPreference = SharedPreference.getInstance(this.context);

        if (sharedPreference.getNightMode()) {
            view = inflater.inflate(R.layout.dark_launch_summary, container, false);
        } else {
            view = inflater.inflate(R.layout.light_launch_summary, container, false);
        }

        launch_date_title = (TextView) view.findViewById(R.id.launch_date_title);
        date = (TextView) view.findViewById(R.id.date);
        launch_window_start = (TextView) view.findViewById(R.id.launch_window_start);
        launch_window_end = (TextView) view.findViewById(R.id.launch_window_end);

        setUpViews();
        return view;
    }

    public void setUpViews(){
        detailLaunch = ((LaunchDetail)getActivity()).getLaunch();

        //Setup SimpleDateFormat to parse out getNet date.
        SimpleDateFormat input = new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss zzz");
        SimpleDateFormat output = new SimpleDateFormat("MMMM dd, yyyy");
        input.toLocalizedPattern();

        Date mDate;
        String dateText = null;

        //Try to convert to Month day, Year.
        try {
            mDate = input.parse(detailLaunch.getNet());
            dateText = output.format(mDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        date.setText(dateText);

        //TODO: Get launch window only if wstamp and westamp is available, hide otherwise.
        launch_window_start.setText("Window Start: " + detailLaunch.getWindowstart());
        launch_window_end.setText("Window End: " + detailLaunch.getWindowend());
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public static Fragment newInstance() {
        return new SummaryDetail();
    }

}