
package me.calebjones.spacelaunchnow.content.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Launch implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String windowstart;
    private String windowend;
    private String net;
    private String holdreason;
    private String failreason;
    private String hashtag;
    private String vidURL;

    private Integer id;
    private Integer probability;
    private Integer wsstamp;
    private Integer westamp;
    private Integer netstamp;
    private Integer status;
    private Integer inhold;
    private Integer tbdtime;

    private Location location;

    private Rocket rocket;

    private Date date;

    private boolean isNotifiedDay = false;
    private boolean isNotifiedHour = false;
    private boolean isNotifiedTenMinute = false;
    private boolean favorite = false;

    private ArrayList<String> vidURLs;
    private ArrayList<String> infoURLs;

    private List<Mission> missions = new ArrayList<Mission>();

    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public boolean getIsNotifiedTenMinute() {
        return isNotifiedTenMinute;
    }

    public void setIsNotifiedTenMinute(boolean isNotifiedTenMinute) {
        this.isNotifiedTenMinute = isNotifiedTenMinute;
    }

    public boolean getIsNotifiedHour() {
        return isNotifiedHour;
    }

    public void setIsNotifiedhour(boolean isNotifiedHour) {
        this.isNotifiedHour = isNotifiedHour;
    }

    public boolean getIsNotifiedDay() {
        return isNotifiedDay;
    }

    public void setIsNotifiedDay(boolean isNotifiedDay) {
        this.isNotifiedDay = isNotifiedDay;
    }

    public Integer getProbability() {
        return probability;
    }

    public void setProbability(Integer probability) {
        this.probability = probability;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public String getFailreason() {
        return failreason;
    }

    public void setFailreason(String failreason) {
        this.failreason = failreason;
    }

    public String getHoldreason() {
        return holdreason;
    }

    public void setHoldreason(String holdreason) {
        this.holdreason = holdreason;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWindowstart() {
        return windowstart;
    }

    public void setWindowstart(String windowstart) {
        this.windowstart = windowstart;
    }

    public String getWindowend() {
        return windowend;
    }

    public void setWindowend(String windowend) {
        this.windowend = windowend;
    }

    public String getNet() {
        return net;
    }

    public void setNet(String net) {
        this.net = net;
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

    public Integer getNetstamp() {
        return netstamp;
    }

    public void setNetstamp(Integer netstamp) {
        this.netstamp = netstamp;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getInhold() {
        return inhold;
    }

    public void setInhold(Integer inhold) {
        this.inhold = inhold;
    }

    public Integer getTbdtime() {
        return tbdtime;
    }

    public void setTbdtime(Integer tbdtime) {
        this.tbdtime = tbdtime;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Rocket getRocket() {
        return rocket;
    }

    public void setRocket(Rocket rocket) {
        this.rocket = rocket;
    }

    public List<Mission> getMissions() {
        return missions;
    }

    public void setMissions(List<Mission> missions) {
        this.missions = missions;
    }

    public String getVidURL() {
        return vidURL;
    }

    public void setVidURL(String vidURL) {
        this.vidURL = vidURL;
    }

    public ArrayList<String> getVidURLs() {
        return vidURLs;
    }

    public void setVidURLs(ArrayList<String> mVidURls) {
        this.vidURLs = mVidURls;
    }

    public ArrayList<String> getInfoURLs() {
        return infoURLs;
    }

    public void setInfoURLs(ArrayList<String> infoURLs) {
        this.infoURLs = infoURLs;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public Date getLaunchDate() {
        return date;
    }

    public void setLaunchDate(Date date) {
        this.date = date;
    }
}
