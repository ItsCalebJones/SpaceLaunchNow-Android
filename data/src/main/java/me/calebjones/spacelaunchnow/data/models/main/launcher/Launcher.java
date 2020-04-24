package me.calebjones.spacelaunchnow.data.models.main.launcher;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Launcher extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("flights")
    @Expose
    public Integer previousFlights;
    @SerializedName("flight_proven")
    @Expose
    public Boolean flightProven;
    @SerializedName("serial_number")
    @Expose
    public String serialNumber;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("details")
    @Expose
    public String details;
    @SerializedName("image_url")
    @Expose
    public String imageUrl;

    public Integer getPreviousFlights() {
        return previousFlights;
    }

    public void setPreviousFlights(Integer previousFlights) {
        this.previousFlights = previousFlights;
    }

    public Boolean getFlightProven() {
        return flightProven;
    }

    public void setFlightProven(Boolean flightProven) {
        this.flightProven = flightProven;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
}
