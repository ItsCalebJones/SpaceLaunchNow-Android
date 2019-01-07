package me.calebjones.spacelaunchnow.data.models.main.astronaut;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class AstronautFlight extends RealmObject {

    @SerializedName("role")
    @Expose
    public String role;
    @SerializedName("astronaut")
    @Expose
    public Astronaut astronaut;
}
