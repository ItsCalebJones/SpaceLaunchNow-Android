package me.calebjones.spacelaunchnow.data.models;

import com.google.gson.annotations.SerializedName;

public class Orbiter {
    final String name;
    final String agency;
    @SerializedName("image_url")
    final String imageURL;
    @SerializedName("nation_url")
    final String nationURL;
    final String history;
    final String details;
    @SerializedName("wiki_link")
    final String wikiLink;

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
