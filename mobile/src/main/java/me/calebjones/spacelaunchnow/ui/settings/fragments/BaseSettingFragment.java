/*
 * Copyright 2014 - 2016 Michael Rapp
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package me.calebjones.spacelaunchnow.ui.settings.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import me.calebjones.spacelaunchnow.utils.analytics.Analytics;

public abstract class BaseSettingFragment extends PreferenceFragment {

    private String name = "Unknown (Name not set)";

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        Analytics.getInstance().sendScreenView(name, name + " resumed.");
    }

    @Override
    public void onPause() {
        super.onPause();
        Analytics.getInstance().notifyGoneBackground();
    }

    public void setName(String name){
        this.name = name;
    }

}
