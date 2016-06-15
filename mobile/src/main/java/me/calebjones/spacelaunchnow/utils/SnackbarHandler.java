package me.calebjones.spacelaunchnow.utils;


import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.crashlytics.android.Crashlytics;

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

    // Display Info snackbar from a message.
    public static void showInfoSnackbar(Context context,  View view, String msg){
        Snackbar
                .make(view, "Error - " + parseErrorMessage(context, msg), Snackbar.LENGTH_SHORT)
                .show();
    }

    // Display Info snackbar from a intent.
    public static void showInfoSnackbar(Context context,  View view, Intent intent){
        Snackbar
                .make(view, "Error - " + parseErrorMessage(context, intent), Snackbar.LENGTH_SHORT)
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

    //TODO build a list of common errors.
    private static String checkError(String msg) {
        Crashlytics.logException(new Throwable(msg));
        return msg;
    }
}
