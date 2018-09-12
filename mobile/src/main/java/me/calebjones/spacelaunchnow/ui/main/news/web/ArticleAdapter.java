package me.calebjones.spacelaunchnow.ui.main.news.web;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.nekocode.badge.BadgeDrawable;
import io.realm.OrderedCollectionChangeSet;
import io.realm.Realm;
import io.realm.RealmList;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.data.models.news.Article;
import me.calebjones.spacelaunchnow.utils.GlideApp;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private Context context;
    private List<Article> articles;
    private int color, altColor;
    private SimpleDateFormat inDateFormat;
    private SimpleDateFormat outDateFormat;
    private Activity activity;

    public ArticleAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        color = ContextCompat.getColor(context, R.color.accent);
        altColor = ContextCompat.getColor(context, R.color.material_color_blue_grey_500);
        String format = DateFormat.getBestDateTimePattern(Locale.getDefault(), "MMMM d, yyyy");
        outDateFormat = new SimpleDateFormat(format, Locale.getDefault());
    }

    public void addItems(final List<Article> articles) {
        this.articles = articles;
        notifyDataSetChanged();
    }

    public void updateItem(Article article, int position){
        Timber.v("Updating %s at position %s", article.getTitle(), position);
        if (articles.size() <= position){
            articles.add(position, article);
            notifyItemChanged(position);
        }
    }

    public void updateItems(OrderedCollectionChangeSet changeSet) {
        // `null`  means the async query returns the first time.
        if (changeSet == null) {
            notifyDataSetChanged();
            return;
        }
        // For deletions, the adapter has to be notified in reverse order.
        OrderedCollectionChangeSet.Range[] deletions = changeSet.getDeletionRanges();
        for (int i = deletions.length - 1; i >= 0; i--) {
            OrderedCollectionChangeSet.Range range = deletions[i];
            notifyItemRangeRemoved(range.startIndex, range.length);
        }

        OrderedCollectionChangeSet.Range[] insertions = changeSet.getInsertionRanges();
        for (OrderedCollectionChangeSet.Range range : insertions) {
            notifyItemRangeInserted(range.startIndex, range.length);
        }

        OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
        for (OrderedCollectionChangeSet.Range range : modifications) {
            notifyItemRangeChanged(range.startIndex, range.length);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.article_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Article article = articles.get(position);
        if (article != null) {
            holder.titleText.setText(article.getTitle());

            if (article.getFeaturedImage() != null) {
                GlideApp.with(context)
                        .load(article.getFeaturedImage())
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(holder.imageView);
            } else {
                tryDefault(article.getUrl(), holder.imageView);
            }

            BadgeDrawable.Builder siteDrawable =
                    new BadgeDrawable.Builder()
                            .type(BadgeDrawable.TYPE_ONLY_ONE_TEXT)
                            .badgeColor(color)
                            .textSize(40)
                            .padding(8, 8, 8, 8, 8)
                            .strokeWidth(16);

            BadgeDrawable.Builder tagDrawable =
                    new BadgeDrawable.Builder()
                            .type(BadgeDrawable.TYPE_ONLY_ONE_TEXT)
                            .badgeColor(altColor)
                            .textSize(32)
                            .padding(8, 8, 8, 8, 8)
                            .strokeWidth(16);

            BadgeDrawable.Builder altTagDrawable =
                    new BadgeDrawable.Builder()
                            .type(BadgeDrawable.TYPE_ONLY_ONE_TEXT)
                            .badgeColor(altColor)
                            .textSize(32)
                            .padding(8, 8, 8, 8, 8)
                            .strokeWidth(16);



            siteDrawable.text1(article.getNewsSite());

            if (article.getTags() != null && article.getTags().size() > 0) {
                holder.tagText.setVisibility(View.VISIBLE);
                String upperString = article.getTags().get(0).substring(0, 1).toUpperCase()
                        + article.getTags().get(0).substring(1);
                tagDrawable.text1(upperString);
                holder.tagText.setText(tagDrawable.build().toSpannable());
            } else {
                holder.tagText.setVisibility(View.GONE);
            }

            if (article.getTags() != null && article.getTags().size() > 1) {
                holder.tagTextAlt.setVisibility(View.VISIBLE);
                String upperString = article.getTags().get(1).substring(0, 1).toUpperCase()
                        + article.getTags().get(1).substring(1);
                altTagDrawable.text1(upperString);
                holder.tagTextAlt.setText(altTagDrawable.build().toSpannable());
            } else {
                holder.tagTextAlt.setVisibility(View.GONE);
            }

            holder.siteText.setText(siteDrawable.build().toSpannable());

            holder.publicationDate.setText(outDateFormat.format(article.getDatePublished()));
        }
    }

    @Override
    public int getItemCount() {
        if (articles != null) {
            return articles.size();
        } else {
            return 0;
        }
    }

    private void tryDefault(String link, ImageView imageView) {
        if (link.contains("spaceflightnow")){
            GlideApp.with(context)
                    .load(context.getString(R.string.spaceflightnow_logo))
                    .centerInside()
                    .placeholder(R.drawable.placeholder).centerInside()
                    .into(imageView);
        } else if (link.contains("spaceflight101")){
            GlideApp.with(context)
                    .load(context.getString(R.string.spaceflight_101))
                    .centerInside()
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);
        } else if (link.contains("spacenews")){
            GlideApp.with(context)
                    .load(context.getString(R.string.spacenews_logo))
                    .centerInside()
                    .placeholder(R.drawable.placeholder).centerInside()
                    .into(imageView);
        } else if (link.contains("nasaspaceflight")){
            GlideApp.with(context)
                    .load(context.getString(R.string.nasaspaceflight_logo))
                    .centerInside()
                    .placeholder(R.drawable.placeholder).centerInside()
                    .into(imageView);
        } else if (link.contains("nasa.gov")){
            GlideApp.with(context)
                    .load(context.getString(R.string.NASA_logo))
                    .centerInside()
                    .placeholder(R.drawable.placeholder).centerInside()
                    .into(imageView);
        } else if (link.contains("spacex.com")){
            GlideApp.with(context)
                    .load(context.getString(R.string.spacex_logo))
                    .centerInside()
                    .placeholder(R.drawable.placeholder).centerInside()
                    .fitCenter()
                    .into(imageView);
        } else {
            GlideApp.with(context)
                    .load(R.drawable.placeholder)
                    .centerCrop()
                    .into(imageView);
        }
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.article_title)
        TextView titleText;
        @BindView(R.id.article_site)
        TextView siteText;
        @BindView(R.id.article_tag)
        TextView tagText;
        @BindView(R.id.article_tag_alt)
        TextView tagTextAlt;
        @BindView(R.id.article_publication_date)
        TextView publicationDate;
        @BindView(R.id.article_image)
        ImageView imageView;
        @BindView(R.id.rootview)
        CoordinatorLayout rootView;

        @OnClick(R.id.rootview)
        void onClick() {
            Utils.openCustomTab(activity, context, articles.get(getAdapterPosition()).getUrl());
        }

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
