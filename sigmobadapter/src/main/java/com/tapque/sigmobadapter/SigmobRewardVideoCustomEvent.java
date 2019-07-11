package com.tapque.sigmobadapter;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mopub.common.LifecycleListener;
import com.mopub.common.MoPubReward;
import com.mopub.mobileads.CustomEventRewardedVideo;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubRewardedVideoManager;
import com.sigmob.windad.WindAdError;
import com.sigmob.windad.WindAdOptions;
import com.sigmob.windad.WindAdRequest;
import com.sigmob.windad.WindAds;
import com.sigmob.windad.rewardedVideo.WindRewardInfo;
import com.sigmob.windad.rewardedVideo.WindRewardedVideoAd;
import com.sigmob.windad.rewardedVideo.WindRewardedVideoAdListener;

import java.lang.ref.WeakReference;
import java.util.Map;

public class SigmobRewardVideoCustomEvent extends CustomEventRewardedVideo {
    private static String TAG = "Ads";
    private  String appKey;
    private  String appid;
    private  String placementId;
    private  String debug;
    private WindAdRequest request;
    private WeakReference<Activity> mWeakActivity;
    boolean isReadly;
    WindRewardedVideoAd windRewardedVideoAd ;
    @Nullable
    @Override
    protected LifecycleListener getLifecycleListener() {
        return null;
    }
    @Override
    protected boolean checkAndInitializeSdk(@NonNull Activity launcherActivity, @NonNull Map<String, Object> localExtras, @NonNull Map<String, String> serverExtras) throws Exception {
        mWeakActivity=new WeakReference<>(launcherActivity);
        appKey=serverExtras.get("appKey");
        appid=serverExtras.get("appid");
        debug=serverExtras.get("debug");
        placementId=serverExtras.get("placementId");
        if(TextUtils.isEmpty(appKey)||TextUtils.isEmpty(appid)||TextUtils.isEmpty(placementId)){
            Log.e(TAG, "mopub  后台中sigmob配置参数有误 " );
            return false;
        }
        return true;
    }

    @Override
    protected void loadWithSdkInitialized(@NonNull Activity activity, @NonNull Map<String, Object> localExtras, @NonNull Map<String, String> serverExtras) throws Exception {
        WindAds ads = WindAds.sharedAds();
        ads.setDebugEnable(!TextUtils.isEmpty(debug));
        ads.startWithOptions(mWeakActivity.get().getApplication(), new WindAdOptions(appid,appKey));
        request = new WindAdRequest(placementId,"",null);
        isReadly=false;
        windRewardedVideoAd=WindRewardedVideoAd.sharedInstance();
        windRewardedVideoAd.setWindRewardedVideoAdListener(new WindRewardedVideoAdListener() {
            @Override
            public void onVideoAdLoadSuccess(String s) {
                Log.e(TAG, " sigmob 加载成功: "+ s);
                isReadly=true;
                MoPubRewardedVideoManager.onRewardedVideoLoadSuccess(SigmobRewardVideoCustomEvent.class,placementId);
            }
            @Override
            public void onVideoAdPreLoadSuccess(String s) {
            }
            @Override
            public void onVideoAdPreLoadFail(String s) {
                isReadly=false;
                MoPubRewardedVideoManager.onRewardedVideoLoadFailure(SigmobRewardVideoCustomEvent.class,placementId, MoPubErrorCode.NO_FILL);
            }

            @Override
            public void onVideoAdPlayStart(String s) {
                MoPubRewardedVideoManager.onRewardedVideoStarted(SigmobRewardVideoCustomEvent.class,placementId);
            }

            @Override
            public void onVideoAdPlayEnd(String s) {
                MoPubRewardedVideoManager.onRewardedVideoCompleted(SigmobRewardVideoCustomEvent.class,placementId, MoPubReward.success("",1));
            }

            @Override
            public void onVideoAdClicked(String s) {
                MoPubRewardedVideoManager.onRewardedVideoClicked(SigmobRewardVideoCustomEvent.class,placementId);
            }
            @Override
            public void onVideoAdClosed(WindRewardInfo windRewardInfo, String s) {
                MoPubRewardedVideoManager.onRewardedVideoClosed(SigmobRewardVideoCustomEvent.class,placementId);
            }
            @Override
            public void onVideoAdLoadError(WindAdError windAdError, String s) {
                isReadly=false;
            }
            @Override
            public void onVideoAdPlayError(WindAdError windAdError, String s) {
            }
        });
        windRewardedVideoAd.loadAd(request);
    }

    @NonNull
    @Override
    protected String getAdNetworkId() {
        return placementId;
    }
    @Override
    protected void onInvalidate() {
        if(windRewardedVideoAd!=null)
            windRewardedVideoAd=null;
    }

    @Override
    protected boolean hasVideoAvailable() {
        if(windRewardedVideoAd!=null&&windRewardedVideoAd.isReady(placementId))
            return  true;
        return false;
    }
    @Override
    protected void showVideo() {
            try {
                if(hasVideoAvailable()){
                    windRewardedVideoAd.show(mWeakActivity.get(),request);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

    }
}
