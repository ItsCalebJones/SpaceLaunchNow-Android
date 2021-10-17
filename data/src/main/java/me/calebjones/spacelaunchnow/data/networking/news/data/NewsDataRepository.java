package me.calebjones.spacelaunchnow.data.networking.news.data;

import android.content.Context;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.UiThread;

import io.realm.Realm;
import io.realm.RealmList;
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
        RealmQuery<NewsItem> query = realm.where(NewsItem.class);
        return query.sort("datePublished", Sort.DESCENDING).findAll();
    }

    public RealmResults<NewsItem> getRelatedNewsFromRealm(String launchId) {
        RealmQuery<NewsItem> query = realm.where(NewsItem.class).contains("launches.id", launchId);
        return query.sort("datePublished", Sort.DESCENDING).findAll();
    }

    public NewsItem getNewsByIdFromRealm(String id) {
        return realm.where(NewsItem.class).equalTo("id", id).findFirst();
    }


    private void getNewsFromNetwork(int limit, final Callbacks.NewsListCallback callback) {

        callback.onNetworkStateChanged(true);
        dataLoader.getNewsList(limit, new Callbacks.NewsListNetworkCallback() {
            @Override
            public void onSuccess(RealmList<NewsItem> news) {
                addNewsToRealm(news);
                callback.onNetworkStateChanged(false);
                callback.onNewsLoaded(getNewsFromRealm());
            }

            @Override
            public void onNetworkFailure(int code) {
                callback.onNetworkStateChanged(false);
                callback.onError("Unable to load news data.", null);
            }

            @Override
            public void onFailure(Throwable throwable) {
                callback.onNetworkStateChanged(false);
                callback.onError("An error has occurred! Uh oh.", throwable);
            }
        });
    }

    public void getNewsById(String id, Callbacks.NewsCallback callback) {
        callback.onNewsLoaded(getNewsByIdFromRealm(id));
        getNewsByIdFromNetwork(id, callback);
    }

    public void getNewsByLaunch(String id, Callbacks.NewsListCallback callback) {
        callback.onNewsLoaded(getRelatedNewsFromRealm(id));
        getNewsByLaunchFromNetwork(5, id, callback);
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
                callback.onError("Unable to load news data.", null);
            }

            @Override
            public void onFailure(Throwable throwable) {
                callback.onNetworkStateChanged(false);
                callback.onError("An error has occurred! Uh oh.", throwable);
            }
        });
    }

    private void getNewsByLaunchFromNetwork(int limit, String launchId, final Callbacks.NewsListCallback callback) {

        callback.onNetworkStateChanged(true);
        dataLoader.getNewsByLaunch(limit, launchId, new Callbacks.NewsListNetworkCallback() {
            @Override
            public void onSuccess(RealmList<NewsItem> news) {
                addNewsToRealm(news);
                callback.onNetworkStateChanged(false);
                callback.onNewsLoaded(getRelatedNewsFromRealm(launchId));
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


