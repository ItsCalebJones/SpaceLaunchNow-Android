package me.calebjones.spacelaunchnow.data.networking.responses.base;

import com.google.gson.annotations.SerializedName;

import me.calebjones.spacelaunchnow.data.models.RocketDetail;

public class VehicleResponse {

    @SerializedName(value="results")
    private RocketDetail[] vehicles;

    public RocketDetail[] getVehicles() {
        return vehicles;
    }
}
