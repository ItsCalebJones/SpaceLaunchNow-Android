package me.calebjones.spacelaunchnow.supporter;


import io.realm.Realm;

public class SupporterHelper {
    // SKU for our subscription (infinite gas)
    public static final String SKU_TWO_DOLLAR = "two_dollar_support";
    public static final String SKU_SIX_DOLLAR = "six_dollar_support";
    public static final String SKU_TWELVE_DOLLAR = "twelve_dollar_support";
    public static final String SKU_OTHER = "beta_supporter";

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
        }
        return product;
    }

    public static boolean isSupporter(){
        Realm realm = Realm.getDefaultInstance();
        boolean supporter = false;
        Products realmResults = realm.where(Products.class).findFirst();

        return realm.where(Products.class).findFirst() != null;
    }
}
