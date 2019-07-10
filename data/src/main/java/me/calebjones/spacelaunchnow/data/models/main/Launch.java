package me.calebjones.spacelaunchnow.data.models.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import me.calebjones.spacelaunchnow.data.models.realm.RealmStr;

public class Launch extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("launch_library_id")
    @Expose
    public Integer launchLibraryId;
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("slug")
    @Expose
    public String slug;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("img_url")
    @Expose
    public String imgUrl;
    @SerializedName("status")
    @Expose
    public LaunchStatus status;
    @SerializedName("net")
    @Expose
    public Date net;
    @SerializedName("window_end")
    @Expose
    public Date windowEnd;
    @SerializedName("window_start")
    @Expose
    public Date windowStart;
    @SerializedName("inhold")
    @Expose
    public Boolean inhold;
    @SerializedName("tbdtime")
    @Expose
    public Boolean tbdtime;
    @SerializedName("tbddate")
    @Expose
    public Boolean tbddate;
    @SerializedName("probability")
    @Expose
    public Integer probability;
    @SerializedName("holdreason")
    @Expose
    public String holdreason;
    @SerializedName("failreason")
    @Expose
    public String failreason;
    @SerializedName("hashtag")
    @Expose
    public String hashtag;
    @SerializedName("mission")
    @Expose
    public Mission mission;
    @SerializedName("pad")
    @Expose
    public Pad pad;
    @SerializedName("infoURLs")
    @Expose
    public RealmList<RealmStr> infoURLs = null;
    @SerializedName("vidURLs")
    @Expose
    public RealmList<RealmStr> vidURLs = null;
    @SerializedName("rocket")
    @Expose
    public Rocket rocket;

    public Long eventID;
    public Date lastUpdate;

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Rocket getRocket() {
        return rocket;
    }

    public void setRocket(Rocket rocket) {
        this.rocket = rocket;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getEventID() {
        return eventID;
    }

    public void setEventID(Long eventID) {
        this.eventID = eventID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public LaunchStatus getStatus() {
        return status;
    }

    public void setStatus(LaunchStatus status) {
        this.status = status;
    }

    public Date getNet() {
        return net;
    }

    public void setNet(Date net) {
        this.net = net;
    }

    public Date getWindowEnd() {
        return windowEnd;
    }

    public void setWindowEnd(Date windowEnd) {
        this.windowEnd = windowEnd;
    }

    public Date getWindowStart() {
        return windowStart;
    }

    public void setWindowStart(Date windowStart) {
        this.windowStart = windowStart;
    }

    public Boolean getInhold() {
        return inhold;
    }

    public void setInhold(Boolean inhold) {
        this.inhold = inhold;
    }

    public Boolean getTbdtime() {
        return tbdtime;
    }

    public void setTbdtime(Boolean tbdtime) {
        this.tbdtime = tbdtime;
    }

    public Boolean getTbddate() {
        return tbddate;
    }

    public void setTbddate(Boolean tbddate) {
        this.tbddate = tbddate;
    }

    public Integer getProbability() {
        return probability;
    }

    public void setProbability(Integer probability) {
        this.probability = probability;
    }

    public String getHoldreason() {
        return holdreason;
    }

    public void setHoldreason(String holdreason) {
        this.holdreason = holdreason;
    }

    public String getFailreason() {
        return failreason;
    }

    public void setFailreason(String failreason) {
        this.failreason = failreason;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public Mission getMission() {
        return mission;
    }

    public void setMission(Mission mission) {
        this.mission = mission;
    }

    public Pad getPad() {
        return pad;
    }

    public void setPad(Pad pad) {
        this.pad = pad;
    }

    public RealmList<RealmStr> getInfoURLs() {
        return infoURLs;
    }

    public void setInfoURLs(RealmList<RealmStr> infoURLs) {
        this.infoURLs = infoURLs;
    }

    public RealmList<RealmStr> getVidURLs() {
        return vidURLs;
    }

    public void setVidURLs(RealmList<RealmStr> vidURLs) {
        this.vidURLs = vidURLs;
    }
}
