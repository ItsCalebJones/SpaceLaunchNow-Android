package me.calebjones.spacelaunchnow.spacestation.detail.fragments.detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import me.calebjones.spacelaunchnow.common.GlideApp;
import me.calebjones.spacelaunchnow.data.models.main.Agency;
import me.calebjones.spacelaunchnow.spacestation.R;

/**
 * This adapter takes data from ListPreferences/LoaderService and applies it to RecyclerView
 */
public class SpacestationOwnerAdapter extends RecyclerView.Adapter<SpacestationOwnerAdapter.ViewHolder> {

        public int position;
        private Context context;
        private List<Agency> agencies;

        public SpacestationOwnerAdapter(Context context) {
            this.context = context;
            agencies = new ArrayList<>();
        }

        public void addItems(List<Agency> items) {
            agencies = items;
            notifyDataSetChanged();
        }



        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spacestation_owner_item, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int i) {
            Agency item = agencies.get(i);
            holder.title.setText(item.getName());
            holder.subtitle.setText(String.format("%s - %s", item.getType(), item.getAbbrev()));
            GlideApp.with(context).load(item.getImageUrl()).circleCrop().placeholder(R.drawable.placeholder).into(holder.icon);
        }

        @Override
        public int getItemCount() {
            return agencies.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private CircleImageView icon;
            private TextView title;
            private TextView subtitle;

            //Add content to the card
            public ViewHolder(View view) {
                super(view);
                icon = view.findViewById(R.id.owner_icon);
                title = view.findViewById(R.id.owner_title);
                subtitle = view.findViewById(R.id.owner_subtitle);
            }
        }
    }

