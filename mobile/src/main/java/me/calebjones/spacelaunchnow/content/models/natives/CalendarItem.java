package me.calebjones.spacelaunchnow.content.models.natives;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import me.calebjones.spacelaunchnow.content.models.realm.LaunchRealm;

public class CalendarItem extends RealmObject {

    @PrimaryKey
    int launchID;
    String name;
    String location;
    String description;
    long start;
    long end;
    long id;

    public  CalendarItem(){

    }

    public CalendarItem(LaunchRealm launch, long id) {
        this.launchID = launch.getId();
        this.name = launch.getName();
        this.location = launch.getName();
        this.description = launch.getName();
        this.start = launch.getStartDate().getTime();
        this.end = launch.getEndDate().getTime();
        this.id = id;
    }


    public void updateItem(LaunchRealm launch, long id){
        this.name = launch.getName();
        this.location = launch.getName();
        this.description = launch.getName();
        this.start = launch.getStartDate().getTime();
        this.end = launch.getEndDate().getTime();
        this.id = id;
    }

    public long getID() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation(){
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLaunchID() {
        return launchID;
    }

    public void setLaunchID(int launchID) {
        this.launchID = launchID;
    }
}
