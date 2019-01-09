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

}
