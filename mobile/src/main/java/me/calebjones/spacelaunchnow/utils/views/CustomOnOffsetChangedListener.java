package me.calebjones.spacelaunchnow.utils.views;

import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.view.Window;
import android.view.WindowManager;
import me.calebjones.spacelaunchnow.utils.Utils;


public class CustomOnOffsetChangedListener implements AppBarLayout.OnOffsetChangedListener {

    private int statusColor;
    private Window window;

    public CustomOnOffsetChangedListener(int statusColor, Window window) {
        this.statusColor = statusColor;
        this.window = window;
    }

    public void updateStatusColor(int statusColor){
        this.statusColor = statusColor;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int totalScroll = appBarLayout.getTotalScrollRange();
        int currentScroll = totalScroll + verticalOffset;

        int r = (statusColor >> 16) & 0xFF;
        int g = (statusColor >> 8) & 0xFF;
        int b = (statusColor >> 0) & 0xFF;

        if ((currentScroll) < 255) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.argb(Utils.reverseNumber(currentScroll, 0, 255), r, g, b));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
    }
}
