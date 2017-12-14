package me.calebjones.spacelaunchnow.data.models.spacelaunchnow;

import com.google.gson.annotations.SerializedName;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LauncherAgency extends RealmObject{

    @PrimaryKey
    @SerializedName(value = "agency")
    public String agency;
    @SerializedName(value = "launchers")
    public String launchers;
    @SerializedName(value = "orbiters")
    public String orbiters;
    @SerializedName(value = "description")
    public String description;
     @SerializedName(value = "image_url")
    public String imageURL;
    @SerializedName(value = "nation_url")
    public String nationURL;

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
