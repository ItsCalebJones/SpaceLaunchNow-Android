package me.calebjones.spacelaunchnow.data.models.main.launcher;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import me.calebjones.spacelaunchnow.data.models.main.Agency;

public class LauncherConfig extends RealmObject {

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
    @SerializedName("manufacturer")
    @Expose
    public Agency manufacturer;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("family")
    @Expose
    public String family;
    @SerializedName("full_name")
    @Expose
    public String fullName;
    @SerializedName("variant")
    @Expose
    public String variant;
    @SerializedName("alias")
    @Expose
    public String alias;
    @SerializedName("min_stage")
    @Expose
    public Integer minStage;
    @SerializedName("max_stage")
    @Expose
    public Integer maxStage;
    @SerializedName("length")
    @Expose
    public Float length;
    @SerializedName("diameter")
    @Expose
    public Float diameter;
    @SerializedName("launch_mass")
    @Expose
    public Integer launchMass;
    @SerializedName("leo_capacity")
    @Expose
    public Integer leoCapacity;
    @SerializedName("gto_capacity")
    @Expose
    public Integer gtoCapacity;
    @SerializedName("to_thrust")
    @Expose
    public Integer toThrust;
    @SerializedName("apogee")
    @Expose
    public Integer apogee;
    @SerializedName("vehicle_range")
    @Expose
    public Integer vehicleRange;
    @SerializedName("image_url")
    @Expose
    public String imageUrl;
    @SerializedName("info_url")
    @Expose
    public String infoUrl;
    @SerializedName("wiki_url")
    @Expose
    public String wikiUrl;
    @SerializedName("maiden_flight")
    @Expose
    public Date maidenFlight;
    @SerializedName("total_launch_count")
    @Expose
    public Integer totalLaunchCount;
    @SerializedName("consecutive_successful_launches")
    @Expose
    public Integer consecutiveSuccessfulLaunches;
    @SerializedName("successful_launches")
    @Expose
    public Integer successfulLaunches;
    @SerializedName("failed_launches")
    @Expose
    public Integer failedLaunches;
    @SerializedName("pending_launches")
    @Expose
    public Integer pendingLaunches;

    public Agency getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Agency manufacturer) {
        this.manufacturer = manufacturer;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Integer getMinStage() {
        return minStage;
    }

    public void setMinStage(Integer minStage) {
        this.minStage = minStage;
    }

    public Integer getMaxStage() {
        return maxStage;
    }

    public void setMaxStage(Integer maxStage) {
        this.maxStage = maxStage;
    }

    public Float getLength() {
        return length;
    }

    public void setLength(Float length) {
        this.length = length;
    }

    public Float getDiameter() {
        return diameter;
    }

    public void setDiameter(Float diameter) {
        this.diameter = diameter;
    }

    public Integer getLaunchMass() {
        return launchMass;
    }

    public void setLaunchMass(Integer launchMass) {
        this.launchMass = launchMass;
    }

    public Integer getLeoCapacity() {
        return leoCapacity;
    }

    public void setLeoCapacity(Integer leoCapacity) {
        this.leoCapacity = leoCapacity;
    }

    public Integer getGtoCapacity() {
        return gtoCapacity;
    }

    public void setGtoCapacity(Integer gtoCapacity) {
        this.gtoCapacity = gtoCapacity;
    }

    public Integer getToThrust() {
        return toThrust;
    }

    public void setToThrust(Integer toThrust) {
        this.toThrust = toThrust;
    }

    public Integer getApogee() {
        return apogee;
    }

    public void setApogee(Integer apogee) {
        this.apogee = apogee;
    }

    public Integer getVehicleRange() {
        return vehicleRange;
    }

    public void setVehicleRange(Integer vehicleRange) {
        this.vehicleRange = vehicleRange;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getInfoUrl() {
        return infoUrl;
    }

    public void setInfoUrl(String infoUrl) {
        this.infoUrl = infoUrl;
    }

    public String getWikiUrl() {
        return wikiUrl;
    }

    public void setWikiUrl(String wikiUrl) {
        this.wikiUrl = wikiUrl;
    }

    public Integer getTotalLaunchCount() {
        return totalLaunchCount;
    }

    public void setTotalLaunchCount(Integer totalLaunchCount) {
        this.totalLaunchCount = totalLaunchCount;
    }

    public Integer getConsecutiveSuccessfulLaunches() {
        return consecutiveSuccessfulLaunches;
    }

    public void setConsecutiveSuccessfulLaunches(Integer consecutiveSuccessfulLaunches) {
        this.consecutiveSuccessfulLaunches = consecutiveSuccessfulLaunches;
    }

    public Integer getSuccessfulLaunches() {
        return successfulLaunches;
    }

    public void setSuccessfulLaunches(Integer successfulLaunches) {
        this.successfulLaunches = successfulLaunches;
    }

    public Integer getFailedLaunches() {
        return failedLaunches;
    }

    public void setFailedLaunches(Integer failedLaunches) {
        this.failedLaunches = failedLaunches;
    }

    public Integer getPendingLaunches() {
        return pendingLaunches;
    }

    public void setPendingLaunches(Integer pendingLaunches) {
        this.pendingLaunches = pendingLaunches;
    }

    public Date getMaidenFlight() {
        return maidenFlight;
    }

    public void setMaidenFlight(Date maidenFlight) {
        this.maidenFlight = maidenFlight;
    }
}
