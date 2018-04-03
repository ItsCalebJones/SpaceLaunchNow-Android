package me.calebjones.spacelaunchnow.ui.main.news.web;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmList;
import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.models.Article;
import me.calebjones.spacelaunchnow.utils.GlideApp;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private Context context;
    private RealmList<Article> articles;

    public ArticleAdapter(Context context) {
        this.context = context;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = articles.get(position);
        if (article != null) {
            holder.textView.setText(article.getTitle());
            if (article.getMediaUrl() != null){
                GlideApp.with(context).load(article.getMediaUrl()).centerCrop().into(holder.imageView);
            } else {
                holder.imageView.setImageResource(R.drawable.placeholder);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (articles != null){
            return articles.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.article_title)
        TextView textView;
        @BindView(R.id.article_image)
        ImageView imageView;
        @BindView(R.id.rootview)
        CoordinatorLayout rootView;

        @OnClick(R.id.rootview) void onClick() {
            Toast.makeText(context, String.format("Clicked %s", getAdapterPosition()), Toast.LENGTH_SHORT).show();
        }

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
