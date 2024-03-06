package me.calebjones.spacelaunchnow.common.ui.supporter;


import io.realm.Realm;
import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.data.models.Products;
import timber.log.Timber;

public class SupporterHelper {
    // SKU for our subscription (infinite gas)
    public static final String SKU_TWO_DOLLAR = "two_dollar_support";
    public static final String SKU_SIX_DOLLAR = "six_dollar_support";
    public static final String SKU_TWELVE_DOLLAR = "twelve_dollar_support";
    public static final String SKU_THIRTY_DOLLAR = "thirty_dollar_support";
    public static final String SKU_2018_TWO_DOLLAR = "2018_two_dollar_support";
    public static final String SKU_2018_SIX_DOLLAR = "2018_six_dollar_support";
    public static final String SKU_2018_TWELVE_DOLLAR = "2018_twelve_dollar_support";
    public static final String SKU_2018_THIRTY_DOLLAR = "2018_thirty_dollar_support";
    public static final String SKU_2020_BRONZE = "2020_bronze_supporter";
    public static final String SKU_2020_METAL = "2020_metal_supporter";
    public static final String SKU_2020_SILVER = "2020_silver_support";
    public static final String SKU_2020_GOLD = "2020_gold_support";
    public static final String SKU_2020_PLATINUM = "2020_platinum_support";
    public static final String SKU_2021_BRONZE = "2021_bronze_supporter";
    public static final String SKU_2021_METAL = "2021_metal_supporter";
    public static final String SKU_2021_SILVER = "2021_silver_support";
    public static final String SKU_2021_GOLD = "2021_gold_support";
    public static final String SKU_2021_PLATINUM = "2021_platinum_support";
    public static final String SKU_2022_BRONZE = "2022_bronze_supporter";
    public static final String SKU_2022_METAL = "2022_metal_supporter";
    public static final String SKU_2022_SILVER = "2022_silver_support";
    public static final String SKU_2022_GOLD = "2022_gold_support";
    public static final String SKU_2022_PLATINUM = "2022_platinum_support";
    public static final String SKU_2023_BRONZE = "2023_bronze_supporter";
    public static final String SKU_2023_METAL = "2023_metal_supporter";
    public static final String SKU_2023_SILVER = "2023_silver_support";
    public static final String SKU_2023_PLATINUM = "2023_platinum_support";

    public static final String SKU_2024_BRONZE = "2024_bronze_supporter";

    public static final String SKU_2024_METAL = "2024_metal_supporter";

    public static final String SKU_2024_SILVER = "2024_silver_support";

    public static final String SKU_2024_PLATINUM = "2024_platinum_support";
    public static final String SKU_OTHER = "beta_supporter";

    public static Products getProduct(String productID){
        Products product = new Products();
        if (productID.equals(SKU_TWO_DOLLAR)) {
            product.setName("Founder 2016 - Bronze");
            product.setSku(productID);
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(2);
        } else if (productID.equals(SKU_SIX_DOLLAR)){
            product.setName("Founder 2016 - Silver");
            product.setSku(productID);
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(6);
        } else if (productID.equals(SKU_TWELVE_DOLLAR)){
            product.setName("Founder 2016 - Gold");
            product.setSku(productID);
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(12);
        } else if (productID.equals(SKU_THIRTY_DOLLAR)) {
            product.setName("Founder 2016 - Platinum");
            product.setSku(productID);
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(30);
        }else if (productID.equals(SKU_2018_THIRTY_DOLLAR)) {
            product.setName("Supporter 2018 - Platinum");
            product.setSku(productID);
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(30);
        }  else if (productID.equals(SKU_2018_TWELVE_DOLLAR)){
            product.setName("Supporter 2018 - Gold");
            product.setSku(productID);
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(30);
        } else if (productID.equals(SKU_2018_SIX_DOLLAR)){
            product.setName("Supporter 2018 - Silver");
            product.setSku(productID);
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(30);
        } else if (productID.equals(SKU_2018_TWO_DOLLAR)){
            product.setName("Supporter 2018 - Bronze");
            product.setSku(productID);
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(30);
        }else if (productID.equals(SKU_2020_PLATINUM)) {
            product.setName("Supporter 2020 - Platinum");
            product.setSku(productID);
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(30);
        }  else if (productID.equals(SKU_2020_GOLD)){
            product.setName("Supporter 2020 - Gold");
            product.setSku(productID);
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(30);
        } else if (productID.equals(SKU_2020_SILVER)){
            product.setName("Supporter 2020 - Silver");
            product.setSku(productID);
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(30);
        } else if (productID.equals(SKU_2020_BRONZE)){
            product.setName("Supporter 2020 - Bronze");
            product.setSku(productID);
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(30);
        } else if (productID.equals(SKU_2020_METAL)) {
            product.setName("Supporter 2020 - Metal");
            product.setSku(productID);
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(6);
        } else if (productID.equals(SKU_2021_PLATINUM)) {
            product.setName("Supporter 2021 - Platinum");
            product.setSku(productID);
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(30);
        }  else if (productID.equals(SKU_2021_GOLD)){
            product.setName("Supporter 2021 - Gold");
            product.setSku(productID);
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(30);
        } else if (productID.equals(SKU_2021_SILVER)){
            product.setName("Supporter 2021 - Silver");
            product.setSku(productID);
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(30);
        } else if (productID.equals(SKU_2021_BRONZE)){
            product.setName("Supporter 2021 - Bronze");
            product.setSku(productID);
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(30);
        } else if (productID.equals(SKU_2021_METAL)){
            product.setName("Supporter 2021 - Metal");
            product.setSku(productID);
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(6);
        } else if (productID.equals(SKU_2022_PLATINUM)) {
            product.setName("Supporter 2022 - Platinum");
            product.setSku(productID);
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(30);
        }  else if (productID.equals(SKU_2022_GOLD)){
            product.setName("Supporter 2022 - Gold");
            product.setSku(productID);
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(15);
        } else if (productID.equals(SKU_2022_SILVER)){
            product.setName("Supporter 2022 - Silver");
            product.setSku(productID);
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(10);
        } else if (productID.equals(SKU_2022_BRONZE)){
            product.setName("Supporter 2022 - Bronze");
            product.setSku(productID);
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(3);
        } else if (productID.equals(SKU_2022_METAL)){
            product.setName("Supporter 2022 - Metal");
            product.setSku(productID);
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(6);
        }  else if (productID.equals(SKU_2023_PLATINUM)) {
            product.setName("Supporter 2023 - Platinum");
            product.setSku(productID);
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(30);
        } else if (productID.equals(SKU_2023_SILVER)){
            product.setName("Supporter 2023 - Silver");
            product.setSku(productID);
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(10);
        } else if (productID.equals(SKU_2023_BRONZE)){
            product.setName("Supporter 2023 - Bronze");
            product.setSku(productID);
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(3);
        } else if (productID.equals(SKU_2023_METAL)){
            product.setName("Supporter 2023 - Metal");
            product.setSku(productID);
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(6);
        }else if (productID.equals(SKU_2024_PLATINUM)) {
            product.setName("Supporter 2024 - Platinum");
            product.setSku(productID);
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(30);
        } else if (productID.equals(SKU_2024_SILVER)){
            product.setName("Supporter 2024 - Silver");
            product.setSku(productID);
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(10);
        } else if (productID.equals(SKU_2024_BRONZE)){
            product.setName("Supporter 2024 - Bronze");
            product.setSku(productID);
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(3);
        } else if (productID.equals(SKU_2024_METAL)){
            product.setName("Supporter 2024 - Metal");
            product.setSku(productID);
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(6);
        } else if (productID.equals(SKU_OTHER)){
            product.setName("Promotion Supporter");
            product.setSku(productID);
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(30);
        }
        return product;
    }

    public static void saveProductAsOwnedAsync(String sku){
        Timber.v("Saving SKU %s as owned...", sku);
        try {
            Realm realm = Realm.getDefaultInstance();
            Products product = SupporterHelper.getProduct(sku);
            realm.executeTransactionAsync(
                    mRealm -> mRealm.copyToRealmOrUpdate(product),
                    () -> Timber.v("SUCCESS!"),
                    Timber::e);
            realm.close();
        } catch (Exception e){
            Timber.e(e);
        }
    }

    public static void saveProductAsOwned(String sku){
        Timber.v("Saving SKU %s as owned...", sku);
        try {
            Realm realm = Realm.getDefaultInstance();
            Products product = SupporterHelper.getProduct(sku);
            realm.executeTransaction(mRealm -> mRealm.copyToRealmOrUpdate(product));
            realm.close();
        } catch (Exception e){
            Timber.e(e);
        }
    }

    public static boolean isOwned(String sku){
        Timber.v("Checking if %s is owned...", sku);
        try {
            Realm realm = Realm.getDefaultInstance();
            RealmResults<Products> products = realm.where(Products.class).findAll();
            if (products != null) {
                for (Products product: products){
                    if (product.getSku().contains(sku)){
                        return true;
                    }
                }
            }
            realm.close();
            return false;
        } catch (Exception e){
            Timber.e(e);
            return false;
        }
    }

    public static boolean isSupporter(){
        try {
            Realm realm = Realm.getDefaultInstance();
            if (realm.where(Products.class).findFirst() != null) {
                realm.close();
                return true;
            } else {
                realm.close();
                return false;
            }
        } catch (Exception e){
            Timber.e(e);
            return false;
        }
    }

    public static boolean is2021Supporter(){
        try {
            Realm realm = Realm.getDefaultInstance();
            RealmResults<Products> products = realm.where(Products.class).findAll();
            if (products != null) {
                for (Products product: products){
                    if (product.getName().contains("2021")){
                        return true;
                    }
                }
            }
            realm.close();
            return false;
        } catch (Exception e){
            Timber.e(e);
            return false;
        }
    }

    public static boolean is2022Supporter(){
        try {
            Realm realm = Realm.getDefaultInstance();
            RealmResults<Products> products = realm.where(Products.class).findAll();
            if (products != null) {
                for (Products product: products){
                    if (product.getName().contains("2022")){
                        return true;
                    }
                }
            }
            realm.close();
            return false;
        } catch (Exception e){
            Timber.e(e);
            return false;
        }
    }

    public static boolean is2023Supporter(){
        try {
            Realm realm = Realm.getDefaultInstance();
            RealmResults<Products> products = realm.where(Products.class).findAll();
            if (products != null) {
                for (Products product: products){
                    if (product.getName().contains("2023")){
                        return true;
                    }
                }
            }
            realm.close();
            return false;
        } catch (Exception e){
            Timber.e(e);
            return false;
        }
    }

    public static boolean is2024Supporter(){
        try {
            Realm realm = Realm.getDefaultInstance();
            RealmResults<Products> products = realm.where(Products.class).findAll();
            if (products != null) {
                for (Products product: products){
                    if (product.getName().contains("2024")){
                        return true;
                    }
                }
            }
            realm.close();
            return false;
        } catch (Exception e){
            Timber.e(e);
            return false;
        }
    }
}
