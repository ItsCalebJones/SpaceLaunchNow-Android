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
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.nekocode.badge.BadgeDrawable;
import io.github.ponnamkarthik.richlinkpreview.MetaData;
import io.github.ponnamkarthik.richlinkpreview.ResponseListener;
import io.github.ponnamkarthik.richlinkpreview.RichPreview;
import io.realm.OrderedCollectionChangeSet;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.data.models.news.Article;
import me.calebjones.spacelaunchnow.utils.GlideApp;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private Context context;
    private RealmList<Article> articles;
    private int color;
    private SimpleDateFormat inDateFormat;
    private SimpleDateFormat outDateFormat;
    private Activity activity;

    public ArticleAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        color = ContextCompat.getColor(context, R.color.accent);
        inDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
        String format = DateFormat.getBestDateTimePattern(Locale.getDefault(), "h:mm a - MMMM d, yyyy");
        outDateFormat = new SimpleDateFormat(format, Locale.getDefault());
    }

    public void addItems(final RealmList<Article> articles) {
        this.articles = articles;
        notifyDataSetChanged();
    }

    public void updateItems(OrderedCollectionChangeSet changeSet){
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
        boolean useStock = false;
        if (article != null) {
            holder.titleText.setText(article.getTitle());
            if (article.getMediaUrl() != null) {
                if (article.getMediaUrl().equals("") || article.getMediaUrl().contains("cropped-fav3-32x32.png") || article.getMediaUrl().contains("favicon.ico")) {
                    useStock = true;
                } else {
                    GlideApp.with(context).load(article.getMediaUrl()).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(holder.imageView);
                }
            } else {
                if (!article.getLink().contains("spaceflight101") && !article.getLink().contains("nasaspaceflight") && !article.getLink().contains("spaceflightnow")) {
                    RichPreview richPreview = new RichPreview(new ResponseListener() {
                        @Override
                        public void onData(final MetaData metaData) {
                            Timber.v("Got metadata URL - %s", metaData.getImageurl());
                            //Implement your Layout
                            if (metaData.getImageurl() != null) {
                                Realm realm = Realm.getDefaultInstance();
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        article.setMediaUrl(metaData.getImageurl());
                                        realm.copyToRealmOrUpdate(article);
                                    }
                                });
                                realm.close();
                                try {
                                    GlideApp.with(context).load(article.getMediaUrl()).placeholder(R.drawable.placeholder).into(holder.imageView);
                                } catch (Exception e){
                                    Timber.e(e);
                                }
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            //handle error
                        }
                    });
                    richPreview.getPreview(article.getLink());
                    useStock = true;
                }

            }
            BadgeDrawable.Builder siteDrawable =
                    new BadgeDrawable.Builder()
                            .type(BadgeDrawable.TYPE_ONLY_ONE_TEXT)
                            .badgeColor(color)

                            .textSize(40)
                            .padding(16,16,16,16,16)
                            .strokeWidth(16);

            if (article.getLink().contains("spaceflightnow")){
                siteDrawable.text1("Space Flight Now");
                if (useStock) GlideApp.with(context)
                        .load(context.getString(R.string.spaceflightnow_logo))
                        .placeholder(R.drawable.placeholder).centerInside()
                        .into(holder.imageView);
            } else  if (article.getLink().contains("spaceflight101")){
                siteDrawable.text1("Space Flight 101");
                if (useStock) GlideApp.with(context)
                        .load(context.getString(R.string.spaceflight_101))
                        .placeholder(R.drawable.placeholder)
                        .into(holder.imageView);
            } else if (article.getLink().contains("spacenews")){
                siteDrawable.text1("Space News");
                if (useStock) GlideApp.with(context)
                        .load(context.getString(R.string.spacenews_logo))
                        .placeholder(R.drawable.placeholder).centerInside()
                        .into(holder.imageView);
            } else if (article.getLink().contains("nasaspaceflight")){
                siteDrawable.text1("NASA Spaceflight");
                if (useStock) GlideApp.with(context)
                        .load(context.getString(R.string.nasaspaceflight_logo))
                        .placeholder(R.drawable.placeholder).centerInside()
                        .into(holder.imageView);
            } else if (article.getLink().contains("nasa.gov")){
                siteDrawable.text1("NASA.Gov");
                if (useStock) GlideApp.with(context)
                        .load(context.getString(R.string.NASA_logo))
                        .placeholder(R.drawable.placeholder).centerInside()
                        .into(holder.imageView);
            } else if (article.getLink().contains("spacex.com")){
                siteDrawable.text1("SpaceX");
                if (useStock) GlideApp.with(context)
                        .load(context.getString(R.string.spacex_logo))
                        .placeholder(R.drawable.placeholder).centerInside()
                        .fitCenter()
                        .into(holder.imageView);
            } else {
                siteDrawable.text1("Unknown");
                if (useStock) GlideApp.with(context)
                        .load(R.drawable.placeholder)
                        .into(holder.imageView);
            }
            holder.siteText.setText(siteDrawable.build().toSpannable());

            try {
                Date pubDate = inDateFormat.parse(article.getPubDate());
                holder.publicationDate.setText(outDateFormat.format(pubDate));
            } catch (ParseException e) {
                e.printStackTrace();
                holder.publicationDate.setText(article.getPubDate());
            }
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.article_title)
        TextView titleText;
        @BindView(R.id.article_site)
        TextView siteText;
        @BindView(R.id.article_publication_date)
        TextView publicationDate;
        @BindView(R.id.article_image)
        ImageView imageView;
        @BindView(R.id.rootview)
        CoordinatorLayout rootView;

        @OnClick(R.id.rootview)
        void onClick() {
            Utils.openCustomTab(activity, context, articles.get(getAdapterPosition()).getLink());
        }

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
