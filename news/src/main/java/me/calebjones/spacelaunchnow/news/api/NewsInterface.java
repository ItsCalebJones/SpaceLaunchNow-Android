package me.calebjones.spacelaunchnow.news.api;

import me.calebjones.spacelaunchnow.data.models.main.news.NewsItem;
import me.calebjones.spacelaunchnow.data.models.main.news.NewsItemResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsInterface {

    String version = "api/v1";

    @GET(version + "/article")
    Call<NewsItem> getArticleById(@Query("_id") String id);

    @GET(version + "/articles")
    Call<NewsItemResponse> getArticles(@Query("limit") int amount);


    @GET(version + "/articles")
    Call<NewsItemResponse> getArticlesByPage(@Query("limit") int amount,
                                           @Query("page") String page);

}
