package me.calebjones.spacelaunchnow.data.models.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import me.calebjones.spacelaunchnow.data.models.main.launcher.LauncherConfig;
import me.calebjones.spacelaunchnow.data.models.main.launcher.LauncherStage;
import me.calebjones.spacelaunchnow.data.models.main.spacecraft.SpacecraftStage;

public class Rocket extends RealmObject {
    @SerializedName("configuration")
    @Expose
    public LauncherConfig configuration;
    @SerializedName("launcher_stage")
    @Expose
    public RealmList<LauncherStage> launcherStage;
    @SerializedName("spacecraft_stage")
    @Expose
    public SpacecraftStage spacecraftStage;
    @SerializedName("reused")
    @Expose
    public Boolean reused;

    public Boolean getReused() {
        return reused;
    }

    public void setReused(Boolean reused) {
        this.reused = reused;
    }

    public LauncherConfig getConfiguration() {
        return configuration;
    }

    public void setConfiguration(LauncherConfig configuration) {
        this.configuration = configuration;
    }

    public RealmList<LauncherStage> getLauncherStage() {
        return launcherStage;
    }

    public void setLauncherStage(RealmList<LauncherStage> launcherStage) {
        this.launcherStage = launcherStage;
    }

    public SpacecraftStage getSpacecraftStage() {
        return spacecraftStage;
    }

    public void setSpacecraftStage(SpacecraftStage spacecraftStage) {
        this.spacecraftStage = spacecraftStage;
    }
}
