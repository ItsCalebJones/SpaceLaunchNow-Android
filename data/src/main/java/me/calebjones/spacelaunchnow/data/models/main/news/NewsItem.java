package me.calebjones.spacelaunchnow.data.models.main.news;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class NewsItem extends RealmObject {

    @PrimaryKey
    @SerializedName("_id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("newsSiteLong")
    private String newsSiteLong;
    @SerializedName("newsSite")
    private String newsSite;
    @SerializedName("featured_image")
    private String featured_image;
    @SerializedName("datePublished")
    private int datePublished;
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

    public String getNewsSiteLong() {
        return newsSiteLong;
    }

    public void setNewsSiteLong(String newsSiteLong) {
        this.newsSiteLong = newsSiteLong;
    }

    public String getNewsSite() {
        return newsSite;
    }

    public void setNewsSite(String newsSite) {
        this.newsSite = newsSite;
    }

    public String getFeatured_image() {
        return featured_image;
    }

    public void setFeatured_image(String featured_image) {
        this.featured_image = featured_image;
    }

    public int getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(int datePublished) {
        this.datePublished = datePublished;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}