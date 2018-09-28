package me.calebjones.spacelaunchnow.content.data.articles;

import android.content.Context;

import java.util.List;

import io.realm.Realm;
import me.calebjones.spacelaunchnow.data.models.news.Article;
import me.calebjones.spacelaunchnow.content.data.articles.network.NewsAPIClient;
import me.calebjones.spacelaunchnow.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import timber.log.Timber;

public class ArticleRepository {

    private Context context;
    private Realm realm;
    private NewsAPIClient newsAPIClient;

    public ArticleRepository(Context context, Retrofit newsRetrofit) {
        this.context = context;
        this.realm = Realm.getDefaultInstance();
        newsAPIClient = new NewsAPIClient(newsRetrofit);
    }

    public void getArticles(boolean forceRefresh, final GetArticlesCallback callback) {
        newsAPIClient.getNews(30, new Callback<List<Article>>() {
            @Override
            public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
                if (response.raw().cacheResponse() != null) {
                    Timber.v("Response pulled from cache.");
                    if (!Utils.isNetworkAvailable(context)) {
                        callback.onFailure("Offline: Showing cached results.", true);
                    }
                }

                if (response.raw().networkResponse() != null) {
                    Timber.v("Response pulled from network.");
                }


                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());

                } else {
                    callback.onNetworkFailure();
                }
            }

            @Override
            public void onFailure(Call<List<Article>> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage(), false);
            }
        });
    }


    public interface GetArticlesCallback {
        void onSuccess(List<Article> articles);

        void onFailure(String error, boolean showContent);

        void onNetworkFailure();

    }
}
