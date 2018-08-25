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
    public Integer id;
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("img_url")
    @Expose
    public String imgUrl;
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("netstamp")
    @Expose
    public Integer netstamp;
    @SerializedName("wsstamp")
    @Expose
    public Integer wsstamp;
    @SerializedName("westamp")
    @Expose
    public Integer westamp;
    @SerializedName("net")
    @Expose
    public Date net;
    @SerializedName("window_end")
    @Expose
    public Date windowEnd;
    @SerializedName("window_start")
    @Expose
    public Date windowStart;
    @SerializedName("isonet")
    @Expose
    public String isonet;
    @SerializedName("isostart")
    @Expose
    public String isostart;
    @SerializedName("isoend")
    @Expose
    public String isoend;
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
    @SerializedName("launcher")
    @Expose
    public Launcher launcher;
    @SerializedName("mission")
    @Expose
    public Mission mission;
    @SerializedName("lsp")
    @Expose
    public Agency lsp;
    @SerializedName("location")
    @Expose
    public Location location;
    @SerializedName("pad")
    @Expose
    public Pad pad;
    @SerializedName("infoURLs")
    @Expose
    public RealmList<RealmStr> infoURLs = null;
    @SerializedName("vidURLs")
    @Expose
    public RealmList<RealmStr> vidURLs = null;

    public Integer eventID;
    public Date lastUpdate;

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Integer getEventID() {
        return eventID;
    }

    public void setEventID(Integer eventID) {
        this.eventID = eventID;
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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getNetstamp() {
        return netstamp;
    }

    public void setNetstamp(Integer netstamp) {
        this.netstamp = netstamp;
    }

    public Integer getWsstamp() {
        return wsstamp;
    }

    public void setWsstamp(Integer wsstamp) {
        this.wsstamp = wsstamp;
    }

    public Integer getWestamp() {
        return westamp;
    }

    public void setWestamp(Integer westamp) {
        this.westamp = westamp;
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

    public String getIsonet() {
        return isonet;
    }

    public void setIsonet(String isonet) {
        this.isonet = isonet;
    }

    public String getIsostart() {
        return isostart;
    }

    public void setIsostart(String isostart) {
        this.isostart = isostart;
    }

    public String getIsoend() {
        return isoend;
    }

    public void setIsoend(String isoend) {
        this.isoend = isoend;
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

    public Launcher getLauncher() {
        return launcher;
    }

    public void setLauncher(Launcher launcher) {
        this.launcher = launcher;
    }

    public Mission getMission() {
        return mission;
    }

    public void setMission(Mission mission) {
        this.mission = mission;
    }

    public Agency getLsp() {
        return lsp;
    }

    public void setLsp(Agency lsp) {
        this.lsp = lsp;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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
