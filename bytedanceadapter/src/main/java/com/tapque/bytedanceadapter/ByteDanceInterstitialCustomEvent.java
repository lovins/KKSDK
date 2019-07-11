package com.tapque.bytedanceadapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.mopub.mobileads.CustomEventInterstitial;
import com.mopub.mobileads.MoPubErrorCode;

import java.lang.ref.WeakReference;
import java.util.Map;

public class ByteDanceInterstitialCustomEvent extends CustomEventInterstitial {
    private TTAdNative ttNativeAd;
    private WeakReference<Activity> mWeakActivity;
    private TTFullScreenVideoAd fullScreenVideoAd;
    private  String appid;
    public  String placementID;
    private static String TAG = "Ads";
    @Override
    protected void loadInterstitial(Context context, final CustomEventInterstitialListener customEventInterstitialListener, Map<String, Object> localExtras, Map<String, String> serverExtras) {
        Log.e(TAG, "loadInterstitial: ByteDanceInterstitialCustomEvent");
        if (!(context instanceof Activity)) {
            Log.e(TAG, "Activity error");
            return;
        }
        mWeakActivity=new WeakReference<>((Activity) context);
        appid=serverExtras.get("appid");
        if(TextUtils.isEmpty(appid)){
            return;
        }else{
            ByteDanceManagerHolder.init(mWeakActivity.get(),appid);
            ttNativeAd= ByteDanceManagerHolder.get().createAdNative(mWeakActivity.get());
        }
        placementID=serverExtras.get("placementID");
       if(TextUtils.isEmpty(placementID)){
           Log.e(TAG, "获取的头条插屏id为空 " );
           return;
       }
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(placementID)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                .setOrientation(TTAdConstant.VERTICAL)
                .build();
       ttNativeAd.loadFullScreenVideoAd(adSlot, new TTAdNative.FullScreenVideoAdListener() {
           @Override
           public void onError(int i, String s) {
               fullScreenVideoAd=null;
               Log.e(TAG, "插屏广告加载失败"+s);
               customEventInterstitialListener.onInterstitialDismissed();
           }
           @Override
           public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ttFullScreenVideoAd) {
               fullScreenVideoAd=ttFullScreenVideoAd;
               fullScreenVideoAd.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {
                   @Override
                   public void onAdShow() {
                       Log.e(TAG, "onAdShow: " );
                       customEventInterstitialListener.onInterstitialShown();
                       customEventInterstitialListener.onInterstitialImpression();
                   }
                   @Override
                   public void onAdVideoBarClick() {
                       Log.e(TAG, "onAdShow: " );
                       customEventInterstitialListener.onInterstitialClicked();
                   }

                   @Override
                   public void onAdClose() {
                       Log.e(TAG, "onAdShow: " );

                       customEventInterstitialListener.onInterstitialDismissed();
                   }
                   @Override
                   public void onVideoComplete() {
                       Log.e(TAG, "onAdShow: " );
                   }
                   @Override
                   public void onSkippedVideo() {
                       Log.e(TAG, "onAdShow: " );
                   }
               });
           }
           @Override
           public void onFullScreenVideoCached() {
               customEventInterstitialListener.onInterstitialLoaded();
           }
       });
    }
    @Override
    protected void showInterstitial() {
        if(null!=fullScreenVideoAd){
            fullScreenVideoAd.showFullScreenVideoAd(mWeakActivity.get());
        }
    }
    @Override
    protected void onInvalidate() {
        if(null!=fullScreenVideoAd){
            fullScreenVideoAd=null;
        }
    }
}
