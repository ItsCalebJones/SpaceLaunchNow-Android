package me.calebjones.spacelaunchnow.data.models.spacelaunchnow;

import com.google.gson.annotations.SerializedName;
import io.realm.RealmObject;

public class Agency extends RealmObject{

    @SerializedName(value = "agency")
    String agency;
    @SerializedName(value = "launchers")
    String launchers;
    @SerializedName(value = "orbiters")
    String orbiters;
    @SerializedName(value = "description")
    String description;
    @SerializedName(value = "image_url")
    String imageURL;
    @SerializedName(value = "nation_url")
    String nationURL;

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public String getLaunchers() {
        return launchers;
    }

    public void setLaunchers(String launchers) {
        this.launchers = launchers;
    }

    public String getOrbiters() {
        return orbiters;
    }

    public void setOrbiters(String orbiters) {
        this.orbiters = orbiters;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getNationURL() {
        return nationURL;
    }

    public void setNationURL(String nationURL) {
        this.nationURL = nationURL;
    }


}
