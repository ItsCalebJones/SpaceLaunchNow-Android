package me.calebjones.spacelaunchnow.data.models.main.spacestation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import me.calebjones.spacelaunchnow.data.models.main.Agency;
import me.calebjones.spacelaunchnow.data.models.main.spacecraft.SpacecraftStage;

public class Spacestation extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("launchStatus")
    @Expose
    public SpacestationStatus status;
    @SerializedName("founded")
    @Expose
    public String founded;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("orbit")
    @Expose
    public String orbit;
    @SerializedName("onboard_crew")
    @Expose
    public Integer onboardCrew;
    @SerializedName("owners")
    @Expose
    public RealmList<Agency> owners = null;
    @SerializedName("docked_vehicles")
    @Expose
    public RealmList<SpacecraftStage> dockedVehicles = null;
    @SerializedName("active_expeditions")
    @Expose
    public RealmList<Expedition> activeExpeditions = null;

}
