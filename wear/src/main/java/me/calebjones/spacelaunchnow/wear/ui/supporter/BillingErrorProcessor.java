package me.calebjones.spacelaunchnow.wear.ui.supporter;

import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE;
import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_DEVELOPER_ERROR;
import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_ERROR;
import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED;
import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED;
import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE;
import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_OK;
import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE;
import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_USER_CANCELED;

public class BillingErrorProcessor {
    public static String getResponseCodeDescription(int errorCode){
        switch (errorCode){
            case BILLING_RESPONSE_RESULT_OK:
                return "Success.";
            case BILLING_RESPONSE_RESULT_USER_CANCELED:
                return "User pressed back or canceled a dialog.";
            case BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE:
                return "Network connection is down";
            case BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE:
                return "Billing API version is not supported for the type requested.";
            case BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE:
                return "Requested product is not available for purchase.";
            case BILLING_RESPONSE_RESULT_DEVELOPER_ERROR:
                return "Unknown Developer Error.";
            case BILLING_RESPONSE_RESULT_ERROR:
                return "Fatal error during the API action.";
            case BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED:
                return "Failure to purchase since item is already owned.";
            case BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED:
                return "Failure to consume since item is not owned.";
            default:
                return "Unknown error encountered.";
        }
    }
}
