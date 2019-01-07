package me.calebjones.spacelaunchnow.data.models.main.spacecraft;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.models.main.astronaut.Astronaut;
import me.calebjones.spacelaunchnow.data.models.main.spacestation.DockingEvent;

public class SpacecraftStage extends RealmObject {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("splashdown")
    @Expose
    public String splashdown;
    @SerializedName("destination")
    @Expose
    public String destination;
    @SerializedName("launch_crew")
    @Expose
    public RealmList<Astronaut> launchCrew = null;
    @SerializedName("onboard_crew")
    @Expose
    public RealmList<Astronaut> onboardCrew = null;
    @SerializedName("landing_crew")
    @Expose
    public RealmList<Astronaut> landingCrew = null;
    @SerializedName("spacecraft")
    @Expose
    public Spacecraft spacecraft;
    @SerializedName("launch")
    @Expose
    public Launch launch;
    @SerializedName("docking_events")
    @Expose
    public RealmList<DockingEvent> dockingEvents = null;

}
