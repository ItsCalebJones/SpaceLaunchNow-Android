package me.calebjones.spacelaunchnow.common.prefs

import android.app.Activity
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import me.calebjones.spacelaunchnow.common.R

object ThemeHelper {

    const val LIGHT_MODE = "light"
    const val DARK_MODE = "dark"
    const val DEFAULT_MODE = "default"

    @JvmStatic
    fun applyTheme(theme: String) {
        val mode = when (theme) {
            LIGHT_MODE -> AppCompatDelegate.MODE_NIGHT_NO
            DARK_MODE -> AppCompatDelegate.MODE_NIGHT_YES
            else -> {
                when {
                    isAtLeastP() -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    isAtLeastL() -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                    else -> AppCompatDelegate.MODE_NIGHT_NO
                }
            }
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    @JvmStatic
    fun isDarkMode(activity: Activity): Boolean {
        return activity.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    @JvmStatic
    fun getIconColor(activity: Activity): Int {
        var color: Int = if (isDarkMode(activity)){
            Color.WHITE
        } else{
            Color.BLACK
        }
        return color
    }


    private fun isAtLeastP() = Build.VERSION.SDK_INT >= 28
    private fun isAtLeastL() = Build.VERSION.SDK_INT >= 21
}