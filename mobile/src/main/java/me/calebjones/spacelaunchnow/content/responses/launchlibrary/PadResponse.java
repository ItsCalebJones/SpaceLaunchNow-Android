package me.calebjones.spacelaunchnow.content.responses.launchlibrary;

import me.calebjones.spacelaunchnow.content.models.realm.PadRealm;

public class PadResponse extends BaseResponse{
    private PadRealm[] pads;

    public PadRealm[] getPads() {
        return pads;
    }
}
