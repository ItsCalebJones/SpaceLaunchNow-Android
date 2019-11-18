package me.calebjones.spacelaunchnow.data.models.main.spacestation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

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
    public Date docking;
    @SerializedName("departure")
    @Expose
    public Date departure;
    @SerializedName("flight_vehicle")
    @Expose
    public SpacecraftStage flightVehicle;
    @SerializedName("docking_location")
    @Expose
    public DockingLocation dockingLocation;

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

    public Date getDocking() {
        return docking;
    }

    public void setDocking(Date docking) {
        this.docking = docking;
    }

    public Date getDeparture() {
        return departure;
    }

    public void setDeparture(Date departure) {
        this.departure = departure;
    }

    public SpacecraftStage getFlightVehicle() {
        return flightVehicle;
    }

    public void setFlightVehicle(SpacecraftStage flightVehicle) {
        this.flightVehicle = flightVehicle;
    }

    public DockingLocation getDockingLocation() {
        return dockingLocation;
    }

    public void setDockingLocation(DockingLocation dockingLocation) {
        this.dockingLocation = dockingLocation;
    }
}
