
package me.calebjones.spacelaunchnow.data.models.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import me.calebjones.spacelaunchnow.data.models.main.launcher.LauncherConfig;
import me.calebjones.spacelaunchnow.data.models.main.spacecraft.SpacecraftConfig;

public class Agency extends RealmObject {

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
    @SerializedName("featured")
    @Expose
    public Boolean featured;
    @SerializedName("launcherConfigs")
    @Expose
    public String launchers_string;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("image_url")
    @Expose
    public String imageUrl;
    @SerializedName("nation_url")
    @Expose
    public String nationUrl;
    @SerializedName("administrator")
    @Expose
    public String administrator;
    @SerializedName("founding_year")
    @Expose
    public String foundingYear;
    @SerializedName("logo_url")
    @Expose
    public String logoUrl;
    @SerializedName("launch_library_url")
    @Expose
    public String launchLibraryUrl;
    @SerializedName("country_code")
    @Expose
    public String countryCode;
    @SerializedName("abbrev")
    @Expose
    public String abbrev;
    @SerializedName("info_url")
    @Expose
    public String infoUrl;
    @SerializedName("wiki_url")
    @Expose
    public String wikiUrl;
    @SerializedName("type")
    @Expose
    public String type;
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
    @SerializedName("consecutive_successful_landings")
    @Expose
    public Integer consecutiveSuccessfulLandings;
    @SerializedName("successful_landings")
    @Expose
    public Integer successfulLandings;
    @SerializedName("failed_landings")
    @Expose
    public Integer failedLandings;
    @SerializedName("attempted_landings")
    @Expose
    public Integer attemptedLandings;
    @SerializedName("launcher_list")
    @Expose
    public RealmList<LauncherConfig> launcherConfigs;
    @SerializedName("spacecraft_list")
    @Expose
    public RealmList<SpacecraftConfig> spacecraftConfigs;


    public RealmList<LauncherConfig> getLauncherConfigs() {
        return launcherConfigs;
    }

    public void setLauncherConfigs(RealmList<LauncherConfig> launcherConfigs) {
        this.launcherConfigs = launcherConfigs;
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

    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public String getLaunchers_string() {
        return launchers_string;
    }

    public void setLaunchers_string(String launchers_string) {
        this.launchers_string = launchers_string;
    }

    public RealmList<SpacecraftConfig> getSpacecraftConfigs() {
        return spacecraftConfigs;
    }

    public void setSpacecraftConfigs(RealmList<SpacecraftConfig> spacecraftConfigs) {
        this.spacecraftConfigs = spacecraftConfigs;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getAdministrator() {
        return administrator;
    }

    public void setAdministrator(String administrator) {
        this.administrator = administrator;
    }

    public String getFoundingYear() {
        return foundingYear;
    }

    public void setFoundingYear(String foundingYear) {
        this.foundingYear = foundingYear;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getLaunchLibraryUrl() {
        return launchLibraryUrl;
    }

    public void setLaunchLibraryUrl(String launchLibraryUrl) {
        this.launchLibraryUrl = launchLibraryUrl;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getAbbrev() {
        return abbrev;
    }

    public void setAbbrev(String abbrev) {
        this.abbrev = abbrev;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Integer getConsecutiveSuccessfulLandings() {
        return consecutiveSuccessfulLandings;
    }

    public void setConsecutiveSuccessfulLandings(Integer consecutiveSuccessfulLandings) {
        this.consecutiveSuccessfulLandings = consecutiveSuccessfulLandings;
    }

    public Integer getSuccessfulLandings() {
        return successfulLandings;
    }

    public void setSuccessfulLandings(Integer successfulLandings) {
        this.successfulLandings = successfulLandings;
    }

    public Integer getFailedLandings() {
        return failedLandings;
    }

    public void setFailedLandings(Integer failedLandings) {
        this.failedLandings = failedLandings;
    }

    public Integer getAttemptedLandings() {
        return attemptedLandings;
    }

    public void setAttemptedLandings(Integer attemptedLandings) {
        this.attemptedLandings = attemptedLandings;
    }
}
