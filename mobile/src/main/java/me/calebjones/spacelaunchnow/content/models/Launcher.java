package me.calebjones.spacelaunchnow.content.models;

public class Launcher {
    final String name, agency, imageURL, nationURL;

    public Launcher(String name, String agency, String imageURL, String nationURL) {
        this.name = name;
        this.agency = agency;
        this.imageURL = imageURL;
        this.nationURL = nationURL;
    }

    public String getName() {
        return name;
    }

    public String getAgency() {
        return agency;
    }

    public String getImageURL(){
        return imageURL;
    }

    public String getNationURL(){return nationURL;}

}
