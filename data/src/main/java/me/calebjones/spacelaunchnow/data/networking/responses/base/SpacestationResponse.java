package me.calebjones.spacelaunchnow.data.networking.responses.base;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import me.calebjones.spacelaunchnow.data.models.main.astronaut.Astronaut;
import me.calebjones.spacelaunchnow.data.models.main.spacestation.Spacestation;

public class SpacestationResponse extends BaseResponse {
    @SerializedName("results")
    private List<Spacestation> spacestations;

    public List<Spacestation> getSpacestations() {
        return spacestations;
    }
}
