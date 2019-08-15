package me.calebjones.spacelaunchnow.news.api;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.realm.RealmObject;
import me.calebjones.spacelaunchnow.data.models.main.news.NewsItem;
import me.calebjones.spacelaunchnow.data.models.main.news.NewsItemResponse;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsDataClient {

    private final NewsInterface newsInterface;
    private Retrofit newsRetrofit;

    public NewsDataClient() {
        newsRetrofit = newsRetrofit();
        newsInterface = newsRetrofit.create(NewsInterface.class);
    }

    public Call<NewsItem> getNewsById(String id, Callback<NewsItem> callback){
        Call<NewsItem> call;

        call = newsInterface.getArticleById(id);

        call.enqueue(callback);

        return call;
    }

    public Call<NewsItemResponse> getArticles(int limit, Callback<NewsItemResponse>  callback){
        Call<NewsItemResponse>  call;

        call = newsInterface.getArticles(limit);

        call.enqueue(callback);

        return call;
    }

    public Call<NewsItemResponse> getArticlesByPage(int limit, String page, Callback<NewsItemResponse>  callback){
        Call<NewsItemResponse>  call;

        call = newsInterface.getArticlesByPage(limit, page);

        call.enqueue(callback);

        return call;
    }

    private static Retrofit newsRetrofit() {

        String BASE_URL = "https://spaceflightnewsapi.net";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(defaultClient())
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .build();
        return retrofit;
    }

    private static OkHttpClient defaultClient() {
        OkHttpClient.Builder client = new OkHttpClient().newBuilder();
        client.connectTimeout(15, TimeUnit.SECONDS);
        client.readTimeout(15, TimeUnit.SECONDS);
        client.writeTimeout(15, TimeUnit.SECONDS);
        return client.build();
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .setDateFormat("MMMM dd, yyyy HH:mm:ss zzz")
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .create();
    }

    private static class DateDeserializer implements JsonDeserializer<Date> {

        @Override
        public Date deserialize(JsonElement jsonElement, Type typeOF,
                                JsonDeserializationContext context) throws JsonParseException {
            for (String format : DATE_FORMATS) {
                try {
                    return new SimpleDateFormat(format, Locale.US).parse(jsonElement.getAsString());
                } catch (ParseException e) {
                }
            }
            throw new JsonParseException("Unparseable date: \"" + jsonElement.getAsString()
                    + "\". Supported formats: " + Arrays.toString(DATE_FORMATS));
        }
    }

    private static final String[] DATE_FORMATS = new String[] {
            "MMMM dd, yyyy HH:mm:ss zzz",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd"
    };

}


