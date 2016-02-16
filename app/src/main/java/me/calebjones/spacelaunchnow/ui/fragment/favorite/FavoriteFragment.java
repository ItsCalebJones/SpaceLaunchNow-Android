package me.calebjones.spacelaunchnow.ui.fragment.favorite;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.malinskiy.superrecyclerview.swipe.SparseItemRemoveAnimator;
import com.malinskiy.superrecyclerview.swipe.SwipeDismissRecyclerViewTouchListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.calebjones.spacelaunchnow.BuildConfig;
import me.calebjones.spacelaunchnow.MainActivity;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.adapter.FavoriteAdapter;
import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.content.services.LaunchDataService;
import me.calebjones.spacelaunchnow.ui.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;


public class FavoriteFragment extends Fragment {

    @Bind(R.id.nasa_switch)
    AppCompatCheckBox nasaSwitch;
    @Bind(R.id.spacex_switch)
    AppCompatCheckBox spacexSwitch;
    @Bind(R.id.roscosmos_switch)
    AppCompatCheckBox roscosmosSwitch;
    @Bind(R.id.ula_switch)
    AppCompatCheckBox ulaSwitch;
    @Bind(R.id.arianespace_switch)
    AppCompatCheckBox arianespaceSwitch;
    @Bind(R.id.casc_switch)
    AppCompatCheckBox cascSwitch;
    @Bind(R.id.isro_switch)
    AppCompatCheckBox isroSwitch;
    @Bind(R.id.custom_switch)
    AppCompatCheckBox customSwitch;
    private List<Launch> rocketLaunches;
    private SharedPreference sharedPreference;
    private static final Field sChildFragmentManagerField;
    private FloatingActionButton menu;
    private View color_reveal;
    private SuperRecyclerView mRecyclerView;
    private FavoriteAdapter adapter;
    private SparseItemRemoveAnimator mSparseAnimator;
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean active;
    private boolean switchChanged;
    private Handler mHandler;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.sharedPreference = SharedPreference.getInstance(getContext());
        this.rocketLaunches = new ArrayList();
        adapter = new FavoriteAdapter(getActivity());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int m_theme;
        final int color;
        Context context1 = getContext();
        active = false;

        sharedPreference = SharedPreference.getInstance(context1);

        if (sharedPreference.getNightMode()) {
            m_theme = R.style.DarkTheme_NoActionBar;
            color = R.color.darkPrimary;
        } else {
            m_theme = R.style.LightTheme_NoActionBar;
            color = R.color.colorPrimary;
        }

        if (!BuildConfig.DEBUG){
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName("FavoriteFragment")
                    .putContentType("Fragment"));
        }

        // create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context context = new ContextThemeWrapper(getActivity(), m_theme);

        super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);

        LayoutInflater lf = getActivity().getLayoutInflater();

        View view = lf.inflate(R.layout.fragment_favorites, container, false);
        ButterKnife.bind(this, view);

        setUpSwitches();

        menu = (FloatingActionButton) view.findViewById(R.id.menu);
        color_reveal = view.findViewById(R.id.color_reveal);
        color_reveal.setBackgroundColor(ContextCompat.getColor(context, color));
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    setUpSwitches();
                    if (!active) {
                        switchChanged = false;
                        active = true;
                        menu.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_close));
                        showView();
                    } else {
                        active = false;
                        menu.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_add_alert));
                        hideView();
                        if (switchChanged) {
                            refreshFavs();
                        }
                    }
                } else {
                    //TODO material dialog for preferences.
                    Toast.makeText(getContext(), "Work in progress!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        displayLaunches();
        mRecyclerView = (SuperRecyclerView) view.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(adapter);
        mSparseAnimator = new SparseItemRemoveAnimator();
        mRecyclerView.getRecyclerView().setItemAnimator(mSparseAnimator);
        mRecyclerView.addOnItemTouchListener(new Utils.RecyclerItemClickListener(context, new Utils.RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Launch launch;
                new Launch();
                launch = rocketLaunches.get(position);
                Intent exploreIntent = new Intent(context, LaunchDetailActivity.class);
                exploreIntent.putExtra("TYPE", "Launch");
                exploreIntent.putExtra("launch", launch);
                context.startActivity(exploreIntent);
            }
        }));
        mRecyclerView.setupSwipeToDismiss(new SwipeDismissRecyclerViewTouchListener.DismissCallbacks() {
            @Override
            public boolean canDismiss(int position) {
                return true;
            }

            @Override
            public void onDismiss(RecyclerView recyclerView, int[] reverseSortedPositions) {
                // Do your stuff like call an Api or update your db
                for (int position : reverseSortedPositions) {
                    mSparseAnimator.setSkipNext(true);
                    adapter.remove(position);
                }
            }
        });
        return view;
    }

    private void refreshFavs() {
        Intent update_upcoming_launches = new Intent(getActivity(), LaunchDataService.class);
        update_upcoming_launches.setAction(Strings.ACTION_GET_UP_LAUNCHES);
        getActivity().startService(update_upcoming_launches);
    }

    private void setUpSwitches() {
        nasaSwitch.setChecked(sharedPreference.getSwitchNasa());
        spacexSwitch.setChecked(sharedPreference.getSwitchSpaceX());
        roscosmosSwitch.setChecked(sharedPreference.getSwitchRoscosmos());
        ulaSwitch.setChecked(sharedPreference.getSwitchULA());
        arianespaceSwitch.setChecked(sharedPreference.getSwitchArianespace());
        cascSwitch.setChecked(sharedPreference.getSwitchCASC());
        isroSwitch.setChecked(sharedPreference.getSwitchISRO());
        customSwitch.setChecked(sharedPreference.getSwitchCustom());
    }

    //TODO respond to selections
    private void showAlertDialog() {
        new MaterialDialog.Builder(getContext())
                .title("Enter a Search String")
                .content("Automatically marks upcoming launches with matching launch vehicles or agencies as favorites.")
                .buttonRippleColorRes(R.color.colorAccentLight)
                .inputRange(3, 20)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("Enter Text: ", null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // Do something
                        sharedPreference.setCustomSearch(input.toString());
                        Timber.v("Setting custom search to: %s", input.toString());
                    }
                })
                .positiveText("Save")
                .negativeText("Close")
                .show();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void hideView() {

        // get the center for the clipping circle
        int x = (int) (menu.getX() + menu.getWidth() / 2);
        int y = (int) (menu.getY() + menu.getHeight() / 2);

        // get the initial radius for the clipping circle
        int initialRadius = Math.max(color_reveal.getWidth(), color_reveal.getHeight());

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(color_reveal, x, y, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                color_reveal.setVisibility(View.INVISIBLE);
            }
        });

        // start the animation
        anim.start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void showView() {

        // get the center for the clipping circle
        int x = (int) (menu.getX() + menu.getWidth() / 2);
        int y = (int) (menu.getY() + menu.getHeight() / 2);

        // get the final radius for the clipping circle
        int finalRadius = Math.max(color_reveal.getWidth(), color_reveal.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(color_reveal, x, y, 0, finalRadius);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

//                showAlertDialog();
            }
        });

        color_reveal.setVisibility(View.VISIBLE);
        anim.start();
    }

    public void displayLaunches() {
        if (rocketLaunches != null) {
            this.rocketLaunches.clear();
        }
        this.rocketLaunches = new ArrayList();
        this.rocketLaunches = this.sharedPreference.getFavoriteLaunches();
        if (rocketLaunches != null && rocketLaunches.size() > 0) {
            adapter.addItems(this.rocketLaunches);
        }
    }

    @Override
    public void onResume() {
        Timber.d("OnResume!");
        super.onResume();
    }

    static {
        Field f = null;
        try {
            f = Fragment.class.getDeclaredField("mChildFragmentManager");
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Timber.e("Error getting mChildFragmentManager field %s", e);
        }
        sChildFragmentManagerField = f;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.favorite_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_sort) {
            Toast.makeText(getContext(), "Work in progress!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_clear) {
            sharedPreference.resetSwitches();
            sharedPreference.removeFavLaunchAll();
            adapter.clear();
            adapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (sChildFragmentManagerField != null) {
            try {
                sChildFragmentManagerField.set(this, null);
            } catch (Exception e) {
                e.getLocalizedMessage();
                Timber.e("Error setting mChildFragmentManager field %s ", e);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.nasa_switch)
    public void nasa_switch() {
        switchChanged = true;
        sharedPreference.setSwitchNasa(!sharedPreference.getSwitchNasa());
    }

    @OnClick(R.id.spacex_switch)
    public void spacex_switch() {
        switchChanged = true;
        sharedPreference.setSwitchSpaceX(!sharedPreference.getSwitchSpaceX());
    }

    @OnClick(R.id.roscosmos_switch)
    public void roscosmos_switch() {
        switchChanged = true;
        sharedPreference.setSwitchRoscosmos(!sharedPreference.getSwitchRoscosmos());
    }

    @OnClick(R.id.ula_switch)
    public void ula_switch() {
        switchChanged = true;
        sharedPreference.setSwitchULA(!sharedPreference.getSwitchULA());
    }

    @OnClick(R.id.arianespace_switch)
    public void arianespace_switch() {
        switchChanged = true;
        sharedPreference.setSwitchArianespace(!sharedPreference.getSwitchArianespace());
    }

    @OnClick(R.id.casc_switch)
    public void casc_switch() {
        switchChanged = true;
        sharedPreference.setSwitchCASC(!sharedPreference.getSwitchCASC());
    }

    @OnClick(R.id.isro_switch)
    public void isro_switch() {
        switchChanged = true;
        sharedPreference.setSwitchISRO(!sharedPreference.getSwitchISRO());
    }

    @OnClick(R.id.custom_switch)
    public void custom_switch() {
        switchChanged = true;
        if (!sharedPreference.getSwitchCustom()) {
            showAlertDialog();
        }
        sharedPreference.setSwitchCustom(!sharedPreference.getSwitchCustom());
    }

    public void refreshViews() {
        adapter.clear();
        adapter.removeAll();
        displayLaunches();
        adapter.notifyDataSetChanged();
    }
}
