package me.calebjones.spacelaunchnow.data.models.main.news;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class NewsItemResponse extends RealmObject {

    @SerializedName("results")
    private RealmList<NewsItem> newsItems;
    private String next;
    private String previous;

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private int count;

    public RealmList<NewsItem> getNewsItems() {
        return newsItems;
    }

    public void setNewsItems(RealmList<NewsItem> newsItems) {
        this.newsItems = newsItems;
    }
}