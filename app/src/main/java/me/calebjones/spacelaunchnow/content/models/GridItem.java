package me.calebjones.spacelaunchnow.content.models;

public class GridItem {
    final String name, agency;
    final int drawableId;

    public GridItem(String name, String agency, int drawableId) {
        this.name = name;
        this.agency = agency;
        this.drawableId = drawableId;
    }

    public String getName() {
        return name;
    }

    public String getAgency() {
        return agency;
    }

    public int getDrawableId() {
        return drawableId;
    }
}
