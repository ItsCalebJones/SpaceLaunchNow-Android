package me.calebjones.spacelaunchnow.data.models.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Orbiter extends RealmObject {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("agency")
    @Expose
    public String agency;
    @SerializedName("history")
    @Expose
    public String history;
    @SerializedName("details")
    @Expose
    public String details;
    @SerializedName("image_url")
    @Expose
    public String imageURL;
    @SerializedName("nation_url")
    @Expose
    public String nationURL;
    @SerializedName("wiki_link")
    @Expose
    public String wikiLink;
    @SerializedName("capability")
    @Expose
    public String capability;

    public String getName() {
        return name;
    }

    public String getAgency() {
        return agency;
    }

    public String getImageURL(){
        return imageURL;
    }

    public String getNationURL() { return nationURL;}

    public String getHistory() { return history;}

    public String getDetails() { return details;}

    public String getWikiLink() { return wikiLink;}

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

    public void setName(String name) {
        this.name = name;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setNationURL(String nationURL) {
        this.nationURL = nationURL;
    }

    public void setWikiLink(String wikiLink) {
        this.wikiLink = wikiLink;
    }

    public String getCapability() {
        return capability;
    }

    public void setCapability(String capability) {
        this.capability = capability;
    }
}
