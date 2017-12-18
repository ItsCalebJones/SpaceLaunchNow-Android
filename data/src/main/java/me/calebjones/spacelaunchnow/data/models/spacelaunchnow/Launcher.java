package me.calebjones.spacelaunchnow.data.models.spacelaunchnow;

import com.google.gson.annotations.SerializedName;

public class Launcher {
    final String name;
    final String agency;

    @SerializedName("image_url")
    final String imageURL;

    @SerializedName("nation_url")
    final String nationURL;

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
