package me.calebjones.spacelaunchnow.common.ui.supporter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;



import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.transitionseverywhere.TransitionManager;

import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.EmptyRequestListener;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.Purchase;
import org.solovyev.android.checkout.RequestListener;
import org.solovyev.android.checkout.ResponseCodes;
import org.solovyev.android.checkout.Sku;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.calebjones.spacelaunchnow.common.BuildConfig;
import me.calebjones.spacelaunchnow.common.LaunchApplication;
import me.calebjones.spacelaunchnow.common.R;
import me.calebjones.spacelaunchnow.common.R2;
import me.calebjones.spacelaunchnow.common.base.BaseActivity;
import me.calebjones.spacelaunchnow.common.ui.views.SnackbarHandler;
import me.calebjones.spacelaunchnow.data.models.Products;
import timber.log.Timber;

import static me.calebjones.spacelaunchnow.common.LaunchApplication.LIST_INAPP_SKUS;
import static org.solovyev.android.checkout.ProductTypes.IN_APP;

public class SupporterActivity extends BaseActivity {

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
    @BindView(R2.id.detail_title)
    TextView title;
    @BindView(R2.id.detail_sub_title)
    TextView subtitle;

    private boolean isAvailable = false;
    private boolean isRefreshable = true;
    private BottomSheetDialog dialog;
    private List<String> ownedProducts;
    private InventoryCallback mInventoryCallback;


    //NEW CHECKOUT LIB CODE
    private class PurchaseListener extends EmptyRequestListener<Purchase> {
        @Override
        public void onSuccess(Purchase purchase) {
            // here you can process the loaded purchase
        }

        @Override
        public void onError(int response, Exception e) {
            // handle errors here
        }
    }

    private class InventoryCallback implements Inventory.Callback {
        private boolean ready = false;
        private Inventory.Products products;

        public Inventory.Products getProducts(){
            return products;
        }

        @Override
        public void onLoaded(Inventory.Products products) {
            this.ready = true;
            this.products = products;
            isAvailable = true;
            if (products.get(IN_APP).getPurchases().size() > 0) {
                SnackbarHandler.showInfoSnackbar(getApplicationContext(), coordinatorLayout, getString(R.string.thanks_support_development));
                animatePurchase();
                for (Purchase purchase : products.get(IN_APP).getPurchases() ) {
                    Products product = SupporterHelper.getProduct(purchase.sku);
                    getRealm().beginTransaction();
                    getRealm().copyToRealmOrUpdate(product);
                    getRealm().commitTransaction();
                }
            }
        }
    }

    private final ActivityCheckout mCheckout = Checkout.forActivity(this, LaunchApplication.get().getBilling());
    private Inventory mInventory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = this;

        setContentView(R.layout.activity_support);
        ButterKnife.bind(this);
        ownedProducts = new ArrayList<>();

        if (SupporterHelper.is2021Supporter()) {
            purchaseButton.setText(R.string.support_us_2021);
            title.setText("Thank You!");
            subtitle.setText("");
        } else if (SupporterHelper.isSupporter()) {
            purchaseButton.setText(R.string.support_again_2021);
            title.setText("Become a 2021 Supporter");
            subtitle.setText("Continue Your Support");
        } else {
            title.setText("Become a Supporter");
            subtitle.setText("Get Pro Features");
            purchaseButton.setText(R.string.supporter_title_2021);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("");
        mCheckout.start();
        mInventoryCallback = new InventoryCallback();
        reloadInventory();


        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                fabSupporter.hide();
            } else {
                if (!fabSupporter.isShown()) {
                    fabSupporter.show();
                }
            }
        });
    }

    private void reloadInventory() {
        final Inventory.Request request = Inventory.Request.create();
        // load purchase info
        request.loadAllPurchases();
        // load SKU details
        request.loadSkus(IN_APP, LIST_INAPP_SKUS);
        mCheckout.loadInventory(request, mInventoryCallback);
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

//            restorePurchaseHistory(true);
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
        mCheckout.stop();

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_support, menu);
        return true;
    }

    @OnClick({R2.id.purchase, R2.id.fab_supporter})
    public void checkClick() {
        if (mInventoryCallback.ready) {
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, null);
            View view = getLayoutInflater().inflate(R.layout.supporter_dialog, null);
            dialog = new BottomSheetDialog(this);
            dialog.setContentView(view);

            View bronzeView = view.findViewById(R.id.bronze_group);
            AppCompatButton bronzeButton = view.findViewById(R.id.bronze_button);
            TextView bronzeTitle = view.findViewById(R.id.bronze_title);
            TextView bronzeDescription = view.findViewById(R.id.bronze_description);

            View metalView = view.findViewById(R.id.metal_group);
            AppCompatButton metalButton = view.findViewById(R.id.metal_button);
            TextView metalTitle = view.findViewById(R.id.metal_title);
            TextView metalDescription = view.findViewById(R.id.metal_description);

            View silverView = view.findViewById(R.id.silver_group);
            AppCompatButton silverButton = view.findViewById(R.id.silver_button);
            TextView silverTitle = view.findViewById(R.id.silver_title);
            TextView silverDescription = view.findViewById(R.id.silver_description);

            View goldView = view.findViewById(R.id.gold_group);
            AppCompatButton goldButton = view.findViewById(R.id.gold_button);
            TextView goldTitle = view.findViewById(R.id.gold_title);
            TextView goldDescription = view.findViewById(R.id.gold_description);

            View platinumView = view.findViewById(R.id.platinum_group);
            AppCompatButton platinumButton = view.findViewById(R.id.platinum_button);
            TextView platinumTitle = view.findViewById(R.id.platinum_title);
            TextView platinumDescription = view.findViewById(R.id.platinum_description);

            TextView bottomMessage = view.findViewById(R.id.bottom_message);

            if (isAvailable) {
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

            Inventory.Products products = mInventoryCallback.getProducts();
            for (Inventory.Product product : products){
                Timber.v(product.id);
                Timber.v(String.valueOf(product.supported));
            }
            Inventory.Product inapp = products.get(IN_APP);
            List<Purchase> purchases = inapp.getPurchases();

            configureProductItem(bronzeButton, bronzeTitle, bronzeDescription, inapp, purchases,
                    SupporterHelper.SKU_2021_BRONZE,
                    new IconicsDrawable(this)
                            .icon(FontAwesome.Icon.faw_thumbs_up)
                            .color(Color.WHITE)
                            .sizeDp(24));

            configureProductItem(metalButton, metalTitle, metalDescription, inapp, purchases,
                    SupporterHelper.SKU_2021_METAL,
                    new IconicsDrawable(this)
                            .icon(GoogleMaterial.Icon.gmd_local_cafe)
                            .color(Color.WHITE)
                            .sizeDp(24));

            configureProductItem(silverButton, silverTitle, silverDescription, inapp, purchases,
                    SupporterHelper.SKU_2021_SILVER,
                    new IconicsDrawable(this)
                            .icon(FontAwesome.Icon.faw_store)
                            .color(Color.WHITE)
                            .sizeDp(24));

            configureProductItem(goldButton, goldTitle, goldDescription, inapp, purchases,
                    SupporterHelper.SKU_2021_GOLD,
                    new IconicsDrawable(this)
                            .icon(GoogleMaterial.Icon.gmd_local_dining)
                            .color(Color.WHITE)
                            .sizeDp(24));

            configureProductItem(platinumButton, platinumTitle, platinumDescription, inapp, purchases,
                    SupporterHelper.SKU_2021_PLATINUM,
                    new IconicsDrawable(this)
                            .icon(FontAwesome.Icon.faw_money_bill_wave)
                            .color(Color.WHITE)
                            .sizeDp(24));
            dialog.show();
        }
    }

    private void configureProductItem(AppCompatButton button, TextView titleView,
                                      TextView descriptionView, Inventory.Product inApp,
                                      List<Purchase> purchases, String sku,
                                      IconicsDrawable drawable) {
        String price = "(Unable to get price)";
        String title = "Supporter Product";
        String description = "Unable to get product description.";

        Sku mSku = inApp.getSku(sku);
        if (mSku != null) {
            price = mSku.price;
            title = mSku.title;
            description = mSku.description;
        }

        button.setText(price);
        titleView.setText(title);
        descriptionView.setText(description);

        if (isOwned(sku, purchases)) {
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
            makePurchase(mSku);
        });

    }

    private boolean isOwned(String currentSku, List<Purchase> purchases) {
        for (Purchase ownedSku : purchases) {
            if (currentSku.equals(ownedSku.sku)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return {@link RequestListener} that reloads inventory when the action is finished
     */
    private <T> RequestListener<T> makeRequestListener() {
        return new RequestListener<T>() {
            @Override
            public void onSuccess(@Nonnull T result) {
                reloadInventory();
            }

            @SuppressLint("ThrowableNotAtBeginning")
            @Override
            public void onError(int response, @Nonnull Exception e) {
                if (BuildConfig.DEBUG)
                    Timber.e("Couldn't complete the purchase %s", e);
                switch (response) {
                    case ResponseCodes.ITEM_ALREADY_OWNED:
                    case ResponseCodes.USER_CANCELED:
                    case ResponseCodes.WRONG_SIGNATURE:
                    case ResponseCodes.SERVICE_NOT_CONNECTED:
                    case ResponseCodes.OK:
                    case ResponseCodes.NULL_INTENT:
                    case ResponseCodes.ITEM_UNAVAILABLE:
                    case ResponseCodes.EXCEPTION:
                    case ResponseCodes.ITEM_NOT_OWNED:
                    case ResponseCodes.ERROR:
                    case ResponseCodes.DEVELOPER_ERROR:
                    case ResponseCodes.BILLING_UNAVAILABLE:
                    case ResponseCodes.ACCOUNT_ERROR:
                        // Handle the errors the way you like
                        break;
                    default:
                        throw new RuntimeException("unhandled response code received");
                }
                reloadInventory();
            }
        };
    }

    private void makePurchase(Sku sku) {
        final RequestListener<Purchase> listener = makeRequestListener();
        mCheckout.startPurchaseFlow(sku, null, listener);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        mCheckout.onActivityResult(requestCode, resultCode, data);
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
}
