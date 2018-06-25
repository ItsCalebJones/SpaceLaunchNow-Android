package me.calebjones.spacelaunchnow.content.data.articles;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.data.models.news.Article;
import me.calebjones.spacelaunchnow.data.models.news.NewsFeedResponse;
import me.calebjones.spacelaunchnow.content.data.articles.network.NewsAPIClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ArticleRepository {

    private Context context;
    private Realm realm;
    private NewsAPIClient newsAPIClient;

    public ArticleRepository(Context context) {
        this.context = context;
        this.realm = Realm.getDefaultInstance();
        newsAPIClient = new NewsAPIClient();
    }

    public void getArticles(boolean forceRefresh, final GetArticlesCallback callback) {
        Date currentDate = new Date();
        currentDate.setTime(currentDate.getTime() - 1000 * 60 * 60);
        RealmList<Article> articles = new RealmList();
        articles.addAll(realm.where(Article.class).sort("date", Sort.DESCENDING).findAll());

        if (articles.size() == 0 || forceRefresh) {
            newsAPIClient.getNews(new Callback<NewsFeedResponse>() {
                @Override
                public void onResponse(Call<NewsFeedResponse> call, Response<NewsFeedResponse> response) {
                    Timber.v("Hello");
                    if (response.isSuccessful()) {
                        final NewsFeedResponse newsResponse = response.body();
                        if (newsResponse != null) {
                            realm.executeTransaction(realm -> {
                                RealmList<Article> finalArticles = new RealmList();
                                SimpleDateFormat inDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
                                SimpleDateFormat altDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm Z", Locale.US);
                                for (Article article: newsResponse.getChannel().getArticles()){
                                    try {
                                        Date pubDate = inDateFormat.parse(article.getPubDate());
                                        article.setDate(pubDate);
                                        finalArticles.add(article);
                                    } catch (ParseException e) {
                                        try {
                                            Date pubDate = altDateFormat.parse(article.getPubDate());
                                            article.setDate(pubDate);
                                            finalArticles.add(article);
                                        } catch (ParseException f){
                                            Timber.e(f, "Unable to parse %s", article.getTitle());
                                        }
                                    }
                                }
                                realm.copyToRealmOrUpdate(finalArticles);
                                callback.onSuccess(finalArticles);
                            });

                        }
                    } else {
                        callback.onNetworkFailure();
                    }
                }

                @Override
                public void onFailure(Call<NewsFeedResponse> call, Throwable t) {
                    callback.onFailure(t);
                }
            });
        } else {
            callback.onSuccess(articles);
        }
    }



    public interface GetArticlesCallback {
        void onSuccess(RealmList<Article> articles);

        void onFailure(Throwable throwable);

        void onNetworkFailure();

    }
}
