package me.calebjones.spacelaunchnow.data.models.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Stage extends RealmObject {
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
}
