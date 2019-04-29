package me.calebjones.spacelaunchnow.news.ui.news;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import me.calebjones.spacelaunchnow.common.GlideApp;
import me.calebjones.spacelaunchnow.common.utils.Utils;
import me.calebjones.spacelaunchnow.data.models.main.news.NewsItem;
import me.calebjones.spacelaunchnow.news.R;


public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<NewsRecyclerViewAdapter.ViewHolder> {

    private List<NewsItem> newsList;
    private Context context;

    public NewsRecyclerViewAdapter(Context context) {
        newsList = new ArrayList<>();
        this.context = context;
    }

    public void addItems(List<NewsItem> events) {
        this.newsList = events;
        this.notifyDataSetChanged();
    }

    public void clear() {
        newsList = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        NewsItem news = newsList.get(position);
        holder.articleTitle.setText(news.getTitle());
        holder.articleSite.setText(news.getNewsSiteLong());
        Date published = new Date(news.getDatePublished() * 1000);
        holder.articlePublicationDate.setText(DateFormat.getDateInstance(DateFormat.LONG).format(((long) news.getDatePublished() * 1000)));
        if (news.getFeatured_image() != null) {
            GlideApp.with(context)
                    .load(news.getFeatured_image())
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(holder.articleImage);
        } else {
            String link = news.getNewsSite();
            if (link.contains("spaceflightnow")) {
                GlideApp.with(context)
                        .load(context.getResources().getString(R.string.spaceflightnow_logo))
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(holder.articleImage);
            } else if (link.contains("spaceflight101")) {
                GlideApp.with(context)
                        .load(context.getResources().getString(R.string.spaceflight_101))
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(holder.articleImage);
            } else if (link.contains("spacenews")) {
                GlideApp.with(context)
                        .load(context.getResources().getString(R.string.spacenews_logo))
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(holder.articleImage);
            } else if (link.contains("nasaspaceflight")) {
                GlideApp.with(context)
                        .load(context.getResources().getString(R.string.nasaspaceflight_logo))
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(holder.articleImage);
            } else if (link.contains("nasa.gov")) {
                GlideApp.with(context)
                        .load(context.getResources().getString(R.string.NASA_logo))
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(holder.articleImage);
            } else if (link.contains("spacex.com")) {
                GlideApp.with(context)
                        .load(context.getResources().getString(R.string.spacex_logo))
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(holder.articleImage);
            } else {
                GlideApp.with(context)
                        .load(R.drawable.placeholder)
                        .centerCrop()
                        .into(holder.articleImage);
            }
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView articleImage;
        private TextView articleSite;
        private TextView articleTitle;
        private TextView articlePublicationDate;
        private CoordinatorLayout rootView;


        public ViewHolder(View view) {
            super(view);
            articleImage = view.findViewById(R.id.article_image);
            articleSite = view.findViewById(R.id.article_site);
            articleTitle = view.findViewById(R.id.article_title);
            articlePublicationDate = view.findViewById(R.id.article_publication_date);
            rootView = view.findViewById(R.id.rootview);

            rootView.setOnClickListener(v -> {
                NewsItem news = newsList.get(getAdapterPosition());
                Utils.openCustomTab(context, news.getUrl());
            });
        }
    }
}
