package me.calebjones.spacelaunchnow.content.models;

public class GridItem {
    final String name, agency, imageURL;

    public GridItem(String name, String agency, String imageURL) {
        this.name = name;
        this.agency = agency;
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public String getAgency() {
        return agency;
    }

    public String getImageURL(){return imageURL;}
}
