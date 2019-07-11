package com.tapque.yumiadapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.mopub.mobileads.CustomEventInterstitial;
import com.mopub.mobileads.MoPubErrorCode;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.YumiInterstitial;
import com.yumi.android.sdk.ads.publish.listener.IYumiInterstitialListener;

import java.lang.ref.WeakReference;
import java.util.Map;

import javax.security.auth.login.LoginException;

public class YumiInterstitialCustomEvent extends CustomEventInterstitial {
    private static String TAG = "Ads";
    private String PLACEMENT_ID_KEY = "placementId";
    private WeakReference<Activity> mWeakActivity;

    private YumiInterstitial interstitial;
    @Override
    protected void loadInterstitial(Context context, final CustomEventInterstitialListener interstitialListener, Map<String, Object> localExtras, Map<String, String> serverExtras) {
        Log.e(TAG, "loadInterstitial: YumiInterstitialCustomEvent");
        if (!(context instanceof Activity)) {
            Log.e(TAG, "初始化玉米广告失败: " );
            return;
        }
        mWeakActivity=new WeakReference<>((Activity)context);
        String placementID=serverExtras.get(PLACEMENT_ID_KEY);
        if(TextUtils.isEmpty(placementID)){
            Log.e(TAG, "玉米插屏id 错误:");
            return;
        }
        Log.e(TAG, "loadInterstitial: "+placementID);
        interstitial = new YumiInterstitial(mWeakActivity.get(),placementID,true);
        interstitial.setInterstitialEventListener(new IYumiInterstitialListener() {
            @Override
            public void onInterstitialPrepared() {
                interstitialListener.onInterstitialLoaded();
            }
            @Override
            public void onInterstitialPreparedFailed(AdError adError) {
                Log.e(TAG, "玉米加载失败: "+adError );
                interstitialListener.onInterstitialFailed(MoPubErrorCode.NO_FILL);
            }

            @Override
            public void onInterstitialExposure() {
                interstitialListener.onInterstitialImpression();
            }

            @Override
            public void onInterstitialClicked() {
              interstitialListener.onInterstitialClicked();
            }
            @Override
            public void onInterstitialClosed() {
                interstitialListener.onInterstitialDismissed();
            }
            @Override
            public void onInterstitialExposureFailed(AdError adError) {
                Log.e(TAG, "onInterstitialExposureFailed: " );
            }
            @Override
            public void onInterstitialStartPlaying() {
                interstitialListener.onInterstitialShown();
            }
        });
        interstitial.requestYumiInterstitial();
    }
    @Override
    protected void showInterstitial() {
        if(interstitial!=null){
            Log.e(TAG, "玉米播放插屏广告: " );
            interstitial.showInterstitial(false);
        }
    }
    @Override
    protected void onInvalidate() {
        if(interstitial!=null){
            interstitial=null;
        }
    }
}
