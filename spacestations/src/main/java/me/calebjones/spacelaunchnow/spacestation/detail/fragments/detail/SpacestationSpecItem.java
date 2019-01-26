package me.calebjones.spacelaunchnow.spacestation.detail.fragments.detail;

import android.graphics.drawable.Drawable;

public class SpacestationSpecItem {

    private Drawable drawable;
    private String title;
    private String value;

    public SpacestationSpecItem(String title, String value, Drawable drawable){
        this.title = title;
        this.value = value;
        this.drawable = drawable;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
}
