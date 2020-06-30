package me.calebjones.spacelaunchnow.deeplink;


import android.app.Activity;
import android.os.Bundle;

import com.airbnb.deeplinkdispatch.DeepLinkHandler;

import me.calebjones.spacelaunchnow.common.deeplink.CommonLibDeepLinkModule;
import me.calebjones.spacelaunchnow.common.deeplink.CommonLibDeepLinkModuleRegistry;
import me.calebjones.spacelaunchnow.events.deeplink.EventsDeepLinkModule;
import me.calebjones.spacelaunchnow.events.deeplink.EventsDeepLinkModuleRegistry;

@DeepLinkHandler({ AppDeepLinkModule.class, EventsDeepLinkModule.class, CommonLibDeepLinkModule.class })
public class DeepLinkActivity extends Activity {
    public static final String ACTION_DEEP_LINK = "DEEP_LINK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // DeepLinkDelegate, LibraryDeepLinkModuleRegistry and AppDeepLinkModuleRegistry
        // are generated at compile-time.
        DeepLinkDelegate deepLinkDelegate =
                new DeepLinkDelegate(new AppDeepLinkModuleRegistry(),
                        new EventsDeepLinkModuleRegistry(),
                        new CommonLibDeepLinkModuleRegistry());
        // Delegate the deep link handling to DeepLinkDispatch.
        // It will start the correct Activity based on the incoming Intent URI
        deepLinkDelegate.dispatchFrom(this);
        // Finish this Activity since the correct one has been just started
        finish();
    }

}

