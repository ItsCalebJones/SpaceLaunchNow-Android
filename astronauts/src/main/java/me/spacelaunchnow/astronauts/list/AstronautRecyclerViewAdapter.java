package me.spacelaunchnow.astronauts.list;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.realm.RealmList;
import me.calebjones.spacelaunchnow.data.models.main.astronaut.Astronaut;
import me.spacelaunchnow.astronauts.R;
import me.spacelaunchnow.astronauts.list.AstronautListFragment.OnListFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;


public class AstronautRecyclerViewAdapter extends RecyclerView.Adapter<AstronautRecyclerViewAdapter.ViewHolder> {

    private List<Astronaut> astronauts;
    private final OnListFragmentInteractionListener mListener;

    public AstronautRecyclerViewAdapter(OnListFragmentInteractionListener listener) {
        astronauts = new ArrayList<>();
        mListener = listener;
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
                .inflate(R.layout.fragment_astronaut, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = astronauts.get(position);
        holder.mIdView.setText(astronauts.get(position).getId().toString());
        holder.mContentView.setText(astronauts.get(position).getName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onAstronautClicked(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return astronauts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Astronaut mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
