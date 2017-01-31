package me.calebjones.spacelaunchnow.wear.launch;

import android.graphics.drawable.Drawable;
import android.support.wearable.view.drawer.WearableNavigationDrawer;

import java.util.ArrayList;

import me.calebjones.spacelaunchnow.wear.model.LaunchCategory;
import timber.log.Timber;


public class NavigationAdapter extends WearableNavigationDrawer.WearableNavigationDrawerAdapter {

    private ArrayList<LaunchCategory> launchCategoryList = new ArrayList<>();
    private int mSelectedCategory = 1;
    private AdapterCallback mAdapterCallback;

    public NavigationAdapter(AdapterCallback callback) {
        this.mAdapterCallback = callback;
        initializeCategories();
    }

    private void initializeCategories() {
        launchCategoryList.add(new LaunchCategory("ALL", 1));
        launchCategoryList.add(new LaunchCategory("SpaceX", 2));
        launchCategoryList.add(new LaunchCategory("ROSCOSMOS", 3));
        launchCategoryList.add(new LaunchCategory("ULA", 4));
    }

    @Override
    public String getItemText(int i) {
        return launchCategoryList.get(i).getName();
    }

    @Override
    public Drawable getItemDrawable(int i) {
        return null;
    }

    @Override
    public void onItemSelected(int i) {
        Timber.d("NavigationAdapter.onItemSelected(): %s", i);
        mSelectedCategory = i;

        LaunchCategory launchCategory = launchCategoryList.get(mSelectedCategory);
        mAdapterCallback.onMethodCallback(launchCategory.getCategory());

    }

    @Override
    public int getCount() {
        return launchCategoryList.size();
    }

    public int getSelectedPlanet(){
        return mSelectedCategory;
    }

    public interface AdapterCallback {
        void onMethodCallback(int category);
    }
}
