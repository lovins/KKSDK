package com.tapque.adscn;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.adjust.sdk.AdjustConfig;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTBannerAd;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.tapque.analyticscn.Analytics;
import com.tapque.kkcnads.R;

import org.json.JSONException;
import org.json.JSONObject;

public class AdsManager {
    String TAG="ADS";
    private static AdsManager adsManagerInstance;
    TTAdManager ttAdManager;
    TTAdNative ttAdNative;
    private  boolean init;

    private boolean showAdsLog;
    Activity activity;
    private boolean autoPlay;

    public String bannerID;
    public String padBannerID;
    private String interstitialID;
    private String otherInterstitialID;
    private String rewardVideoID;

    public static AdsManager instance() {
        if (null == adsManagerInstance) {
            adsManagerInstance = new AdsManager();
        }
        return adsManagerInstance;
    }
    private void init(Activity activity, boolean debug) {
        if(null==this.activity){
            this.activity = activity;
        }
        this.showAdsLog = debug;
        isCallbackToAndroid = false;
        Analytics.setContext(activity);
        log("初始化中国版本广告");
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                init();
            }
        });
    }


    protected   void setLayaActivity(Activity activity){
        log("设置上下文");
        this.activity=activity;
    }

    protected   void initAdsFromLaya(boolean debug){
        this.showAdsLog=debug;
        init(activity,showAdsLog);
    }
    boolean isForceClose=false;

    public  void forceCloseDebug(boolean close){
        isForceClose=close;
    }
    private   void log(String message){
        if(showAdsLog&&!isForceClose){
            Log.e("Ads", message );
        }
    }
    public  void init(){
        init=true;
        TTAdManagerHolder.init(activity);
        ttAdManager= TTAdManagerHolder.get();
        ttAdNative=ttAdManager.createAdNative(activity);
        bannerID =Utils.readIntValueFromManifest(activity, "bannerID")+"";
        padBannerID = Utils.readIntValueFromManifest(activity, "padBannerID")+"";
        interstitialID = Utils.readIntValueFromManifest(activity, "interstitialID")+"";
        otherInterstitialID = Utils.readIntValueFromManifest(activity, "otherInterstitialID")+"";
        rewardVideoID =Utils.readIntValueFromManifest(activity, "rewardVideoID")+"";
        log("插屏id"+interstitialID+"激励id"+rewardVideoID);
        if(!TextUtils.isEmpty(bannerID)){
            View view=LayoutInflater.from(activity).inflate(R.layout.banner_ad_top,null);
            activity.addContentView(view,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mBannerContainer=view.findViewById(R.id.banner_container);
        }
        AdsCallbackCenter.sendMessageToEngine("INIT_SUCCEED");
    }
    private int adsIntervalTime = 0;
    private long lastTime = 0;
    private  void resetAdIntervalTime(){
        if(showAdsLog){
            log("重置插屏广告间隔时间");
        }
        lastTime = System.currentTimeMillis();
    }
    private boolean canPlayAds() {
        long currentlyTime = System.currentTimeMillis();
        long d = currentlyTime - lastTime;
        log("时间差值:"+d);
        if (d > adsIntervalTime * 1000) {
            return true;
        }
        log("CD时间: 还剩" +(adsIntervalTime-(d/1000f))+"秒可播放插屏广告");
        return false;
    }
    private TTFullScreenVideoAd otherFullScreenVideoAd;
    private TTFullScreenVideoAd fullScreenVideoAd;
    long lastRequestInterstitialTime;
    public void requestInterstitial(final int indexId) {
        if(!init){
            Log.e(TAG, "请先初始化广告");
            return;
        }
        String  currentlyInterstitialID=indexId==0?interstitialID:otherInterstitialID;
        if(TextUtils.isEmpty(currentlyInterstitialID)){
            Log.e(TAG, "请求插屏广告失败，请在清单文件中配置插屏id: ");
            return;
        }
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(currentlyInterstitialID)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                .setOrientation(TTAdConstant.VERTICAL)
                .build();
        log("正在请求插屏广告");
        Analytics.setThinkTrackEvent(AdsState.INTERSTITIAL_REQUEST);
        ttAdNative.loadFullScreenVideoAd(adSlot, new TTAdNative.FullScreenVideoAdListener() {
            @Override
            public void onError(int i, String s) {
                double loadInterstitialTime=(System.currentTimeMillis()-lastRequestInterstitialTime)/1000.0;
                log("插屏加载失败:"+s+"耗时:"+loadInterstitialTime);
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("errorCode",s);
                    jsonObject.put("LOAD_IN_FAILED_TIME",loadInterstitialTime);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Analytics.setThinkTrackEvent(AdsState.INTERSTITIAL_FAILED,jsonObject);
                AdsCallbackCenter.sendMessageToEngine(AdsState.INTERSTITIAL_FAILED);
            }
            @Override
            public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ttFullScreenVideoAd) {
                if(indexId==0){
                    fullScreenVideoAd=ttFullScreenVideoAd;
                    fullScreenVideoAd.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {
                        @Override
                        public void onAdShow() {
                            log("打开插屏");
                            Analytics.setThinkTrackEvent(AdsState.INTERSTITIAL_OPEN);
                            AdsCallbackCenter.sendMessageToEngine(AdsState.INTERSTITIAL_OPEN);
                        }
                        @Override
                        public void onAdVideoBarClick() {

                        }
                        @Override
                        public void onAdClose() {
                            log("插屏广告关闭");
                            resetAdIntervalTime();
                            Analytics.logAdjustEvent(Utils.readValueFromManifest(activity,"interstitial_impression"));
                            AdsCallbackCenter.sendMessageToEngine(AdsState.INTERSTITIAL_CLOSE);
                            Analytics.setThinkTrackEvent(AdsState.INTERSTITIAL_IMPRESSION);
                            fullScreenVideoAd=null;
                            requestInterstitial(0);
                        }
                        @Override
                        public void onVideoComplete() {
                        }
                        @Override
                        public void onSkippedVideo() {

                        }
                    });
                }else{
                    otherFullScreenVideoAd =ttFullScreenVideoAd;
                    otherFullScreenVideoAd.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {
                        @Override
                        public void onAdShow() {
                            log("另外一个插屏广告打开");
                        }
                        @Override
                        public void onAdVideoBarClick() {

                        }
                        @Override
                        public void onAdClose() {
                            log("另外一个插屏广告关闭");
                            resetAdIntervalTime();
                            otherFullScreenVideoAd =null;
                        }
                        @Override
                        public void onVideoComplete() {

                        }
                        @Override
                        public void onSkippedVideo() {

                        }
                    });
                }
            }
            @Override
            public void onFullScreenVideoCached() {
                if(indexId==0){
                    double loadInterstitialTime=(System.currentTimeMillis()-lastRequestInterstitialTime)/1000.0;
                    log("插屏加载成功,所需要的时长"+loadInterstitialTime);
                    JSONObject jsonObject=new JSONObject();
                    try {
                        jsonObject.put("LOAD_IN_SUCCEED_TIME",loadInterstitialTime);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Analytics.setThinkTrackEvent(AdsState.INTERSTITIAL_LOADED,jsonObject);
                    AdsCallbackCenter.sendMessageToEngine(AdsState.INTERSTITIAL_LOADED);
                }else{
                    log("另外一个插屏广告加载成功");
                    AdsCallbackCenter.sendMessageToEngine("STATIC_INTERSTITIAL_LOADED");
                }
            }
        });
    }
    protected void showInterstitial(int unitIdIndex) {
        if(unitIdIndex==0){
            if(hasIntertitial(0)){
                Analytics.setThinkTrackEvent(AdsState.INTERSTITIAL_SHOW);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fullScreenVideoAd.showFullScreenVideoAd(activity);
                    }
                });
            }

        }else if(unitIdIndex==1){
            if(hasIntertitial(1)){
                Analytics.setThinkTrackEvent(AdsState.OTHER_INTERSTITIAL_SHOW);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        otherFullScreenVideoAd.showFullScreenVideoAd(activity);
                    }
                });
            }
        }else{
            log("广告位错误");
        }
    }
    public boolean hasIntertitial(int unitIDIndex) {
        if(unitIDIndex==0){
            if(null!=fullScreenVideoAd){
                if(canPlayAds()){
                    return  true;
                }else{
                    return  false;
                }
            }else{
                requestInterstitial(0);
                return  false;
            }

        }else if(unitIDIndex==1){
            if(null!= otherFullScreenVideoAd){
                if(canPlayAds()){
                    return  true;
                }else{
                    return  false;
                }
            }else{
                requestInterstitial(1);
                return  false;
            }
        }else{
            log("广告位错误");
            return  false;
        }
    }
    public void setAdsIntervalTime(int cdTime) {
        log("设置插屏广告间隔时间为"+cdTime);
        this.adsIntervalTime = cdTime;
    }

    private TTRewardVideoAd rewardVideoAd;
    private  boolean isCompleteRewardVideo;
    private  boolean isLoadingRewardVideo;
    protected boolean isCallbackToAndroid;
    protected    void setRewardVideoAutoPlayWhenLoadedStatus(boolean autoPlay){
        log(autoPlay?"打开激励视频加载成功自动播放":"关闭激励视频自动播放");
        this.autoPlay = autoPlay;
        isCallbackToAndroid=false;
    }
    public   void setRewardVideoAutoPlayWhenLoadedStatusInAndroid(boolean autoPlay){
        log(autoPlay?"打开激励视频加载成功自动播放":"关闭激励视频自动播放");
        this.autoPlay = autoPlay;
        isCallbackToAndroid=true;
    }
    public boolean hasReawrdVideo() {
        if(null!=rewardVideoAd){
            return true;
        }else{
            requestRewardVideo();
            return  false;
        }
    }
    protected void showRewardVideo() {
        isCallbackToAndroid = false;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(hasReawrdVideo()){
                    rewardVideoAd.showRewardVideoAd(activity);
                }
            }
        });
    }
    protected  void getGoogleID(){
        AdsCallbackCenter.sendMessageToEngine(Utils.getDeviceIdString(activity));
    }
    private  long lastRequestRewardVideoTime;

    public void requestRewardVideo() {
        if(isLoadingRewardVideo)
            return;
        if(TextUtils.isEmpty(rewardVideoID)){
            Log.e(TAG, "激励视频请求失败，请在清单文件中配置激励视频id");
            return;
        }

        isLoadingRewardVideo=true;
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(rewardVideoID)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                .setRewardName("金币") //奖励的名称
                .setRewardAmount(3)  //奖励的数量
                .setUserID("user123")//用户id,必传参数
                .setOrientation(TTAdConstant.VERTICAL) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .build();
        log("请求激励视频");
        Analytics.setThinkTrackEvent(AdsState.REWARD_REQUEST);
        ttAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int i, String s) {
                isLoadingRewardVideo=false;
                double loadReardVideoTime=(System.currentTimeMillis()-lastRequestRewardVideoTime)/1000.0;
                log("激励加载失败"+s+"耗时"+loadReardVideoTime);
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("errorCode",s);
                    jsonObject.put("LOAD_RV__FAILED_TIME",loadReardVideoTime);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Analytics.setThinkTrackEvent(AdsState.REWARD_FAILED,jsonObject);
                AdsCallbackCenter.sendMessageToEngine(AdsState.REWARD_FAILED);
            }
            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ttRewardVideoAd) {
                rewardVideoAd=ttRewardVideoAd;
                isLoadingRewardVideo=false;
                rewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {
                    @Override
                    public void onAdShow() {
                        autoPlay=false;
                        Analytics.setThinkTrackEvent(AdsState.REWARD_OPEN);
                        isCompleteRewardVideo=false;
                    }
                    @Override
                    public void onAdVideoBarClick() {

                    }
                    @Override
                    public void onAdClose() {
                        autoPlay=false;
                        resetAdIntervalTime();
                        AdsCallbackCenter.sendMessageToEngine(AdsState.REWARD_COMPLETE);
                        rewardVideoAd=null;
                        requestRewardVideo();
                        Analytics.logAdjustEvent(Utils.readValueFromManifest(activity,"reward_impression"));
                        Analytics.setThinkTrackEvent(AdsState.REWARD_IMPRESSION);
                    }
                    @Override
                    public void onVideoComplete() {
                        isCompleteRewardVideo=true;
                    }
                    @Override
                    public void onVideoError() {

                    }
                    @Override
                    public void onRewardVerify(boolean b, int i, String s) {
                        isCompleteRewardVideo=true;
                    }
                });
            }
            @Override
            public void onRewardVideoCached() {
                isLoadingRewardVideo=false;
                double loadReardVideoTime=(System.currentTimeMillis()-lastRequestRewardVideoTime)/1000.0;
                log("激励加载成功,加载所需时长"+loadReardVideoTime);
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("LOAD_RV__SUCCEED_TIME",loadReardVideoTime);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Analytics.setThinkTrackEvent(AdsState.REWARD_LOADED,jsonObject);
                AdsCallbackCenter.sendMessageToEngine(AdsState.REWARD_LOADED);

                if(autoPlay){
                    log("自动播放激励视频");
                    rewardVideoAd.showRewardVideoAd(activity);
                }
            }
        });
    }
    boolean isPad=false;
    FrameLayout mBannerContainer;
    protected void showBanner(boolean isTop) {
        if(TextUtils.isEmpty(bannerID)){
            Log.e(TAG, "banner请求失败:请在清单文件中配置bannerid ");
            return;
        }
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(bannerID) //广告位id
                .setSupportDeepLink(true)
                .setImageAcceptedSize(640, 100)
                .build();

        ttAdNative.loadBannerAd(adSlot, new TTAdNative.BannerAdListener() {
            @Override
            public void onError(int code, String message) {
                mBannerContainer.removeAllViews();
            }
            @Override
            public void onBannerAdLoad(final TTBannerAd ad) {
                if (ad == null) {
                    return;
                }
                View bannerView = ad.getBannerView();
                if (bannerView == null) {
                    return;
                }
                ad.setSlideIntervalTime(30 * 1000);
                mBannerContainer.removeAllViews();
                mBannerContainer.addView(bannerView);
                //设置广告互动监听回调
                ad.setBannerInteractionListener(new TTBannerAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int type) {
                    }
                    @Override
                    public void onAdShow(View view, int type) {
                        AdsCallbackCenter.sendMessageToEngine("BANNER_SHOW");
                    }
                });
                //在banner中显示网盟提供的dislike icon，有助于广告投放精准度提升
                ad.setShowDislikeIcon(new TTAdDislike.DislikeInteractionCallback() {
                    @Override
                    public void onSelected(int position, String value) {
                        //用户选择不喜欢原因后，移除广告展示
                        mBannerContainer.removeAllViews();
                    }
                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
    }
    protected void hideBanner() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mBannerContainer!=null)
                    mBannerContainer.removeAllViews();
            }
        });
    }
}
