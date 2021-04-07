package me.calebjones.spacelaunchnow.utils.analytics;

import android.content.Context;
import androidx.annotation.NonNull;

import com.google.firebase.analytics.FirebaseAnalytics;

import me.calebjones.spacelaunchnow.data.models.Products;
import timber.log.Timber;

public class Analytics {

    public static final String TYPE_PREVIOUS_LAUNCH = "Previous";
    public static final String TYPE_UPCOMING_LAUNCH = "Upcoming";

    private String mLastScreenName;
    private Context mContext;
    private static Analytics mInstance;
    private FirebaseAnalytics firebaseAnalytics;

    public Analytics(Context context){
        mContext = context;
        firebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
    }

    /**
     * Applications must call create to configure the DataClient singleton
     */
    public static void create(Context context) {
        mInstance = new Analytics(context);
    }

    /**
     * Singleton accessor
     * <p/>
     * Will throw an exception if {@link #create(Context context)} was never called
     *
     * @return the Analytics singleton
     */
    public static Analytics getInstance() {
        if (mInstance == null) {
            throw new AssertionError("Did you forget to call create() ?");
        }
        return mInstance;
    }

    public void sendNotificationEvent(String launchName, String content) {

    }

    //Logging methods.
    public void sendScreenView(@NonNull String screenName, @NonNull String state) {
        if (mLastScreenName != null) {
            if (!screenName.equals(mLastScreenName)) {
                mLastScreenName = screenName;

                Timber.v("UI Event: %s - %s", screenName, state);
            }
        }
    }

    public void sendScreenView(@NonNull String screenName) {

        if (!screenName.equals(mLastScreenName)) {
            mLastScreenName = screenName;
            Timber.v("UI Event: %s", screenName);
        }
    }

    public void notifyGoneBackground() {
    }

    public void sendLaunchDetailViewedEvent(@NonNull String launchName, @NonNull String launchType, @NonNull String launchID) {

        Timber.v("Launch Detail: %s- %s ID:%s viewed", launchType, launchName, launchID);
    }

    public void sendLaunchShared(@NonNull String launchName, @NonNull String launchID) {

        Timber.v("Share Event: %s - %s", launchName, launchID);
    }

    public void sendSearchEvent(@NonNull String query, @NonNull String launchType, @NonNull int resultCount) {

        Timber.v("Search Event %s: Query - %s Result - %s", launchType, query, resultCount);
    }

    public void sendLaunchMapClicked(@NonNull String launchName) {
        Timber.v("Map Click: %s", launchName);
    }

    public void sendNetworkEvent(@NonNull String eventName, @NonNull String eventURL, @NonNull boolean result, @NonNull String response) {
        Timber.v("Network Event: %s Success - %s URL - %s", eventName, result, eventURL);
    }

    public void sendNetworkEvent(@NonNull String eventName, @NonNull String eventURL, @NonNull boolean result) {

        Timber.v("Network Event: %s Success - %s URL - %s", eventName, result, eventURL);
    }

    public void sendNetworkEvent(@NonNull String eventName) {
        Timber.v("Network Event: %s Success", eventName);
    }

    public void sendButtonClickedWithURL(@NonNull String buttonName, @NonNull String eventURL) {
        Timber.v("Button Clicked: %s URL: %s", buttonName, eventURL);
    }

    public void sendButtonClickedWithURL(@NonNull String buttonName, @NonNull String launchName,@NonNull String eventURL) {
        Timber.v("Button Clicked: %s - %s URL: %s", buttonName, launchName, eventURL);
    }

    public void sendButtonClicked(@NonNull String buttonName, @NonNull String launchName) {

        Timber.v("Button Clicked: %s - %s", buttonName, launchName);
    }

    public void sendButtonClicked(@NonNull String buttonName) {

        Timber.v("Button Clicked: %s", buttonName);
    }

    public void sendWeatherEvent(@NonNull String launchName, @NonNull boolean result, String localizedMessage) {

        Timber.v("Weather Event: %s Result: %s", launchName, result);
    }

    public void sendPreferenceEvent(@NonNull String prefName, @NonNull boolean result) {

        Timber.v("Preference Event: %s Result: %s", prefName, result);
    }

    public void sendPreferenceEvent(@NonNull String prefName, @NonNull String result) {

        Timber.v("Preference Event: %s Result: %s", prefName, result);
    }

    public void sendPreferenceEvent(@NonNull String prefName) {

        Timber.v("Preference Event: %s changed.", prefName);
    }

    public void sendAddToCartEvent(@NonNull Products products, String sku){

        Timber.v("Add to Cart: %s %s - $%s SKU: %s", products.getName(), products.getType(), products.getPrice(), sku);
    }

    public void sendStartCheckout(@NonNull Products products) {

        Timber.v("StartCheckout: %s %s - $%s", products.getName(), products.getType(), products.getPrice());
    }

    public void sendPurchaseEvent(@NonNull Products products, String sku) {

        Timber.v("Purchased: %s %s - $%sSKU: %s", products.getName(), products.getType(), products.getPrice(), sku);
    }

}
