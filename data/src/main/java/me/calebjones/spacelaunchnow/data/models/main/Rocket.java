package me.calebjones.spacelaunchnow.data.models.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Rocket extends RealmObject {
    @SerializedName("configuration")
    @Expose
    public LauncherConfig configuration;
    @SerializedName("first_stage")
    @Expose
    public RealmList<Stage> firstStage;
    @SerializedName("second_stage")
    @Expose
    public Stage  secondStage;

    public LauncherConfig getConfiguration() {
        return configuration;
    }

    public void setConfiguration(LauncherConfig configuration) {
        this.configuration = configuration;
    }

    public RealmList<Stage> getFirstStage() {
        return firstStage;
    }

    public void setFirstStage(RealmList<Stage> firstStage) {
        this.firstStage = firstStage;
    }

    public Stage getSecondStage() {
        return secondStage;
    }

    public void setSecondStage(Stage secondStage) {
        this.secondStage = secondStage;
    }
}
