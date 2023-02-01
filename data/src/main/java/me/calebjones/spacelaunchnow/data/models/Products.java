package me.calebjones.spacelaunchnow.data.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Products extends RealmObject {

    @PrimaryKey
    private String name;
    private String sku;
    private String type;
    private String description;
    private Integer price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
