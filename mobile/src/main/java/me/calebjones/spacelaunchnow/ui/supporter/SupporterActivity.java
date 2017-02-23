package me.calebjones.spacelaunchnow.ui.supporter;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.crashlytics.android.answers.AddToCartEvent;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.PurchaseEvent;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsContextWrapper;

import java.math.BigDecimal;
import java.util.Currency;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.common.BaseActivity;
import me.calebjones.spacelaunchnow.data.models.realm.Products;
import me.calebjones.spacelaunchnow.utils.SnackbarHandler;
import timber.log.Timber;
import xyz.hanks.library.SmallBang;

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

    BillingProcessor bp;
    SmallBang mSmallBang;

    private ImageView icon;
    private AppCompatSeekBar seekbar;
    private TextView text;

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
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("");

        mSmallBang = SmallBang.attach2Window(this);

        boolean isAvailable = BillingProcessor.isIabServiceAvailable(context);
        if (isAvailable) {
            // continue
            bp = new BillingProcessor(this, getResources().getString(R.string.rsa_key), this);
        }
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bp != null) {
            bp.release();
        }
    }

    @OnClick(R.id.purchase)
    public void checkClick(View v) {
        switch (v.getId()) {
            case R.id.purchase:
                MaterialDialog dialog = new MaterialDialog.Builder(this)
                        .title("Thanks for your Support!")
                        .customView(R.layout.seekbar_dialog_supporter, true)
                        .positiveText("Ok")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                makePurchase(seekbar.getProgress());
                            }
                        })
                        .show();

                seekbar = (AppCompatSeekBar) dialog.getCustomView().findViewById(R.id.dialog_seekbar);
                icon = (ImageView) dialog.getCustomView().findViewById(R.id.dialog_icon);
                text = (TextView) dialog.getCustomView().findViewById(R.id.dialog_text);

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
    }

    private void makePurchase(int product) {
        String sku = null;
        switch (product){
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
        if (BillingProcessor.isIabServiceAvailable(this)) {
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
            SnackbarHandler.showErrorSnackbar(this, coordinatorLayout, "Issues connecting to Google Play Billing");
        }
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Timber.v("%s purchased.", productId);
        SnackbarHandler.showInfoSnackbar(this, coordinatorLayout, "Thanks for helping keep the gears turning!");
        animatePurchase();
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
            animatePurchase();
            SnackbarHandler.showInfoSnackbar(this, coordinatorLayout, "Purchase history restored.");
        }
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
        bp.loadOwnedPurchasesFromGoogle();
        for (final String sku : bp.listOwnedProducts()) {
            Products product = SupporterHelper.getProduct(sku);
            getRealm().beginTransaction();
            getRealm().copyToRealmOrUpdate(product);
            getRealm().commitTransaction();
        }
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

        // get the center for the clipping circle
        int cx = myView.getMeasuredWidth() / 2;
        int cy = myView.getMeasuredHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(myView.getWidth(), myView.getHeight()) / 2;

        // create the animator for this view (the start radius is zero)
        Animator anim;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.setDuration(400);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    delayedSmallBand(myView);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            // make the view visible and start the animation
            myView.setVisibility(View.VISIBLE);
            anim.start();
        } else {
            myView.setVisibility(View.VISIBLE);
            delayedSmallBand(myView);
        }
    }

    private void delayedSmallBand(final View myView) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSmallBang.bang(myView);
            }
        }, 250);

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
