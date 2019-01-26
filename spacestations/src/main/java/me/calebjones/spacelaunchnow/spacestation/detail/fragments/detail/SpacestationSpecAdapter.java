package me.calebjones.spacelaunchnow.spacestation.detail.fragments.detail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import me.calebjones.spacelaunchnow.spacestation.R;

/**
 * This adapter takes data from ListPreferences/LoaderService and applies it to RecyclerView
 */
public class SpacestationSpecAdapter extends RecyclerView.Adapter<SpacestationSpecAdapter.ViewHolder> {

        public int position;
        private List<SpacestationSpecItem> specs;

        public SpacestationSpecAdapter() {
            specs = new ArrayList<>();
        }

        public void addItems(List<SpacestationSpecItem> items) {
            specs = items;
            notifyDataSetChanged();
        }



        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spacestation_spec_item, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int i) {
            SpacestationSpecItem item = specs.get(i);
            holder.title.setText(item.getTitle());
            holder.value.setText(item.getValue());
            holder.icon.setImageDrawable(item.getDrawable());
        }

        @Override
        public int getItemCount() {
            return specs.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private ImageView icon;
            private TextView title;
            private TextView value;

            //Add content to the card
            public ViewHolder(View view) {
                super(view);
                icon = view.findViewById(R.id.spec_icon);
                title = view.findViewById(R.id.spec_title);
                value = view.findViewById(R.id.spec_value);
            }
        }
    }

