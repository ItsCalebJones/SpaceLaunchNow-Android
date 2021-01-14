package me.calebjones.spacelaunchnow.data.networking.news.data;

import androidx.annotation.Nullable;

import io.realm.RealmList;
import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.data.models.main.news.NewsItem;
import me.calebjones.spacelaunchnow.data.models.main.news.NewsItemResponse;

public class Callbacks {
    public interface NewsListNetworkCallback {
        void onSuccess(RealmList<NewsItem> news);
        void onNetworkFailure(int code);
        void onFailure(Throwable throwable);
    }

    public interface NewsListCallback {
        void onNewsLoaded(RealmResults<NewsItem> news);
        void onNetworkStateChanged(boolean refreshing);
        void onError(String message, @Nullable Throwable throwable);
    }

    public interface NewsNetworkCallback {
        void onSuccess(NewsItem news);
        void onNetworkFailure(int code);
        void onFailure(Throwable throwable);
    }

    public interface NewsCallback {
        void onNewsLoaded(NewsItem event);
        void onNetworkStateChanged(boolean refreshing);
        void onError(String message, @Nullable Throwable throwable);
    }
}
