package me.calebjones.spacelaunchnow.common.ui.views;


import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import me.calebjones.spacelaunchnow.common.utils.Connectivity;

public class SnackbarHandler {

    // Display error Snackbar from a string.
    public static void showErrorSnackbar(Context context,  View view, String msg){
        Snackbar
                .make(view, "Error - " + parseErrorMessage(context, msg), Snackbar.LENGTH_LONG)
                .show();
    }

    // Display error Snackbar from a intent.
    public static void showErrorSnackbar(Context context,  View view, Intent intent){
        Snackbar
                .make(view, "Error - " + parseErrorMessage(context, intent), Snackbar.LENGTH_LONG)
                .show();
    }

    // Display error Snackbar from a intent.
    public static void showErrorSnackbar(Context context,  View view, Throwable error){
        Snackbar
                .make(view, "Error - " + parseErrorMessage(context, error), Snackbar.LENGTH_LONG)
                .show();
    }

    // Display Info snackbar from a getMessage.
    public static void showInfoSnackbar(Context context,  View view, String msg){
        Snackbar
                .make(view, msg, Snackbar.LENGTH_SHORT)
                .show();
    }

    //
    public static String parseErrorMessage(Context context, String msg){
        String error;
        if (Connectivity.isConnected(context)) {
            error = checkError(msg);
        } else {
            error = "Connection timed out, check network connectivity?";
        }
        return error;
    }

    //
    public static String parseErrorMessage(Context context, Intent intent){
        String error;
        if (Connectivity.isConnected(context)) {
            error = checkError(intent.getStringExtra("error"));
        } else {
            error = "Connection timed out, check network connectivity?";
        }
        return error;
    }

    private static String parseErrorMessage(Context context, Throwable error) {
        String errorMsg;
        if (Connectivity.isConnected(context)) {
            errorMsg = checkError(error);
        } else {
            errorMsg = "Connection timed out, check network connectivity?";
        }
        return errorMsg;
    }

    //TODO build a list of common errors.
    private static String checkError(String msg) {
        return msg;
    }

    private static String checkError(Throwable error) {
        return error.getLocalizedMessage();
    }
}
