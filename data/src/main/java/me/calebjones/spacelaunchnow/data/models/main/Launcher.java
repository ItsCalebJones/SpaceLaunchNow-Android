package me.calebjones.spacelaunchnow.data.models.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Launcher extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("previous_flights")
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

    public Integer getPreviousFlights() {
        return previousFlights;
    }

    public void setPreviousFlights(Integer previousFlights) {
        this.previousFlights = previousFlights;
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

    public Boolean isFlightProven() {
        return flightProven;
    }

    public void setFlightProven(Boolean flightProven) {
        this.flightProven = flightProven;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
}
