package me.calebjones.spacelaunchnow.spacestation.detail.adapter;

import me.calebjones.spacelaunchnow.data.models.main.spacecraft.SpacecraftStage;
import me.calebjones.spacelaunchnow.data.models.main.spacestation.Expedition;

public class DockedVehicleItem implements ListItem {

    private SpacecraftStage spacecraftStage;

    public DockedVehicleItem(SpacecraftStage spacecraftStage){
        this.spacecraftStage = spacecraftStage;
    }

    public SpacecraftStage getDockedVehicle() {
        return spacecraftStage;
    }

    public void setExpedition(SpacecraftStage spacecraftStage) {
        this.spacecraftStage = spacecraftStage;
    }

    @Override
    public int getListItemType() {
        return ListItem.TYPE_DOCKED_VEHICLE;
    }
}
