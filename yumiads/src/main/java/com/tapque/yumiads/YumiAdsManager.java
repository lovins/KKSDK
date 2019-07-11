package com.tapque.yumiads;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.YumiBanner;
import com.yumi.android.sdk.ads.publish.enumbean.AdSize;
import com.yumi.android.sdk.ads.publish.listener.IYumiBannerListener;

public class YumiAdsManager {
    String TAG="Ads";
    public  void requestBanner(Activity activity,FrameLayout container){
        YumiBanner banner = new YumiBanner(activity,"uz852t89",true);
        banner.setBannerContainer(container,AdSize.BANNER_SIZE_AUTO,true);
        banner.setBannerEventListener(new IYumiBannerListener() {
            @Override
            public void onBannerPrepared() {

                Log.e(TAG, "onBannerPrepared: ");
            }

            @Override
            public void onBannerPreparedFailed(AdError adError) {
                Log.e(TAG, "onBannerPreparedFailed: ");

            }

            @Override
            public void onBannerExposure() {
                Log.e(TAG, "onBannerExposure: " );

            }

            @Override
            public void onBannerClicked() {


                Log.e(TAG, "onBannerClicked: ");

            }

            @Override
            public void onBannerClosed() {


                Log.e(TAG, "onBannerClosed: " );

            }
        });
        banner.requestYumiBanner();
    }

}
