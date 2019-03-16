package me.calebjones.spacelaunchnow.data.networking.responses.base;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import me.calebjones.spacelaunchnow.data.models.main.spacecraft.Spacecraft;
import me.calebjones.spacelaunchnow.data.models.main.spacestation.Spacestation;

public class SpacecraftResponse extends BaseResponse {
    @SerializedName("results")
    private List<Spacecraft> spacecraft;

    public List<Spacecraft> getSpacecraft() {
        return spacecraft;
    }
}
