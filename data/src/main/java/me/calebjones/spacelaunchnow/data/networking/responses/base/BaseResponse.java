package me.calebjones.spacelaunchnow.data.networking.responses.base;

public class BaseResponse {

    private int count;
    private String next;
    private String previous;

    public String getPrevious() {
        return previous;
    }

    public String getNext() {
        return next;
    }

    public int getCount() {
        return count;
    }
}
