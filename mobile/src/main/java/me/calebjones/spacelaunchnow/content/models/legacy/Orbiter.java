package me.calebjones.spacelaunchnow.content.models.legacy;

public class Orbiter {
    final String name, agency, imageURL, nationURL, history, details, wikiLink;

    public Orbiter(String name, String agency, String imageURL,
                   String nationURL, String history, String details, String wikiLink) {
        this.name = name;
        this.agency = agency;
        this.imageURL = imageURL;
        this.nationURL = nationURL;
        this.history = history;
        this.details = details;
        this.wikiLink = wikiLink;
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

    public String getNationURL() { return nationURL;}

    public String getHistory() { return history;}

    public String getDetails() { return details;}

    public String getWikiLink() { return wikiLink;}
}
