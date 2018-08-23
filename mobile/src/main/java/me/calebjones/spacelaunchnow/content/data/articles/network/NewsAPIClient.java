package me.calebjones.spacelaunchnow.content.data.articles.network;

import java.util.concurrent.TimeUnit;

import me.calebjones.spacelaunchnow.data.models.news.NewsFeedResponse;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class NewsAPIClient {

    private final NewsService newsService;
    private Retrofit newsRetrofit;

    public NewsAPIClient(){
        OkHttpClient.Builder client = new OkHttpClient().newBuilder();
        client.connectTimeout(15, TimeUnit.SECONDS);
        client.readTimeout(15, TimeUnit.SECONDS);
        client.writeTimeout(15, TimeUnit.SECONDS);

        newsRetrofit = new Retrofit.Builder()
                .baseUrl("https://feed.rssunify.com")
                .client(client.build())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        newsService = newsRetrofit.create(NewsService.class);
    }

    public Call<NewsFeedResponse> getNews(Callback<NewsFeedResponse> callback) {
        Call<NewsFeedResponse> call;
        call = newsService.getNews();
        call.enqueue(callback);
        return call;
    }

}
