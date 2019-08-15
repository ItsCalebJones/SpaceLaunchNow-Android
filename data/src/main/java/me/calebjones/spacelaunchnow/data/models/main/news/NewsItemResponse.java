package me.calebjones.spacelaunchnow.data.models.main.news;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class NewsItemResponse extends RealmObject {

    @SerializedName("docs")
    private RealmList<NewsItem> newsItems;
    private int totalDocs;
    private int limit;
    private int page;
    private int totalPages;
    private int pagingCounter;
    private int prevPage;
    private int nextPage;
    private boolean hasPrevPage;
    private boolean hasNextPage;

    public RealmList<NewsItem> getNewsItems() {
        return newsItems;
    }

    public void setNewsItems(RealmList<NewsItem> newsItems) {
        this.newsItems = newsItems;
    }

    public int getTotalDocs() {
        return totalDocs;
    }

    public void setTotalDocs(int totalDocs) {
        this.totalDocs = totalDocs;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getPagingCounter() {
        return pagingCounter;
    }

    public void setPagingCounter(int pagingCounter) {
        this.pagingCounter = pagingCounter;
    }

    public int getPrevPage() {
        return prevPage;
    }

    public void setPrevPage(int prevPage) {
        this.prevPage = prevPage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public boolean isHasPrevPage() {
        return hasPrevPage;
    }

    public void setHasPrevPage(boolean hasPrevPage) {
        this.hasPrevPage = hasPrevPage;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }
}