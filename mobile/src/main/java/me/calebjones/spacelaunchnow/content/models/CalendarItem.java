package me.calebjones.spacelaunchnow.content.models;

public class CalendarItem {
    String name, location, description;
    long start, end, id;


    public CalendarItem(Launch launch, long id) {
        this.name = launch.getName();
        this.location = launch.getName();
        this.description = launch.getName();
        this.start = launch.getStartDate().getTime();
        this.end = launch.getEndDate().getTime();
        this.id = id;
    }

    public void updateItem(Launch launch, long id){
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
}
