package me.calebjones.spacelaunchnow.data.models.spacelaunchnow;

import com.google.gson.annotations.SerializedName;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SLNAgency extends RealmObject{

    @PrimaryKey
    public int id;
    @SerializedName(value = "name")
    public String name;
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
    @SerializedName(value = "ceo")
    public String CEO;
    @SerializedName(value = "founding_year")
    public String foundingYear;
    @SerializedName(value = "launch_library_url")
    public String launchLibraryURL;
    @SerializedName(value = "launch_library_id")
    public int launchLibraryId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCEO() {
        return CEO;
    }

    public void setCEO(String CEO) {
        this.CEO = CEO;
    }

    public String getFoundingYear() {
        return foundingYear;
    }

    public void setFoundingYear(String foundingYear) {
        this.foundingYear = foundingYear;
    }

    public String getLaunchLibraryURL() {
        return launchLibraryURL;
    }

    public void setLaunchLibraryURL(String launchLibraryURL) {
        this.launchLibraryURL = launchLibraryURL;
    }

    public int getLaunchLibraryId() {
        return launchLibraryId;
    }

    public void setLaunchLibraryId(int launchLibraryId) {
        this.launchLibraryId = launchLibraryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
