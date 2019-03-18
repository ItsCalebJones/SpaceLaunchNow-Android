package me.calebjones.spacelaunchnow.common.ui.supporter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.transitionseverywhere.TransitionManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.calebjones.spacelaunchnow.common.R;
import me.calebjones.spacelaunchnow.common.R2;
import me.calebjones.spacelaunchnow.common.base.BaseActivity;
import me.calebjones.spacelaunchnow.common.content.worker.WearSyncWorker;
import me.calebjones.spacelaunchnow.common.ui.views.SnackbarHandler;
import me.calebjones.spacelaunchnow.data.models.Products;
import timber.log.Timber;

import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_OK;
import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_USER_CANCELED;

public class SupporterActivity extends BaseActivity implements BillingProcessor.IBillingHandler {

    @BindView(R2.id.purchase)
    AppCompatButton purchaseButton;
    @BindView(R2.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R2.id.support_thank_you)
    View supportThankYou;
    @BindView(R2.id.fab_supporter)
    FloatingActionButton fabSupporter;
    @BindView(R2.id.nested_scroll_view)
    NestedScrollView nestedScrollView;

    private BillingProcessor bp;
    private ImageView icon;
    private AppCompatSeekBar seekbar;
    private TextView productPrice;
    private Button okButton;
    private boolean isAvailable;
    private boolean isRefreshable = true;
    private BottomSheetDialog dialog;

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
            purchaseButton.setText(R.string.you_again);
            enterReveal(supportThankYou);
        } else {
            purchaseButton.setText(R.string.supporter_title);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("");

        isAvailable = BillingProcessor.isIabServiceAvailable(context);
        if (isAvailable) {
            // continue
            bp = new BillingProcessor(this, getResources().getString(R.string.rsa_key), this);
        } else {
            SnackbarHandler.showErrorSnackbar(this, coordinatorLayout, getString(R.string.billing_not_available));
        }

        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                fabSupporter.hide();
            } else {
                if (!fabSupporter.isShown()){
                    fabSupporter.show();
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
            onBackPressed();
        }

        if (id == R.id.action_restore) {
            isRefreshable = true;
            restorePurchaseHistory(true);
        }

        if (id == R.id.action_support) {
            new MaterialDialog.Builder(this)
                    .title(R.string.need_support)
                    .content(R.string.need_support_description)
                    .neutralText(R.string.email)
                    .positiveColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .neutralColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .negativeText(R.string.cancel)
                    .positiveText(R.string.discord)
                    .onNeutral((dialog, which) -> {
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:"));
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@spacelaunchnow.me"});
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Space Launch Now - Feedback");

                        startActivity(Intent.createChooser(intent, "Email via..."));
                    })
                    .onPositive((dialog, which) -> {
                        String url = "https://discord.gg/WVfzEDW";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    })
                    .onNegative((dialog, which) -> dialog.dismiss())
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

    @OnClick({R2.id.purchase, R2.id.fab_supporter})
    public void checkClick() {
        View view = getLayoutInflater().inflate(R.layout.seekbar_dialog_supporter, null);
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);

        seekbar = view.findViewById(R.id.dialog_seekbar);
        icon = view.findViewById(R.id.dialog_icon);
        productPrice = view.findViewById(R.id.product_price);
        okButton = view.findViewById(R.id.ok_button);

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

        okButton.setOnClickListener(view1 -> {
            dialog.dismiss();
            makePurchase(seekbar.getProgress());
        });
        dialog.show();
    }

    private void makePurchase(int product) {
        String sku = null;
        switch (product) {
            case 0:
                sku = SupporterHelper.SKU_2018_TWO_DOLLAR;
                break;
            case 1:
                sku = SupporterHelper.SKU_2018_SIX_DOLLAR;
                break;
            case 2:
                sku = SupporterHelper.SKU_2018_TWELVE_DOLLAR;
                break;
            case 3:
                sku = SupporterHelper.SKU_2018_THIRTY_DOLLAR;
                break;
        }

        //Get Product from SKU
        Products products = SupporterHelper.getProduct(sku);

        //Initiate purchase
        if (BillingProcessor.isIabServiceAvailable(this)) {
            bp.purchase(this, sku);
        } else {
            SnackbarHandler.showErrorSnackbar(this, coordinatorLayout, getString(R.string.issues_connecting_billing));
        }
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Timber.v("%s purchased.", productId);
        SnackbarHandler.showInfoSnackbar(this, coordinatorLayout, getString(R.string.thanks_support_development));
        animatePurchase();
        Products product = SupporterHelper.getProduct(productId);
        getRealm().beginTransaction();
        getRealm().copyToRealmOrUpdate(product);
        getRealm().commitTransaction();
        WearSyncWorker.syncImmediately();
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
    }

    @Override
    public void onPurchaseHistoryRestored() {
        Timber.d("Purchase History restored.");
        restorePurchaseHistory(false);
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        if (error != null) {
            SnackbarHandler.showErrorSnackbar(this, coordinatorLayout, error.getLocalizedMessage());
        } else if (errorCode != BILLING_RESPONSE_RESULT_USER_CANCELED && errorCode != BILLING_RESPONSE_RESULT_OK) {
            SnackbarHandler.showErrorSnackbar(this, coordinatorLayout, BillingErrorProcessor.getResponseCodeDescription(errorCode));
        }
    }

    @Override
    public void onBillingInitialized() {
        Timber.v("Billing initialized.");
        restorePurchaseHistory(false);
    }

    private void restorePurchaseHistory(boolean userRequested) {
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
                    SnackbarHandler.showInfoSnackbar(this, coordinatorLayout, getString(R.string.purchase_history));
                } else {
                    if (userRequested) {
                        if (bp == null){
                            SnackbarHandler.showErrorSnackbar(this, coordinatorLayout, getString(R.string.unable_to_start_billing));
                        } else if (bp !=null && bp.listOwnedProducts().size() == 0){
                            SnackbarHandler.showInfoSnackbar(this, coordinatorLayout, getString(R.string.no_purchase_history));
                        }
                    }
                    Timber.d("Purchase History - None purchased.");
                }
            }
        } else {
            SnackbarHandler.showErrorSnackbar(this, coordinatorLayout, getString(R.string.billing_not_available));
        }
        WearSyncWorker.syncImmediately();
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
        SkuDetails details;
        String price = "(Unable to get price)";
        switch (iconPosition) {
            case 0:
                details = bp.getPurchaseListingDetails(SupporterHelper.SKU_2018_TWO_DOLLAR);
                if (details != null){
                    price = String.format("(%s)", details.priceText);
                }
                productPrice.setText(price);
                icon.setImageDrawable(new IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_local_drink)
                        .color(Color.BLACK)
                        .sizeDp(96));
                break;
            case 1:
                details = bp.getPurchaseListingDetails(SupporterHelper.SKU_2018_SIX_DOLLAR);
                if (details != null){
                    price = String.format("(%s)", details.priceText);
                }
                productPrice.setText(price);
                icon.setImageDrawable(new IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_local_cafe)
                        .color(Color.BLACK)
                        .sizeDp(96));
                break;
            case 2:
                details = bp.getPurchaseListingDetails(SupporterHelper.SKU_2018_TWELVE_DOLLAR);
                if (details != null){
                    price = String.format("(%s)", details.priceText);
                }
                productPrice.setText(price);
                icon.setImageDrawable(new IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_local_dining)
                        .color(Color.BLACK)
                        .sizeDp(96));
                break;
            case 3:
                details = bp.getPurchaseListingDetails(SupporterHelper.SKU_2018_THIRTY_DOLLAR);
                if (details != null){
                    price = String.format("(%s)", details.priceText);
                }
                productPrice.setText(price);
                icon.setImageResource(R.drawable.take_my_money);
                break;
        }
    }
}
