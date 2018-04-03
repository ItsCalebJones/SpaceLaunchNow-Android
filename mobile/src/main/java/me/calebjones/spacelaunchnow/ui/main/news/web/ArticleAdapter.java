package me.calebjones.spacelaunchnow.ui.main.news.web;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.nekocode.badge.BadgeDrawable;
import io.github.ponnamkarthik.richlinkpreview.MetaData;
import io.github.ponnamkarthik.richlinkpreview.ResponseListener;
import io.github.ponnamkarthik.richlinkpreview.RichPreview;
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

    public ArticleAdapter(Context context) {
        this.context = context;
        color = ContextCompat.getColor(context, R.color.accent);
    }

    public void addItems(RealmResults<Article> articles) {
        if (this.articles != null) {
            this.articles.addAll(articles);
        } else {
            this.articles = new RealmList<>();
            this.articles.addAll(articles);
        }
        this.notifyDataSetChanged();
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
            GlideApp.with(context).load(R.drawable.placeholder).into(holder.imageView);
            if (article.getMediaUrl() == null) {
                try {
                    RichPreview richPreview = new RichPreview(new ResponseListener() {
                        @Override
                        public void onData(final MetaData metaData) {
                            //Implement your Layout
                            if (metaData.getImageurl() != null) {
                                Realm realm = Realm.getDefaultInstance();
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        article.setMediaUrl(metaData.getImageurl());
                                    }
                                });
                                realm.close();
                                GlideApp.with(context).load(metaData.getImageurl()).placeholder(R.drawable.placeholder).into(holder.imageView);
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            //handle error
                        }
                    });
                    richPreview.getPreview(article.getLink());
                } catch (Exception e) {
                    Timber.e(e);
                }
            } else {
                GlideApp.with(context).load(article.getMediaUrl()).placeholder(R.drawable.placeholder).into(holder.imageView);
            }
            final BadgeDrawable siteDrawable =
                    new BadgeDrawable.Builder()
                            .type(BadgeDrawable.TYPE_ONLY_ONE_TEXT)
                            .badgeColor(color)
                            .text1("Space.com")
                            .textSize(40)
                            .padding(16,16,16,16,16)
                            .strokeWidth(16)
                            .build();
            holder.siteText.setText(siteDrawable.toSpannable());
            holder.publicationDate.setText(article.getPubDate());
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
            Toast.makeText(context, String.format("Clicked %s", getAdapterPosition()), Toast.LENGTH_SHORT).show();
        }

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
