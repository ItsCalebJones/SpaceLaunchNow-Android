package me.calebjones.spacelaunchnow.wear.launch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.data.models.realm.LaunchWear;
import me.calebjones.spacelaunchnow.wear.R;
import me.calebjones.spacelaunchnow.wear.content.ContentManager;
import timber.log.Timber;

public class LaunchAdapter extends RecyclerView.Adapter<LaunchViewHolder> implements  ContentManager.ContentCallback{

    private int category;
    private RealmResults<LaunchWear> launchList;
    private ContentManager contentManager;



    public LaunchAdapter(Context context, int category) {
        this.category = category;
        contentManager = new ContentManager(context, this, category);
        loadData();
    }

    private void loadData() {
        launchList = contentManager.getLaunchList(this.category);
        notifyDataSetChanged();
    }

    @Override
    public LaunchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.launch_card, parent, false);
        return new LaunchViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(LaunchViewHolder holder, int position) {
        LaunchWear launch = launchList.get(position);
        holder.launchName.setText(launch.getName());
    }

    @Override
    public int getItemCount() {
        return launchList.size();
    }

    public void setCategory(int category) {
        this.category = category;
        loadData();
    }

    public void onClick(int position){
        Timber.v("Launch: %s Position: %s",launchList.get(position).getName(), position);
    }

    public void cleanup(){
        contentManager.cleanup();
    }

    @Override
    public void dataLoaded() {
        loadData();
    }
}
