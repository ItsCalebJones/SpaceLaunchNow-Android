package me.calebjones.spacelaunchnow.content.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RocketDetails implements Serializable {

    private String LVName;
    private String LVFamily;
    private String LVSFamily;
    private String LVManufacturer;
    private String LVVariant;
    private String LVAlias;
    private String Description;
    private Integer MinStage;
    private Integer MaxStage;
    private String Length;
    private String Diameter;
    private String LaunchMass;
    private String LEOCapacity;
    private String GTOCapacity;
    private String TOThrust;
    private String Class;
    private String Apogee;
    private String ImageURL;
    private String Range;
    private String InfoURL;
    private String WikiURL;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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

    public String getLVName() {
        return LVName;
    }

    public void setLVName(String LVName) {
        this.LVName = LVName;
    }

    public String getLVFamily() {
        return LVFamily;
    }

    public void setLVFamily(String LVFamily) {
        this.LVFamily = LVFamily;
    }

    public String getLVSFamily() {
        return LVSFamily;
    }

    public void setLVSFamily(String LVSFamily) {
        this.LVSFamily = LVSFamily;
    }

    public String getLVManufacturer() {
        return LVManufacturer;
    }

    public void setLVManufacturer(String LVManufacturer) {
        this.LVManufacturer = LVManufacturer;
    }

    public String getLVVariant() {
        return LVVariant;
    }

    public void setLVVariant(String LVVariant) {
        this.LVVariant = LVVariant;
    }

    public String getLVAlias() {
        return LVAlias;
    }

    public void setLVAlias(String LVAlias) {
        this.LVAlias = LVAlias;
    }

    public Integer getMinStage() {
        return MinStage;
    }

    public void setMinStage(Integer MinStage) {
        this.MinStage = MinStage;
    }

    public Integer getMaxStage() {
        return MaxStage;
    }

    public void setMaxStage(Integer MaxStage) {
        this.MaxStage = MaxStage;
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
        return LaunchMass;
    }

    public void setLaunchMass(String LaunchMass) {
        this.LaunchMass = LaunchMass;
    }

    public String getLEOCapacity() {
        return LEOCapacity;
    }

    public void setLEOCapacity(String LEOCapacity) {
        this.LEOCapacity = LEOCapacity;
    }

    public String getGTOCapacity() {
        return GTOCapacity;
    }

    public void setGTOCapacity(String GTOCapacity) {
        this.GTOCapacity = GTOCapacity;
    }

    public String getTOThrust() {
        return TOThrust;
    }

    public void setTOThrust(String TOThrust) {
        this.TOThrust = TOThrust;
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

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}