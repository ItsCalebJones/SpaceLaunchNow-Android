package me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary;

import me.calebjones.spacelaunchnow.data.models.realm.PadRealm;

public class PadResponse extends BaseResponse{
    private PadRealm[] pads;

    public PadRealm[] getPads() {
        return pads;
    }
}
