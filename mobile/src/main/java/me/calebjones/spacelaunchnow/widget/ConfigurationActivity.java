package me.calebjones.spacelaunchnow.widget;


import android.app.Activity;
import android.os.Bundle;

import me.calebjones.spacelaunchnow.R;

public class ConfigurationActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_tutorial);
        setResult(RESULT_CANCELED);
    }
}
