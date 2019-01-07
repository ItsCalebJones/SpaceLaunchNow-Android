package me.calebjones.spacelaunchnow.data.models.main.astronaut;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import me.calebjones.spacelaunchnow.data.models.main.Agency;
import me.calebjones.spacelaunchnow.data.models.main.Launch;

public class Astronaut extends RealmObject {

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
    public AstronautStatus status;
    @SerializedName("agency")
    @Expose
    public Agency agency;
    @SerializedName("date_of_birth")
    @Expose
    public String dateOfBirth;
    @SerializedName("date_of_death")
    @Expose
    public String dateOfDeath;
    @SerializedName("nationality")
    @Expose
    public String nationality;
    @SerializedName("twitter")
    @Expose
    public String twitter;
    @SerializedName("instagram")
    @Expose
    public String instagram;
    @SerializedName("bio")
    @Expose
    public String bio;
    @SerializedName("profile_image")
    @Expose
    public String profileImage;
    @SerializedName("wiki")
    @Expose
    public String wiki;
    @SerializedName("flights")
    @Expose
    public RealmList<Launch> flights = null;
}
