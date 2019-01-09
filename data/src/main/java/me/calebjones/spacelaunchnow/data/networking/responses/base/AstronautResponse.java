package me.calebjones.spacelaunchnow.data.networking.responses.base;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.models.main.astronaut.Astronaut;

public class AstronautResponse extends BaseResponse {
    @SerializedName("results")
    private List<Astronaut> astronauts;

    public List<Astronaut> getAstronauts() {
        return astronauts;
    }
}
