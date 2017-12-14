package me.calebjones.spacelaunchnow.data.networking.responses.base;

import com.google.gson.annotations.SerializedName;

import me.calebjones.spacelaunchnow.data.models.launchlibrary.Launcher;
import me.calebjones.spacelaunchnow.data.models.spacelaunchnow.Agency;

public class LauncherResponse {
    @SerializedName(value="results")
    private Agency[] launchers;

    public Agency[] getLaunchers() {
        return launchers;
    }
}
