/*
 * Copyright (c) 2017    Mathijs Lagerberg, Pixplicity BV
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

package me.calebjones.spacelaunchnow.common.customviews.generate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import me.calebjones.spacelaunchnow.R;

/**
 * When your app has launched a couple of times, this class will ask to give your app a rating on
 * the Play Store. If the user does not want to rate your app and indicates a complaint, you have
 * the option to redirect them to a feedback link.
 * <p>
 * To use, call the following on every app start (or when appropriate):<br>
 * <code>
 * Rate mRate = new Rate.Builder(context)
 * .setTriggerCount(10)
 * .setMinimumInstallTime(TimeUnit.DAYS.toMillis(7))
 * .setMessage(R.string.my_message_text)
 * .setSnackBarParent(view)
 * .build();
 * mRate.count();
 * </code>
 * When it is a good time to show a rating request, call:
 * <code>
 * mRate.showRequest();
 * </code>
 * </p>
 */
public final class Rate {

    private static final String PREFS_NAME = "pirate";
    private static final String KEY_INT_LAUNCH_COUNT = "launch_count";
    private static final String KEY_LONG_LAUNCH_COUNT = "launch_count_l";
    private static final String KEY_BOOL_ASKED = "asked";
    private static final String KEY_LONG_FIRST_LAUNCH = "first_launch";
    private static final int DEFAULT_COUNT = 6;
    private static final int DEFAULT_REPEAT_COUNT = 15;
    private static final long DEFAULT_INSTALL_TIME = TimeUnit.DAYS.toMillis(3);
    private static final boolean DEFAULT_CHECKED = false;

    private final SharedPreferences mPrefs;
    private final String mPackageName;
    private final Context mContext;
    private CharSequence mMessage, mTextPositive, mTextNegative, mTextCancel, mTextNever;
    private int mTriggerCount = DEFAULT_COUNT;
    private long mMinInstallTime = DEFAULT_INSTALL_TIME;
    private ViewGroup mParentView;
    private OnFeedbackListener mFeedbackAction;
    private boolean mSnackBarSwipeToDismiss = true;
    private Snackbar snackbar;

    private Rate(@NonNull Context context) {
        mContext = context;
        mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        mPackageName = context.getPackageName();
        mMessage = context.getString(R.string.please_rate);
        mTextPositive = context.getString(R.string.button_yes);
        mTextNegative = context.getString(R.string.button_feedback);
        mTextCancel = context.getString(R.string.button_no);
        mTextNever = context.getString(R.string.button_dont_ask);
    }

    /**
     * Use {@link #count()} instead
     *
     * @return the {@link Rate} instance
     */
    @Deprecated
    public Rate launched() {
        return count();
    }

    /**
     * Call this method whenever your app is launched to increase the launch counter. Or whenever
     * the user performs an action that indicates immersion.
     *
     * @return the {@link Rate} instance
     */
    @NonNull
    public Rate count() {
        return increment(false);
    }

    @NonNull
    private Rate increment(final boolean force) {
        Editor editor = mPrefs.edit();
        // Get current launch count
        long count = getCount();
        // Increment, but only when we're not on a launch point. Otherwise we could miss
        // it when .count and .showRequest calls are not called exactly alternated
        final boolean isAtLaunchPoint = getRemainingCount() == 0;
        if (force || !isAtLaunchPoint) {
            count++;
        }
        editor.putLong(KEY_LONG_LAUNCH_COUNT, count).apply();
        // Save first launch timestamp
        if (mPrefs.getLong(KEY_LONG_FIRST_LAUNCH, -1) == -1) {
            editor.putLong(KEY_LONG_FIRST_LAUNCH, System.currentTimeMillis());
        }
        editor.apply();
        return this;
    }

    /**
     * Returns how often The Action has been performed, ever. This is usually the app launch event.
     *
     * @return Number of times the app was launched.
     */
    private long getCount() {
        long count = mPrefs.getLong(KEY_LONG_LAUNCH_COUNT, 0L);
        // For apps ugrading from the 1.1.6 version:
        if (count == 0) {
            count = mPrefs.getInt(KEY_INT_LAUNCH_COUNT, 0);
        }
        return count;
    }

    /**
     * Returns how many more times the trigger action should be performed before it triggers the
     * rating request. This can be either the first request or consequent requests after dismissing
     * previous ones. This method does NOT consider if the request will be shown at all, e.g. when
     * "don't ask again" was checked.
     * <p>
     * If this method returns `0` (zero), the next call to {@link #showRequest()} will show the dialog.
     * </p>
     *
     * @return Remaining count before the next request is triggered.
     */
    public long getRemainingCount() {
        long count = getCount();
        if (count < mTriggerCount) {
            return mTriggerCount - count;
        } else {
            return (DEFAULT_REPEAT_COUNT - ((count - mTriggerCount) % DEFAULT_REPEAT_COUNT)) % DEFAULT_REPEAT_COUNT;
        }
    }

    /**
     * Use {@link #showRequest()} instead
     *
     * @return See {@link #showRequest()}
     */
    @Deprecated
    public boolean check() {
        return showRequest();
    }

    /**
     * Checks if the app has been launched often enough to ask for a rating, and shows the rating
     * request if so. The rating request can be a SnackBar (preferred) or a dialog.
     *
     * @return If the request is shown or not
     * @see Builder#setSnackBarParent(ViewGroup)
     */
    public boolean showRequest() {
        final boolean asked = mPrefs.getBoolean(KEY_BOOL_ASKED, false);
        final long firstLaunch = mPrefs.getLong(KEY_LONG_FIRST_LAUNCH, 0);
        final boolean shouldShowRequest =
                getRemainingCount() == 0
                        && !asked
                        && System.currentTimeMillis() > firstLaunch + mMinInstallTime;
        if (shouldShowRequest) {
            showRatingRequest();
        }
        return shouldShowRequest;
    }

    /**
     * Creates an Intent to launch the proper store page. This does not guarantee the Intent can be
     * launched (i.e. that the Play Store is installed).
     *
     * @return The Intent to launch the store.
     */
    @NonNull
    private Intent getStoreIntent() {
        final Uri uri = Uri.parse("market://details?id=" + mPackageName);
        return new Intent(Intent.ACTION_VIEW, uri);
    }

    /**
     * Shows the rating request immediately. For testing.
     *
     * @return the {@link Rate} instance
     */
    @NonNull
    public Rate test() {
        showRatingRequest();
        return this;
    }

    /**
     * Resets all data saved by Gene-rate. This is not advised in production builds
     * as behavior against user preferences can occur.
     *
     * @return the {@link Rate} instance
     */
    @NonNull
    public Rate reset() {
        mPrefs.edit().clear().apply();
        return this;
    }

    private void showRatingRequest() {
        increment(true);
        if (mParentView == null) {
            showRatingDialog();
        } else {
            showRatingSnackbar();
        }
    }

    private void showRatingSnackbar() {
        // Wie is hier nou de snackbar?
        snackbar = Snackbar.make(mParentView, mMessage,
                mSnackBarSwipeToDismiss ? Snackbar.LENGTH_INDEFINITE : Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();

        // Hide default text
        TextView textView = layout.findViewById(R.id.snackbar_text);
        textView.setVisibility(View.INVISIBLE);

        // Inflate our custom view
        final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams")
        View snackView = inflater.inflate(R.layout.in_snackbar, null);
        // Configure the view
        TextView tvMessage = snackView.findViewById(R.id.text);
        tvMessage.setText(mMessage);
        final CheckBox checkBox = snackView.findViewById(R.id.cb_never);
        checkBox.setText(mTextNever);
        checkBox.setChecked(DEFAULT_CHECKED);
        final Button btFeedback = snackView.findViewById(R.id.bt_negative);
        btFeedback.setPaintFlags(btFeedback.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        final Button btRate = snackView.findViewById(R.id.bt_positive);
        snackView.findViewById(R.id.tv_swipe).setVisibility(
                mSnackBarSwipeToDismiss ? View.VISIBLE : View.GONE);

        // Remember to not ask again if user swiped it
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, @DismissEvent int event) {
                super.onDismissed(transientBottomBar, event);
                if (event == Snackbar.Callback.DISMISS_EVENT_SWIPE
                        && checkBox.isChecked()) {
                    saveAsked();
                }
            }
        });

        // Rate listener
        btRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                openPlayStore();
                saveAsked();
            }
        });
        // Feedback listener
        if (mFeedbackAction != null) {
            btFeedback.setText(mTextNegative);
            btFeedback.setVisibility(View.VISIBLE);
            btFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()) {
                        saveAsked();
                    }
                    snackbar.dismiss();
                    mFeedbackAction.onFeedbackTapped();
                }
            });
        }
        // Checkbox listener
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean checked) {
                mPrefs.edit().putBoolean(KEY_BOOL_ASKED, checked).apply();
            }
        });

        // Add the view to the Snackbar's layout
        layout.addView(snackView, 0);

        // Show the Snackbar
        snackbar.show();
    }

    public boolean isShown(){
        if (snackbar != null){
            return snackbar.isShown();
        }
        return false;
    }

    private void showRatingDialog() {
        LayoutInflater inflater;
        if (mContext instanceof Activity) {
            inflater = ((Activity) mContext).getLayoutInflater();
        } else {
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        assert inflater != null;
        @SuppressLint("InflateParams") final ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.in_dialog, null);
        final CheckBox checkBox = layout.findViewById(R.id.cb_never);
        checkBox.setText(mTextNever);
        checkBox.setChecked(DEFAULT_CHECKED);
        final Button btFeedback = layout.findViewById(R.id.bt_negative);
        btFeedback.setPaintFlags(btFeedback.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Build dialog with positive and cancel buttons
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setMessage(mMessage)
                .setView(layout)
                .setCancelable(false)
                // OK -> redirect to Play Store and never ask again
                .setPositiveButton(mTextPositive, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface anInterface, int i) {
                        openPlayStore();
                        saveAsked();
                        anInterface.dismiss();
                    }
                })
                // Cancel -> close dialog, ask again later
                .setNeutralButton(mTextCancel, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface anInterface, int i) {
                        if (checkBox.isChecked()) {
                            saveAsked();
                        }
                        anInterface.dismiss();
                    }
                });

        // If possible, make dialog cancelable and remember checkbox state on cancel
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
            builder
                    .setCancelable(true)
                    .setOnDismissListener(new OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface anInterface) {
                            if (checkBox.isChecked()) {
                                saveAsked();
                            }
                        }
                    });
        }

        // Create dialog before we can continue
        final AlertDialog dialog = builder.create();

        // If negative button action is set, add negative button
        if (mFeedbackAction != null) {
            // Nooope -> redirect to feedback form
            btFeedback.setText(mTextNegative);
            btFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (checkBox.isChecked()) {
                        saveAsked();
                    }
                    dialog.dismiss();
                    mFeedbackAction.onFeedbackTapped();
                }
            });
        }

        // Go go go!
        dialog.show();
    }

    private void openPlayStore() {
        final Intent intent = getStoreIntent();
        if (!(mContext instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        try {
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e){
            Toast.makeText(mContext, "Unable to launch the Play Store", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Checks if the app can be rated, i.e. if the store Intent can be launched, i.e. if the Play
     * Store is installed.
     *
     * @return if the app can be rated
     * @see #getStoreIntent()
     */
    private boolean canRateApp() {
        return canOpenIntent(getStoreIntent());
    }

    /**
     * Checks if the system or any 3rd party app can handle the Intent
     *
     * @param intent the Intent
     * @return if the Intent can be handled by the system
     */
    private boolean canOpenIntent(@NonNull Intent intent) {
        return mContext
                .getPackageManager()
                .queryIntentActivities(intent, 0)
                .size()
                > 0;
    }

    private void saveAsked() {
        mPrefs.edit().putBoolean(KEY_BOOL_ASKED, true).apply();
    }

    @SuppressWarnings({"unused", "WeakerAccess", "SameParameterValue"})
    public static class Builder {

        private final Rate mRate;

        public Builder(@NonNull Context context) {
            mRate = new Rate(context);
        }

        /**
         * Set number of times {@link #count()} should be called before triggering the rating
         * request
         *
         * @param count Number of times (inclusive) to call {@link #count()} before rating
         *              request should show. Defaults to {@link #DEFAULT_COUNT}
         * @return The current {@link Builder}
         */
        @NonNull
        public Builder setTriggerCount(int count) {
            mRate.mTriggerCount = count;
            return this;
        }

        /**
         * Set amount of time the app should be installed before asking for a rating. Defaults to 5
         * days.
         *
         * @param millis Amount of time in milliseconds the app should be installed before asking a
         *               rating.
         * @return The current {@link Builder}
         */
        @NonNull
        public Builder setMinimumInstallTime(long millis) {
            mRate.mMinInstallTime = millis;
            return this;
        }

        /**
         * Sets the message to show in the rating request.
         *
         * @param message The message that asks the user for a rating
         * @return The current {@link Builder}
         * @see #setMessage(int)
         */
        @NonNull
        public Builder setMessage(@Nullable CharSequence message) {
            mRate.mMessage = message;
            return this;
        }

        /**
         * Sets the message to show in the rating request.
         *
         * @param resId The message that asks the user for a rating
         * @return The current {@link Builder}
         * @see #setMessage(CharSequence)
         */
        @NonNull
        public Builder setMessage(@StringRes int resId) {
            return setMessage(mRate.mContext.getString(resId));
        }

        /**
         * Sets the text to show in the rating request on the positive button.
         *
         * @param message The text on the positive button
         * @return The current {@link Builder}
         * @see #setPositiveButton(int)
         */
        @NonNull
        public Builder setPositiveButton(@Nullable CharSequence message) {
            mRate.mTextPositive = message;
            return this;
        }

        /**
         * Sets the text to show in the rating request on the positive button.
         *
         * @param resId The text on the positive button
         * @return The current {@link Builder}
         * @see #setPositiveButton(CharSequence)
         */
        @NonNull
        public Builder setPositiveButton(@StringRes int resId) {
            return setPositiveButton(mRate.mContext.getString(resId));
        }

        /**
         * Sets the text to show in the rating request on the negative button.
         *
         * @param message The text on the negative button
         * @return The current {@link Builder}
         * @see #setNegativeButton(int)
         */
        @NonNull
        public Builder setNegativeButton(@Nullable CharSequence message) {
            mRate.mTextNegative = message;
            return this;
        }

        /**
         * Sets the text to show in the rating request on the negative button.
         *
         * @param resId The text on the negative button
         * @return The current {@link Builder}
         * @see #setNegativeButton(CharSequence)
         */
        @NonNull
        public Builder setNegativeButton(@StringRes int resId) {
            return setNegativeButton(mRate.mContext.getString(resId));
        }

        /**
         * Sets the text to show in the rating request on the cancel button.
         * Note that this will not be used when using a SnackBar.
         *
         * @param message The text on the cancel button
         * @return The current {@link Builder}
         * @see #setSnackBarParent(ViewGroup)
         * @see #setCancelButton(int)
         */
        @NonNull
        public Builder setCancelButton(@Nullable CharSequence message) {
            mRate.mTextCancel = message;
            return this;
        }

        /**
         * Sets the text to show in the rating request on the cancel button.
         * Note that this will not be used when using a SnackBar.
         *
         * @param resId The text on the cancel button
         * @return The current {@link Builder}
         * @see #setSnackBarParent(ViewGroup)
         * @see #setCancelButton(CharSequence)
         */
        @NonNull
        public Builder setCancelButton(@StringRes int resId) {
            return setCancelButton(mRate.mContext.getString(resId));
        }

        /**
         * Sets the text to show in the rating request on the checkbox.
         *
         * @param message The text on the checkbox
         * @return The current {@link Builder}
         */
        @NonNull
        public Builder setNeverAgainText(@Nullable CharSequence message) {
            mRate.mTextNever = message;
            return this;
        }

        /**
         * Sets the text to show in the rating request on the checkbox.
         *
         * @param resId The text on the checkbox
         * @return The current {@link Builder}
         */
        @NonNull
        public Builder setNeverAgainText(@StringRes int resId) {
            return setNeverAgainText(mRate.mContext.getString(resId));
        }

        /**
         * Sets the Uri to open when the user clicks the feedback button.
         * This can use the scheme `mailto:`, `tel:`, `geo:`, `https:`, etc.
         *
         * @param uri The Uri to open, or {@code null} to hide the feedback button
         * @return The current {@link Builder}
         * @see #setFeedbackAction(OnFeedbackListener)
         */
        @NonNull
        public Builder setFeedbackAction(@Nullable final Uri uri) {
            if (uri == null) {
                mRate.mFeedbackAction = null;
            } else {
                mRate.mFeedbackAction = new OnFeedbackListener() {

                    @Override
                    public void onFeedbackTapped() {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (mRate.canOpenIntent(intent)) {
                            mRate.mContext.startActivity(intent);
                        }
                    }
                };
            }
            return this;
        }

        /**
         * Sets the action to perform when the user clicks the feedback button.
         *
         * @param action Callback when the user taps the feedback button, or {@code null} to hide
         *               the feedback button
         * @return The current {@link Builder}
         * @see #setFeedbackAction(Uri)
         */
        @NonNull
        public Builder setFeedbackAction(@Nullable OnFeedbackListener action) {
            mRate.mFeedbackAction = action;
            return this;
        }

        /**
         * Sets the parent view for a Snackbar. This enables the use of a Snackbar for the rating
         * request instead of the default dialog.
         *
         * @param parent The parent view to put the Snackbar in, or {@code null} to disable the
         *               Snackbar
         * @return The current {@link Builder}
         */
        @NonNull
        public Builder setSnackBarParent(@Nullable ViewGroup parent) {
            mRate.mParentView = parent;
            return this;
        }

        /**
         * Shows or hides the 'swipe to dismiss' notion in the Snackbar. When disabled, the
         * Snackbar will automatically hide after a view seconds. When enabled, the Snackbar will
         * show indefinitively until dismissed by the user. <strong>Note that the
         * Snackbar can only be swiped when one of the parent views is a
         * {@code CoordinatorLayout}!</strong> Also, <strong>toggling this does not change
         * if the Snackbar can actually be swiped to dismiss!</strong>
         *
         * @param visible Show/hide the 'swipe to dismiss' text, and disable/enable auto-hide.
         *                Default is {code true}.
         * @return The current {@link Builder}
         */
        @NonNull
        public Builder setSwipeToDismissVisible(boolean visible) {
            mRate.mSnackBarSwipeToDismiss = visible;
            return this;
        }

        /**
         * Build the {@link Rate} instance
         *
         * @return a new Rate instance as configured by the current {@link Builder}
         */
        @NonNull
        public Rate build() {
            return mRate;
        }
    }
}
