package me.calebjones.spacelaunchnow.data.models.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RocketDetailsRealm extends RealmObject {

    private String InfoURL;
    private String WikiURL;
    private String Description;

    @PrimaryKey
    private String name;

    private String LV_Name;
    private String LV_Family;
    private String LV_SFamily;
    private String LV_Manufacturer;
    private String LV_Variant;
    private String LV_Alias;
    private Integer Min_Stage;
    private Integer Max_Stage;
    private String Length;
    private String Diameter;
    private String Launch_Mass;
    private String LEO_Capacity;
    private String GTO_Capacity;
    private String TO_Thrust;
    private String Class;
    private String Apogee;
    private String Range;
    private String ImageURL;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfoURL() {
        return InfoURL;
    }

    public void setInfoURL(String InfoURL) {
        this.InfoURL = InfoURL;
    }

    public String getWikiURL() {
        return WikiURL;
    }

    public void setWikiURL(String WikiURL) {
        this.WikiURL = WikiURL;
    }

    public String getLV_Name() {
        return LV_Name;
    }

    public void setLV_Name(String LV_Name) {
        this.LV_Name = LV_Name;
    }

    public String getLV_Family() {
        return LV_Family;
    }

    public void setLV_Family(String LV_Family) {
        this.LV_Family = LV_Family;
    }

    public String getLV_SFamily() {
        return LV_SFamily;
    }

    public void setLV_SFamily(String LV_SFamily) {
        this.LV_SFamily = LV_SFamily;
    }

    public String getLV_Manufacturer() {
        return LV_Manufacturer;
    }

    public void setLV_Manufacturer(String LV_Manufacturer) {
        this.LV_Manufacturer = LV_Manufacturer;
    }

    public String getLV_Variant() {
        return LV_Variant;
    }

    public void setLV_Variant(String LV_Variant) {
        this.LV_Variant = LV_Variant;
    }

    public String getLVAlias() {
        return LV_Alias;
    }

    public void setLVAlias(String LVAlias) {
        this.LV_Alias = LVAlias;
    }

    public Integer getMinStage() {
        return Min_Stage;
    }

    public void setMinStage(Integer MinStage) {
        this.Min_Stage = MinStage;
    }

    public Integer getMax_Stage() {
        return Max_Stage;
    }

    public void setMax_Stage(Integer MaxStage) {
        this.Max_Stage = MaxStage;
    }

    public String getLength() {
        return Length;
    }

    public void setLength(String Length) {
        this.Length = Length;
    }

    public String getDiameter() {
        return Diameter;
    }

    public void setDiameter(String Diameter) {
        this.Diameter = Diameter;
    }

    public String getLaunchMass() {
        return Launch_Mass;
    }

    public void setLaunchMass(String LaunchMass) {
        this.Launch_Mass = LaunchMass;
    }

    public String getLEOCapacity() {
        return LEO_Capacity;
    }

    public void setLEOCapacity(String LEOCapacity) {
        this.LEO_Capacity = LEOCapacity;
    }

    public String getGTOCapacity() {
        return GTO_Capacity;
    }

    public void setGTOCapacity(String GTOCapacity) {
        this.GTO_Capacity = GTOCapacity;
    }

    public String getTOThrust() {
        return TO_Thrust;
    }

    public void setTOThrust(String TOThrust) {
        this.TO_Thrust = TOThrust;
    }

    public String getClass_() {
        return Class;
    }

    public void setClass_(String Class) {
        this.Class = Class;
    }

    public String getApogee() {
        return Apogee;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getDescription() {
        return Description;
    }

    public void setApogee(String Apogee) {
        this.Apogee = Apogee;
    }

    public String getRange() {
        return Range;
    }

    public void setRange(String Range) {
        this.Range = Range;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String ImageURL) {
        this.ImageURL = ImageURL;
    }



}