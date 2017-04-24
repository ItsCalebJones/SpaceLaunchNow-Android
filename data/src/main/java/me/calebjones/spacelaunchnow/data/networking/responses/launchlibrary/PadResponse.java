package me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary;

import me.calebjones.spacelaunchnow.data.models.Pad;

public class PadResponse extends BaseResponse{
    private Pad[] pads;

    public Pad[] getPads() {
        return pads;
    }
}
