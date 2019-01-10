package me.spacelaunchnow.astronauts.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.calebjones.spacelaunchnow.common.GlideApp;
import me.calebjones.spacelaunchnow.data.models.main.astronaut.Astronaut;
import me.spacelaunchnow.astronauts.R;
import me.spacelaunchnow.astronauts.R2;
import me.spacelaunchnow.astronauts.list.AstronautListFragment.OnListFragmentInteractionListener;


public class AstronautRecyclerViewAdapter extends RecyclerView.Adapter<AstronautRecyclerViewAdapter.ViewHolder> {


    private List<Astronaut> astronauts;
    private final OnListFragmentInteractionListener mListener;
    private Context context;

    public AstronautRecyclerViewAdapter(OnListFragmentInteractionListener listener, Context context) {
        astronauts = new ArrayList<>();
        mListener = listener;
        this.context = context;
    }

    public void addItems(List<Astronaut> astronauts) {
        this.astronauts = astronauts;
        this.notifyDataSetChanged();
    }

    public void clear() {
        astronauts = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.astronaut_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = astronauts.get(position);
        holder.astronautName.setText(holder.mItem.getName());
        holder.astronautStatus.setText(holder.mItem.getStatus().getName());
        String abbrev = "";
        if (holder.mItem.getAgency() != null && holder.mItem.getAgency().getAbbrev() != null){
            abbrev = holder.mItem.getAgency().getAbbrev();
        }
        holder.astronautNationality.setText(String.format("%s %s",
                holder.mItem.getNationality(), abbrev));

        GlideApp.with(context)
                .load(holder.mItem.getProfileImageThumbnail())
                .circleCrop()
                .into(holder.astronautImage);

        holder.rootview.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onAstronautClicked(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return astronauts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.astronaut_image)
        ImageView astronautImage;
        @BindView(R2.id.astronaut_name)
        TextView astronautName;
        @BindView(R2.id.astronaut_nationality)
        TextView astronautNationality;
        @BindView(R2.id.astronaut_status)
        TextView astronautStatus;
        @BindView(R2.id.rootview)
        ConstraintLayout rootview;
        public Astronaut mItem;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
