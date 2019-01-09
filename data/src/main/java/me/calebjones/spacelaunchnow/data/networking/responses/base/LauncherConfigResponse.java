package me.calebjones.spacelaunchnow.data.networking.responses.base;

import com.google.gson.annotations.SerializedName;

import me.calebjones.spacelaunchnow.data.models.main.launcher.LauncherConfig;

public class LauncherConfigResponse extends BaseResponse {

    @SerializedName(value="results")
    private LauncherConfig[] vehicles;

    public LauncherConfig[] getVehicles() {
        return vehicles;
    }
}
