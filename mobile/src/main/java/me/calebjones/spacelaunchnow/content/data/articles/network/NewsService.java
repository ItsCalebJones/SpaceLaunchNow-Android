package me.calebjones.spacelaunchnow.content.data.articles.network;

import java.util.List;

import me.calebjones.spacelaunchnow.data.models.news.Article;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NewsService {

    @GET("/articles/")
    Call<List<Article>> getNews(@Query("limit") int amount);

    @GET("/article/{id}")
    Call<Article> getArticle(@Path("id") String id);

}
