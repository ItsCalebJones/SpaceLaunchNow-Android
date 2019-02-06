package me.calebjones.spacelaunchnow.common.ui.settings

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.*
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatDelegate
import com.jaredrummler.cyanea.CyaneaResources
import com.jaredrummler.cyanea.app.BaseCyaneaActivity
import com.jaredrummler.cyanea.delegate.BaseAppCompatDelegate
import com.jaredrummler.cyanea.delegate.CyaneaDelegate
import me.calebjones.spacelaunchnow.common.BuildConfig
import me.calebjones.spacelaunchnow.common.R
import androidx.preference.PreferenceFragmentCompat
import de.mrapp.android.preference.activity.PreferenceActivity



class CyaneaSettingsActivity : PreferenceActivity(), BaseAppCompatDelegate, BaseCyaneaActivity {

    private val appCompatDelegate: AppCompatDelegate by lazy {
        AppCompatDelegate.create(this, null)
    }

    private val delegate: CyaneaDelegate by lazy {
        CyaneaDelegate.create(this, cyanea, getThemeResId())
    }

    private val resources: CyaneaResources by lazy {
        CyaneaResources(super.getResources(), cyanea)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(delegate.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        delegate.onCreate(savedInstanceState)
        appCompatDelegate.installViewFactory()
        appCompatDelegate.onCreate(savedInstanceState)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        appCompatDelegate.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        appCompatDelegate.supportActionBar?.setDisplayShowHomeEnabled(true)
        super.onCreate(savedInstanceState)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        appCompatDelegate.onPostCreate(savedInstanceState)
        delegate.onPostCreate(savedInstanceState)
    }

    public override fun onCreateNavigation(@NonNull fragment: PreferenceFragmentCompat) {
        if (BuildConfig.DEBUG) {
            if (cyanea.isDark){
                fragment.addPreferencesFromResource(R.xml.preference_headers_debug_dark)
            } else {
                fragment.addPreferencesFromResource(R.xml.preference_headers_debug)
            }
        } else {
            if (cyanea.isDark){
                fragment.addPreferencesFromResource(R.xml.preference_headers_dark)
            } else {
                fragment.addPreferencesFromResource(R.xml.preference_headers)
            }
        }
//        initializeAppearanceNavigationPreference(fragment)
//        initializeBehaviorNavigationPreference(fragment)
    }

    public override fun onStart() {
        super.onStart()
        delegate.onStart()
    }

    override fun onResume() {
        super.onResume()
        delegate.onResume()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onPostResume() {
        super.onPostResume()
        appCompatDelegate.onPostResume()
    }

    override fun onStop() {
        super.onStop()
        appCompatDelegate.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        appCompatDelegate.onDestroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        appCompatDelegate.onConfigurationChanged(newConfig)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        delegate.onCreateOptionsMenu(menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item!!.itemId

        if (id == R.id.home) {
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun invalidateOptionsMenu() {
        appCompatDelegate.invalidateOptionsMenu()
    }

    override fun onTitleChanged(title: CharSequence, color: Int) {
        super.onTitleChanged(title, color)
        appCompatDelegate.setTitle(title)
    }

    override fun setContentView(@LayoutRes layoutResID: Int) {
        appCompatDelegate.setContentView(layoutResID)
    }

    override fun setContentView(view: View) {
        appCompatDelegate.setContentView(view)
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams) {
        appCompatDelegate.setContentView(view, params)
    }

    override fun addContentView(view: View, params: ViewGroup.LayoutParams) {
        appCompatDelegate.addContentView(view, params)
    }

    override fun getSupportActionBar(): ActionBar? = appCompatDelegate.supportActionBar

    override fun getMenuInflater(): MenuInflater = appCompatDelegate.menuInflater

    override fun getResources(): Resources = resources

    override fun getDelegate(): AppCompatDelegate = appCompatDelegate

}