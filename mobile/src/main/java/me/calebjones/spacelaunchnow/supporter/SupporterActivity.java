package me.calebjones.spacelaunchnow.supporter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.crashlytics.android.answers.AddToCartEvent;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.PurchaseEvent;
import com.mikepenz.iconics.context.IconicsContextWrapper;

import java.math.BigDecimal;
import java.util.Currency;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.ui.activity.BaseActivity;
import me.calebjones.spacelaunchnow.utils.SnackbarHandler;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;
import xyz.hanks.library.SmallBang;

public class SupporterActivity extends BaseActivity implements BillingProcessor.IBillingHandler, AppBarLayout.OnOffsetChangedListener {

    @BindView(R.id.toolbar_support) Toolbar toolbar;
    @BindView(R.id.twoDollar) AppCompatButton two;
    @BindView(R.id.sixDollar) AppCompatButton six;
    @BindView(R.id.twelveDollar) AppCompatButton twelve;
    @BindView(R.id.other) AppCompatButton other;
    @BindView(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.app_bar_layout) AppBarLayout appBarLayout;
    @BindView(R.id.main_textview_title) TextView mTitle;
    @BindView(R.id.main_linearlayout_title) LinearLayout mTitleContainer;

    BillingProcessor bp;
    SmallBang mSmallBang;

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS     = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION              = 200;

    private boolean mIsTheTitleVisible          = false;
    private boolean mIsTheTitleContainerVisible = true;

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

        appBarLayout.addOnOffsetChangedListener(this);

        startAlphaAnimation(mTitle, 0, View.INVISIBLE);


        mSmallBang = SmallBang.attach2Window(this);

        boolean isAvailable = BillingProcessor.isIabServiceAvailable(context);
        if(isAvailable) {
            // continue
            bp = new BillingProcessor(this, getResources().getString(R.string.rsa_key), this);
        } else {
            two.setVisibility(View.GONE);
            six.setVisibility(View.GONE);
            twelve.setVisibility(View.GONE);
            other.setText("PayPal");
        }
    }

    public int reverseNumber(int num, int min, int max) {
        int number = (max + min) - num;
        return number;
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
                makePurchase(SupporterHelper.SKU_TWO_DOLLAR);
                break;
            case R.id.sixDollar:
                makePurchase(SupporterHelper.SKU_SIX_DOLLAR);
                break;
            case R.id.twelveDollar:
                makePurchase(SupporterHelper.SKU_TWELVE_DOLLAR);
                break;
            case R.id.other:
                Toast.makeText(this, "Supporter features will be unlocked via promotion code after purchase confirmation", Toast.LENGTH_LONG);
                Utils.openCustomTab(this, getApplicationContext(), "https://www.paypal.me/cejones");
                break;
        }
    }

    private void makePurchase(String sku) {
        if(BillingProcessor.isIabServiceAvailable(this)) {
            // continue
            Products products = SupporterHelper.getProduct(sku);
            Answers.getInstance().logAddToCart(new AddToCartEvent()
                    .putItemPrice(BigDecimal.valueOf(products.getPrice()))
                    .putCurrency(Currency.getInstance("USD"))
                    .putItemName(products.getName())
                    .putItemType(products.getType())
                    .putItemId(sku));
            bp.purchase(this, sku);
        } else {
            SnackbarHandler.showErrorSnackbar(this,coordinatorLayout, "Issues connecting to Google Play Billing");
        }
    }


    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Timber.v("%s purchased.", productId);
        SnackbarHandler.showInfoSnackbar(this, coordinatorLayout, "Thanks for helping keep the gears turning!");
        animatePurchase(productId);
        Products products = SupporterHelper.getProduct(productId);
        getRealm().beginTransaction();
        getRealm().copyToRealmOrUpdate(products);
        getRealm().commitTransaction();
        Answers.getInstance().logPurchase(new PurchaseEvent()
                .putItemPrice(BigDecimal.valueOf(products.getPrice()))
                .putCurrency(Currency.getInstance("USD"))
                .putItemName(products.getName())
                .putItemType(products.getType())
                .putItemId(productId)
                .putSuccess(true));
        }


    @Override
    public void onPurchaseHistoryRestored() {
        Timber.v("Purchase History restored.");
        if (bp != null && bp.listOwnedProducts().size() > 0) {
            SnackbarHandler.showInfoSnackbar(this, coordinatorLayout, "Purchase history restored.");
        }
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        if (error != null) {
            SnackbarHandler.showErrorSnackbar(this, coordinatorLayout, error.getLocalizedMessage());
        } else if (errorCode != 0){
            SnackbarHandler.showErrorSnackbar(this, coordinatorLayout, "billing error code - " + errorCode);
        }
    }

    @Override
    public void onBillingInitialized() {
        Timber.v("Billing initialized.");
        int count = 500;
        bp.loadOwnedPurchasesFromGoogle();
        for (final String sku : bp.listOwnedProducts()) {
            Products product = SupporterHelper.getProduct(sku);
            getRealm().beginTransaction();
            getRealm().copyToRealmOrUpdate(product);
            getRealm().commitTransaction();
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
            case SupporterHelper.SKU_TWO_DOLLAR:
                mSmallBang.bang(two);
                two.setText("PURCHASED");
                break;
            case SupporterHelper.SKU_SIX_DOLLAR:
                mSmallBang.bang(six);
                six.setText("PURCHASED");
                break;
            case SupporterHelper.SKU_TWELVE_DOLLAR:
                mSmallBang.bang(twelve);
                twelve.setText("PURCHASED");
                break;
            case SupporterHelper.SKU_OTHER:
                mSmallBang.bang(other);
                other.setText("Thanks!");
                break;
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if(!mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if(mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation (View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }
}
