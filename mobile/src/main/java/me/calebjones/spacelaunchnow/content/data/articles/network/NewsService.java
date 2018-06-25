package me.calebjones.spacelaunchnow.content.data.articles.network;

import me.calebjones.spacelaunchnow.data.models.news.NewsFeedResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface NewsService {
    @GET("5ac455da55299/rss.xml")
    Call<NewsFeedResponse> getNews();
}
