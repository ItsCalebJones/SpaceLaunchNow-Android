package me.calebjones.spacelaunchnow.wear.ui.supporter;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

import androidx.appcompat.widget.AppCompatButton;
import io.realm.Realm;
import me.calebjones.spacelaunchnow.data.models.Products;
import me.calebjones.spacelaunchnow.wear.R;
import timber.log.Timber;

import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_OK;
import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_USER_CANCELED;

public class SupporterActivity extends WearableActivity implements BillingProcessor.IBillingHandler {

    private AppCompatButton supporterButton;
    private BillingProcessor bp;
    private boolean isAvailable;
    private boolean isRefreshable = true;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supporter);

        supporterButton = findViewById(R.id.supporter_button);
        
        realm = Realm.getDefaultInstance();

        if (SupporterHelper.isSupporter()) {
            supporterButton.setText("You again? Thanks!");
        } else {
            supporterButton.setText("Become a Supporter!");
        }

        isAvailable = BillingProcessor.isIabServiceAvailable(this);
        if (isAvailable) {
            // continue
            bp = new BillingProcessor(this, getResources().getString(R.string.rsa_key), this);
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
        }

        // Enables Always-on
        setAmbientEnabled();
        supporterButton.setOnClickListener(v -> {
            makePurchase();
        });
    }

    private void makePurchase() {
        //Initiate purchase
        if (BillingProcessor.isIabServiceAvailable(this)) {
            bp.purchase(this, SupporterHelper.SKU_2018_TWO_DOLLAR);
        } else {
            Toast.makeText(this, "Unable to connect to Google Play billing!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Timber.v("%s purchased.", productId);
        Toast.makeText(this, "Thanks!", Toast.LENGTH_LONG).show();
        Products product = SupporterHelper.getProduct(productId);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(product);
        realm.commitTransaction();
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
            Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        } else if (errorCode != BILLING_RESPONSE_RESULT_USER_CANCELED && errorCode != BILLING_RESPONSE_RESULT_OK) {
            Toast.makeText(this, BillingErrorProcessor.getResponseCodeDescription(errorCode), Toast.LENGTH_LONG).show();
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
                    Timber.d("Purchase History - Number of items purchased: %s", bp.listOwnedProducts().size());
                    for (final String sku : bp.listOwnedProducts()) {
                        Timber.v("Purchase History - SKU: %s", sku);
                        Products product = SupporterHelper.getProduct(sku);
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(product);
                        realm.commitTransaction();
                    }
                    Toast.makeText(this, "Purchase history restored!", Toast.LENGTH_LONG).show();
                } else {
                    if (userRequested) {
                        if (bp == null){
                            Toast.makeText(this, "Unable to initialize billing.", Toast.LENGTH_LONG).show();
                        } else if (bp !=null && bp.listOwnedProducts().size() == 0){
                            Toast.makeText(this, "Purchase history restored - no products found with this account.", Toast.LENGTH_LONG).show();
                        }
                    }
                    Timber.d("Purchase History - None purchased.");
                }
            }
        } else {
            Toast.makeText(this, "Play Billing not available.", Toast.LENGTH_LONG).show();
        }
    }
}
