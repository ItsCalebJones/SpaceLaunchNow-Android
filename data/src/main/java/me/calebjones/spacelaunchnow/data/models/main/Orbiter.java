package me.calebjones.spacelaunchnow.data.models.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

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
    @SerializedName("info_link")
    @Expose
    public String infoLink;
    @SerializedName("capability")
    @Expose
    public String capability;
    @SerializedName("maiden_flight")
    @Expose
    public Date maidenFlight;
    @SerializedName("height")
    @Expose
    public Float height;
    @SerializedName("diameter")
    @Expose
    public Float diameter;
    @SerializedName("human_rated")
    @Expose
    public Boolean humanRated;
    @SerializedName("in_use")
    @Expose
    public Boolean inUse;
    @SerializedName("crew_capacity")
    @Expose
    public Integer crewCapacity;
    @SerializedName("payload_capacity")
    @Expose
    public Integer payloadCapacity;
    @SerializedName("flight_life")
    @Expose
    public String flightLife;

    public String getInfoLink() {
        return infoLink;
    }

    public void setInfoLink(String infoLink) {
        this.infoLink = infoLink;
    }

    public Boolean getInUse() {
        return inUse;
    }

    public void setInUse(Boolean inUse) {
        this.inUse = inUse;
    }

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

    public Date getMaidenFlight() {
        return maidenFlight;
    }

    public void setMaidenFlight(Date maidenFlight) {
        this.maidenFlight = maidenFlight;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public Float getDiameter() {
        return diameter;
    }

    public void setDiameter(Float diameter) {
        this.diameter = diameter;
    }

    public Boolean getHumanRated() {
        return humanRated;
    }

    public void setHumanRated(Boolean humanRated) {
        this.humanRated = humanRated;
    }

    public Integer getCrewCapacity() {
        return crewCapacity;
    }

    public void setCrewCapacity(Integer crewCapacity) {
        this.crewCapacity = crewCapacity;
    }

    public Integer getPayloadCapacity() {
        return payloadCapacity;
    }

    public void setPayloadCapacity(Integer payloadCapacity) {
        this.payloadCapacity = payloadCapacity;
    }

    public String getFlightLife() {
        return flightLife;
    }

    public void setFlightLife(String flightLife) {
        this.flightLife = flightLife;
    }
}
