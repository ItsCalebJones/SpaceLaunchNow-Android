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

package me.calebjones.spacelaunchnow.wear;

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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.Html;
import android.util.Log;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
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
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * Digital watch face with seconds. In ambient mode, the seconds aren't displayed. On devices with
 * low-bit ambient mode, the text is drawn without anti-aliasing in ambient mode.
 */
public class SpaceLaunchWatchFace extends CanvasWatchFaceService {
    private static final Typeface NORMAL_TYPEFACE =
            Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
    private static final TimeZone timeZone = TimeZone.getTimeZone("EDT");
    private static final String NAME_KEY = "me.calebjones.spacelaunchnow.wear.nextname";
    private static final String TIME_KEY = "me.calebjones.spacelaunchnow.wear.nexttime";
    private static final String DATE_KEY = "me.calebjones.spacelaunchnow.wear.nextdate";
    private static final String HOUR_KEY = "me.calebjones.spacelaunchnow.wear.hourmode";
    private static final String BACKGROUND_KEY = "me.calebjones.spacelaunchnow.wear.background";
    private static final int BACKGROUND_NORMAL = 0;
    private static final int BACKGROUND_CUSTOM = 1;
    private static final int BACKGROUND_DYNAMIC = 2;
    private boolean twentyfourhourmode = false;
    private boolean custombackground = false;
    private String timeText;

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

    private class Engine extends CanvasWatchFaceService.Engine implements
            GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
        final Handler mUpdateTimeHandler = new EngineHandler(this);
        boolean mRegisteredTimeZoneReceiver = false;
        Paint mBackgroundPaint;
        Paint mTextPaint;
        Paint mLaunchName;
        Paint mLaunchTime;
        Paint mDatePaint;
        boolean mAmbient;
        boolean isRound;
        Calendar mTime;
        float timeXoffset;
        float timeYoffset;
        private Bitmap background;
        private Bitmap customBackgroundBitmap;
        private GoogleApiClient googleApiClient;
        private WatchFaceStyle watchFaceStyle;
        private Asset asset;
        String launchName;
        String launchNameText;
        Date launchDate;
        int launchTime;

        float mXOffset = 0;
        float mYOffset = 0;
        private int specW, specH;
        private View myLayout;
        private LinearLayout utcDateContainer;
        private LinearLayout launchInfoContainer;
        private ImageView imageView;
        private TextView timeView;
        private TextView utcTimeView;
        private TextView dateView;
        private TextView launchNameView;
        private TextView launchCountdownView;
        private final Point displaySize = new Point();
        private LayoutInflater layoutInflater;


        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        boolean mLowBitAmbient;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            Timber.plant(new Timber.DebugTree());

            watchFaceStyle = new WatchFaceStyle.Builder(SpaceLaunchWatchFace.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_VARIABLE)
                    .setHotwordIndicatorGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL)
                    .setStatusBarGravity(Gravity.TOP | Gravity.CENTER)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .setShowUnreadCountIndicator(true)
                    .build();
            setWatchFaceStyle(watchFaceStyle);


            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            myLayout = layoutInflater.inflate(R.layout.watchface, null);

            // Load the display spec - we'll need this later for measuring myLayout
            Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay();
            display.getSize(displaySize);

            // Find some views for later use
            timeView = (TextView) myLayout.findViewById(R.id.time);
            utcTimeView = (TextView) myLayout.findViewById(R.id.utc_time);
            dateView = (TextView) myLayout.findViewById(R.id.date);
            launchNameView = (TextView) myLayout.findViewById(R.id.launch_name);
            launchCountdownView = (TextView) myLayout.findViewById(R.id.launch_countdown);
            utcDateContainer = (LinearLayout) myLayout.findViewById(R.id.utc_date_container);
            launchInfoContainer = (LinearLayout) myLayout.findViewById(R.id.launch_info_container);
            imageView = (ImageView) myLayout.findViewById(R.id.background);

            googleApiClient = new GoogleApiClient.Builder(SpaceLaunchWatchFace.this)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                registerReceiver();
                googleApiClient.connect();
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

            } else if (insets.getSystemWindowInsetBottom() > 0) {
                Timber.v("Watch has chin.");
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) launchInfoContainer.getLayoutParams();
                params.setMargins(0, 0, 0, 10);

                launchInfoContainer.setLayoutParams(params);
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
                if (mLowBitAmbient) {
                    mTextPaint.setAntiAlias(!inAmbientMode);
                }
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
            SimpleDateFormat twentyFourHourMode = new SimpleDateFormat("kk:mm");
            SimpleDateFormat twelveHourMode = new SimpleDateFormat("h:mm");
            if (twentyfourhourmode) {
                // Draw H:MM in ambient mode or H:MM:SS in interactive mode.
                timeText = twentyFourHourMode.format(now);
            } else {
                // Draw H:MM in ambient mode or H:MM:SS in interactive mode.
                timeText = twelveHourMode.format(now);
            }


            Calendar utcTime = Calendar.getInstance(timeZone);
            utcTime.setTimeInMillis(mTime.getTimeInMillis());

            Date utcDate = new Date(utcTime.getTimeInMillis());

            Date date = new Date(mTime.getTimeInMillis());

            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d'" + getDayNumberSuffix(mTime.get(Calendar.DAY_OF_MONTH)) + "'");
            String dateText = dateFormat.format(date);
            twentyFourHourMode.setTimeZone(TimeZone.getTimeZone("UTC"));
            String utcText = twentyFourHourMode.format(utcDate) + " UTC";

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
                launchNameText = "Waiting for Next Launch...";
            }

            //Parse time into day, hour, minute, and second
            String day = String.valueOf(TimeUnit.MILLISECONDS.toDays(timeToFinish));
            String hour;
            String minute;
            String second;

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

            String countdownTimer = String.format("L - %s %s:%s:%s", day, hour, minute, second);


            //Current Time
            timeView.setText(timeText);

            //Date Text
            dateView.setText(dateText);

            //UTC Text
            utcTimeView.setText(utcText);


            //Launch Name
            if (launchName != null) {
                launchNameView.setText(launchNameText);
                //Launch Countdown/Status
                if (launchTime == 0 && launchDate != null) {
                    String dayNumberSuffix = getDayNumberSuffix(utcDate.getDate());
                    SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM d'" + dayNumberSuffix + "'");
                    launchCountdownView.setText(Html.fromHtml(formatter.format(launchDate)));
                }
            }

            // Else align text in a manner that fits


            //Launch Countdown/Status
            if (launchTime != 0) {
                launchCountdownView.setText(countdownTimer);
            }

            // Update the layout
            myLayout.measure(specW, specH);
            myLayout.layout(0, 0, myLayout.getMeasuredWidth(), myLayout.getMeasuredHeight());

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

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Log.v("Space Launch Wear", "onConnected");

            Wearable.DataApi.addListener(googleApiClient, onDataChangedListener);
            Wearable.DataApi.getDataItems(googleApiClient).setResultCallback(onConnectedResultCallback);
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.v("Space Launch Wear", "onConnectedSuspended");
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Timber.e("onConnectionFailed - %s %s", connectionResult.getErrorCode(), connectionResult.getErrorMessage());

        }

        private final DataApi.DataListener onDataChangedListener = new DataApi.DataListener() {
            @Override
            public void onDataChanged(DataEventBuffer dataEvents) {
                Log.v("Space Launch Wear", "onDataChanged");
                for (DataEvent event : dataEvents) {
                    if (event.getType() == DataEvent.TYPE_CHANGED) {
                        DataItem item = event.getDataItem();
                        processConfigurationFor(item);
                    }
                }

                dataEvents.release();
                invalidateIfNecessary();
            }
        };

        private void processConfigurationFor(DataItem item) {
            Log.v("Space Launch Wear", "processConfigurationFor");
            if (item.getUri().getPath().equals("/nextLaunch")) {
                DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                if (dataMap.containsKey(NAME_KEY)) {
                    launchName = dataMap.getString(NAME_KEY);
                    Log.v("Space Launch Wear", "Name = " + launchName);
                }

                if (dataMap.containsKey(TIME_KEY)) {
                    launchTime = dataMap.getInt(TIME_KEY);
                    Log.v("Space Launch Wear", "Time = " + launchTime);
                }

                if (dataMap.containsKey(DATE_KEY)) {
                    launchDate = new Date(dataMap.getLong(DATE_KEY));
                    Log.v("Space Launch Wear", "Date = " + launchDate.toString());
                }

                if (dataMap.containsKey(BACKGROUND_KEY)) {
                    final Asset profileAsset = dataMap.getAsset(BACKGROUND_KEY);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap bitmap = loadBitmapFromAsset(profileAsset);
                            imageView.setImageBitmap(bitmap);
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        }
                    }).start();
                    Log.v("Space Launch Wear", "Date = " + launchDate.toString());
                }
            }
            if (item.getUri().getPath().equals("/config")) {
                DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                if (dataMap.containsKey(HOUR_KEY)) {
                    twentyfourhourmode = dataMap.getBoolean(HOUR_KEY);
                    Timber.v("24 Hour Mode = %s", twentyfourhourmode);
                }
            }
        }

        public Bitmap loadBitmapFromAsset(Asset asset) {
            if (asset == null) {
                throw new IllegalArgumentException("Asset must be non-null");
            }
            ConnectionResult result =
                    googleApiClient.blockingConnect(5000, TimeUnit.MILLISECONDS);
            if (!result.isSuccess()) {
                return null;
            }
            // convert asset into a file descriptor and block until it's ready
            InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                    googleApiClient, asset).await().getInputStream();
            googleApiClient.disconnect();

            if (assetInputStream == null) {
                Timber.e("Requested an unknown Asset.");
                return null;
            }
            // decode the stream into a bitmap
            return BitmapFactory.decodeStream(assetInputStream);
        }

        private final ResultCallback<DataItemBuffer> onConnectedResultCallback = new ResultCallback<DataItemBuffer>() {
            @Override
            public void onResult(DataItemBuffer dataItems) {
                Log.v("Space Launch Wear", "onResult");
                for (DataItem item : dataItems) {
                    processConfigurationFor(item);
                }

                dataItems.release();
                invalidateIfNecessary();
            }
        };

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
    }
}
