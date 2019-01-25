package me.calebjones.spacelaunchnow.data.models.main.spacestation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import me.calebjones.spacelaunchnow.data.models.main.spacecraft.SpacecraftStage;

public class DockingEvent extends RealmObject {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("docking")
    @Expose
    public String docking;
    @SerializedName("departure")
    @Expose
    public String departure;
    @SerializedName("flight_vehicle")
    @Expose
    public SpacecraftStage flightVehicle;
    @SerializedName("docking_location")
    @Expose
    public String dockingLocation;

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

    public String getDocking() {
        return docking;
    }

    public void setDocking(String docking) {
        this.docking = docking;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public SpacecraftStage getFlightVehicle() {
        return flightVehicle;
    }

    public void setFlightVehicle(SpacecraftStage flightVehicle) {
        this.flightVehicle = flightVehicle;
    }

    public String getDockingLocation() {
        return dockingLocation;
    }

    public void setDockingLocation(String dockingLocation) {
        this.dockingLocation = dockingLocation;
    }
}
