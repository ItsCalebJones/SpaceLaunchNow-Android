package me.calebjones.spacelaunchnow.data.networking.news.api;

import io.realm.RealmList;
import me.calebjones.spacelaunchnow.data.models.main.news.NewsItem;
import me.calebjones.spacelaunchnow.data.models.main.news.NewsItemResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NewsInterface {

    String version = "api/v2";

    @GET(version + "/article/{id}")
    Call<NewsItem> getArticleById(@Path("id") String id);

    @GET(version + "/articles")
    Call<RealmList<NewsItem>> getArticles(@Query("_limit") int amount);

    @GET(version + "/articles/launch/{id}")
    Call<RealmList<NewsItem>> getArticlesByLaunch(@Path("id") String launchId, @Query("_limit") int amount);

}
