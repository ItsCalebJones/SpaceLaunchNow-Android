package me.calebjones.spacelaunchnow.data.models.main.spacecraft;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Spacecraft extends RealmObject {

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
    @SerializedName("serial_number")
    @Expose
    public String serialNumber;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("configuration")
    @Expose
    public SpacecraftConfig configuration;
    @SerializedName("flights")
    @Expose
    public RealmList<SpacecraftStage> flights = null;

}
