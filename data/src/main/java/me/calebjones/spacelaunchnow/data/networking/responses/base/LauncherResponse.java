package me.calebjones.spacelaunchnow.data.networking.responses.base;

import com.google.gson.annotations.SerializedName;

import me.calebjones.spacelaunchnow.data.models.spacelaunchnow.SLNAgency;

public class LauncherResponse {
    @SerializedName(value="results")
    private SLNAgency[] launchers;

    public SLNAgency[] getLaunchers() {
        return launchers;
    }
}
