/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.calebjones.spacelaunchnow.wear.watchface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import me.calebjones.spacelaunchnow.wear.R;
import timber.log.Timber;

/**
 * Digital watch face with seconds. In ambient mode, the seconds aren't displayed. On devices with
 * low-bit ambient mode, the text is drawn without anti-aliasing in ambient mode.
 */
public class SpaceLaunchWatchFace extends CanvasWatchFaceService {
    private static final Typeface NORMAL_TYPEFACE = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
    private static final TimeZone timeZone = TimeZone.getDefault();
    private static final String NAME_KEY = "me.calebjones.spacelaunchnow.wear.nextname";
    private static final String TIME_KEY = "me.calebjones.spacelaunchnow.wear.nexttime";
    private static final String DATE_KEY = "me.calebjones.spacelaunchnow.wear.nextdate";
    private static final String HOUR_KEY = "me.calebjones.spacelaunchnow.wear.hourmode";
    private static final String UTC_KEY = "me.calebjones.spacelaunchnow.wear.utcmode";

    private static final String BACKGROUND_KEY = "me.calebjones.spacelaunchnow.wear.background";
    private static final int BACKGROUND_NORMAL = 0;
    private static final int BACKGROUND_CUSTOM = 1;
    private static final int BACKGROUND_DYNAMIC = 2;
    private boolean twentyfourhourmode = false;

    private boolean utcMode = true;

    private boolean custombackground = false;
    private String timeText;
    private int primaryTextColor;
    private int secondaryTextColor;

    /**
     * Update rate in milliseconds for interactive mode. We update once a second since seconds are
     * displayed in interactive mode.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private static class EngineHandler extends Handler {
        private final WeakReference<SpaceLaunchWatchFace.Engine> mWeakReference;

        public EngineHandler(SpaceLaunchWatchFace.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            SpaceLaunchWatchFace.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    public static SimpleDateFormat getSimpleDateFormatForUI(String pattern) {
        String format =  DateFormat.getBestDateTimePattern(Locale.getDefault(), pattern);
        return new SimpleDateFormat(format, Locale.getDefault());
    }

    private class Engine extends CanvasWatchFaceService.Engine implements DataClient.OnDataChangedListener {
        final Handler mUpdateTimeHandler = new EngineHandler(this);
        boolean mRegisteredTimeZoneReceiver = false;
        Paint mTextPaint;
        boolean mAmbient;
        boolean isRound;
        private  boolean textDynamic;
        Calendar mTime;
        private WatchFaceStyle watchFaceStyle;
        private Asset asset;
        String launchName;
        String launchNameText;
        Date launchDate;
        int launchTime;

        float mXOffset = 0;
        float mYOffset = 0;

        private int specW, specH;
        private LayoutInflater layoutInflater;
        private View myLayout;
        private LinearLayout utcDateContainer;
        private LinearLayout launchInfoContainer;
        private LinearLayout countdownLayout;
        private ImageView imageView;
        private TextView countdownPrimary;
        private TextView countdownPrimaryLabel;
        private TextView countdownSecondary;
        private TextView countdownSecondaryLabel;
        private TextView timeView;
        private TextView utcTimeView;
        private TextView dateView;
        private TextView launchNameView;
        private TextView launchCountdownView;
        private final Point displaySize = new Point();
        private int apiConnectedRefresh;
        private Bitmap bitmap;
        private DataClient dataClient;

        private SimpleDateFormat twentyFourHourFormat;
        private SimpleDateFormat twentyFourHourFormatUTC;
        private SimpleDateFormat twelveHourFormat;



        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        boolean mLowBitAmbient;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            Timber.plant(new Timber.DebugTree());
            Timber.v("onCreate");

            watchFaceStyle = new WatchFaceStyle.Builder(SpaceLaunchWatchFace.this)
                    .setStatusBarGravity(Gravity.TOP | Gravity.CENTER)
                    .setShowUnreadCountIndicator(true)
                    .build();
            setWatchFaceStyle(watchFaceStyle);


            twentyFourHourFormat = new SimpleDateFormat("HH:mm");
            twentyFourHourFormatUTC = new SimpleDateFormat("HH:mm");
            twelveHourFormat = new SimpleDateFormat("h:mm");


            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            myLayout = layoutInflater.inflate(R.layout.watchface, null);

            // Load the display spec - we'll need this later for measuring myLayout
            Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay();
            display.getSize(displaySize);

            // Find some views for later use
            timeView = myLayout.findViewById(R.id.time);
            utcTimeView = myLayout.findViewById(R.id.utc_time);
            dateView = myLayout.findViewById(R.id.date);
            launchNameView = myLayout.findViewById(R.id.launch_name);
            launchCountdownView = myLayout.findViewById(R.id.launch_countdown);
            utcDateContainer = myLayout.findViewById(R.id.utc_date_container);
            launchInfoContainer = myLayout.findViewById(R.id.launch_info_container);
            imageView = myLayout.findViewById(R.id.background);
            countdownLayout = myLayout.findViewById(R.id.countdown_layout);
            countdownPrimary =  myLayout.findViewById(R.id.countdown_primary);
            countdownPrimaryLabel = myLayout.findViewById(R.id.countdown_primary_label);
            countdownSecondary = myLayout.findViewById(R.id.countdown_secondary);
            countdownSecondaryLabel = myLayout.findViewById(R.id.countdown_secondary_label);

            primaryTextColor = ContextCompat.getColor(getApplicationContext(), R.color.material_typography_primary_text_color_light);
            secondaryTextColor = ContextCompat.getColor(getApplicationContext(), R.color.material_typography_secondary_text_color_light);



            dataClient = Wearable.getDataClient(getApplicationContext());
            dataClient.addListener(this);
            dataClient.getDataItems().addOnCompleteListener(new OnCompleteListener<DataItemBuffer>() {
                @Override
                public void onComplete(@NonNull Task<DataItemBuffer> task) {
                    if (task.isSuccessful()) {
                        DataItemBuffer dataItems = task.getResult();
                        for (DataItem item: dataItems){
                            processConfigurationFor(item);
                        }
                        if (dataItems.getCount() == 0 ) applyDefault();
                    } else {
                        applyDefault();
                    }
                }
            });
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            dataClient.removeListener(this);
            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            Timber.v("onVisibilityChanged");
            if (visible) {
                registerReceiver();
            } else {
                unregisterReceiver();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);

            // Load resources that have alternate values for round watches.
            isRound = insets.isRound();
            if (insets.isRound()) {
                Timber.v("Watch is Round");
                mXOffset = mYOffset = 0;
//                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) launchInfoContainer.getLayoutParams();
//                params.setMargins(0, 0, 0, 24);
//
//                launchInfoContainer.setLayoutParams(params);

            } else if (insets.getSystemWindowInsetBottom() > 0) {
                Timber.v("Watch has chin.");
//                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) launchInfoContainer.getLayoutParams();
//                params.setMargins(0, 0, 0, 24);

//                launchInfoContainer.setLayoutParams(params);
            } else {
                Timber.v("Watch is square");
                mXOffset = mYOffset = 0;
                timeView.setGravity(Gravity.START | Gravity.CENTER);
                utcTimeView.setGravity(Gravity.START | Gravity.CENTER);
                dateView.setGravity(Gravity.START | Gravity.CENTER);
                utcDateContainer.setGravity(Gravity.START | Gravity.CENTER);
            }

            // Recompute the MeasureSpec fields - these determine the actual size of the layout
            specW = View.MeasureSpec.makeMeasureSpec(displaySize.x, View.MeasureSpec.EXACTLY);
            specH = View.MeasureSpec.makeMeasureSpec(displaySize.y, View.MeasureSpec.EXACTLY);

            if(displaySize.x < 300 || displaySize.y < 300) {
                launchNameView.setMaxLines(1);
            } else {
                launchNameView.setMaxLines(2);
            }
            Timber.v("Screen - width: %s height %s", displaySize.x, displaySize.y);
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            if (mAmbient != inAmbientMode) {
                mAmbient = inAmbientMode;
                invalidate();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {

            Date now = new Date();
            mTime = Calendar.getInstance();

            if (twentyfourhourmode) {
                // Draw H:MM in ambient mode or H:MM:SS in interactive mode.
                timeText = twentyFourHourFormat.format(now);
            } else {
                // Draw H:MM in ambient mode or H:MM:SS in interactive mode.
                timeText = twelveHourFormat.format(now);
            }


            Calendar utcTime = Calendar.getInstance(timeZone);
            utcTime.setTimeInMillis(mTime.getTimeInMillis());

            Date utcDate = new Date(utcTime.getTimeInMillis());

            Date date = new Date(mTime.getTimeInMillis());

            SimpleDateFormat dateFormat = getSimpleDateFormatForUI("EEEE, MMMM d'" + getDayNumberSuffix(mTime.get(Calendar.DAY_OF_MONTH)) + "'");
            String dateText = dateFormat.format(date);
            twentyFourHourFormatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
            String utcText = twentyFourHourFormatUTC.format(utcDate) + " UTC";

            long longdate = launchTime;
            longdate = longdate * 1000;
            final Date mDate = new Date(longdate);
            Calendar future = DateToCalendar(mDate);
            long timeToFinish = future.getTimeInMillis() - mTime.getTimeInMillis();
            if (timeToFinish <= 0) {
                launchTime = 0;
            }

            if (launchName != null) {
                launchNameText = launchName;
            } else {
                launchNameText = "Waiting for Next LaunchCategory...";
            }

            //Parse time into day, hour, minute, and second
            String day = String.valueOf(TimeUnit.MILLISECONDS.toDays(timeToFinish));
            String hour;
            String minute;
            String second;

            int days = (int) TimeUnit.MILLISECONDS.toDays(timeToFinish);
            int hours = (int) (TimeUnit.MILLISECONDS.toHours(timeToFinish) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(timeToFinish)));
            int minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(timeToFinish) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeToFinish)));

            if (TimeUnit.MILLISECONDS.toHours(timeToFinish) -
                    TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(timeToFinish)) < 10) {
                hour = "0" + String.valueOf(TimeUnit.MILLISECONDS.toHours(timeToFinish) -
                        TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(timeToFinish)));
            } else {
                hour = String.valueOf(TimeUnit.MILLISECONDS.toHours(timeToFinish) -
                        TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(timeToFinish)));
            }

            if (TimeUnit.MILLISECONDS.toMinutes(timeToFinish) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeToFinish)) < 10) {

                minute = "0" + String.valueOf(TimeUnit.MILLISECONDS.toMinutes(timeToFinish) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeToFinish)));
            } else {
                minute = String.valueOf(TimeUnit.MILLISECONDS.toMinutes(timeToFinish) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeToFinish)));
            }

            if (TimeUnit.MILLISECONDS.toSeconds(timeToFinish) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeToFinish)) < 10) {

                second = "0" + String.valueOf(TimeUnit.MILLISECONDS.toSeconds(timeToFinish) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeToFinish)));
            } else {
                second = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(timeToFinish) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeToFinish)));
            }

            //Current Time
            timeView.setText(timeText);

            //Date Text
            dateView.setText(dateText);

            if (utcMode) {
                utcTimeView.setVisibility(View.VISIBLE);
                //UTC Text
                utcTimeView.setText(utcText);
            } else {
                utcTimeView.setVisibility(View.GONE);
            }



            //LaunchCategory Name
            if (launchName != null) {
                launchNameView.setText(launchNameText);
                //LaunchCategory Countdown/Status
                if (launchTime == 0 && launchDate != null) {
                    String dayNumberSuffix = getDayNumberSuffix(utcDate.getDate());
                    SimpleDateFormat formatter = getSimpleDateFormatForUI("EEEE, MMMM d'" + dayNumberSuffix + "'");
                    launchCountdownView.setText(Html.fromHtml(formatter.format(launchDate)));
                }
            }

            // Else align text in a manner that fits


            //LaunchCategory Countdown/Status
            if (launchTime != 0) {
                launchCountdownView.setVisibility(View.GONE);
                countdownLayout.setVisibility(View.VISIBLE);
                if (days > 0){
                    countdownPrimaryLabel.setText("Days");
                    countdownSecondaryLabel.setText("Hours");
                    countdownPrimary.setText(day);
                    countdownSecondary.setText(hour);

                } else {
                    countdownPrimaryLabel.setText("Hours");
                    countdownSecondaryLabel.setText("Minutes");
                    countdownPrimary.setText(hour);
                    countdownSecondary.setText(minute);
                }
            } else {
                launchCountdownView.setVisibility(View.VISIBLE);
                countdownLayout.setVisibility(View.GONE);
            }

            // Update the layout
            myLayout.measure(specW, specH);
            myLayout.layout(0, 0, myLayout.getMeasuredWidth(), myLayout.getMeasuredHeight());

            if (bitmap != null){
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

            // Draw it to the Canvas
            canvas.drawColor(Color.BLACK);
            canvas.translate(mXOffset, mYOffset);
            myLayout.draw(canvas);
        }

        private String getDayNumberSuffix(int day) {
            if (day >= 11 && day <= 13) {
                return "th";
            }
            switch (day % 10) {
                case 1:
                    return "st<";
                case 2:
                    return "nd";
                case 3:
                    return "rd";
                default:
                    return "th";
            }
        }

        /**
         * Starts the {@link #mUpdateTimeHandler} timer if it should be running and isn't currently
         * or stops it if it shouldn't be running but currently is.
         */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS
                        - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }

        private void processConfigurationFor(DataItem item) {
            Timber.v("processConfigurationFor: %s", item.toString());
            if (item.getUri().getPath().equals("/nextLaunch")) {
                DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                if (dataMap.containsKey(NAME_KEY)) {
                    launchName = dataMap.getString(NAME_KEY);
                    Timber.v("Name = %s", launchName);
                }

                if (dataMap.containsKey(TIME_KEY)) {
                    launchTime = dataMap.getInt(TIME_KEY);
                    Timber.v("Time = %s", launchTime);
                }

                if (dataMap.containsKey(DATE_KEY)) {
                    launchDate = new Date(dataMap.getLong(DATE_KEY));
                    Timber.v("Date = %s", launchDate.toString());
                }

                if (dataMap.containsKey(BACKGROUND_KEY)) {
                    Timber.v("Retrieving Asset...");
                    final Asset profileAsset = dataMap.getAsset(BACKGROUND_KEY);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap bitmap = loadBitmapFromAsset(profileAsset);
                            applyDefault();
                            setBitmap(bitmap);
                        }
                    }).start();
                }
            }
            if (item.getUri().getPath().equals("/config")) {
                DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                if (dataMap.containsKey(HOUR_KEY)) {
                    twentyfourhourmode = dataMap.getBoolean(HOUR_KEY);
                    Timber.v("24 Hour Mode = %s", twentyfourhourmode);
                }
                if (dataMap.containsKey(UTC_KEY)) {
                    utcMode = dataMap.getBoolean(UTC_KEY);
                    Timber.v("UTC Mode = %s", utcMode);
                }

            }
        }

        private void applyDefault() {
            Timber.v("Applying Default color...");
            timeView.setTextColor(primaryTextColor);
            utcTimeView.setTextColor(secondaryTextColor);
            dateView.setTextColor(secondaryTextColor);

            launchNameView.setTextColor(primaryTextColor);
            launchCountdownView.setTextColor(secondaryTextColor);
            countdownPrimary.setTextColor(primaryTextColor);
            countdownSecondary.setTextColor(primaryTextColor);
            countdownPrimaryLabel.setTextColor(secondaryTextColor);
            countdownSecondaryLabel.setTextColor(secondaryTextColor);

            int shadowColor = ContextCompat.getColor(getApplicationContext(), R.color.text_shadow);

            timeView.setShadowLayer(1,0,0,shadowColor);
            utcTimeView.setShadowLayer(1,0,0,shadowColor);
            dateView.setShadowLayer(1,0,0,shadowColor);
            launchNameView.setShadowLayer(1,0,0,shadowColor);
            launchCountdownView.setShadowLayer(1,0,0,shadowColor);
            countdownPrimary.setShadowLayer(1,0,0,shadowColor);
            countdownSecondary.setShadowLayer(1,0,0,shadowColor);
            countdownPrimaryLabel.setShadowLayer(1,0,0,shadowColor);
            countdownSecondaryLabel.setShadowLayer(1,0,0,shadowColor);
        }


        public Bitmap loadBitmapFromAsset(Asset asset) {
            if (asset == null) {
                throw new IllegalArgumentException("Asset must be non-null");
            }

            // convert asset into a file descriptor and block until it's ready
            InputStream assetInputStream = null;
            try {
                assetInputStream = Tasks.await(Wearable.getDataClient(getApplicationContext()).getFdForAsset(asset)).getInputStream();
            } catch (ExecutionException | InterruptedException e) {
                Timber.e(e);
                e.printStackTrace();
            }
            if (assetInputStream == null) {
                Timber.e("Requested an unknown Asset.");
                return null;
            }
            // decode the stream into a bitmap
            return BitmapFactory.decodeStream(assetInputStream);
        }

        private void invalidateIfNecessary() {
            if (isVisible() && !isInAmbientMode()) {
                invalidate();
            }
        }

        public Calendar DateToCalendar(Date date) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal;
        }

        @Override
        public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
            Timber.v("onDataChanged");
            for (DataEvent event : dataEventBuffer) {
                if (event.getType() == DataEvent.TYPE_CHANGED) {
                    DataItem item = event.getDataItem();
                    processConfigurationFor(item);
                }
            }

            dataEventBuffer.release();
            invalidateIfNecessary();
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
            imageView.setImageBitmap(bitmap);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            invalidate();
        }
    }
}
