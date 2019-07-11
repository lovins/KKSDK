//package com.lovin.ads;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//
//import com.playmil.blockfit3d.R;
//import java.util.List;
//
//public class IAPTestActivity extends Activity implements View.OnClickListener {
//    String TAG="IAP";
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.iap_main_test);
//        initView();
//        iapManager=IAP.instance();
//        iapManager.initIAPManager(this, new IAP.BillingUpdatesListener() {
//            @Override
//            public void onBillingClientSetupFinished() {
//                Log.e(TAG, "onBillingClientSetupFinished: " );
//            }
//            @Override
//            public void onConsumeFinished(String token, int result) {
//                Log.d(TAG, "Consumption finished. Purchase token: " + token + ", result: " + result);
//                if (result == BillingClient.BillingResponse.OK) {
//                    Log.e(TAG, "消费完成，处理逻辑: " );
//                }
//            }
//            @Override
//            public void onPurchasesUpdated(List<Purchase> purchaseList) {
//                Log.e(TAG, "购买更新成功: " );
//                for (Purchase purchase : purchaseList) {
//                    switch (purchase.getSku()) {
//                        case IAPConstant.GEM1:
//                            Log.e(TAG, "购买完成: "+purchase.getOriginalJson());
//                            // We should consume the purchase and fill up the tank once it was consumed
//                            iapManager.consumeAsync(purchase.getPurchaseToken());
//                            break;
//                        case IAPConstant.GEM2:
//                            Log.e(TAG, "购买完成: "+purchase.getOriginalJson());
//                            iapManager.consumeAsync(purchase.getPurchaseToken());
//                            break;
//                        case IAPConstant.GEM3:
//                            Log.e(TAG, "购买完成: "+purchase.getOriginalJson());
//                            iapManager.consumeAsync(purchase.getPurchaseToken());
//                            break;
//                        case IAPConstant.GEM4:
//                            Log.e(TAG, "购买完成: "+purchase.getOriginalJson());
//                            iapManager.consumeAsync(purchase.getPurchaseToken());
//                            break;
//                        case IAPConstant.GEM5:
//                            Log.e(TAG, "购买完成: "+purchase.getOriginalJson());
//                            iapManager.consumeAsync(purchase.getPurchaseToken());
//                            break;
//                        case IAPConstant.ON_TIME:
//                            Log.e(TAG, "购买完成: "+purchase.getOriginalJson());
//                            break;
//                        case IAPConstant.WEEKLY:
//                            if(purchase.isAutoRenewing()){
//                                Log.e(TAG, "订阅有效: ");
//                            }else{
//                                Log.e(TAG, "订阅取消: ");
//                            }
//                            Log.e(TAG, "购买完成: "+purchase.getOriginalJson());
//                            break;
//                    }
//                }
//            }
//        });
//    }
//    void initView(){
//        findViewById(R.id.gem1).setOnClickListener(this);
//        findViewById(R.id.gem2).setOnClickListener(this);
//        findViewById(R.id.gem3).setOnClickListener(this);
//        findViewById(R.id.gem4).setOnClickListener(this);
//        findViewById(R.id.gem5).setOnClickListener(this);
//        findViewById(R.id.remove_ads).setOnClickListener(this);
//        findViewById(R.id.weekly).setOnClickListener(this);
//        findViewById(R.id.cancel_button).setOnClickListener(this);
//    }
//
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case  R.id.gem1:
//                Log.e(TAG, "onClick: gem1" );
//                iapManager.initiatePurchaseFlow(IAPConstant.GEM1,BillingClient.SkuType.INAPP);
//                break;
//            case  R.id.gem2:
//                Log.e(TAG, "onClick: gem2" );
//                iapManager.initiatePurchaseFlow(IAPConstant.GEM2,BillingClient.SkuType.INAPP);
//                break;
//            case  R.id.gem3:
//                Log.e(TAG, "onClick: gem3" );
//                iapManager.initiatePurchaseFlow(IAPConstant.GEM3,BillingClient.SkuType.INAPP);
//                break;
//            case  R.id.gem4:
//                Log.e(TAG, "onClick: gem4" );
//                iapManager.initiatePurchaseFlow(IAPConstant.GEM4,BillingClient.SkuType.INAPP);
//                break;
//            case  R.id.gem5:
//                Log.e(TAG, "onClick: gem5" );
//                iapManager.initiatePurchaseFlow(IAPConstant.GEM5,BillingClient.SkuType.INAPP);
//                break;
//            case  R.id.remove_ads:
//                Log.e(TAG, "onClick: remove_ads" );
//                iapManager.initiatePurchaseFlow(IAPConstant.ON_TIME,BillingClient.SkuType.INAPP);
//                break;
//            case  R.id.weekly:
//                Log.e(TAG, "onClick: weekly" );
//                iapManager.initiatePurchaseFlow(IAPConstant.WEEKLY,BillingClient.SkuType.SUBS);
//                break;
//
//            case  R.id.cancel_button:
//                Log.e(TAG, "取消订阅" );
//                break;
//        }
//    }
//
//}
