package me.calebjones.spacelaunchnow.common.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;
import me.calebjones.spacelaunchnow.common.GlideApp;
import me.calebjones.spacelaunchnow.common.R;
import me.calebjones.spacelaunchnow.common.R2;
import me.calebjones.spacelaunchnow.common.utils.Utils;
import me.calebjones.spacelaunchnow.data.models.main.news.NewsItem;
import timber.log.Timber;

/**
 * This adapter takes data from ListPreferences/LoaderService and applies it to RecyclerView
 */
public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.ViewHolder> {
    public int position;
    private RealmList<NewsItem> newsList;
    private Context mContext;

    public NewsListAdapter(Context context) {
        newsList = new RealmList<>();
        mContext = context;

    }

    public void addItems(List<NewsItem> newList) {

        if (this.newsList != null) {
            this.newsList.addAll(newList);
        } else {
            this.newsList = new RealmList<>();
            this.newsList.addAll(newList);
        }
        this.notifyDataSetChanged();
    }

    public void clear() {
        newsList.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Timber.v("onCreate ViewHolder.");
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int i) {
        final NewsItem item = newsList.get(i);
        if (item != null) {
            GlideApp.with(mContext)
                    .load(item.getFeaturedImage())
                    .placeholder(R.drawable.placeholder)
                    .thumbnail(0.5f)
                    .circleCrop()
                    .into(holder.newsIcon);
        }
        holder.title.setText(item.getTitle());
        holder.subtitle.setText(item.getNewsSite());
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R2.id.news_subtitle)
        TextView subtitle;
        @BindView(R2.id.news_title)
        TextView title;
        @BindView(R2.id.news_icon)
        ImageView newsIcon;
        @BindView(R2.id.rootView)
        View rootView;


        //Add content to the card
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            rootView.setOnClickListener(this);
        }

        //React to click events.
        @Override
        public void onClick(View v) {
            final NewsItem item = newsList.get(getAdapterPosition());
            if (item != null) {
                Utils.openCustomTab(mContext, item.getUrl());
            }
        }
    }

}

