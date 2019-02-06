package me.calebjones.spacelaunchnow.data.networking.responses.base;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import me.calebjones.spacelaunchnow.data.models.main.spacecraft.Spacecraft;
import me.calebjones.spacelaunchnow.data.models.main.spacecraft.SpacecraftStage;

public class SpacecraftStageResponse extends BaseResponse {
    @SerializedName("results")
    private List<SpacecraftStage> spacecraftStages;

    public List<SpacecraftStage> getSpacecraftStages() {
        return spacecraftStages;
    }
}
