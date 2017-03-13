package me.calebjones.spacelaunchnow.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.crashlytics.android.answers.AddToCartEvent;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.PurchaseEvent;
import com.crashlytics.android.answers.SearchEvent;
import com.crashlytics.android.answers.ShareEvent;
import com.crashlytics.android.answers.StartCheckoutEvent;

import java.math.BigDecimal;
import java.util.Currency;

import me.calebjones.spacelaunchnow.data.models.realm.Products;
import timber.log.Timber;

public class Analytics {

    public static final String TYPE_PREVIOUS_LAUNCH = "Previous";
    public static final String TYPE_UPCOMING_LAUNCH = "Upcoming";

    public static Analytics from(Context context){
        return ((Provider) context.getApplicationContext()).getAnalytics();
    }

    public static Analytics from(Fragment fragment) {
        return from(fragment.getActivity());
    }

    public interface Provider {
        Analytics getAnalytics();
    }

    //Logging methods.
    public void sendLaunchDetailViewedEvent(@NonNull String launchName, @NonNull String launchType, @NonNull String launchID) {
        Answers.getInstance().logCustom(new LaunchDetailViewed()
                                                .putLaunchName(launchName + " " + launchID)
                                                .putLaunchType(launchType));
        Timber.v("Launch Detail: % - % ID:% viewed", launchType, launchName, launchID);
    }

    public void sendLaunchShared(@NonNull String launchName, @NonNull String launchID) {
        Answers.getInstance().logShare(new ShareEvent()
                                               .putContentName(launchName)
                                               .putContentId(launchID));
        Timber.v("Share Event: %-%", launchName, launchID);
    }

    public void sendSearchEvent(@NonNull String query, @NonNull String launchType, @NonNull int resultCount) {
        Answers.getInstance().logSearch(new SearchEvent()
                                                .putQuery(query)
                                                .putCustomAttribute("type", launchType)
                                                .putCustomAttribute("result", resultCount));
        Timber.v("Search Event %: Query - %  Result - %", launchType, query, resultCount);
    }

    public void sendLaunchMapClicked(@NonNull String launchName) {
        Answers.getInstance().logCustom(new MapClicked().putLaunchName(launchName));
        Timber.v("Map Click: %", launchName);
    }

    public void sendNetworkEvent(@NonNull String eventName, @NonNull String eventURL, @NonNull boolean result, @NonNull String response) {
        Answers.getInstance().logCustom(new NetworkEvent()
                                                .putEventName(eventName)
                                                .putURL(eventURL)
                                                .putResult(result)
                                                .putResponse(response)
        );
        Timber.v("Network Event: % Success - %s URL - %s", eventName, result, eventURL);
    }

    public void sendButtonClickedWithURL(@NonNull String buttonName, @NonNull String eventURL) {
        Answers.getInstance().logCustom(new ButtonClicked()
                                                .putButtonName(buttonName)
                                                .putURL(eventURL)
        );
        Timber.v("Button Clicked: %s URL: %s", buttonName, eventURL);
    }

    public void sendButtonClickedWithURL(@NonNull String buttonName, @NonNull String launchName,@NonNull String eventURL) {
        Answers.getInstance().logCustom(new ButtonClicked()
                                                .putButtonName(buttonName)
                                                .putLaunchName(launchName)
                                                .putURL(eventURL)
        );
        Timber.v("Button Clicked: %s - %s URL: %s", buttonName, launchName, eventURL);
    }

    public void sendButtonClicked(@NonNull String buttonName, @NonNull String launchName) {
        Answers.getInstance().logCustom(new ButtonClicked()
                                                .putButtonName(buttonName)
                                                .putLaunchName(launchName)
        );
        Timber.v("Button Clicked: %s - %s", buttonName, launchName);
    }

    public void sendButtonClicked(@NonNull String buttonName) {
        Answers.getInstance().logCustom(new ButtonClicked()
                                                .putButtonName(buttonName)
        );
        Timber.v("Button Clicked: %s", buttonName);
    }

    public void sendWeatherEvent(@NonNull String launchName, @NonNull boolean result) {
        Answers.getInstance().logCustom(new WeatherEvent()
                                                .putLaunchName(launchName)
                                                .putResult(String.valueOf(result))
        );
        Timber.v("Weather Event: %s Result: %s", launchName, result);
    }

    public void sendPreferenceEvent(@NonNull String prefName, @NonNull boolean result) {
        Answers.getInstance().logCustom(new PreferenceEvent()
                                                .putPreferenceName(prefName)
                                                .putEnabled(String.valueOf(result))
                                                );
        Timber.v("Weather Event: %s Result: %s", prefName, result);
    }

    public void sendPreferenceEvent(@NonNull String prefName) {
        Answers.getInstance().logCustom(new PreferenceEvent()
                                                .putPreferenceName(prefName)
        );
        Timber.v("Weather Event: %s changed.", prefName);
    }

    public void sendAddToCartEvent(@NonNull Products products, String sku){
        Answers.getInstance().logAddToCart(new AddToCartEvent()
                                                   .putItemPrice(BigDecimal.valueOf(products.getPrice()))
                                                   .putCurrency(Currency.getInstance("USD"))
                                                   .putItemName(products.getName())
                                                   .putItemType(products.getType())
                                                   .putItemId(sku));
        Timber.v("Add to Cart: %s %s - $% SKU: %s", products.getName(), products.getType(), products.getPrice(), sku);
    }

    public void sendStartCheckout(@NonNull Products products) {
        Answers.getInstance().logStartCheckout(new StartCheckoutEvent().putTotalPrice(BigDecimal.valueOf(products.getPrice()))
                                                   .putCurrency(Currency.getInstance("USD"))
                                                   .putItemCount(1));
        Timber.v("StartCheckout: %s %s - $%", products.getName(), products.getType(), products.getPrice());
    }

    public void sendPurchaseEvent(@NonNull Products products, String sku) {
        Answers.getInstance().logPurchase(new PurchaseEvent()
                                                  .putItemPrice(BigDecimal.valueOf(products.getPrice()))
                                                  .putCurrency(Currency.getInstance("USD"))
                                                  .putItemName(products.getName())
                                                  .putItemType(products.getType())
                                                  .putItemId(sku)
                                                  .putSuccess(true));
        Timber.v("Purchased: %s %s - $% SKU: %s", products.getName(), products.getType(), products.getPrice(), sku);
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
            super("LaunchDetailViewed");
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

        NetworkEvent() {
            super("NetworkEvent");
        }

        NetworkEvent putEventName(@NonNull String eventName) {
            this.putCustomAttribute("eventName", eventName);
            return this;
        }

        NetworkEvent putURL(@NonNull String url) {
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

    private class ButtonClicked extends CustomEvent {

        ButtonClicked() {
            super("ButtonClicked");
        }

        ButtonClicked putButtonName(@NonNull String eventName) {
            this.putCustomAttribute("buttonName", eventName);
            return this;
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
            super("NetworkEvent");
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

        PreferenceEvent() {
            super("PreferenceEvent");
        }

        PreferenceEvent putPreferenceName(@NonNull String prefName) {
            this.putCustomAttribute("preference", prefName);
            return this;
        }

        PreferenceEvent putEnabled(@NonNull String result) {
            this.putCustomAttribute("enabled", result);
            return this;
        }
    }

}
