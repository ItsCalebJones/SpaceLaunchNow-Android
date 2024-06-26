package me.calebjones.spacelaunchnow.data.models.main.news;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import me.calebjones.spacelaunchnow.data.models.realm.RealmStr;

public class NewsItem extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("news_site")
    private String newsSite;
    @SerializedName("image_url")
    private String featured_image;
    @SerializedName("url")
    private String url;
    @SerializedName("launches")
    @Expose
    public RealmList<NewsItemLaunch> launches = null;
    @SerializedName("published_at")
    private Date datePublished;
    @SerializedName("updated_at")
    private Date lastUpdate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNewsSite() {
        return newsSite;
    }

    public void setNewsSite(String newsSite) {
        this.newsSite = newsSite;
    }

    public String getFeaturedImage() {
        return featured_image;
    }

    public void setFeaturedImage(String featured_image) {
        this.featured_image = featured_image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(Date datePublished) {
        this.datePublished = datePublished;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }


    public RealmList<NewsItemLaunch> getLaunches() {
        return launches;
    }

    public void setLaunches(RealmList<NewsItemLaunch> launches) {
        this.launches = launches;
    }
}