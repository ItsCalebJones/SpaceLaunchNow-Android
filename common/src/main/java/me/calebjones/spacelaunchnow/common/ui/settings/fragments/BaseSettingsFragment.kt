package me.calebjones.spacelaunchnow.common.ui.settings.fragments


import com.jaredrummler.cyanea.Cyanea
import com.jaredrummler.cyanea.app.BaseCyaneaActivity
import android.view.*
import de.mrapp.android.preference.activity.PreferenceFragment


/**
 * Base class for fragments[Fragment] that use [Cyanea] for dynamic themes.
 */
abstract class BaseSettingsFragment : PreferenceFragment() {

    private var name = "Unknown (Name not set)"

    /**
     * The [Cyanea] instance used for styling.
     */
    open val cyanea: Cyanea get() = (activity as? BaseCyaneaActivity)?.cyanea ?: Cyanea.instance

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater?) {
        applyMenuTint(menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    protected open fun applyMenuTint(menu: Menu) = cyanea.tint(menu, requireActivity())

    fun setName(name: String) {
        this.name = name
    }

//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//        val view = super.onCreateView(inflater, container, savedInstanceState)
//        view.setBackgroundColor(resources.getColor(cyanea.backgroundColor))
//
//        return view
//    }

}