package me.calebjones.spacelaunchnow.data.networking.responses.base;

import com.google.gson.annotations.SerializedName;

import me.calebjones.spacelaunchnow.data.models.RocketDetails;

public class VehicleResponse extends BaseSLNResponse  {

    @SerializedName(value="results")
    private RocketDetails[] vehicles;

    public RocketDetails[] getVehicles() {
        return vehicles;
    }
}
