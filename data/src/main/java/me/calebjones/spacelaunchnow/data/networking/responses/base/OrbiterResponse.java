package me.calebjones.spacelaunchnow.data.networking.responses.base;

import me.calebjones.spacelaunchnow.data.models.Orbiter;

public class OrbiterResponse {
    private Orbiter[] results;

    public Orbiter[] getItem() {
        return results;
    }
}
