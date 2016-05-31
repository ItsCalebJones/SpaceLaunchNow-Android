package me.calebjones.spacelaunchnow.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.AddToCartEvent;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.PurchaseEvent;

import java.math.BigDecimal;
import java.util.Currency;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.models.legacy.Products;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;
import xyz.hanks.library.SmallBang;

public class SupportActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    @Bind(R.id.toolbar_support) Toolbar toolbar;
    @Bind(R.id.twoDollar) AppCompatButton two;
    @Bind(R.id.sixDollar) AppCompatButton six;
    @Bind(R.id.twelveDollar) AppCompatButton twelve;
    @Bind(R.id.other) AppCompatButton other;

    // SKU for our subscription (infinite gas)
    static final String SKU_TWO_DOLLAR = "two_dollar_support";
    static final String SKU_SIX_DOLLAR = "six_dollar_support";
    static final String SKU_TWELVE_DOLLAR = "twelve_dollar_support";

    BillingProcessor bp;
    SmallBang mSmallBang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int m_theme;
        final Context context = this;

        ListPreferences sharedPreference = ListPreferences.getInstance(context);

        if (sharedPreference.getNightMode()) {
            m_theme = R.style.DarkTheme;
        } else {
            m_theme = R.style.LightTheme;
        }
        setTheme(m_theme);

        setContentView(R.layout.activity_support);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setTitle("Support");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mSmallBang = SmallBang.attach2Window(this);

        bp = new BillingProcessor(this, getResources().getString(R.string.rsa_key), this);
        bp.loadOwnedPurchasesFromGoogle();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_support, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        if(id == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bp != null)
            bp.release();
    }

    @OnClick({ R.id.twoDollar, R.id.sixDollar, R.id.twelveDollar, R.id.other })
    public void checkClick(View v) {
        switch (v.getId()) {
            case R.id.twoDollar:
                makePurchase(SKU_TWO_DOLLAR);
                break;
            case R.id.sixDollar:
                makePurchase(SKU_SIX_DOLLAR);
                break;
            case R.id.twelveDollar:
                makePurchase(SKU_TWELVE_DOLLAR);
                break;
            case R.id.other:
                Utils.openCustomTab(this, getApplicationContext(), "https://www.paypal.me/cejones");
                break;
        }
    }

    private void makePurchase(String sku) {
        if(BillingProcessor.isIabServiceAvailable(this)) {
            // continue
            Products products = getProduct(sku);
            Answers.getInstance().logAddToCart(new AddToCartEvent()
                    .putItemPrice(products.getPrice())
                    .putCurrency(Currency.getInstance("USD"))
                    .putItemName(products.getName())
                    .putItemType(products.getType())
                    .putItemId(sku));
            bp.purchase(this, sku);
        } else {
            Toast.makeText(this, "Issues connecting to Google Play Billing", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Timber.v("%s purchased.", productId);
        Toast.makeText(this, "Thanks for helping keep the gears turning!", Toast.LENGTH_LONG).show();
        animatePurchase(productId);
        Products products = getProduct(productId);
        Answers.getInstance().logPurchase(new PurchaseEvent()
                .putItemPrice(products.getPrice())
                .putCurrency(Currency.getInstance("USD"))
                .putItemName(products.getName())
                .putItemType(products.getType())
                .putItemId(productId)
                .putSuccess(true));
        }


    @Override
    public void onPurchaseHistoryRestored() {
        Timber.v("Purchase History restored.");
        Toast.makeText(this, "Restored purchase, thanks for supporting me!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Toast.makeText(this, "Error processing billing request.", Toast.LENGTH_LONG).show();
        Crashlytics.logException(error);
    }

    @Override
    public void onBillingInitialized() {
        Timber.v("Billing initialized.");
        int count = 500;
        for (final String sku : bp.listOwnedProducts()) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Timber.v("SKU: %s", sku);
                    animatePurchase(sku);
                }
            }, count);
            count += 850;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    private void animatePurchase(String productId) {
        switch (productId) {
            case SKU_TWO_DOLLAR:
                mSmallBang.bang(two);
                two.setText("PURCHASED");
                break;
            case SKU_SIX_DOLLAR:
                mSmallBang.bang(six);
                six.setText("PURCHASED");
                break;
            case SKU_TWELVE_DOLLAR:
                mSmallBang.bang(twelve);
                twelve.setText("PURCHASED");
                break;
        }
    }

    private Products getProduct(String productID){
        Products product = new Products();
        if (productID.equals(SKU_TWO_DOLLAR)) {
            product.setName("Founder 2016 - Bronze");
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(BigDecimal.valueOf(1.99));
        } else if (productID.equals(SKU_SIX_DOLLAR)){
            product.setName("Founder 2016 - Silver");
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(BigDecimal.valueOf(5.99));
        } else if (productID.equals(SKU_TWELVE_DOLLAR)){
            product.setName("Founder 2016 - Gold");
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(BigDecimal.valueOf(11.99));
        }
        return product;
    }
}
