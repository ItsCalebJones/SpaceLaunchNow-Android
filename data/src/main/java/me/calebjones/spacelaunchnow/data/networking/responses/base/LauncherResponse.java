package me.calebjones.spacelaunchnow.data.networking.responses.base;

import com.google.gson.annotations.SerializedName;

import me.calebjones.spacelaunchnow.data.models.spacelaunchnow.LauncherAgency;

public class LauncherResponse {
    @SerializedName(value="results")
    private LauncherAgency[] launchers;

    public LauncherAgency[] getLaunchers() {
        return launchers;
    }
}
