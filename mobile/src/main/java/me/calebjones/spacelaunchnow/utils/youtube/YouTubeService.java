package me.calebjones.spacelaunchnow.utils.youtube;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import me.calebjones.spacelaunchnow.utils.youtube.models.Video;
import me.calebjones.spacelaunchnow.utils.youtube.models.VideoResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface YouTubeService {

    @GET("/youtube/v3/videos?fields=items(id,snippet(channelId,title,categoryId),statistics)&part=snippet,statistics")
    Call<VideoResponse> getVideoById(@Query("id") String videoId, @Query("key") String apiKey);
}