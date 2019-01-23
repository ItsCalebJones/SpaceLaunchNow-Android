package me.calebjones.spacelaunchnow.spacestation.detail.expeditions;


public interface ListItem {
    int TYPE_DOCKED_VEHICLE = 1;
    int TYPE_DOCKING_EVENT = 2;
    int TYPE_ACTIVE_EXPEDITION = 3;

    int getListItemType();
}

