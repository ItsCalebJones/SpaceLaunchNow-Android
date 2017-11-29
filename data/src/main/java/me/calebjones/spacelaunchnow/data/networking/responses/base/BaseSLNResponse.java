package me.calebjones.spacelaunchnow.data.networking.responses.base;

import com.google.gson.annotations.SerializedName;

public class BaseSLNResponse {

    @SerializedName(value="next")
    private String nextPage;

    @SerializedName(value="count")
    private int count;


    public String getNextPage() {
        return nextPage;
    }


    public int getCount(){
        return count;
    }
}
