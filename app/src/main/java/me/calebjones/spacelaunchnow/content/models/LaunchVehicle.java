package me.calebjones.spacelaunchnow.content.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class LaunchVehicle implements Serializable {

    private String LVName;
    private String LVFamily;
    private String LVSFamily;
    private String LVManufacturer;
    private String LVVariant;
    private String LVAlias;
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
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The LVName
     */
    public String getLVName() {
        return LVName;
    }

    /**
     *
     * @param LVName
     * The LV_Name
     */
    public void setLVName(String LVName) {
        this.LVName = LVName;
    }

    /**
     *
     * @return
     * The LVFamily
     */
    public String getLVFamily() {
        return LVFamily;
    }

    /**
     *
     * @param LVFamily
     * The LV_Family
     */
    public void setLVFamily(String LVFamily) {
        this.LVFamily = LVFamily;
    }

    /**
     *
     * @return
     * The LVSFamily
     */
    public String getLVSFamily() {
        return LVSFamily;
    }

    /**
     *
     * @param LVSFamily
     * The LV_SFamily
     */
    public void setLVSFamily(String LVSFamily) {
        this.LVSFamily = LVSFamily;
    }

    /**
     *
     * @return
     * The LVManufacturer
     */
    public String getLVManufacturer() {
        return LVManufacturer;
    }

    /**
     *
     * @param LVManufacturer
     * The LV_Manufacturer
     */
    public void setLVManufacturer(String LVManufacturer) {
        this.LVManufacturer = LVManufacturer;
    }

    /**
     *
     * @return
     * The LVVariant
     */
    public String getLVVariant() {
        return LVVariant;
    }

    /**
     *
     * @param LVVariant
     * The LV_Variant
     */
    public void setLVVariant(String LVVariant) {
        this.LVVariant = LVVariant;
    }

    /**
     *
     * @return
     * The LVAlias
     */
    public String getLVAlias() {
        return LVAlias;
    }

    /**
     *
     * @param LVAlias
     * The LV_Alias
     */
    public void setLVAlias(String LVAlias) {
        this.LVAlias = LVAlias;
    }

    /**
     *
     * @return
     * The MinStage
     */
    public Integer getMinStage() {
        return MinStage;
    }

    /**
     *
     * @param MinStage
     * The Min_Stage
     */
    public void setMinStage(Integer MinStage) {
        this.MinStage = MinStage;
    }

    /**
     *
     * @return
     * The MaxStage
     */
    public Integer getMaxStage() {
        return MaxStage;
    }

    /**
     *
     * @param MaxStage
     * The Max_Stage
     */
    public void setMaxStage(Integer MaxStage) {
        this.MaxStage = MaxStage;
    }

    /**
     *
     * @return
     * The Length
     */
    public String getLength() {
        return Length;
    }

    /**
     *
     * @param Length
     * The Length
     */
    public void setLength(String Length) {
        this.Length = Length;
    }

    /**
     *
     * @return
     * The Diameter
     */
    public String getDiameter() {
        return Diameter;
    }

    /**
     *
     * @param Diameter
     * The Diameter
     */
    public void setDiameter(String Diameter) {
        this.Diameter = Diameter;
    }

    /**
     *
     * @return
     * The LaunchMass
     */
    public String getLaunchMass() {
        return LaunchMass;
    }

    /**
     *
     * @param LaunchMass
     * The Launch_Mass
     */
    public void setLaunchMass(String LaunchMass) {
        this.LaunchMass = LaunchMass;
    }

    /**
     *
     * @return
     * The LEOCapacity
     */
    public String getLEOCapacity() {
        return LEOCapacity;
    }

    /**
     *
     * @param LEOCapacity
     * The LEO_Capacity
     */
    public void setLEOCapacity(String LEOCapacity) {
        this.LEOCapacity = LEOCapacity;
    }

    /**
     *
     * @return
     * The GTOCapacity
     */
    public String getGTOCapacity() {
        return GTOCapacity;
    }

    /**
     *
     * @param GTOCapacity
     * The GTO_Capacity
     */
    public void setGTOCapacity(String GTOCapacity) {
        this.GTOCapacity = GTOCapacity;
    }

    /**
     *
     * @return
     * The TOThrust
     */
    public String getTOThrust() {
        return TOThrust;
    }

    /**
     *
     * @param TOThrust
     * The TO_Thrust
     */
    public void setTOThrust(String TOThrust) {
        this.TOThrust = TOThrust;
    }

    /**
     *
     * @return
     * The Class
     */
    public String getClass_() {
        return Class;
    }

    /**
     *
     * @param Class
     * The Class
     */
    public void setClass_(String Class) {
        this.Class = Class;
    }

    /**
     *
     * @return
     * The Apogee
     */
    public String getApogee() {
        return Apogee;
    }

    /**
     *
     * @param Apogee
     * The Apogee
     */
    public void setApogee(String Apogee) {
        this.Apogee = Apogee;
    }

    /**
     *
     * @return
     * The Range
     */
    public String getRange() {
        return Range;
    }

    /**
     *
     * @param Range
     * The Range
     */
    public void setRange(String Range) {
        this.Range = Range;
    }

    /**
     *
     * @return
     * The ImageURL
     */
    public String getImageURL() {
        return ImageURL;
    }

    /**
     *
     * @param ImageURL
     * The ImageURL
     */
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