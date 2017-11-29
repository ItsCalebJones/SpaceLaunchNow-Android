package me.calebjones.spacelaunchnow.data.networking.responses.base;

import com.google.gson.annotations.SerializedName;

import me.calebjones.spacelaunchnow.data.models.Launcher;

public class LauncherResponse extends BaseSLNResponse {
    @SerializedName(value="results")
    private Launcher[] launchers;

    public Launcher[] getLaunchers() {
        return launchers;
    }
}
