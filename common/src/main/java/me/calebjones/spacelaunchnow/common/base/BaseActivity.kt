package me.calebjones.spacelaunchnow.common.base

import android.content.Context
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.pixplicity.easyprefs.library.Prefs
import io.realm.Realm
import me.calebjones.spacelaunchnow.common.utils.Utils
import timber.log.Timber
import java.util.*

open class BaseActivity : AppCompatActivity() {

    lateinit var mRealm: Realm
    lateinit var name: String
    lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRealm = Realm.getDefaultInstance()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }

    override fun attachBaseContext(newBase: Context) {
        val context: Context = if (!Prefs.getBoolean("locale_changer", true)) {
            Utils.changeLang(newBase, "en-US")
        } else {
            Utils.changeLang(newBase, Locale.getDefault().language)
        }
        super.attachBaseContext(context)
    }

    override fun onDestroy() {
        Timber.d("onDestroy")
        mRealm.close()
        super.onDestroy()
    }

    public override fun onStart() {
        super.onStart()
        Timber.d("onStart")
        if (mRealm.isClosed) {
            mRealm = Realm.getDefaultInstance()
        }
    }

    public override fun onResume() {
        super.onResume()
        Timber.d("onResume")
    }

    public override fun onPause() {
        super.onPause()
        Timber.d("onPause")
    }


    fun getRealm(): Realm {
        if (mRealm.isClosed) {
            mRealm = Realm.getDefaultInstance()
        }
        return mRealm
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        return true
    }


}
