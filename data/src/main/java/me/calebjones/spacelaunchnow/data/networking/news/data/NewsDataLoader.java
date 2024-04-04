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

        newsDataClient.getArticles(limit, new Callback<NewsItemResponse>() {
            @Override
            public void onResponse(Call<NewsItemResponse> call, Response<NewsItemResponse> response) {
                if (response.isSuccessful()) {
                    RealmList<NewsItem> news = new RealmList<>();
                    if (response.body() != null) {
                        news.addAll(response.body().getNewsItems());
                    }
                    networkCallback.onSuccess(news);
                } else {
                    networkCallback.onNetworkFailure(response.code());
                }
            }

            @Override
            public void onFailure(Call<NewsItemResponse> call, Throwable t) {
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
        newsDataClient.getArticlesByLaunch(limit, launchId, new Callback<NewsItemResponse>() {
            @Override
            public void onResponse(Call<NewsItemResponse> call, Response<NewsItemResponse> response) {
                if (response.isSuccessful()) {
                    RealmList<NewsItem> news = new RealmList<>();
                    if (response.body() != null) {
                        news.addAll(response.body().getNewsItems());
                    }

                    networkCallback.onSuccess(news);

                } else {
                    networkCallback.onNetworkFailure(response.code());

                }
            }

            @Override
            public void onFailure(Call<NewsItemResponse> call, Throwable t) {
                networkCallback.onFailure(t);
            }
        });
    }

}
