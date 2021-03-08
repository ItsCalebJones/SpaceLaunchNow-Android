package me.calebjones.spacelaunchnow.data.models.main.astronaut;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class AstronautFlight extends RealmObject {

    @SerializedName("role")
    @Expose
    public AstronautRole role;
    @SerializedName("astronaut")
    @Expose
    public Astronaut astronaut;

    public AstronautRole getRole() {
        return role;
    }

    public void setRole(AstronautRole role) {
        this.role = role;
    }

    public Astronaut getAstronaut() {
        return astronaut;
    }

    public void setAstronaut(Astronaut astronaut) {
        this.astronaut = astronaut;
    }
}
