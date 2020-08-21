package me.calebjones.spacelaunchnow.data.models.main.starship;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;

public class Notice extends RealmObject {

    @SerializedName("type")
    @Expose
    public NoticeType type;
    @SerializedName("date")
    @Expose
    public Date date;
    @SerializedName("url")
    @Expose
    public String url;

    public NoticeType getType() {
        return type;
    }

    public void setType(NoticeType type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
