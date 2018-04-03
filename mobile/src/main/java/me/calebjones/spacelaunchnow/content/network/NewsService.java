package me.calebjones.spacelaunchnow.content.network;

import me.calebjones.spacelaunchnow.data.models.news.NewsFeedResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface NewsService {

    @GET("/5ac0fac52c452/rss.xml")
    Call<NewsFeedResponse> getNews();
}
