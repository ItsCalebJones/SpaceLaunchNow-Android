package me.calebjones.spacelaunchnow.ui.supporter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.transitionseverywhere.TransitionManager;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.common.BaseActivity;
import me.calebjones.spacelaunchnow.content.jobs.SyncWearJob;
import me.calebjones.spacelaunchnow.content.jobs.UpdateWearJob;
import me.calebjones.spacelaunchnow.content.wear.WearWatchfaceManager;
import me.calebjones.spacelaunchnow.data.models.Products;
import me.calebjones.spacelaunchnow.ui.imageviewer.FullscreenImageActivity;
import me.calebjones.spacelaunchnow.utils.analytics.Analytics;
import me.calebjones.spacelaunchnow.utils.views.SnackbarHandler;
import timber.log.Timber;

public class SupporterActivity extends BaseActivity implements BillingProcessor.IBillingHandler {

    @BindView(R.id.purchase)
    AppCompatButton purchaseButton;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.support_thank_you)
    View supportThankYou;
    @BindView(R.id.fab_supporter)
    FloatingActionButton fabSupporter;
    @BindView(R.id.nested_scroll_view)
    NestedScrollView nestedScrollView;

    private BillingProcessor bp;
    private ImageView icon;
    private AppCompatSeekBar seekbar;
    private TextView text;
    private boolean isAvailable;
    private boolean isRefreshable = true;

    public SupporterActivity() {
        super("Supporter Activity");
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int m_theme;
        final Context context = this;

        m_theme = R.style.BaseAppTheme;
        setTheme(m_theme);

        setContentView(R.layout.activity_support);
        ButterKnife.bind(this);

        if (SupporterHelper.isSupporter()) {
            purchaseButton.setText("You again? Sure!");
            enterReveal(supportThankYou);
        } else {
            purchaseButton.setText("Become a Supporter");
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("");

        isAvailable = BillingProcessor.isIabServiceAvailable(context);
        if (isAvailable) {
            // continue
            bp = new BillingProcessor(this, getResources().getString(R.string.rsa_key), this);
        } else {
            SnackbarHandler.showErrorSnackbar(this, coordinatorLayout, "Google Play billing services not available.");
        }

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    fabSupporter.hide();
                } else {
                    if (!fabSupporter.isShown()){
                        fabSupporter.show();
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Analytics.from(this).sendButtonClicked("Back - From Supporter Page");
            onBackPressed();
        }

        if (id == R.id.action_restore) {
            isRefreshable = true;
            restorePurchaseHistory();
        }

        if (id == R.id.action_support) {
            new MaterialDialog.Builder(this)
                    .title("Need Support?")
                    .content("The fastest and most reliable way to get support is through Discord. If thats not an option feel free to email me directly.")
                    .neutralText("Email")
                    .positiveColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .neutralColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .negativeText("Cancel")
                    .positiveText("Discord")
                    .onNeutral(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.parse("mailto:"));
                            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@calebjones.me"});
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Space Launch Now - Feedback");

                            startActivity(Intent.createChooser(intent, "Email via..."));
                        }
                    })
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            String url = "https://discord.gg/WVfzEDW";
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .show();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bp != null) {
            bp.release();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_support, menu);
        return true;
    }

    @OnClick({R.id.purchase, R.id.fab_supporter})
    public void checkClick() {
        Analytics.from(this).sendButtonClicked("Supporter Button clicked.");
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Thanks for your Support!")
                .customView(R.layout.seekbar_dialog_supporter, true)
                .positiveText("Ok")
                .neutralText("BTC | ETH | LTC")
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        showCryptoDialog();
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        makePurchase(seekbar.getProgress());
                    }
                })
                .show();

        seekbar = dialog.getCustomView().findViewById(R.id.dialog_seekbar);
        icon = dialog.getCustomView().findViewById(R.id.dialog_icon);
        text = dialog.getCustomView().findViewById(R.id.dialog_text);

        setIconPosition(1);
        seekbar.setProgress(1);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean b) {
                setIconPosition(position);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void showCryptoDialog(){
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Thanks for your Support!")
                .customView(R.layout.dialog_crypto, true)
                .positiveText("Ok")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
        ImageView ltcIcon = dialog.getCustomView().findViewById(R.id.ltc_icon);
        ImageView btcIcon = dialog.getCustomView().findViewById(R.id.btc_icon);
        ImageView ethIcon = dialog.getCustomView().findViewById(R.id.eth_icon);

        ltcIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent animateIntent = new Intent(getApplicationContext(), FullscreenImageActivity.class);
                animateIntent.putExtra("image", "ltc");
                getApplicationContext().startActivity(animateIntent);
            }
        });

        btcIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent animateIntent = new Intent(getApplicationContext(), FullscreenImageActivity.class);
                animateIntent.putExtra("image", "btc");
                getApplicationContext().startActivity(animateIntent);
            }
        });

        ethIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent animateIntent = new Intent(getApplicationContext(), FullscreenImageActivity.class);
                animateIntent.putExtra("image", "eth");
                getApplicationContext().startActivity(animateIntent);
            }
        });
    }

    private void makePurchase(int product) {
        String sku = null;
        switch (product) {
            case 0:
                sku = SupporterHelper.SKU_TWO_DOLLAR;
                break;
            case 1:
                sku = SupporterHelper.SKU_SIX_DOLLAR;
                break;
            case 2:
                sku = SupporterHelper.SKU_TWELVE_DOLLAR;
                break;
            case 3:
                sku = SupporterHelper.SKU_THIRTY_DOLLAR;
                break;
        }

        //Get Product from SKU
        Products products = SupporterHelper.getProduct(sku);
        Analytics.from(this).sendAddToCartEvent(products, sku);

        //Initiate purchase
        if (BillingProcessor.isIabServiceAvailable(this)) {
            Analytics.from(this).sendStartCheckout(products);
            bp.purchase(this, sku);
        } else {
            SnackbarHandler.showErrorSnackbar(this, coordinatorLayout, "Issues connecting to Google Play Billing");
        }
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Timber.v("%s purchased.", productId);
        SnackbarHandler.showInfoSnackbar(this, coordinatorLayout, "Thanks for helping keep the gears turning!");
        animatePurchase();
        Products product = SupporterHelper.getProduct(productId);
        getRealm().beginTransaction();
        getRealm().copyToRealmOrUpdate(product);
        getRealm().commitTransaction();
        UpdateWearJob.scheduleJobNow();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("weather", true);
        editor.apply();
        Timber.i("Purchase Data: %s", details.purchaseInfo.purchaseData.packageName);
        Timber.i("Purchase Data: %s", details.purchaseInfo.purchaseData.developerPayload);
        Timber.i("Purchase Data: %s", details.purchaseInfo.purchaseData.orderId);
        Timber.i("Purchase Data: %s", details.purchaseInfo.purchaseData.purchaseTime);
        Timber.i("Purchase Data: %s", details.purchaseInfo.purchaseData.purchaseToken);
        Timber.i("Purchase Data: %s", details.purchaseInfo.purchaseData.purchaseState.name());
        Analytics.from(this).sendPurchaseEvent(product, productId);
    }

    @Override
    public void onPurchaseHistoryRestored() {
        Timber.d("Purchase History restored.");
        restorePurchaseHistory();
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        if (error != null) {
            SnackbarHandler.showErrorSnackbar(this, coordinatorLayout, error.getLocalizedMessage());
        } else if (errorCode != 0) {
            SnackbarHandler.showErrorSnackbar(this, coordinatorLayout, "billing error code - " + errorCode);
        }
    }

    @Override
    public void onBillingInitialized() {
        Timber.v("Billing initialized.");
        restorePurchaseHistory();
    }

    private void restorePurchaseHistory() {
        if (isAvailable) {
            if (isRefreshable) {
                isRefreshable = false;
                if (bp == null) {
                    bp = new BillingProcessor(this, getResources().getString(R.string.rsa_key), this);
                }
                bp.loadOwnedPurchasesFromGoogle();
                if (bp != null && bp.listOwnedProducts().size() > 0) {
                    animatePurchase();
                    Timber.d("Purchase History - Number of items purchased: %s", bp.listOwnedProducts().size());
                    for (final String sku : bp.listOwnedProducts()) {
                        Timber.v("Purchase History - SKU: %s", sku);
                        Products product = SupporterHelper.getProduct(sku);
                        getRealm().beginTransaction();
                        getRealm().copyToRealmOrUpdate(product);
                        getRealm().commitTransaction();
                    }
                    SnackbarHandler.showInfoSnackbar(this, coordinatorLayout, "Purchase history restored.");
                } else {
                    Timber.d("Purchase History - None purchased.");
                }
            }
        } else {
            SnackbarHandler.showErrorSnackbar(this, coordinatorLayout, "Google Play billing services not available.");
        }
        UpdateWearJob.scheduleJobNow();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void animatePurchase() {
        if (supportThankYou.getVisibility() != View.VISIBLE) {
            enterReveal(supportThankYou);
        }
    }

    void enterReveal(View view) {
        // previously invisible view
        final View myView = view;
        //To not have empty scroll, the container is INVISIBLE with 0dp height.
        //Otherwise the Reveal effect will not work at the first click.
        //Here I set the parameters programmatically.
        myView.setLayoutParams(new AppBarLayout.LayoutParams(
                AppBarLayout.LayoutParams.MATCH_PARENT,
                AppBarLayout.LayoutParams.WRAP_CONTENT
        ));
        TransitionManager.beginDelayedTransition(coordinatorLayout);
        myView.setVisibility(View.VISIBLE);
    }


    public void setIconPosition(int iconPosition) {

        switch (iconPosition) {
            case 0:
                text.setText("Bronze - $1.99");
                icon.setImageDrawable(new IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_local_drink)
                        .color(Color.BLACK)
                        .sizeDp(96));
                break;
            case 1:
                text.setText("Silver - $5.99");
                icon.setImageDrawable(new IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_local_cafe)
                        .color(Color.BLACK)
                        .sizeDp(96));
                break;
            case 2:
                text.setText("Gold - $11.99");
                icon.setImageDrawable(new IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_local_dining)
                        .color(Color.BLACK)
                        .sizeDp(96));
                break;
            case 3:
                text.setText("Platinum - $29.99");
                icon.setImageResource(R.drawable.take_my_money);
                break;
        }
    }
}
