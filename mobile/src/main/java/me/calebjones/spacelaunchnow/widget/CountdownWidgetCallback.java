package me.calebjones.spacelaunchnow.widget;


import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

class CountdownWidgetCallback implements Runnable {
    final /* synthetic */ CountDownWidgetService countDownWidgetService;

    CountdownWidgetCallback(CountDownWidgetService countDownWidgetService) {
        this.countDownWidgetService = countDownWidgetService;
    }

    public void run() {
        Timber.v("Callback - Countdown");
        if (this.countDownWidgetService.enabled) {
            this.countDownWidgetService.updateWidget();
            Executors.newSingleThreadScheduledExecutor().schedule(this, 1, TimeUnit.SECONDS);
        }
    }
}
