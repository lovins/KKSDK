package com.tapque.bytedanceadapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTBannerAd;
import com.mopub.mobileads.CustomEventBanner;
import com.mopub.mobileads.MoPubErrorCode;

import java.lang.ref.WeakReference;
import java.util.Map;

public class ByteDanceBannerCustomEvent extends CustomEventBanner {

    private TTAdNative ttNativeAd;
    private WeakReference<Activity> mWeakActivity;
    private  String appid;
    public  String placementID;
    private static String TAG = "Ads";

    TTBannerAd ttBannerAd;

    public  static FrameLayout bannerContainer;
    private  ViewGroup rootView;
    private boolean isInit;
    public  void createBannerContainner(final Activity activity){
        if(null==rootView)
            rootView= (ViewGroup) LayoutInflater.from(activity).inflate(R.layout.banner_view2,null);
        if(!isInit){
            activity.addContentView(rootView,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
            bannerContainer=rootView.findViewById(R.id.adview);
            isInit=true;
        }else{
            Log.e(TAG, "移除子view: " );
        }
    }
    @Override
    protected void loadBanner(Context context, final CustomEventBannerListener customEventBannerListener, Map<String, Object> localExtras, Map<String, String> serverExtras) {
        Log.e(TAG, " 加载头条banner广告 " );
        if (!(context instanceof Activity)) {
            Log.e(TAG, "Activity error");
            return;
        }
        mWeakActivity=new WeakReference<>((Activity) context);
        createBannerContainner(mWeakActivity.get());
        appid=serverExtras.get("appid");
        if(TextUtils.isEmpty(appid)){
            return;
        }else{
            ByteDanceManagerHolder.init(mWeakActivity.get(),appid);
            ttNativeAd= ByteDanceManagerHolder.get().createAdNative(mWeakActivity.get());
        }
        placementID=serverExtras.get("placementID");
        if(TextUtils.isEmpty(placementID)){
            return;
        }
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(placementID) //广告位id
                .setSupportDeepLink(true)
                .setImageAcceptedSize(640, 100)
                .build();
        ttNativeAd.loadBannerAd(adSlot, new TTAdNative.BannerAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.e(TAG, "头条banner加载失败"+message);
                customEventBannerListener.onBannerFailed(MoPubErrorCode.UNSPECIFIED);
            }
            @Override
            public void onBannerAdLoad(final TTBannerAd ad) {
                if (ad == null) {
                    return;
                }
                Log.e(TAG, "头条banner加载成功" );
                rootView.removeAllViews();
                customEventBannerListener.onBannerLoaded(bannerContainer);
                //设置广告互动监听回调
                ad.setBannerInteractionListener(new TTBannerAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int type) {
                        customEventBannerListener.onBannerClicked();
                    }
                    @Override
                    public void onAdShow(View view, int type) {
                        customEventBannerListener.onBannerExpanded();
                    }
                    });
                    ad.setShowDislikeIcon(new TTAdDislike.DislikeInteractionCallback() {
                    @Override
                    public void onSelected(int position, String value) {

                    }
                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
    }
    @Override
    protected void onInvalidate() {
        if(ttBannerAd!=null){
            ttBannerAd=null;
        }
    }
}
