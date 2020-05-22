package me.calebjones.spacelaunchnow.common.ui.supporter;


import io.realm.Realm;
import me.calebjones.spacelaunchnow.data.models.Products;
import timber.log.Timber;

public class SupporterHelper {
    // SKU for our subscription (infinite gas)
    public static final String SKU_TWO_DOLLAR = "two_dollar_support";
    private static final String SKU_SIX_DOLLAR = "six_dollar_support";
    private static final String SKU_TWELVE_DOLLAR = "twelve_dollar_support";
    private static final String SKU_THIRTY_DOLLAR = "thirty_dollar_support";
    private static final String SKU_2018_TWO_DOLLAR = "2018_two_dollar_support";
    private static final String SKU_2018_SIX_DOLLAR = "2018_six_dollar_support";
    private static final String SKU_2018_TWELVE_DOLLAR = "2018_twelve_dollar_support";
    private static final String SKU_2018_THIRTY_DOLLAR = "2018_thirty_dollar_support";
    static final String SKU_2020_BRONZE = "2020_bronze_supporter";
    static final String SKU_2020_METAL = "2020_metal_supporter";
    static final String SKU_2020_SILVER = "2020_silver_support";
    static final String SKU_2020_GOLD = "2020_gold_support";
    static final String SKU_2020_PLATINUM = "2020_platinum_support";
    private static final String SKU_OTHER = "beta_supporter";

    public static Products getProduct(String productID){
        Products product = new Products();
        if (productID.equals(SKU_TWO_DOLLAR)) {
            product.setName("Founder 2016 - Bronze");
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(2);
        } else if (productID.equals(SKU_SIX_DOLLAR)){
            product.setName("Founder 2016 - Silver");
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(6);
        } else if (productID.equals(SKU_TWELVE_DOLLAR)){
            product.setName("Founder 2016 - Gold");
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(12);
        } else if (productID.equals(SKU_THIRTY_DOLLAR)) {
            product.setName("Founder 2016 - Platinum");
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(30);
        }else if (productID.equals(SKU_2018_THIRTY_DOLLAR)) {
            product.setName("Supporter 2018 - Platinum");
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(30);
        }  else if (productID.equals(SKU_2018_TWELVE_DOLLAR)){
            product.setName("Supporter 2018 - Gold");
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(30);
        } else if (productID.equals(SKU_2018_SIX_DOLLAR)){
            product.setName("Supporter 2018 - Silver");
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(30);
        } else if (productID.equals(SKU_2018_TWO_DOLLAR)){
            product.setName("Supporter 2018 - Bronze");
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(30);
        }else if (productID.equals(SKU_2020_PLATINUM)) {
            product.setName("Supporter 2020 - Platinum");
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(30);
        }  else if (productID.equals(SKU_2020_GOLD)){
            product.setName("Supporter 2020 - Gold");
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(30);
        } else if (productID.equals(SKU_2020_SILVER)){
            product.setName("Supporter 2020 - Silver");
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(30);
        } else if (productID.equals(SKU_2020_BRONZE)){
            product.setName("Supporter 2020 - Bronze");
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(30);
        } else if (productID.equals(SKU_2020_METAL)){
            product.setName("Supporter 2020 - Metal");
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(6);
        } else if (productID.equals(SKU_OTHER)){
            product.setName("Promotion Supporter");
            product.setDescription("This ensures you will always have access to every supporter features.");
            product.setType("Supporter");
            product.setPrice(30);
        }
        return product;
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


}
