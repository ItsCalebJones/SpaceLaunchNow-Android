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

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

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
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d");
    private static final TimeZone timeZone = TimeZone.getTimeZone("EDT");
    private static final String NAME_KEY = "me.calebjones.spacelaunchnow.wear.nextname";
    private static final String TIME_KEY = "me.calebjones.spacelaunchnow.wear.nexttime";
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
        int launchTime;

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
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build();

            setWatchFaceStyle(watchFaceStyle);
            Resources resources = SpaceLaunchWatchFace.this.getResources();

            background = BitmapFactory.decodeResource(resources, R.drawable.nav_header);

//            if (custombackground && customBackgroundBitmap != null){
//                background = customBackgroundBitmap;
//            } else {
//                background = BitmapFactory.decodeResource(resources, R.drawable.nav_header);
//            }

            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(resources.getColor(R.color.background));

            mTextPaint = new Paint();
            mTextPaint = createTextPaint(resources.getColor(R.color.digital_text));

            mDatePaint = new Paint();
            mDatePaint = createTextPaint(resources.getColor(R.color.secondary_text));

            mLaunchName = new Paint();
            mLaunchName = createTextPaint(resources.getColor(R.color.digital_text));

            mLaunchTime = new Paint();
            mLaunchTime = createTextPaint(resources.getColor(R.color.countdown_text));

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

        private Paint createTextPaint(int textColor) {
            Paint paint = new Paint();
            paint.setColor(textColor);
            paint.setTypeface(NORMAL_TYPEFACE);
            paint.setAntiAlias(true);
            return paint;
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
            Resources resources = SpaceLaunchWatchFace.this.getResources();
            isRound = insets.isRound();
            float textSize = resources.getDimension(isRound
                    ? R.dimen.digital_text_size_round : R.dimen.digital_text_size);
            float smallTextSize = resources.getDimension(isRound
                    ? R.dimen.digital_text_size_round_small : R.dimen.digital_text_size_small);

            float countdownTextSize = resources.getDimension(R.dimen.digital_text_size_countdown);

            mTextPaint.setTextSize(textSize);
            mDatePaint.setTextSize(smallTextSize);
            mLaunchName.setTextSize(smallTextSize);
            mLaunchTime.setTextSize(countdownTextSize);
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
            // Draw the background.
            if (isInAmbientMode()) {
                canvas.drawColor(Color.BLACK);
            } else {
                canvas.drawBitmap(background, 0, 0, null);
            }

            Date now = new Date();
            mTime = Calendar.getInstance();
            if (twentyfourhourmode) {
                // Draw H:MM in ambient mode or H:MM:SS in interactive mode.
                SimpleDateFormat sdf = new SimpleDateFormat("kk:mm");
                timeText = sdf.format(now);
            } else {
                // Draw H:MM in ambient mode or H:MM:SS in interactive mode.
                SimpleDateFormat sdf = new SimpleDateFormat("h:mm");
                timeText = sdf.format(now);
            }


            Calendar utcTime = Calendar.getInstance(timeZone);
            utcTime.setTimeInMillis(mTime.getTimeInMillis());

            Date date = new Date(mTime.getTimeInMillis());

            String dateText = dateFormat.format(date);
            String utcText = String.format("%d:%02d UTC",
                    utcTime.get(Calendar.HOUR_OF_DAY),
                    utcTime.get(Calendar.MINUTE));

            long longdate = launchTime;
            longdate = longdate * 1000;
            final Date mDate = new Date(longdate);
            Calendar future = DateToCalendar(mDate);
            long timeToFinish = future.getTimeInMillis() - mTime.getTimeInMillis();
            if (timeToFinish <= 0){
                launchName = "Syncing Next Launch...";
                launchTime = 0;
            }

            //Parse time into day, hour, minute, and second
            String day = String.valueOf(TimeUnit.MILLISECONDS.toDays(timeToFinish));
            String hour;
            String minute;
            String second;

            if (TimeUnit.MILLISECONDS.toHours(timeToFinish) -
                    TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(timeToFinish)) < 10){
                hour = "0" + String.valueOf(TimeUnit.MILLISECONDS.toHours(timeToFinish) -
                        TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(timeToFinish)));
            } else {
                hour = String.valueOf(TimeUnit.MILLISECONDS.toHours(timeToFinish) -
                        TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(timeToFinish)));
            }

            if (TimeUnit.MILLISECONDS.toMinutes(timeToFinish) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeToFinish)) < 10){

                minute = "0" + String.valueOf(TimeUnit.MILLISECONDS.toMinutes(timeToFinish) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeToFinish)));
            } else {
                minute = String.valueOf(TimeUnit.MILLISECONDS.toMinutes(timeToFinish) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeToFinish)));
            }

            if (TimeUnit.MILLISECONDS.toSeconds(timeToFinish) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeToFinish)) < 10){

                second = "0" + String.valueOf(TimeUnit.MILLISECONDS.toSeconds(timeToFinish) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeToFinish)));
            } else {
                second = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(timeToFinish) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeToFinish)));
            }

            String countdownTimer = String.format("L - %s %s:%s:%s", day, hour, minute, second);

            //If round, align text in a rough cross.
            if (isRound) {

                timeXoffset = computeXOffset(timeText, mTextPaint, bounds);
                timeYoffset = computeTimeYOffset(timeText, mTextPaint, bounds);

                //Current Time
                canvas.drawText(timeText,
                        timeXoffset,
                        timeYoffset,
                        mTextPaint);

                //Date Text
                canvas.drawText(dateText,
                        computeDateRoundOffset(dateText, mDatePaint, bounds),
                        computeCenterYOffset(dateText, mDatePaint, bounds) + 1,
                        mDatePaint);

                //UTC Text
                canvas.drawText(utcText,
                        computeUTCRoundOffset(timeText, mDatePaint, bounds),
                        computeCenterYOffset(utcText, mDatePaint, bounds) + 1,
                        mDatePaint);

                //Launch Name

                if (launchName != null) {
                    canvas.drawText(launchName,
                            bounds.centerX() - (mLaunchName.measureText(launchName))/2,
                            computeBottomYOffset(dateText, mLaunchName, bounds) - 10,
                            mLaunchName);
                }

                //Launch Countdown/Status
                if (launchTime != 0) {
                    canvas.drawText(countdownTimer,
                            bounds.centerX() - (mLaunchTime.measureText(countdownTimer))/2,
                            computeBottomYOffset(dateText, mLaunchTime, bounds) - 34,
                            mLaunchTime);
                }
            // Else align text in a manner that fits
            } else {

                watchFaceStyle = new WatchFaceStyle.Builder(SpaceLaunchWatchFace.this)
                        .setCardPeekMode(WatchFaceStyle.PEEK_MODE_VARIABLE)
                        .setHotwordIndicatorGravity(Gravity.CENTER_HORIZONTAL)
                        .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                        .setShowSystemUiTime(false)
                        .build();
                setWatchFaceStyle(watchFaceStyle);

                timeXoffset = computeXStart(timeText, mTextPaint, bounds) + 76;
                timeYoffset = computeTimeYOffset(timeText, mTextPaint, bounds);

                canvas.drawText(timeText,
                        timeXoffset - 5,
                        timeYoffset,
                        mTextPaint);

                canvas.drawText(dateText,
                        timeXoffset + 15,
                        timeYoffset + 24,
                        mDatePaint);

                canvas.drawText(utcText,
                        timeXoffset + 15,
                        timeYoffset + 48,
                        mDatePaint);

                if (launchName != null) {
                    canvas.drawText(launchName,
                            bounds.centerX() - (mLaunchName.measureText(launchName))/2,
                            computeBottomYOffset(dateText, mLaunchName, bounds) - 10,
                            mLaunchName);
                }

                //Launch Countdown/Status
                if (launchTime != 0) {
                    canvas.drawText(countdownTimer,
                            bounds.centerX() - (mLaunchTime.measureText(countdownTimer))/2,
                            computeBottomYOffset(dateText, mLaunchTime, bounds) - 34,
                            mLaunchTime);
                }
            }
        }

        private float computeXOffset(String text, Paint paint, Rect watchBounds) {
            float centerX = watchBounds.exactCenterX();
            float timeLength = paint.measureText(text);
            return centerX - (timeLength / 2.0f);
        }

        private float computeTimeYOffset(String timeText, Paint timePaint, Rect watchBounds) {
            float top = watchBounds.top + 85;
            Rect textBounds = new Rect();
            timePaint.getTextBounds(timeText, 0, timeText.length(), textBounds);
            int textHeight = textBounds.height();
            return top + (textHeight / 2.0f);
        }

        private float computeDateRoundOffset(String dateText, Paint datePaint, Rect watchBounds) {
            float centerX = watchBounds.right - 60;
            float dateLength = datePaint.measureText(dateText);
            return centerX - (dateLength / 2.0f);
        }

        private float computeUTCRoundOffset(String dateText, Paint datePaint, Rect watchBounds) {
            float centerX = watchBounds.left + 35;
            float dateLength = datePaint.measureText(dateText);
            return centerX - (dateLength / 2.0f);
        }

        private float computeSquareXOffset(String dateText, Paint datePaint, Rect watchBounds) {
            float centerX = watchBounds.exactCenterX() - 95;
            float dateLength = datePaint.measureText(dateText);
            return centerX - (dateLength / 2.0f);
        }

        private float computeXStart(String text, Paint paint, Rect watchBounds) {
            float centerX = watchBounds.left + 40;
            float timeLength = paint.measureText(text);
            return centerX - (timeLength / 2.0f);
        }

        private float computeCenterYOffset(String dateText, Paint datePaint, Rect watchBounds) {
            float centerY = watchBounds.exactCenterY();
            Rect textBounds = new Rect();
            datePaint.getTextBounds(dateText, 0, dateText.length(), textBounds);
            int textHeight = textBounds.height();
            return centerY + (textHeight / 2.0f);
        }

        private float computeCenterXOffset(String dateText, Paint datePaint, Rect watchBounds) {
            float centerX = watchBounds.exactCenterX();
            Rect textBounds = new Rect();
            datePaint.getTextBounds(dateText, 0, dateText.length(), textBounds);
            int textHeight = textBounds.height();
            return centerX + (textHeight / 2.0f);
        }

        private float computeBottomYOffset(String dateText, Paint datePaint, Rect watchBounds) {
            float centerY = watchBounds.bottom - 70;
            Rect textBounds = new Rect();
            datePaint.getTextBounds(dateText, 0, dateText.length(), textBounds);
            int textHeight = textBounds.height();
            return centerY + (textHeight / 2.0f);
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
            Log.e("Space Launch Wear", "onConnectionFailed " + connectionResult.getErrorMessage());
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
                    Log.v("Space Launch Wear", "Name = " + launchTime);
                }
            }
            if (item.getUri().getPath().equals("/config")) {
                DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                if (dataMap.containsKey(HOUR_KEY)) {
                    twentyfourhourmode = dataMap.getBoolean(HOUR_KEY);
                    Timber.v("24 Hour Mode = %s", twentyfourhourmode);
                }

//                if (dataMap.containsKey(BACKGROUND_KEY)) {
//                    custombackground = dataMap.getBoolean(BACKGROUND_KEY);
//                    Timber.v("Background = %s", custombackground);
//                    asset = dataMap.getAsset("background");
//                    customBackgroundBitmap = loadBitmapFromAsset(asset);
//                    background = customBackgroundBitmap;
//                }
            }
        }

        public Bitmap loadBitmapFromAsset(Asset asset) {
            if (asset == null) {
                throw new IllegalArgumentException("Asset must be non-null");
            }
            ConnectionResult result =
                    googleApiClient.blockingConnect(1000, TimeUnit.MILLISECONDS);
            if (!result.isSuccess()) {
                return null;
            }
            // convert asset into a file descriptor and block until it's ready
            InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                    googleApiClient, asset).await().getInputStream();
            googleApiClient.disconnect();

            if (assetInputStream == null) {
                Log.w("Space launch Now", "Requested an unknown Asset.");
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
