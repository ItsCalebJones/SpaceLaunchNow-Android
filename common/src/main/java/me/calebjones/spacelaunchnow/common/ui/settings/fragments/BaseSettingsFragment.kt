package me.calebjones.spacelaunchnow.common.ui.settings.fragments


import android.view.*
import androidx.preference.PreferenceFragment



abstract class BaseSettingsFragment : PreferenceFragment() {

    private var name = "Unknown (Name not set)"

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    fun setName(name: String) {
        this.name = name
    }

}