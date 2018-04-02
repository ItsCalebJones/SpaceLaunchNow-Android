package me.calebjones.spacelaunchnow.content.repository;

import android.content.Context;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObjectSchema;
import io.realm.RealmResults;
import io.realm.RealmSchema;
import io.realm.Sort;
import io.realm.annotations.RealmField;
import me.calebjones.spacelaunchnow.content.models.Article;
import me.calebjones.spacelaunchnow.content.models.NewsFeedResponse;
import me.calebjones.spacelaunchnow.content.network.NewsAPIClient;
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

    public RealmResults<Article> getArticles() {
        Date currentDate = new Date();
        currentDate.setTime(currentDate.getTime() - 1000 * 60 * 60);
        final RealmResults<Article> articles = realm.where(Article.class).greaterThanOrEqualTo("channel.newsFeedResponse.lastUpdate", currentDate.getTime()).findAll();
//        final RealmResults<Article> articles = realm.where(Article.class).findAll();
        newsAPIClient.getNews(new Callback<NewsFeedResponse>() {
            @Override
            public void onResponse(Call<NewsFeedResponse> call, Response<NewsFeedResponse> response) {
                Timber.v("Hello");
                final NewsFeedResponse newsResponse = response.body();
                if (newsResponse != null) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            newsResponse.setLastUpdate(new Date().getTime());
                            realm.copyToRealmOrUpdate(newsResponse);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<NewsFeedResponse> call, Throwable t) {
                Timber.v("Goodbye... %s", t.getLocalizedMessage());
            }
        });
        if (articles.size() == 0) {
            newsAPIClient.getNews(new Callback<NewsFeedResponse>() {
                @Override
                public void onResponse(Call<NewsFeedResponse> call, Response<NewsFeedResponse> response) {
                    Timber.v("Hello");
                    final NewsFeedResponse newsResponse = response.body();
                    if (newsResponse != null) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                newsResponse.setLastUpdate(new Date().getTime());
                                realm.copyToRealmOrUpdate(newsResponse);
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<NewsFeedResponse> call, Throwable t) {
                    Timber.v("Goodbye... %s", t.getLocalizedMessage());
                }
            });
        }
        return articles;
    }
}
