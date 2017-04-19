package me.calebjones.spacelaunchnow.data.models;

import me.calebjones.spacelaunchnow.data.networking.error.LibraryError;
import me.calebjones.spacelaunchnow.data.networking.error.SpaceLaunchNowError;
import retrofit2.Call;

/**
 * Created by cjones on 4/19/17.
 */

public class Result {
    private boolean successful;
    private String action;
    private String errorMessage;
    private String requestURL;

    public Result(String action, boolean successful, Call call) {
        this.successful = successful;
        this.requestURL = call.request().url().toString();
        this.action = action;
    }

    public Result(String action, boolean successful, Call call, String errorMessage) {
        this.errorMessage = errorMessage;
        this.successful = successful;
        this.requestURL = call.request().url().toString();
        this.action = action;
    }

    public Result(String action, boolean successful, Call call, LibraryError error) {
        this.errorMessage = error.getStatus() + " " + error.getMessage();
        this.successful = successful;
        this.requestURL = call.request().url().toString();
        this.action = action;
    }

    public Result(String action, boolean successful, Call call, SpaceLaunchNowError error) {
        this.errorMessage = error.status() + " - " + error.message();
        this.successful = successful;
        this.requestURL = call.request().url().toString();
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public String getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }
}
