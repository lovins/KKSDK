package com.tapque.kkiap;

import com.android.billingclient.api.BillingClient;

public class LayaIAPManager {

    public  static  void setListener(){
        IAP.instance().setLayaListener();
    }
    public  static  void purchaseItem(String productID,int type){
        IAP.instance().initiatePurchaseFlow(productID,type==0?BillingClient.SkuType.INAPP:BillingClient.SkuType.SUBS);
    }

}
