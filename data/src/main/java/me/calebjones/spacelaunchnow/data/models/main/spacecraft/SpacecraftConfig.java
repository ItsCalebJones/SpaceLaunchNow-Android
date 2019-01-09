package me.calebjones.spacelaunchnow.data.models.main.spacecraft;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SpacecraftConfig extends RealmObject {

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
    @SerializedName("in_use")
    @Expose
    public Boolean inUse;
    @SerializedName("capability")
    @Expose
    public String capability;
    @SerializedName("history")
    @Expose
    public String history;
    @SerializedName("details")
    @Expose
    public String details;
    @SerializedName("maiden_flight")
    @Expose
    public String maidenFlight;
    @SerializedName("height")
    @Expose
    public Double height;
    @SerializedName("diameter")
    @Expose
    public Double diameter;
    @SerializedName("human_rated")
    @Expose
    public Boolean humanRated;
    @SerializedName("crew_capacity")
    @Expose
    public Integer crewCapacity;
    @SerializedName("payload_capacity")
    @Expose
    public Integer payloadCapacity;
    @SerializedName("flight_life")
    @Expose
    public String flightLife;
    @SerializedName("image_url")
    @Expose
    public String imageUrl;
    @SerializedName("nation_url")
    @Expose
    public String nationUrl;
    @SerializedName("wiki_link")
    @Expose
    public String wikiLink;
    @SerializedName("info_link")
    @Expose
    public String infoLink;

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

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public Boolean getInUse() {
        return inUse;
    }

    public void setInUse(Boolean inUse) {
        this.inUse = inUse;
    }

    public String getCapability() {
        return capability;
    }

    public void setCapability(String capability) {
        this.capability = capability;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getMaidenFlight() {
        return maidenFlight;
    }

    public void setMaidenFlight(String maidenFlight) {
        this.maidenFlight = maidenFlight;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getDiameter() {
        return diameter;
    }

    public void setDiameter(Double diameter) {
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getNationUrl() {
        return nationUrl;
    }

    public void setNationUrl(String nationUrl) {
        this.nationUrl = nationUrl;
    }

    public String getWikiLink() {
        return wikiLink;
    }

    public void setWikiLink(String wikiLink) {
        this.wikiLink = wikiLink;
    }

    public String getInfoLink() {
        return infoLink;
    }

    public void setInfoLink(String infoLink) {
        this.infoLink = infoLink;
    }
}
