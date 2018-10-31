package me.calebjones.spacelaunchnow.content.data.articles.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.RealmList;
import me.calebjones.spacelaunchnow.data.helpers.Utils;
import me.calebjones.spacelaunchnow.data.models.news.Article;
import me.calebjones.spacelaunchnow.data.models.realm.RealmStr;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsAPIClient {

    private final NewsService newsService;
    private Retrofit newsRetrofit;

    public NewsAPIClient(Retrofit newsRetrofit){
        this.newsRetrofit = newsRetrofit;
        newsService = this.newsRetrofit.create(NewsService.class);
    }

    public Call<List<Article>> getNews(int limit, int page, Callback<List<Article>> callback) {
        Call<List<Article>> call;
        call = newsService.getNews(limit, page);
        call.enqueue(callback);
        return call;
    }

    public Call<List<Article>> getNewsBySite(int limit, int page, String sites, Callback<List<Article>> callback) {
        Call<List<Article>> call;
        call = newsService.getNewsBySite(limit, page, sites);
        call.enqueue(callback);
        return call;
    }

    public Call<Article> getArticle(Callback<Article> callback) {
        Call<Article> call;
        call = newsService.getArticle("5b98f79158ed93e183e0f834");
        call.enqueue(callback);
        return call;
    }


}
