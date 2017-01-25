package me.calebjones.spacelaunchnow.data.models.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LaunchNotification extends RealmObject {

    @PrimaryKey
    private Integer id;
    private boolean isNotifiedDay = false;
    private boolean isNotifiedHour = false;
    private boolean isNotifiedTenMinute = false;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isNotifiedDay() {
        return isNotifiedDay;
    }

    public void setNotifiedDay(boolean notifiedDay) {
        isNotifiedDay = notifiedDay;
    }

    public boolean isNotifiedHour() {
        return isNotifiedHour;
    }

    public void setNotifiedHour(boolean notifiedHour) {
        isNotifiedHour = notifiedHour;
    }

    public boolean isNotifiedTenMinute() {
        return isNotifiedTenMinute;
    }

    public void setNotifiedTenMinute(boolean notifiedTenMinute) {
        isNotifiedTenMinute = notifiedTenMinute;
    }

    public void resetNotifiers(){
        isNotifiedDay = false;
        isNotifiedHour = false;
        isNotifiedTenMinute = false;
    }
}
