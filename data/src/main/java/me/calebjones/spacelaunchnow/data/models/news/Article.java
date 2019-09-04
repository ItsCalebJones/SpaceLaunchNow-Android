package me.calebjones.spacelaunchnow.data.models.news;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import me.calebjones.spacelaunchnow.data.models.realm.RealmStr;

public class Article {

    @SerializedName("tags")
    @Expose
    public List<String> tags = null;
    @SerializedName("categories")
    @Expose
    public List<String> categories = null;
    @SerializedName("news_site_long")
    @Expose
    public String newsSite;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("launches")
    @Expose
    public RealmList<RealmStr> launches = null;
    @SerializedName("date_published")
    @Expose
    public Date datePublished;
    @SerializedName("date_Added")
    @Expose
    public Date dateAdded;
    @SerializedName("featured_image")
    @Expose
    public String featuredImage;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getNewsSite() {
        return newsSite;
    }

    public void setNewsSite(String newsSite) {
        this.newsSite = newsSite;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(Date datePublished) {
        this.datePublished = datePublished;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getFeaturedImage() {
        return featuredImage;
    }

    public void setFeaturedImage(String featuredImage) {
        this.featuredImage = featuredImage;
    }

    public RealmList<RealmStr> getLaunches() {
        return launches;
    }

    public void setLaunches(RealmList<RealmStr> launches) {
        this.launches = launches;
    }
}
