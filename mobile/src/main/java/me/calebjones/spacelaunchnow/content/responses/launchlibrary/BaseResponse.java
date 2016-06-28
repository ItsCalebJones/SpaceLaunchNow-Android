package me.calebjones.spacelaunchnow.content.responses.launchlibrary;

public class BaseResponse {
    private int total;
    private int offset;
    private int count;

    public int getTotal() {
        return total;
    }

    public int getOffset() {
        return offset;
    }

    public int getCount(){
        return count;
    }
}
