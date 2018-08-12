package me.calebjones.spacelaunchnow.data.networking.responses.base;

import com.google.gson.annotations.SerializedName;

import me.calebjones.spacelaunchnow.data.models.main.Launcher;

public class VehicleResponse extends BaseResponse {

    @SerializedName(value="results")
    private Launcher[] vehicles;

    public Launcher[] getVehicles() {
        return vehicles;
    }
}
