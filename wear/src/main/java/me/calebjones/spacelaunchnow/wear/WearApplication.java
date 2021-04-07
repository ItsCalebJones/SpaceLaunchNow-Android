package me.calebjones.spacelaunchnow.wear;

import android.app.Application;
import android.content.ContextWrapper;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.evernote.android.job.JobManager;
import com.pixplicity.easyprefs.library.Prefs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import me.calebjones.spacelaunchnow.data.models.Products;
import me.calebjones.spacelaunchnow.data.models.realm.LaunchDataModule;
import me.calebjones.spacelaunchnow.wear.content.job.WearJobCreator;
import me.calebjones.spacelaunchnow.wear.ui.supporter.SupporterHelper;
import timber.log.Timber;


public class WearApplication extends Application {
    private static final long DB_SCHEMA_VERSION = 1;
    private BillingProcessor bp;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        Realm.init(this);

        // Get a Realm instance for this thread
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder()
                                              .schemaVersion(DB_SCHEMA_VERSION)
                                              .modules(Realm.getDefaultModule(), new LaunchDataModule())
                                              .deleteRealmIfMigrationNeeded()
                                              .build());

        boolean isAvailable = BillingProcessor.isIabServiceAvailable(this);
        if (isAvailable) {
            bp = new BillingProcessor(this, getResources().getString(R.string.rsa_key), new BillingProcessor.IBillingHandler() {
                @Override
                public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
                    Timber.d("onProductPurchased");
                }

                @Override
                public void onPurchaseHistoryRestored() {
                    Timber.d("onPurchaseHistoryRestored");
                }

                @Override
                public void onBillingError(int errorCode, @Nullable Throwable error) {
                    Timber.d("onBillingError");
                }

                @Override
                public void onBillingInitialized() {
                    Timber.d("onBillingInitialized");
                    restorePurchases();
                }
            });
        }
        JobManager.create(this).addJobCreator(new WearJobCreator());

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }

    private void restorePurchases() {
        bp.loadOwnedPurchasesFromGoogle();
        Timber.d("Purchase History Restored - Number of items purchased: %s", bp.listOwnedProducts().size());
        if (bp != null && bp.listOwnedProducts().size() > 0) {
            for (final String sku : bp.listOwnedProducts()) {
                Timber.v("Purchase History - SKU: %s", sku);
                Products product = SupporterHelper.getProduct(sku);
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(product));
                realm.close();
            }
        }
    }
}
