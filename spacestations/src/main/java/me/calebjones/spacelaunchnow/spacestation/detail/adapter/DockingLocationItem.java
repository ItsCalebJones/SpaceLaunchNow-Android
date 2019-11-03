package me.calebjones.spacelaunchnow.spacestation.detail.adapter;

import me.calebjones.spacelaunchnow.data.models.main.spacecraft.SpacecraftStage;
import me.calebjones.spacelaunchnow.data.models.main.spacestation.DockingLocation;
import me.calebjones.spacelaunchnow.data.models.main.spacestation.Expedition;

public class DockingLocationItem implements ListItem {

    private DockingLocation dockingLocation;

    public DockingLocationItem(DockingLocation dockingLocation){
        this.dockingLocation = dockingLocation;
    }

    public DockingLocation getDockingLocation() {
        return dockingLocation;
    }

    @Override
    public int getListItemType() {
        return ListItem.TYPE_DOCKED_VEHICLE;
    }
}
