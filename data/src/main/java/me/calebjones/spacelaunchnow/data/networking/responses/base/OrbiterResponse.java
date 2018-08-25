package me.calebjones.spacelaunchnow.data.networking.responses.base;

import com.google.gson.annotations.SerializedName;

import me.calebjones.spacelaunchnow.data.models.main.Orbiter;

public class OrbiterResponse extends BaseResponse {
    @SerializedName(value="results")
    private Orbiter[] orbiters;

    public Orbiter[] getOrbiters() {
        return orbiters;
    }
}
