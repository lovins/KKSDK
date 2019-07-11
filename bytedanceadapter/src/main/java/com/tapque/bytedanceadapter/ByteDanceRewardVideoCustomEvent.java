package com.tapque.bytedanceadapter;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.mopub.common.LifecycleListener;
import com.mopub.common.MoPubReward;
import com.mopub.mobileads.CustomEventRewardedVideo;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubRewardedVideoManager;

import java.lang.ref.WeakReference;
import java.util.Map;

public class ByteDanceRewardVideoCustomEvent extends CustomEventRewardedVideo {
    private static String TAG = "Ads";
    private  String appid;
    private String placementID;
    private TTAdNative ttNativeAd;
    private WeakReference<Activity> mWeakActivity;
    private boolean isReadly;

    private TTRewardVideoAd rewardVideoAd;

    @Override
    protected boolean hasVideoAvailable() {
        return rewardVideoAd!=null&&isReadly;
    }

    @Override
    protected void showVideo() {
        if(hasVideoAvailable()&&mWeakActivity!=null&&mWeakActivity.get()!=null){
            rewardVideoAd.showRewardVideoAd(mWeakActivity.get());
        }
    }
    @Nullable
    @Override
    protected LifecycleListener getLifecycleListener() {
        return null;
    }
    @Override
    protected boolean checkAndInitializeSdk(@NonNull Activity launcherActivity, @NonNull Map<String, Object> localExtras, @NonNull Map<String, String> serverExtras) throws Exception {
        appid=serverExtras.get("appid");
        placementID=serverExtras.get("placementID");
        if(TextUtils.isEmpty(appid)){
            if(TextUtils.isEmpty(serverExtras.get(placementID))){
                Log.e(TAG, "获取的头条激励视频id为空");
                return  false;
            }
            Log.e(TAG, "初始化头条广告失败，无效的appid");
            return  false;
        }else{
            ByteDanceManagerHolder.init(launcherActivity,appid);
            ttNativeAd=ByteDanceManagerHolder.get().createAdNative(launcherActivity);
            return  true;
        }
    }

    @Override
    protected void loadWithSdkInitialized(@NonNull Activity activity, @NonNull Map<String, Object> localExtras, @NonNull Map<String, String> serverExtras) throws Exception {
        mWeakActivity=new WeakReference<>(activity);
        placementID=serverExtras.get("placementID");
        if(!TextUtils.isEmpty(placementID)){
            isReadly=false;
            AdSlot adSlot = new AdSlot.Builder()
                    .setCodeId(placementID)
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(1080, 1920)
                    .setRewardName("金币") //奖励的名称
                    .setRewardAmount(3)  //奖励的数量
                    .setUserID("user123")//用户id,必传参数
                    .setOrientation(TTAdConstant.VERTICAL) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                    .build();
            ttNativeAd.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
                @Override
                public void onError(int i, String s) {
                    isReadly=false;
                    MoPubRewardedVideoManager.onRewardedVideoLoadFailure(ByteDanceRewardVideoCustomEvent.class,placementID, MoPubErrorCode.NO_FILL);
                }

                @Override
                public void onRewardVideoAdLoad(TTRewardVideoAd ttRewardVideoAd) {
                    rewardVideoAd=ttRewardVideoAd;
                    rewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {
                        @Override
                        public void onAdShow() {
                            MoPubRewardedVideoManager.onRewardedVideoStarted(ByteDanceRewardVideoCustomEvent.class,getAdNetworkId());
                        }

                        @Override
                        public void onAdVideoBarClick() {
                            MoPubRewardedVideoManager.onRewardedVideoClicked(ByteDanceRewardVideoCustomEvent.class,getAdNetworkId());
                        }

                        @Override
                        public void onAdClose() {
                            MoPubRewardedVideoManager.onRewardedVideoClosed(ByteDanceRewardVideoCustomEvent.class,getAdNetworkId());
                        }

                        @Override
                        public void onVideoComplete() {
                            MoPubRewardedVideoManager.onRewardedVideoCompleted(ByteDanceRewardVideoCustomEvent.class,getAdNetworkId(), MoPubReward.success("",1));
                        }
                        @Override
                        public void onVideoError() {
                            MoPubRewardedVideoManager.onRewardedVideoPlaybackError(ByteDanceRewardVideoCustomEvent.class,getAdNetworkId(),MoPubErrorCode.UNSPECIFIED);
                        }
                        @Override
                        public void onRewardVerify(boolean b, int i, String s) {

                        }
                    });
                }
                @Override
                public void onRewardVideoCached() {
                    isReadly=true;
                    MoPubRewardedVideoManager.onRewardedVideoLoadSuccess(ByteDanceRewardVideoCustomEvent.class,placementID);
                }
            });
        }
    }
    @NonNull
    @Override
    protected String getAdNetworkId() {
        return placementID;
    }
    @Override
    protected void onInvalidate() {
        if (rewardVideoAd != null) {
            rewardVideoAd = null;
        }
    }
}
