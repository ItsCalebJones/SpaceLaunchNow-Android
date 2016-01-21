package me.calebjones.spacelaunchnow.ui.fragment.favorite;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
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

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.adapter.FavoriteAdapter;
import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.Launch;
import timber.log.Timber;


public class FavoriteFragment extends Fragment {

    private List<Launch> rocketLaunches;
    private SharedPreference sharedPreference;
    private static final Field sChildFragmentManagerField;
    private FloatingActionButton menu;
    private View color_reveal;
    private SuperRecyclerView mRecyclerView;
    private FavoriteAdapter adapter;
    private SparseItemRemoveAnimator mSparseAnimator;
    private RecyclerView.LayoutManager mLayoutManager;
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
        int color;
        Context context1 = getContext();

        sharedPreference = SharedPreference.getInstance(context1);

        if (sharedPreference.getNightMode()) {
            m_theme = R.style.DarkTheme_NoActionBar;
            color = R.color.darkPrimary;
        } else {
            m_theme = R.style.LightTheme_NoActionBar;
            color = R.color.colorPrimary;
        }

        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("FavoriteFragment")
                .putContentType("Fragment"));

        // create ContextThemeWrapper from the original Activity Context with the custom theme
        Context context = new ContextThemeWrapper(getActivity(), m_theme);

        super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);

        LayoutInflater lf = getActivity().getLayoutInflater();

        View view = lf.inflate(R.layout.fragment_favorites, container, false);
        menu = (FloatingActionButton) view.findViewById(R.id.menu);
        color_reveal = view.findViewById(R.id.color_reveal);
        color_reveal.setBackgroundColor(ContextCompat.getColor(context,color));
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        displayLaunches();
        mRecyclerView = (SuperRecyclerView) view.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(adapter);
        mSparseAnimator = new SparseItemRemoveAnimator();
        mRecyclerView.getRecyclerView().setItemAnimator(mSparseAnimator);
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
            }});
        return view;
    }

    //TODO respond to selections
    private void showAlertDialog() {
        new MaterialDialog.Builder(getContext())
                .title("Select an Agency")
                .content("Automatically marks upcoming launches as favorites.")
                .items(R.array.agencies)
                .buttonRippleColorRes(R.color.colorAccentLight)
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        /**
                         * If you use alwaysCallMultiChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected check box to actually be selected.
                         * See the limited multi choice dialog example in the sample project for details.
                         **/
                        return true;
                    }
                })
                .positiveText("Filter")
                .negativeText("Close")
                .icon(ContextCompat.getDrawable(getContext(), R.mipmap.ic_launcher))
                .showListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            showView();
                        }
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            hideView();
                        }
                    }
                })
                .show();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void hideView() {

        // get the center for the clipping circle
        int x = (int) (menu.getX() + menu.getWidth()  / 2);
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
        int x = (int) (menu.getX() + menu.getWidth()  / 2);
        int y = (int) (menu.getY() + menu.getHeight() / 2);

        // get the final radius for the clipping circle
        int finalRadius = Math.max(color_reveal.getWidth(), color_reveal.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(color_reveal, x, y, 0, finalRadius);

        // make the view visible and start the animation
        color_reveal.setVisibility(View.VISIBLE);
        anim.start();
    }

    public void displayLaunches() {
        this.rocketLaunches.clear();
        this.rocketLaunches = this.sharedPreference.getFavoriteLaunches();
        adapter.addItems(this.rocketLaunches);
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

}
