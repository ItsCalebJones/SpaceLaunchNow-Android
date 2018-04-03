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

    public void getArticles(boolean forceRefresh, final GetArticlesCallback callback) {
        Date currentDate = new Date();
        currentDate.setTime(currentDate.getTime() - 1000 * 60 * 60);
        final RealmResults<NewsFeedResponse> oldResponses = realm.where(NewsFeedResponse.class).lessThan("lastUpdate", currentDate.getTime()).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                oldResponses.deleteAllFromRealm();
            }
        });
        final RealmResults<Article> articles = realm.where(Article.class).greaterThanOrEqualTo("channel.newsFeedResponse.lastUpdate", currentDate.getTime()).findAll();
        final RealmResults<Article> articlesW = realm.where(Article.class).isNotNull("media").findAll();
        newsAPIClient.getNews(new Callback<NewsFeedResponse>() {
            @Override
            public void onResponse(Call<NewsFeedResponse> call, Response<NewsFeedResponse> response) {
                Timber.v("Hello");
                if (response.isSuccessful()) {
                    final NewsFeedResponse newsResponse = response.body();
                    if (newsResponse != null) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                newsResponse.setLastUpdate(new Date().getTime());
                                realm.copyToRealmOrUpdate(newsResponse);
                            }
                        });
                        callback.onSuccess(articles);
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
        if (articles.size() == 0 || forceRefresh) {
            newsAPIClient.getNews(new Callback<NewsFeedResponse>() {
                @Override
                public void onResponse(Call<NewsFeedResponse> call, Response<NewsFeedResponse> response) {
                    Timber.v("Hello");
                    if (response.isSuccessful()) {
                        final NewsFeedResponse newsResponse = response.body();
                        if (newsResponse != null) {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    newsResponse.setLastUpdate(new Date().getTime());
                                    realm.copyToRealmOrUpdate(newsResponse);
                                }
                            });
                            callback.onSuccess(articles);
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
        void onSuccess(RealmResults<Article> articles);

        void onFailure(Throwable throwable);

        void onNetworkFailure();

    }
}
