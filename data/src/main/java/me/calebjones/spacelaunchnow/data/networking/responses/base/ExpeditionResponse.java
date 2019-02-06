package me.calebjones.spacelaunchnow.data.networking.responses.base;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import me.calebjones.spacelaunchnow.data.models.main.spacecraft.Spacecraft;
import me.calebjones.spacelaunchnow.data.models.main.spacestation.Expedition;

public class ExpeditionResponse extends BaseResponse {
    @SerializedName("results")
    private List<Expedition> expeditions;

    public List<Expedition> getExpeditions() {
        return expeditions;
    }
}
