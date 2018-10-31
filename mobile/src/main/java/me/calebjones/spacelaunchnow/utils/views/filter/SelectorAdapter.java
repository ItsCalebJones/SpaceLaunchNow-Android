package me.calebjones.spacelaunchnow.utils.views.filter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pixplicity.easyprefs.library.Prefs;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;
import me.calebjones.spacelaunchnow.R;
import timber.log.Timber;

public class SelectorAdapter extends RecyclerView.Adapter<SelectorAdapter.ViewHolder> {

    private List<FilterItem> filterList;

    public SelectorAdapter(Context context) {
        filterList = new RealmList<>();
    }

    public void addItems(List<FilterItem> launchList) {

        if (this.filterList != null) {
            this.filterList.addAll(launchList);
        } else {
            this.filterList = new RealmList<>();
            this.filterList.addAll(launchList);
        }
        this.notifyDataSetChanged();
    }

    public void clear() {
        filterList.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Timber.v("onCreate ViewHolder.");
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.filter_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int i) {
        FilterItem item = filterList.get(i);

        holder.checkbox.setChecked(Prefs.getBoolean(item.getPreference_key(), true));
        holder.name.setText(item.getName());

        holder.rootView.setOnClickListener(v -> {
            holder.checkbox.performClick();
        });
        holder.checkbox.setOnClickListener(v -> {
            Prefs.putBoolean(item.getPreference_key(), holder.checkbox.isChecked());
        });
    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.checkbox)
        AppCompatCheckBox checkbox;
        @BindView(R.id.root_view)
        View rootView;

        //Add content to the card
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
