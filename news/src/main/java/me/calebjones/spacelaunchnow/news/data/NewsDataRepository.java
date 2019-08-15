package me.calebjones.spacelaunchnow.news.data;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.UiThread;

import com.pixplicity.easyprefs.library.Prefs;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import jonathanfinerty.once.Once;
import me.calebjones.spacelaunchnow.data.models.main.news.NewsItem;
import me.calebjones.spacelaunchnow.data.models.main.news.NewsItemResponse;
import timber.log.Timber;

/**
 * Responsible for retrieving data from the Realm cache.
 */

public class NewsDataRepository {

    private NewsDataLoader dataLoader;
    private Realm realm;
    private final Context context;
    private boolean moreDataAvailable;

    public NewsDataRepository(Context context, Realm realm) {
        this.context = context;
        this.dataLoader = new NewsDataLoader(context);
        this.realm = realm;
    }

    @UiThread
    public void getNews(int limit, boolean forceUpdate, Callbacks.NewsListCallback callback) {

        final RealmResults<NewsItem> newsList = getNewsFromRealm();
        Timber.v("Current count in DB: %s", newsList.size());
        if (!Once.beenDone(TimeUnit.HOURS, 1, "forceNewsUpdate")) {
            forceUpdate = true;
        }

        Timber.v("Limit: %s ", limit);
        if (forceUpdate || newsList.size() == 0 ) {
            Timber.v("Getting from network!");
            getNewsFromNetwork(limit, callback);
        } else {
            callback.onNewsLoaded(newsList);
        }
    }

    //TODO fix query
    public RealmResults<NewsItem> getNewsFromRealm() {
        RealmQuery<NewsItem> query = realm.where(NewsItem.class).isNotNull("id");
        return query.sort("datePublished", Sort.DESCENDING).findAll();
    }

    public NewsItem getNewsByIdFromRealm(String id) {
        return realm.where(NewsItem.class).equalTo("id", id).findFirst();
    }


    private void getNewsFromNetwork(int limit, final Callbacks.NewsListCallback callback) {

        callback.onNetworkStateChanged(true);
        dataLoader.getNewsList(limit, new Callbacks.NewsListNetworkCallback() {
            @Override
            public void onSuccess(NewsItemResponse news) {
                addNewsToRealm(news.getNewsItems());
                callback.onNetworkStateChanged(false);
                callback.onNewsLoaded(getNewsFromRealm());
            }

            @Override
            public void onNetworkFailure(int code) {
                callback.onNetworkStateChanged(false);
                callback.onError("Unable to load launch data.", null);
            }

            @Override
            public void onFailure(Throwable throwable) {
                callback.onNetworkStateChanged(false);
                callback.onError("An error has occurred! Uh oh.", throwable);
            }
        });
    }

    @UiThread
    public void getNewsById(String id, Callbacks.NewsCallback callback) {
        callback.onNewsLoaded(getNewsByIdFromRealm(id));
        getNewsByIdFromNetwork(id, callback);
    }

    private void getNewsByIdFromNetwork(String id, final Callbacks.NewsCallback callback) {

        callback.onNetworkStateChanged(true);
        dataLoader.getNewsById(id, new Callbacks.NewsNetworkCallback() {
            @Override
            public void onSuccess(NewsItem event) {

                callback.onNetworkStateChanged(false);
                callback.onNewsLoaded(event);
            }

            @Override
            public void onNetworkFailure(int code) {
                callback.onNetworkStateChanged(false);
                callback.onError("Unable to load launch data.", null);
            }

            @Override
            public void onFailure(Throwable throwable) {
                callback.onNetworkStateChanged(false);
                callback.onError("An error has occurred! Uh oh.", throwable);
            }
        });
    }

    public void addNewsToRealm(final List<NewsItem> news) {
        realm.executeTransaction(realm -> realm.copyToRealmOrUpdate(news));
    }
}


