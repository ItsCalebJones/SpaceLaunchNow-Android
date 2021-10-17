package me.calebjones.spacelaunchnow.data.networking.news.data;


import android.content.Context;

import io.realm.RealmList;
import me.calebjones.spacelaunchnow.data.models.main.news.NewsItem;
import me.calebjones.spacelaunchnow.data.models.main.news.NewsItemResponse;
import me.calebjones.spacelaunchnow.data.networking.news.api.NewsDataClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class NewsDataLoader {

    private Context context;
    private NewsDataClient newsDataClient;

    public NewsDataLoader(Context context) {
        this.context = context;
        this.newsDataClient = new NewsDataClient();
    }

    public void getNewsList(int limit, final Callbacks.NewsListNetworkCallback networkCallback) {
        Timber.i("Running get news list");

        newsDataClient.getArticles(limit, new Callback<RealmList<NewsItem>>() {
            @Override
            public void onResponse(Call<RealmList<NewsItem>> call, Response<RealmList<NewsItem>> response) {
                if (response.isSuccessful()) {
                    RealmList<NewsItem> responseBody = response.body();
                    networkCallback.onSuccess(responseBody);
                } else {
                    networkCallback.onNetworkFailure(response.code());
                }
            }

            @Override
            public void onFailure(Call<RealmList<NewsItem>> call, Throwable t) {
                networkCallback.onFailure(t);
            }
        });
    }

    public void getNewsById(String id, final Callbacks.NewsNetworkCallback networkCallback) {
        Timber.i("Running get news by id");
        newsDataClient.getNewsById(id, new Callback<NewsItem>() {
            @Override
            public void onResponse(Call<NewsItem> call, Response<NewsItem> response) {
                if (response.isSuccessful()) {
                    NewsItem event = response.body();
                    networkCallback.onSuccess(event);

                } else {
                    networkCallback.onNetworkFailure(response.code());

                }
            }

            @Override
            public void onFailure(Call<NewsItem> call, Throwable t) {
                networkCallback.onFailure(t);
            }
        });
    }


    public void getNewsByLaunch(int limit, String launchId, final Callbacks.NewsListNetworkCallback networkCallback) {
        Timber.i("Running get news by launch");
        newsDataClient.getArticlesByLaunch(limit, launchId, new Callback<RealmList<NewsItem>>() {
            @Override
            public void onResponse(Call<RealmList<NewsItem>> call, Response<RealmList<NewsItem>> response) {
                if (response.isSuccessful()) {
                    RealmList<NewsItem> news = response.body();
                    networkCallback.onSuccess(news);

                } else {
                    networkCallback.onNetworkFailure(response.code());

                }
            }

            @Override
            public void onFailure(Call<RealmList<NewsItem>> call, Throwable t) {
                networkCallback.onFailure(t);
            }
        });
    }

}
