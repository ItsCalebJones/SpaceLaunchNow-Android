package me.calebjones.spacelaunchnow.data.networking.news.api;

import io.realm.RealmList;
import me.calebjones.spacelaunchnow.data.models.main.news.NewsItem;
import me.calebjones.spacelaunchnow.data.models.main.news.NewsItemResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NewsInterface {

    String version = "v4";

    @GET(version + "/articles/{id}")
    Call<NewsItem> getArticleById(@Path("id") String id);

    @GET(version + "/articles/")
    Call<NewsItemResponse> getArticles(@Query("limit") int amount);

    @GET(version + "/articles/")
    Call<NewsItemResponse> getArticlesByLaunch(@Query("launch") String launchId, @Query("limit") int amount);

}
