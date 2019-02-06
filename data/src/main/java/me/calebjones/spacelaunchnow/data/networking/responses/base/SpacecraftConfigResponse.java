package me.calebjones.spacelaunchnow.data.networking.responses.base;

import com.google.gson.annotations.SerializedName;

import me.calebjones.spacelaunchnow.data.models.main.spacecraft.SpacecraftConfig;

public class SpacecraftConfigResponse extends BaseResponse {
    @SerializedName(value="results")
    private SpacecraftConfig[] orbiters;

    public SpacecraftConfig[] getOrbiters() {
        return orbiters;
    }
}
