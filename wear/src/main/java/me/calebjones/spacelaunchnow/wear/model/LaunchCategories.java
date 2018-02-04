package me.calebjones.spacelaunchnow.wear.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import me.calebjones.spacelaunchnow.wear.R;
import static me.calebjones.spacelaunchnow.wear.model.Constants.*;


public enum LaunchCategories {
    ALL("ALL", AGENCY_ALL, R.drawable.ic_select_all),
    SPACEX("SpaceX", AGENCY_SPACEX, R.drawable.ic_spacex),
    ROSCOSMOS("ROSCOSMOS", AGENCY_ROSCOSMOS, R.drawable.ic_roscosmos),
    ULA("ULA", AGENCY_ULA, R.drawable.ic_ula),
    NASA("NASA", AGENCY_NASA, R.drawable.ic_nasa),
    CNSA("CNSA", AGENCY_CNSA, R.drawable.ic_cnsa);

    private String name;
    private Integer category;
    private int icon;

    LaunchCategories(String name, int category, int icon) {
        this.name = name;
        this.category = category;
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }

    public int getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    private static final Map<Integer, String> mMap = Collections.unmodifiableMap(initializeMapping());

    private static Map<Integer, String> initializeMapping() {
        Map<Integer, String> mMap = new HashMap<>();
        for (LaunchCategories s : LaunchCategories.values()) {
            mMap.put(s.getCategory(), s.getName());
        }
        return mMap;
    }

    public static String findByKey(int key) {
        if (mMap.containsKey(key)) {
            return mMap.get(key);
        }
        return null;
    }
}
