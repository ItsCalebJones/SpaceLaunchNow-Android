package me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary;

import com.google.gson.annotations.SerializedName;


public class NewLaunchResponse<T> extends BaseResponse {

    @SerializedName("launches")
    public T launches;
}
