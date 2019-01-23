package me.calebjones.spacelaunchnow.spacestation.detail.expeditions;

import me.calebjones.spacelaunchnow.data.models.main.spacestation.Expedition;

public class ExpeditionItem extends Expedition implements ListItem {

    @Override
    public int getListItemType() {
        return ListItem.TYPE_ACTIVE_EXPEDITION;
    }
}
