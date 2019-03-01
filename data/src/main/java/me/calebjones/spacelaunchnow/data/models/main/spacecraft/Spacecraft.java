package me.calebjones.spacelaunchnow.data.models.main.spacecraft;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Spacecraft extends RealmObject {

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
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("serial_number")
    @Expose
    public String serialNumber;
    @SerializedName("status")
    @Expose
    public SpacecraftStatus status;
    @SerializedName(value="configuration", alternate={"spacecraft_config"})
    @Expose
    public SpacecraftConfig configuration;
    @SerializedName("flights")
    @Expose
    public RealmList<SpacecraftStage> flights = null;

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

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public SpacecraftStatus getStatus() {
        return status;
    }

    public void setStatus(SpacecraftStatus status) {
        this.status = status;
    }

    public SpacecraftConfig getConfiguration() {
        return configuration;
    }

    public void setConfiguration(SpacecraftConfig configuration) {
        this.configuration = configuration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RealmList<SpacecraftStage> getFlights() {
        return flights;
    }

    public void setFlights(RealmList<SpacecraftStage> flights) {
        this.flights = flights;
    }
}
