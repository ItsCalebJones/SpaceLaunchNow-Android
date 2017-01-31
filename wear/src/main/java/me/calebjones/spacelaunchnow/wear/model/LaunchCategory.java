package me.calebjones.spacelaunchnow.wear.model;

public class LaunchCategory {
    private String name;
    private int category;

    public LaunchCategory(
            String name,
            int category){

        this.name = name;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public int getCategory() {
        return category;
    }
}
