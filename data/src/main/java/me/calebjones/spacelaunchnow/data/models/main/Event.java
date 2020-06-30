package me.calebjones.spacelaunchnow.data.models.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import me.calebjones.spacelaunchnow.data.models.main.spacestation.Expedition;
import me.calebjones.spacelaunchnow.data.models.main.spacestation.Spacestation;

public class Event extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("slug")
    @Expose
    public String slug;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("type")
    @Expose
    public EventType type;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("location")
    @Expose
    public String location;
    @SerializedName("feature_image")
    @Expose
    public String featureImage;
    @SerializedName("news_url")
    @Expose
    public String newsUrl;
    @SerializedName("video_url")
    @Expose
    public String videoUrl;
    @SerializedName("webcast_live")
    @Expose
    public Boolean webcastLive;
    @SerializedName("date")
    @Expose
    public Date date;

    @SerializedName("launches")
    private RealmList<LaunchList> launches;

    public RealmList<LaunchList> getLaunches() {
        return launches;
    }

    @SerializedName("expeditions")
    private RealmList<Expedition> expeditions;

    public RealmList<Expedition> getExpeditions() {
        return expeditions;
    }

    @SerializedName("spacestations")
    private RealmList<Spacestation> spacestations;

    public RealmList<Spacestation> getSpacestations() {
        return spacestations;
    }

    private Date lastUpdate;

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    public void setNewsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFeatureImage() {
        return featureImage;
    }

    public void setFeatureImage(String featureImage) {
        this.featureImage = featureImage;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Boolean getWebcastLive() {
        return webcastLive;
    }

    public void setWebcastLive(Boolean webcastLive) {
        this.webcastLive = webcastLive;
    }
}
