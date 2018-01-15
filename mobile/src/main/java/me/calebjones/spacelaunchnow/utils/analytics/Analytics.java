package me.calebjones.spacelaunchnow.utils.analytics;

import android.content.Context;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;

import com.crashlytics.android.answers.AddToCartEvent;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.PurchaseEvent;
import com.crashlytics.android.answers.SearchEvent;
import com.crashlytics.android.answers.ShareEvent;
import com.crashlytics.android.answers.StartCheckoutEvent;

import java.math.BigDecimal;
import java.util.Currency;

import me.calebjones.spacelaunchnow.data.models.Products;
import me.calebjones.spacelaunchnow.data.models.launchlibrary.Launch;
import timber.log.Timber;

public class Analytics {

    public static final String TYPE_PREVIOUS_LAUNCH = "Previous";
    public static final String TYPE_UPCOMING_LAUNCH = "Upcoming";

    private String mLastScreenName;

    public static Analytics from(Context context){
        return ((Provider) context.getApplicationContext()).getAnalytics();
    }

    public static Analytics from(Fragment fragment) {
        return from(fragment.getActivity());
    }

    public static Analytics from(PreferenceFragment fragment) {
        return from(fragment.getActivity());
    }

    public void sendNotificationEvent(String launchName, String content) {
        Answers.getInstance().logCustom(new NotificationEvent().putLaunchName(launchName).putContent(content));
    }

    public interface Provider {
        Analytics getAnalytics();
    }

    //Logging methods.
    public void sendScreenView(@NonNull String screenName, @NonNull String state) {

        if (!screenName.equals(mLastScreenName)) {
            mLastScreenName = screenName;
            Answers.getInstance().logCustom(new UIEvent(screenName)
                                                    .putState(state));
            Timber.v("UI Event: %s - %s", screenName, state);
        }
    }

    public void sendScreenView(@NonNull String screenName) {

        if (!screenName.equals(mLastScreenName)) {
            mLastScreenName = screenName;
            Answers.getInstance().logCustom(new UIEvent(screenName));
            Timber.v("UI Event: %s", screenName);
        }
    }

    public void notifyGoneBackground() {
        Answers.getInstance().logCustom(new UIEvent("In background"));
    }

    public void sendLaunchDetailViewedEvent(@NonNull String launchName, @NonNull String launchType, @NonNull String launchID) {
        Answers.getInstance().logCustom(new LaunchDetailViewed()
                                                .putLaunchName(launchName + " " + launchID)
                                                .putLaunchType(launchType));
        Timber.v("Launch Detail: %s- %s ID:%s viewed", launchType, launchName, launchID);
    }

    public void sendLaunchShared(@NonNull String launchName, @NonNull String launchID) {
        Answers.getInstance().logShare(new ShareEvent()
                                               .putContentName(launchName)
                                               .putContentId(launchID));
        Timber.v("Share Event: %s - %s", launchName, launchID);
    }

    public void sendSearchEvent(@NonNull String query, @NonNull String launchType, @NonNull int resultCount) {
        Answers.getInstance().logSearch(new SearchEvent()
                                                .putQuery(query)
                                                .putCustomAttribute("type", launchType)
                                                .putCustomAttribute("result", resultCount));
        Timber.v("Search Event %s: Query - %s Result - %s", launchType, query, resultCount);
    }

    public void sendLaunchMapClicked(@NonNull String launchName) {
        Answers.getInstance().logCustom(new MapClicked().putLaunchName(launchName));
        Timber.v("Map Click: %s", launchName);
    }

    public void sendNetworkEvent(@NonNull String eventName, @NonNull String eventURL, @NonNull boolean result, @NonNull String response) {
        Answers.getInstance().logCustom(new NetworkEvent(eventName)
                                                .putURL(eventURL)
                                                .putResult(result)
                                                .putResponse(response)
        );
        Timber.v("Network Event: %s Success - %s URL - %s", eventName, result, eventURL);
    }

    public void sendNetworkEvent(@NonNull String eventName, @NonNull String eventURL, @NonNull boolean result) {
        Answers.getInstance().logCustom(new NetworkEvent(eventName)
                                                .putURL(eventURL)
                                                .putResult(result)
        );
        Timber.v("Network Event: %s Success - %s URL - %s", eventName, result, eventURL);
    }

    public void sendNetworkEvent(@NonNull String eventName) {
        Answers.getInstance().logCustom(new NetworkEvent(eventName));
        Timber.v("Network Event: %s Success", eventName);
    }

    public void sendButtonClickedWithURL(@NonNull String buttonName, @NonNull String eventURL) {
        Answers.getInstance().logCustom(new ButtonClicked(buttonName)
                                                .putURL(eventURL)
        );
        Timber.v("Button Clicked: %s URL: %s", buttonName, eventURL);
    }

    public void sendButtonClickedWithURL(@NonNull String buttonName, @NonNull String launchName,@NonNull String eventURL) {
        Answers.getInstance().logCustom(new ButtonClicked(buttonName)
                                                .putLaunchName(launchName)
                                                .putURL(eventURL));
        Timber.v("Button Clicked: %s - %s URL: %s", buttonName, launchName, eventURL);
    }

    public void sendButtonClicked(@NonNull String buttonName, @NonNull String launchName) {
        Answers.getInstance().logCustom(new ButtonClicked(buttonName)
                                                .putLaunchName(launchName));
        Timber.v("Button Clicked: %s - %s", buttonName, launchName);
    }

    public void sendButtonClicked(@NonNull String buttonName) {
        Answers.getInstance().logCustom(new ButtonClicked(buttonName));
        Timber.v("Button Clicked: %s", buttonName);
    }

    public void sendWeatherEvent(@NonNull String launchName, @NonNull boolean result, String localizedMessage) {
        Answers.getInstance().logCustom(new WeatherEvent()
                                                .putLaunchName(launchName)
                                                .putResult(String.valueOf(result))
        );
        Timber.v("Weather Event: %s Result: %s", launchName, result);
    }

    public void sendPreferenceEvent(@NonNull String prefName, @NonNull boolean result) {
        Answers.getInstance().logCustom(new PreferenceEvent(prefName)
                                                .putEnabled(String.valueOf(result))
                                                );
        Timber.v("Preference Event: %s Result: %s", prefName, result);
    }

    public void sendPreferenceEvent(@NonNull String prefName, @NonNull String result) {
        Answers.getInstance().logCustom(new PreferenceEvent(prefName)
                                                .putStatus(String.valueOf(result))
        );
        Timber.v("Preference Event: %s Result: %s", prefName, result);
    }

    public void sendPreferenceEvent(@NonNull String prefName) {
        Answers.getInstance().logCustom(new PreferenceEvent(prefName));
        Timber.v("Preference Event: %s changed.", prefName);
    }

    public void sendAddToCartEvent(@NonNull Products products, String sku){
        Answers.getInstance().logAddToCart(new AddToCartEvent()
                                                   .putItemPrice(BigDecimal.valueOf(products.getPrice()))
                                                   .putCurrency(Currency.getInstance("USD"))
                                                   .putItemName(products.getName())
                                                   .putItemType(products.getType())
                                                   .putItemId(sku));
        Timber.v("Add to Cart: %s %s - $%s SKU: %s", products.getName(), products.getType(), products.getPrice(), sku);
    }

    public void sendStartCheckout(@NonNull Products products) {
        Answers.getInstance().logStartCheckout(new StartCheckoutEvent().putTotalPrice(BigDecimal.valueOf(products.getPrice()))
                                                   .putCurrency(Currency.getInstance("USD"))
                                                   .putItemCount(1));
        Timber.v("StartCheckout: %s %s - $%s", products.getName(), products.getType(), products.getPrice());
    }

    public void sendPurchaseEvent(@NonNull Products products, String sku) {
        Answers.getInstance().logPurchase(new PurchaseEvent()
                                                  .putItemPrice(BigDecimal.valueOf(products.getPrice()))
                                                  .putCurrency(Currency.getInstance("USD"))
                                                  .putItemName(products.getName())
                                                  .putItemType(products.getType())
                                                  .putItemId(sku)
                                                  .putSuccess(true));
        Timber.v("Purchased: %s %s - $%sSKU: %s", products.getName(), products.getType(), products.getPrice(), sku);
    }

    //Custom Answer Events
    private class MapClicked extends CustomEvent {

        MapClicked() {
            super("MapClicked");
        }

        MapClicked putLaunchName(@NonNull String launchName) {
            this.putCustomAttribute("Launch Name", launchName);
            return this;
        }
    }

    private class LaunchDetailViewed extends CustomEvent {

        LaunchDetailViewed() {
            super("Launch Details Viewed");
        }

        LaunchDetailViewed putLaunchName(@NonNull String launchName) {
            this.putCustomAttribute("Launch Name", launchName);
            return this;
        }

        LaunchDetailViewed putLaunchType(@NonNull String launchType) {
            this.putCustomAttribute("Launch Name", launchType);
            return this;
        }
    }

    private class NetworkEvent extends CustomEvent {

        NetworkEvent(String eventName) {
            super(eventName + " Network Event");
        }

        NetworkEvent putURL(@NonNull String url) {
            if (url.length() > 100){
                url = url.substring(0,99);
            }
            this.putCustomAttribute("URL", url);
            return this;
        }

        NetworkEvent putResponse(@NonNull String response) {
            this.putCustomAttribute("response", response);
            return this;
        }

        NetworkEvent putResult(@NonNull boolean result) {
            this.putCustomAttribute("result", String.valueOf(result));
            return this;
        }
    }

    private class NotificationEvent extends CustomEvent {

        NotificationEvent() {
            super("Launch Notification Event");
        }

        NotificationEvent putLaunchName(@NonNull String launchName) {
            this.putCustomAttribute("launchName", launchName);
            return this;
        }

        NotificationEvent putContent(@NonNull String content) {
            this.putCustomAttribute("content", content);
            return this;
        }

    }

    private class ButtonClicked extends CustomEvent {

        ButtonClicked(String eventName) {
            super(eventName + " Button Clicked");
        }

        ButtonClicked putLaunchName(@NonNull String launchName) {
            this.putCustomAttribute("launchName", launchName);
            return this;
        }

        ButtonClicked putURL(@NonNull String url) {
            this.putCustomAttribute("URL", url);
            return this;
        }
    }

    private class WeatherEvent extends CustomEvent {

        WeatherEvent() {
            super("Weather Event");
        }

        WeatherEvent putLaunchName(@NonNull String launchName) {
            this.putCustomAttribute("launchName", launchName);
            return this;
        }

        WeatherEvent putResult(@NonNull String result) {
            this.putCustomAttribute("result", result);
            return this;
        }
    }

    private class PreferenceEvent extends CustomEvent {

        PreferenceEvent(String prefName) {
            super(prefName + " Preference Event");
        }

        PreferenceEvent putEnabled(@NonNull String result) {
            this.putCustomAttribute("enabled", result);
            return this;
        }

        PreferenceEvent putStatus(@NonNull String result) {
            this.putCustomAttribute("status", result);
            return this;
        }
    }

    private class UIEvent extends CustomEvent {

        UIEvent(String screen) {
            super(screen + " UI Event");
        }

        UIEvent putState(@NonNull String state) {
            this.putCustomAttribute("state", state);
            return this;
        }
    }

}
