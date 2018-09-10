package me.calebjones.spacelaunchnow.ui.orbiter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmList;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.data.models.main.Orbiter;
import me.calebjones.spacelaunchnow.utils.GlideApp;
import me.calebjones.spacelaunchnow.utils.Utils;

public class OrbiterDetailAdapter extends RecyclerView.Adapter<OrbiterDetailAdapter.ViewHolder> {

    public int position;
    private Context context;
    private Activity activity;
    private List<Orbiter> items;
    private RequestOptions requestOptions;
    private int backgroundColor = 0;

    public OrbiterDetailAdapter(Context context, Activity activity) {
        items = new ArrayList<>();
        requestOptions = new RequestOptions()
                .placeholder(R.drawable.placeholder)
                .centerCrop();
        this.context = context;
        this.activity = activity;
    }

    public void addItems(List<Orbiter> items) {
        if (this.items != null) {
            this.items.addAll(items);
        } else {
            this.items = new RealmList<>();
            this.items.addAll(items);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.orbiter_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int i) {
        Orbiter orbiter = items.get(holder.getAdapterPosition());


        //Set up vehicle card Information
        holder.orbiterTitle.setText(String.format("%s Spacecraft", orbiter.getName()));
        holder.orbiterSubtitle.setText(orbiter.getAgency());

        holder.orbiterName.setText(String.format(context.getString(R.string.spacecraft_details), orbiter.getName()));
        holder.orbiterDescription.setText(orbiter.getDetails());

        holder.orbiterHistory.setText(String.format(context.getString(R.string.spacecraft_history), orbiter.getName()));
        holder.orbiterHistoryDescription.setText(orbiter.getHistory());

        if (backgroundColor != 0) {
            holder.orbiterTitle.setBackgroundColor(backgroundColor);
            holder.orbiterSubtitle.setBackgroundColor(backgroundColor);
        }
        GlideApp.with(context)
                .load(orbiter.getImageURL())
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.orbiterImage.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.orbiterImage.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(holder.orbiterImage);


        if (orbiter.getWikiLink() != null && orbiter.getWikiLink().length() > 0) {
            holder.wikiButton.setOnClickListener(v -> Utils.openCustomTab(activity, context, orbiter.getWikiLink()));
        } else {
            holder.wikiButton.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateColor(int color) {

        backgroundColor = color;

        backgroundColor = color;

    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.orbiter_image)
        ImageView orbiterImage;
        @BindView(R.id.orbiter_title)
        TextView orbiterTitle;
        @BindView(R.id.orbiter_subtitle)
        TextView orbiterSubtitle;
        @BindView(R.id.orbiter_name)
        TextView orbiterName;
        @BindView(R.id.orbiter_description)
        TextView orbiterDescription;
        @BindView(R.id.orbiter_description_layout)
        LinearLayout orbiterDescriptionLayout;
        @BindView(R.id.orbiter_history)
        TextView orbiterHistory;
        @BindView(R.id.orbiter_history_description)
        TextView orbiterHistoryDescription;
        @BindView(R.id.wikiButton)
        AppCompatButton wikiButton;

        //Add content to the card
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.wikiButton)
        public void onViewClicked() {
        }
    }
}
