package me.calebjones.spacelaunchnow.data.models.main.launcher;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Launcher extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("flights")
    @Expose
    public Integer previousFlights;
    @SerializedName("successful_landings")
    @Expose
    public Integer successfulLandings;
    @SerializedName("attempted_landings")
    @Expose
    public Integer attemptedLandings;
    @SerializedName("flight_proven")
    @Expose
    public Boolean flightProven;
    @SerializedName("serial_number")
    @Expose
    public String serialNumber;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("details")
    @Expose
    public String details;
    @SerializedName("image_url")
    @Expose
    public String imageUrl;
    @SerializedName("last_launch_date")
    @Expose
    public Date lastLaunchDate;
    @SerializedName("first_launch_date")
    @Expose
    public Date firstLaunchDate;

    public LauncherConfig getLauncherConfig() {
        return launcherConfig;
    }

    public void setLauncherConfig(LauncherConfig launcherConfig) {
        this.launcherConfig = launcherConfig;
    }

    @SerializedName("launcher_config")
    @Expose
    public LauncherConfig launcherConfig;

    public Integer getPreviousFlights() {
        return previousFlights;
    }

    public void setPreviousFlights(Integer previousFlights) {
        this.previousFlights = previousFlights;
    }

    public Boolean getFlightProven() {
        return flightProven;
    }

    public void setFlightProven(Boolean flightProven) {
        this.flightProven = flightProven;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Integer getSuccessfulLandings() {
        return successfulLandings;
    }

    public void setSuccessfulLandings(Integer successfulLandings) {
        this.successfulLandings = successfulLandings;
    }

    public Integer getAttemptedLandings() {
        return attemptedLandings;
    }

    public void setAttemptedLandings(Integer attemptedLandings) {
        this.attemptedLandings = attemptedLandings;
    }

    public Date getLastLaunchDate() {
        return lastLaunchDate;
    }

    public void setLastLaunchDate(Date lastLaunchDate) {
        this.lastLaunchDate = lastLaunchDate;
    }

    public Date getFirstLaunchDate() {
        return firstLaunchDate;
    }

    public void setFirstLaunchDate(Date firstLaunchDate) {
        this.firstLaunchDate = firstLaunchDate;
    }
}
