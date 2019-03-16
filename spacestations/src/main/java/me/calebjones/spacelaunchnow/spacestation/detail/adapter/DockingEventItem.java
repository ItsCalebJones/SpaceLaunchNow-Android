package me.calebjones.spacelaunchnow.spacestation.detail.adapter;

import me.calebjones.spacelaunchnow.data.models.main.spacestation.DockingEvent;

public class DockingEventItem implements ListItem {

    private DockingEvent dockingEvent;

    public DockingEventItem(DockingEvent dockingEvent){
        this.dockingEvent = dockingEvent;
    }

    public DockingEvent getDockingEvent() {
        return dockingEvent;
    }

    public void setExpedition(DockingEvent dockingEvent) {
        this.dockingEvent = dockingEvent;
    }

    @Override
    public int getListItemType() {
        return ListItem.TYPE_DOCKING_EVENT;
    }
}
