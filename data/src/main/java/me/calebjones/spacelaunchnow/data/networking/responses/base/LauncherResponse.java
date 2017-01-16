package me.calebjones.spacelaunchnow.data.networking.responses.base;

import me.calebjones.spacelaunchnow.data.models.natives.Launcher;

public class LauncherResponse {
    private Launcher[] items;

    public Launcher[] getItem() {
        return items;
    }
}
