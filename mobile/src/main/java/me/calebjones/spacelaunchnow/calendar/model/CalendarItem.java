package me.calebjones.spacelaunchnow.calendar.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CalendarItem extends RealmObject{

    @PrimaryKey
    private Integer id;
    private String accountName;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
