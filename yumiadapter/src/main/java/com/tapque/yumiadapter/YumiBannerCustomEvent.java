package com.tapque.yumiadapter;

import android.app.Activity;
import android.content.Context;
import android.print.PrinterId;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.mopub.common.DataKeys;
import com.mopub.mobileads.CustomEventBanner;
import com.mopub.mobileads.MoPubErrorCode;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.YumiBanner;
import com.yumi.android.sdk.ads.publish.enumbean.AdSize;
import com.yumi.android.sdk.ads.publish.listener.IYumiBannerListener;

import java.lang.ref.WeakReference;
import java.util.Map;

public class YumiBannerCustomEvent extends CustomEventBanner {
    private static String TAG = "Ads";
    private YumiBanner bannerAd;
    private String PLACEMENT_ID_KEY = "placementId";
    private WeakReference<Activity> mWeakActivity;
    private  ViewGroup rootView;
    private boolean isInit;
    public  static FrameLayout bannerContainer;

    public  void createBannerContainner(final Activity activity){
        if(null==rootView)
            rootView= (ViewGroup) LayoutInflater.from(activity).inflate(R.layout.banner_view,null);
        if(!isInit){
            Log.e(TAG, "run: " );
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
        Log.e(TAG, "banner: 加载玉米banner" );
        if (!(context instanceof Activity)) {
            return;
        }
        mWeakActivity=new WeakReference<>((Activity) context);
        createBannerContainner(mWeakActivity.get());
        final String placementId;
        placementId=serverExtras.get(PLACEMENT_ID_KEY);
        if(TextUtils.isEmpty(placementId)){
            Log.e(TAG, "玉米banner id错误: "  );
            return;
        }
        final YumiBanner banner = new YumiBanner(mWeakActivity.get(), placementId,true);
        banner.setBannerContainer(bannerContainer, AdSize.BANNER_SIZE_AUTO,true);
        banner.setBannerEventListener(new IYumiBannerListener() {
            @Override
            public void onBannerPrepared() {
                rootView.removeAllViews();
                customEventBannerListener.onBannerLoaded(bannerContainer);
                Log.e(TAG, "玉米banner准备成功: " );
            }
            @Override
            public void onBannerPreparedFailed(AdError adError) {
                Log.e(TAG, "玉米banner加爱失败: "+adError);
                customEventBannerListener.onBannerFailed(MoPubErrorCode.UNSPECIFIED);
            }
            @Override
            public void onBannerExposure() {
                Log.e(TAG, "玉米banner展示: " );
                customEventBannerListener.onBannerImpression();
            }
            @Override
            public void onBannerClicked() {
                Log.e(TAG, "玉米banner点击: " );
                customEventBannerListener.onBannerClicked();
            }
            @Override
            public void onBannerClosed() {
                Log.e(TAG, "玉米banner关闭: ");
               customEventBannerListener.onBannerCollapsed();
            }
        });
        banner.requestYumiBanner();
    }
    @Override
    protected void onInvalidate() {
        if (bannerAd != null) {
            bannerAd = null;
        }
    }
}
