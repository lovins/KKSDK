//package com.lovin.ads;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.os.Debug;
//import android.support.annotation.Nullable;
//import android.util.Log;
//import android.view.View;
//
//import com.iapppay.interfaces.callback.IPayResultCallback;
//import com.iapppay.sdk.main.IAppPay;
//import com.iapppay.sdk.main.IAppPayOrderUtils;
//import com.playmil.blockfit3d.R;
//import com.tapque.adscn.AdsCallbackCenter;
//import com.tapque.iapcn.IAPManager;
//import com.tapque.kkiap.IAP;
//
//public class IAPCNActivity extends Activity implements View.OnClickListener, IPayResultCallback {
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.iap_cn_main);
//        initView();
//        IAPManager.initIAPConfig(false,this,"purchase.json","Block_Fit_3d","3022715834",
//                "MIICXQIBAAKBgQCwb2qTb82EA6tzHraaLRHb/mp1/3EXWniWmXWKFy/cs4waz/yNW8PBVzQyxbV/vPaswsKikyaY2vhfM8PvLjpsgpWxYFt4AQ/6Uu8gX+looV+R2XLYt8ly+KxoHDa9wXDyVirqNDbXRMYdOjpDL2mnPDsyjp0rh8bR6rMV/c6MiwIDAQABAoGBAIRCgToUzZLwzU128JuSvZiUl2ntGvHOuCbs0te5mf9v+M8AqumsqIn+vPpyiyQ+mwvW0+DbPkvHmfiD7ZUWqmP7FJjpDfXUMLBdZaAg79WG/AubBUC4ue0jqgV5KdBjzd8l2QFIwBUSbYOfuyY13tRfoQt0Tpv7YW0/Q5zUY7BBAkEA90bzNhMH93q/C5T/qTPo2w5CeDvwyKGAsk0y/xOjvIXjr1UYC8yXaAJnPwRXZ4y7ziDpKoZngcUbXprfVQoJnQJBALaouWzSQsEOJFGzby0ZrlhheJzyj7wotTdJG4BbHvNghBCNf5vBgwHPj2S6FGDpVKf6S/KbKYVfwXc0dLswykcCQQCeTsEEPHjcOqw2mQR4afBI1b8+RHmpqKPBAuWEIimwVzNmG59SZoAEKY257WibtaDCnZVJEVUQ4oTcM2CJi+yNAkBQOiFfU6awRrbbD7XIGFyEQyOHw94v5a1agAjNQriAayb2Yj/nOIKDHBg+RKDIxBNDyGLtRwtgomNGKUsQIOjnAkAb7+PfY85NN04nPeK5V/d4qiYZnuld8JpTSJF317+V4tB3rhi3krBT2N1zw6CEwYRKfKDF2SYJ/gzWIUH/Cug4",
//                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCMYik5PHpFFpWBkjaF5/dtyXdN1bgoOlRvxtL6G28TqAfaTrexO0c3wtBDA5TBxOYV9J17fxk3ZkwU5Bw2pfk7cqKkt5GXEXpuL+llltB3gahQFrQiMI7mhGin+AR7soTlXq4Gkb8D3iQyz57wMYfUqZhcqqdVJx66233fcocyRwIDAQAB");
//    }
//    void initView(){
//        findViewById(R.id.purchase1_btn).setOnClickListener(this);
//    }
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case  R.id.purchase1_btn:
//                IAPManager.startPlay(this,"测试",this);
//                break;
//        }
//    }
//    @Override
//    public void onPayResult(int resultCode, String signValue, String resultInfo) {
//        switch (resultCode){
//            case IAppPay.PAY_SUCCESS:
//                boolean payState = IAppPayOrderUtils.checkPayResult(signValue, IAPManager.PUBLIC_KEY);
//                if(null==IAPManager.currentPurchasingItem){
//                    Log.e("IAP", "系统错误" );
//                    return;
//                }
//                AdsCallbackCenter.sendPurchaseState(IAPManager.productName);
//                break;
//            case IAppPay.PAY_CANCEL:
//                Log.e("IAP", "取消支付"+resultInfo);
//
//                break;
//            default:
//
//        }
//    }
//}
