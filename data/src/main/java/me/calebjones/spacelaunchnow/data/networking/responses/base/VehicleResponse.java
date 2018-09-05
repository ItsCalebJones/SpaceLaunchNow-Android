package me.calebjones.spacelaunchnow.data.networking.responses.base;

import com.google.gson.annotations.SerializedName;

import me.calebjones.spacelaunchnow.data.models.main.LauncherConfig;

public class VehicleResponse extends BaseResponse {

    @SerializedName(value="results")
    private LauncherConfig[] vehicles;

    public LauncherConfig[] getVehicles() {
        return vehicles;
    }
}
