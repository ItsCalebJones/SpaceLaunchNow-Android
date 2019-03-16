package me.calebjones.spacelaunchnow.wear.ui.launch;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.wear.widget.drawer.WearableNavigationDrawerView;
import me.calebjones.spacelaunchnow.wear.model.LaunchCategories;
import timber.log.Timber;


public class NavigationAdapter extends WearableNavigationDrawerView.WearableNavigationDrawerAdapter implements WearableNavigationDrawerView.OnItemSelectedListener{

    private List<LaunchCategories> launchCategoryList = new ArrayList<>();
    private int mSelectedCategory = 1;
    private AdapterCallback mAdapterCallback;
    private Context context;

    public NavigationAdapter(AdapterCallback callback, Context context) {
        this.mAdapterCallback = callback;
        this.context = context;
        initializeCategories();
    }

    private void initializeCategories() {
        launchCategoryList = Arrays.asList(LaunchCategories.values());
    }

    @Override
    public String getItemText(int i) {
        return launchCategoryList.get(i).getName();
    }

    @Override
    public Drawable getItemDrawable(int i) {
        return ContextCompat.getDrawable(context, launchCategoryList.get(i).getIcon());
    }

    @Override
    public void onItemSelected(int i) {
        Timber.d("NavigationAdapter.onItemSelected(): %s", i);
        mSelectedCategory = i;

        LaunchCategories launchCategory = launchCategoryList.get(mSelectedCategory);
        mAdapterCallback.onMethodCallback(launchCategory.getCategory());

    }

    @Override
    public int getCount() {
        return launchCategoryList.size();
    }

    public int getSelectedCategory(){
        return mSelectedCategory;
    }

    public interface AdapterCallback {
        void onMethodCallback(int category);
    }
}
