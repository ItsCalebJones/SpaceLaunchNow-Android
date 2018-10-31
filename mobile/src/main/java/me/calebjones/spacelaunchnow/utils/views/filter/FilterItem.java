package me.calebjones.spacelaunchnow.utils.views.filter;

public class FilterItem {

    public String name;
    public boolean enabled;
    public String preference_key;

    public FilterItem(String name, boolean enabled, String preference_key) {
        this.name = name;
        this.enabled = enabled;
        this.preference_key = preference_key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPreference_key() {
        return preference_key;
    }

    public void setPreference_key(String preference_key) {
        this.preference_key = preference_key;
    }
}
