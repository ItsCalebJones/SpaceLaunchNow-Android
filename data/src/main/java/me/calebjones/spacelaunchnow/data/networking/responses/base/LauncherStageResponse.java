package me.calebjones.spacelaunchnow.data.networking.responses.base;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import me.calebjones.spacelaunchnow.data.models.main.launcher.LauncherStage;
import me.calebjones.spacelaunchnow.data.models.main.spacecraft.SpacecraftStage;

public class LauncherStageResponse extends BaseResponse {
    @SerializedName("results")
    private List<LauncherStage> launcherStages;

    public List<LauncherStage> getLauncherStages() {
        return launcherStages;
    }
}
