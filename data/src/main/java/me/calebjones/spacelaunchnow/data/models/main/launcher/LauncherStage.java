package me.calebjones.spacelaunchnow.data.models.main.launcher;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;
import me.calebjones.spacelaunchnow.data.models.main.Landing;

public class LauncherStage extends RealmObject {
    @SerializedName("launcher")
    @Expose
    public Launcher launcher;

    @SerializedName("landing")
    @Expose
    public Landing landing;

    @SerializedName("type")
    @Expose
    public String type;

    @SerializedName("reused")
    @Expose
    public Boolean reused;

    @SerializedName("turn_around_time_days")
    @Expose
    public Integer turnAroundTimeDays;

    @SerializedName("previous_flight_date")
    @Expose
    public Date previousFlightDate;

    @SerializedName("launcher_flight_number")
    @Expose
    public Integer flightNumber;


    public Integer getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(Integer flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getReused() {
        return reused;
    }

    public void setReused(Boolean reused) {
        this.reused = reused;
    }

    public Launcher getLauncher() {
        return launcher;
    }

    public void setLauncher(Launcher launcher) {
        this.launcher = launcher;
    }

    public Landing getLanding() {
        return landing;
    }

    public void setLanding(Landing landing) {
        this.landing = landing;
    }

    public Integer getTurnAroundTimeDays() {
        return turnAroundTimeDays;
    }

    public void setTurnAroundTimeDays(Integer turnAroundTimeDays) {
        this.turnAroundTimeDays = turnAroundTimeDays;
    }

    public Date getPreviousFlightDate() {
        return previousFlightDate;
    }

    public void setPreviousFlightDate(Date previousFlightDate) {
        this.previousFlightDate = previousFlightDate;
    }
}
