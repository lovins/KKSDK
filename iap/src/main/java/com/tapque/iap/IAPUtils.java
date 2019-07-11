package com.tapque.iap;

import java.util.ArrayList;
import java.util.HashMap;

public class IAPUtils {

    //release
    public static String PIXEL_ART_NO_AD_SKU = "com.pixelart.removeads";
    public static String PIXEL_ART_PREMIUMS_SKU = "com.pixelart.premiums";
    private static String DEFAULT_PRODUCT_NAME = "com.pixelart.purchase";
    private static double DEFAULT_PRODUCT_PRICE = 0.99;

    private static HashMap<String, Double> priceMappingForSKU = new HashMap<>();
    private static HashMap<String, String> productNameMappingForSKU = new HashMap<>();
    private static ArrayList<String> iPlaySkuNameList = new ArrayList<>();


    public static void createPriceMapping() {
        priceMappingForSKU.clear();
        priceMappingForSKU.put(PIXEL_ART_NO_AD_SKU,Double.valueOf("2.99"));
        priceMappingForSKU.put(PIXEL_ART_PREMIUMS_SKU,Double.valueOf("1.99"));
    }


    public static void createProductMapping(){
        productNameMappingForSKU.clear();
        productNameMappingForSKU.put(PIXEL_ART_NO_AD_SKU,"com.pixelart.removeads");
        productNameMappingForSKU.put(PIXEL_ART_PREMIUMS_SKU,"com.pixelart.premiums");
    }

    public static double getPriceBySku(String sku) {

        if (sku != null) {
            if (priceMappingForSKU == null || priceMappingForSKU.isEmpty()) {
                createPriceMapping();
            }
            return priceMappingForSKU.get(sku);
        }

        return DEFAULT_PRODUCT_PRICE;
    }

    /**
     * get productName sku
     * @param sku
     * @return
     */
    public static String getProductNameBySku(String sku) {

        if (sku != null) {
            if (productNameMappingForSKU == null || productNameMappingForSKU.isEmpty()) {
                createProductMapping();
            }

            return productNameMappingForSKU.get(sku);
        }

        return DEFAULT_PRODUCT_NAME;
    }

    public static ArrayList<String> loadSubscriptionSKUList(){
        if(iPlaySkuNameList == null){
            iPlaySkuNameList = new ArrayList<>();
        }
        if(iPlaySkuNameList.size() > 0){
            return iPlaySkuNameList;
        }else{
            iPlaySkuNameList.add(PIXEL_ART_NO_AD_SKU);
            iPlaySkuNameList.add(PIXEL_ART_PREMIUMS_SKU);
            return iPlaySkuNameList;
        }
    }
}
