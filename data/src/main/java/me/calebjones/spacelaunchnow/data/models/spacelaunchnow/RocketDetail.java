package me.calebjones.spacelaunchnow.data.models.spacelaunchnow;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RocketDetail extends RealmObject {

    @PrimaryKey
    @SerializedName(value = "id")
    private int id;
    @SerializedName(value = "info_url")
    private String infoURL;
    @SerializedName(value = "wiki_url")
    private String wikiURL;
    @SerializedName(value = "description")
    private String description;
    @SerializedName(value = "name")
    private String name;
    @SerializedName(value = "family")
    private String family;
    @SerializedName(value = "s_family")
    private String sFamily;
    @SerializedName(value = "agency")
    private String agency;
    @SerializedName(value = "variant")
    private String variant;
    @SerializedName(value = "alias")
    private String alias;
    @SerializedName(value = "min_stage")
    private Integer minStage;
    @SerializedName(value = "max_stage")
    private Integer maxStage;
    @SerializedName(value = "length")
    private String length;
    @SerializedName(value = "diameter")
    private String diameter;
    @SerializedName(value = "launch_mass")
    private String launchMass;
    @SerializedName(value = "leo_capacity")
    private String leoCapacity;
    @SerializedName(value = "gto_capacity")
    private String gtoCapacity;
    @SerializedName(value = "to_thrust")
    private String thrust;
    @SerializedName(value = "vehicle_class")
    private String vehicleClass;
    @SerializedName(value = "apogee")
    private String apogee;
    @SerializedName(value = "vehicle_range")
    private String range;
    @SerializedName(value = "image_url")
    private String imageURL;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfoURL() {
        return infoURL;
    }

    public void setInfoURL(String InfoURL) {
        this.infoURL = InfoURL;
    }

    public String getWikiURL() {
        return wikiURL;
    }

    public void setWikiURL(String WikiURL) {
        this.wikiURL = WikiURL;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getsFamily() {
        return sFamily;
    }

    public void setsFamily(String sFamily) {
        this.sFamily = sFamily;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getLVAlias() {
        return alias;
    }

    public void setLVAlias(String LVAlias) {
        this.alias = LVAlias;
    }

    public Integer getMinStage() {
        return minStage;
    }

    public void setMinStage(Integer MinStage) {
        this.minStage = MinStage;
    }

    public Integer getMaxStage() {
        return maxStage;
    }

    public void setMaxStage(Integer MaxStage) {
        this.maxStage = MaxStage;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String Length) {
        this.length = Length;
    }

    public String getDiameter() {
        return diameter;
    }

    public void setDiameter(String Diameter) {
        this.diameter = Diameter;
    }

    public String getLaunchMass() {
        return launchMass;
    }

    public void setLaunchMass(String LaunchMass) {
        this.launchMass = LaunchMass;
    }

    public String getLEOCapacity() {
        return leoCapacity;
    }

    public void setLEOCapacity(String LEOCapacity) {
        this.leoCapacity = LEOCapacity;
    }

    public String getGTOCapacity() {
        return gtoCapacity;
    }

    public void setGTOCapacity(String GTOCapacity) {
        this.gtoCapacity = GTOCapacity;
    }

    public String getTOThrust() {
        return thrust;
    }

    public void setTOThrust(String TOThrust) {
        this.thrust = TOThrust;
    }

    public String getClass_() {
        return vehicleClass;
    }

    public void setClass_(String Class) {
        this.vehicleClass = Class;
    }

    public String getApogee() {
        return apogee;
    }

    public void setDescription(String Description) {
        this.description = Description;
    }

    public String getDescription() {
        return description;
    }

    public void setApogee(String Apogee) {
        this.apogee = Apogee;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String Range) {
        this.range = Range;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String ImageURL) {
        this.imageURL = ImageURL;
    }


    public String getVehicleClass() {
        return vehicleClass;
    }

    public void setVehicleClass(String vehicleClass) {
        this.vehicleClass = vehicleClass;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
