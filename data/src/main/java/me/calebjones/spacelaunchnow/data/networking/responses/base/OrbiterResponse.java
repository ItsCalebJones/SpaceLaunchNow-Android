package me.calebjones.spacelaunchnow.data.networking.responses.base;

import me.calebjones.spacelaunchnow.data.models.natives.Orbiter;

public class OrbiterResponse {
    private Orbiter[] items;

    public Orbiter[] getItem() {
        return items;
    }
}
