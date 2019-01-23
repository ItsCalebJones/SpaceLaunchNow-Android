package me.calebjones.spacelaunchnow.spacestation.detail.expeditions;

import me.calebjones.spacelaunchnow.data.models.main.spacestation.DockingEvent;

public class DockedEventItem extends DockingEvent implements ListItem {

    @Override
    public int getListItemType() {
        return ListItem.TYPE_DOCKING_EVENT;
    }
}
