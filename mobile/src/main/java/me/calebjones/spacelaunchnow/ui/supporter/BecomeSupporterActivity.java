package me.calebjones.spacelaunchnow.ui.supporter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.common.R2;
import me.calebjones.spacelaunchnow.common.base.BaseActivity;
import me.calebjones.spacelaunchnow.common.prefs.ThemeHelper;
import me.calebjones.spacelaunchnow.common.ui.supporter.BillingErrorProcessor;
import me.calebjones.spacelaunchnow.common.ui.supporter.SupporterHelper;
import me.calebjones.spacelaunchnow.data.models.Products;
import me.calebjones.spacelaunchnow.common.ui.views.SnackbarHandler;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;

import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_OK;
import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_USER_CANCELED;

public class BecomeSupporterActivity extends BaseActivity implements BillingProcessor.IBillingHandler {

    @BindView(R.id.support_button)
    AppCompatButton supportButton;
    @BindView(R.id.patreon_button)
    AppCompatButton patreonButton;
    @BindView(R.id.back)
    ImageButton back;
    @BindView(R.id.titleView)
    TextView titleView;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.exit_button)
    AppCompatButton exitButton;

    private BillingProcessor bp;
    private boolean isAvailable;
    private boolean isRefreshable = true;
    private BottomSheetDialog dialog;
    private List<String> ownedProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.supporter_year_overview);
        ButterKnife.bind(this);

        if (SupporterHelper.isSupporter()) {
            titleView.setText("Become a 2021 Supporter!");
            supportButton.setText("Become a 2021 Supporter!");
            description.setText(getString(R.string.supporter_thank_you_2021));
        } else {
            titleView.setText("Become a Supporter!");
            supportButton.setText("Become a Supporter!");
            description.setText(getString(R.string.thank_you_2021));
        }

        back.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_arrow_back)
                .color(ThemeHelper.getIconColor(this))
                .sizeDp(24));

        isAvailable = BillingProcessor.isIabServiceAvailable(this);

        if (isAvailable) {
            // continue
            bp = new BillingProcessor(this, getResources().getString(me.calebjones.spacelaunchnow.common.R.string.rsa_key), this);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @OnClick({R.id.support_button, R.id.patreon_button, R.id.back, R.id.exit_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.support_button:
                checkClick();
                break;
            case R.id.patreon_button:
                Utils.openCustomTab(this, getApplicationContext(),
                        "https://www.patreon.com/spacelaunchnow");
                break;
            case R.id.back:
            case R.id.exit_button:
                onBackPressed();
                break;
        }
    }


    public void checkClick() {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, null);
        View view = getLayoutInflater().inflate(me.calebjones.spacelaunchnow.common.R.layout.supporter_dialog, null);
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);

        View bronzeView = view.findViewById(me.calebjones.spacelaunchnow.common.R.id.bronze_group);
        AppCompatButton bronzeButton = view.findViewById(me.calebjones.spacelaunchnow.common.R.id.bronze_button);
        TextView bronzeTitle = view.findViewById(me.calebjones.spacelaunchnow.common.R.id.bronze_title);
        TextView bronzeDescription = view.findViewById(me.calebjones.spacelaunchnow.common.R.id.bronze_description);

        View metalView = view.findViewById(me.calebjones.spacelaunchnow.common.R.id.metal_group);
        AppCompatButton metalButton = view.findViewById(me.calebjones.spacelaunchnow.common.R.id.metal_button);
        TextView metalTitle = view.findViewById(me.calebjones.spacelaunchnow.common.R.id.metal_title);
        TextView metalDescription = view.findViewById(me.calebjones.spacelaunchnow.common.R.id.metal_description);

        View silverView = view.findViewById(me.calebjones.spacelaunchnow.common.R.id.silver_group);
        AppCompatButton silverButton = view.findViewById(me.calebjones.spacelaunchnow.common.R.id.silver_button);
        TextView silverTitle = view.findViewById(me.calebjones.spacelaunchnow.common.R.id.silver_title);
        TextView silverDescription = view.findViewById(me.calebjones.spacelaunchnow.common.R.id.silver_description);

        View goldView = view.findViewById(me.calebjones.spacelaunchnow.common.R.id.gold_group);
        AppCompatButton goldButton = view.findViewById(me.calebjones.spacelaunchnow.common.R.id.gold_button);
        TextView goldTitle = view.findViewById(me.calebjones.spacelaunchnow.common.R.id.gold_title);
        TextView goldDescription = view.findViewById(me.calebjones.spacelaunchnow.common.R.id.gold_description);

        View platinumView = view.findViewById(me.calebjones.spacelaunchnow.common.R.id.platinum_group);
        AppCompatButton platinumButton = view.findViewById(me.calebjones.spacelaunchnow.common.R.id.platinum_button);
        TextView platinumTitle = view.findViewById(me.calebjones.spacelaunchnow.common.R.id.platinum_title);
        TextView platinumDescription = view.findViewById(me.calebjones.spacelaunchnow.common.R.id.platinum_description);

        TextView bottomMessage = view.findViewById(me.calebjones.spacelaunchnow.common.R.id.bottom_message);

        if (isAvailable){
            bronzeView.setVisibility(View.VISIBLE);
            metalView.setVisibility(View.VISIBLE);
            silverView.setVisibility(View.VISIBLE);
            goldView.setVisibility(View.VISIBLE);
            platinumView.setVisibility(View.VISIBLE);

            bottomMessage.setText("Pay what you want - get the same features!");
        } else {
            bronzeView.setVisibility(View.GONE);
            metalView.setVisibility(View.GONE);
            silverView.setVisibility(View.GONE);
            goldView.setVisibility(View.GONE);
            platinumView.setVisibility(View.GONE);

            bottomMessage.setText("Unable to load in-app-products.");
        }

        configureProductItem(bronzeButton, bronzeTitle, bronzeDescription,
                SupporterHelper.SKU_2021_BRONZE,
                new IconicsDrawable(this)
                        .icon(FontAwesome.Icon.faw_thumbs_up)
                        .color(Color.WHITE)
                        .sizeDp(24));

        configureProductItem(metalButton, metalTitle, metalDescription,
                SupporterHelper.SKU_2021_METAL,
                new IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_local_cafe)
                        .color(Color.WHITE)
                        .sizeDp(24));

        configureProductItem(silverButton, silverTitle, silverDescription,
                SupporterHelper.SKU_2021_SILVER,
                new IconicsDrawable(this)
                        .icon(FontAwesome.Icon.faw_store)
                        .color(Color.WHITE)
                        .sizeDp(24));

        configureProductItem(goldButton, goldTitle, goldDescription,
                SupporterHelper.SKU_2021_GOLD,
                new IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_local_dining)
                        .color(Color.WHITE)
                        .sizeDp(24));

        configureProductItem(platinumButton, platinumTitle, platinumDescription,
                SupporterHelper.SKU_2021_PLATINUM,
                new IconicsDrawable(this)
                        .icon(FontAwesome.Icon.faw_money_bill_wave)
                        .color(Color.WHITE)
                        .sizeDp(24));
        dialog.show();
    }

    private void configureProductItem(AppCompatButton button, TextView titleView, TextView descriptionView,
                                      String sku, IconicsDrawable drawable) {
        String price = "(Unable to get price)";
        String title = "Supporter Product";
        String description = "Unable to get product description.";
        SkuDetails details = getProductInfo(sku);
        if (details != null) {
            price = String.format("%s", details.priceText);
            title = details.title.replaceAll("\\(.*\\)", "");
            description = details.description;
        }
        button.setText(price);
        titleView.setText(title);
        descriptionView.setText(description);

        if (isOwned(sku)) {
            button.setCompoundDrawablesRelative(
                    new IconicsDrawable(this)
                            .icon(FontAwesome.Icon.faw_check)
                            .color(Color.WHITE)
                            .sizeDp(24),
                    null, null, null);
        } else {
            button.setCompoundDrawablesRelative(drawable, null, null, null);
        }

        button.setOnClickListener(view1 -> {
            dialog.dismiss();
            makePurchase(sku);
        });

    }

    private boolean isOwned(String currentSku) {
        try {
            for (String ownedSku : ownedProducts) {
                if (currentSku.equals(ownedSku)) {
                    return true;
                }
            }
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    private void makePurchase(String sku) {
        //Get Product from SKU
        Products products = SupporterHelper.getProduct(sku);

        //Initiate purchase
        if (BillingProcessor.isIabServiceAvailable(this)) {
            bp.purchase(this, sku);
        } else {
            Toast.makeText(this, getString(me.calebjones.spacelaunchnow.common.R.string.issues_connecting_billing), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Timber.v("%s purchased.", productId);
        Toast.makeText(this, getString(me.calebjones.spacelaunchnow.common.R.string.thanks_support_development), Toast.LENGTH_SHORT).show();
        Products product = SupporterHelper.getProduct(productId);
        getRealm().beginTransaction();
        getRealm().copyToRealmOrUpdate(product);
        getRealm().commitTransaction();
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
            Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        } else if (errorCode != BILLING_RESPONSE_RESULT_USER_CANCELED && errorCode != BILLING_RESPONSE_RESULT_OK) {
            Toast.makeText(this, BillingErrorProcessor.getResponseCodeDescription(errorCode), Toast.LENGTH_SHORT).show();
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
                    bp = new BillingProcessor(this, getResources().getString(me.calebjones.spacelaunchnow.common.R.string.rsa_key), this);
                }
                bp.loadOwnedPurchasesFromGoogle();
                if (bp != null && bp.listOwnedProducts().size() > 0) {
                    Timber.d("Purchase History - Number of items purchased: %s", bp.listOwnedProducts().size());
                    ownedProducts = bp.listOwnedProducts();
                    for (final String sku : bp.listOwnedProducts()) {
                        Timber.v("Purchase History - SKU: %s", sku);
                        Products product = SupporterHelper.getProduct(sku);
                        getRealm().beginTransaction();
                        getRealm().copyToRealmOrUpdate(product);
                        getRealm().commitTransaction();
                    }
                    Toast.makeText(this, getString(R.string.purchase_history), Toast.LENGTH_SHORT).show();
                } else {
                    if (userRequested) {
                        if (bp == null) {
                            Toast.makeText(this, getString(me.calebjones.spacelaunchnow.common.R.string.unable_to_start_billing), Toast.LENGTH_SHORT).show();
                        } else if (bp != null && bp.listOwnedProducts().size() == 0) {
                            Toast.makeText(this, getString(me.calebjones.spacelaunchnow.common.R.string.no_purchase_history), Toast.LENGTH_SHORT).show();
                        }
                    }
                    Timber.d("Purchase History - None purchased.");
                }
            }
        } else {
            Toast.makeText(this, getString(me.calebjones.spacelaunchnow.common.R.string.billing_not_available), Toast.LENGTH_SHORT).show();
        }
    }

    public SkuDetails getProductInfo(String sku) {
        SkuDetails details = null;

        switch (sku) {
            case SupporterHelper.SKU_2021_BRONZE:
                if (bp != null) {
                    details = bp.getPurchaseListingDetails(SupporterHelper.SKU_2021_BRONZE);
                }
                return details;

            case SupporterHelper.SKU_2021_METAL:
                if (bp != null) {
                    details = bp.getPurchaseListingDetails(SupporterHelper.SKU_2021_METAL);
                }
                return details;
            case SupporterHelper.SKU_2021_SILVER:
                if (bp != null) {
                    details = bp.getPurchaseListingDetails(SupporterHelper.SKU_2021_SILVER);
                }
                return details;
            case SupporterHelper.SKU_2021_GOLD:
                if (bp != null) {
                    details = bp.getPurchaseListingDetails(SupporterHelper.SKU_2021_GOLD);
                }
                return details;
            case SupporterHelper.SKU_2021_PLATINUM:
                if (bp != null) {
                    details = bp.getPurchaseListingDetails(SupporterHelper.SKU_2021_PLATINUM);
                }
                return details;
            default:
                return null;
        }
    }
}
