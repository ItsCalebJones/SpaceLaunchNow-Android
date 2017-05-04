package me.calebjones.spacelaunchnow.data.networking.error;

public class SpaceLaunchNowError {

    private int statusCode;
    private String message;

    public SpaceLaunchNowError() {
    }

    public int status() {
        return statusCode;
    }

    public String message() {
        return message;
    }
}
