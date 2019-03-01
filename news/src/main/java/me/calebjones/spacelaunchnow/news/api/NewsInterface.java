package me.calebjones.spacelaunchnow.news.api;

import java.util.List;

import me.calebjones.spacelaunchnow.data.models.main.news.NewsItem;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsInterface {

    @GET("/article")
    Call<NewsItem> getArticleById(@Query("_id") String id);

    @GET("/articles")
    Call<List<NewsItem>> getArticles(@Query("limit") int amount);


    @GET("/articles")
    Call<List<NewsItem>> getArticlesByPage(@Query("limit") int amount,
                                           @Query("page") String page);

}
