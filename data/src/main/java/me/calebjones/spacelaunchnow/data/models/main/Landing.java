package me.calebjones.spacelaunchnow.data.models.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Landing extends RealmObject{

    @SerializedName("success")
    @Expose
    public Boolean success;
    @SerializedName("attempt")
    @Expose
    public Boolean attempt;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("type")
    @Expose
    public LandingType landingType;
    @SerializedName("location")
    @Expose
    public LandingLocation landingLocation;


    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Boolean getAttempt() {
        return attempt;
    }

    public void setAttempt(Boolean attempt) {
        this.attempt = attempt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LandingType getLandingType() {
        return landingType;
    }

    public void setLandingType(LandingType landingType) {
        this.landingType = landingType;
    }

    public LandingLocation getLandingLocation() {
        return landingLocation;
    }

    public void setLandingLocation(LandingLocation landingLocation) {
        this.landingLocation = landingLocation;
    }
}
