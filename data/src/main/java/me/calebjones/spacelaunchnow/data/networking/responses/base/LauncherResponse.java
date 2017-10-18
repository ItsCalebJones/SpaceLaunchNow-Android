package me.calebjones.spacelaunchnow.data.networking.responses.base;

import me.calebjones.spacelaunchnow.data.models.Launcher;

public class LauncherResponse {
    private Launcher[] results;

    public Launcher[] getItem() {
        return results;
    }
}
