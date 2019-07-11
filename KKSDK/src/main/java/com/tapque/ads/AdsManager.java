package com.tapque.ads;
import android.app.Activity;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.adjust.sdk.Adjust;
import com.adjust.sdk.OnDeviceIdsRead;
import com.facebook.ads.AudienceNetworkAds;
import com.mopub.common.MoPub;
import com.mopub.common.MoPubReward;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubRewardedVideoListener;
import com.mopub.mobileads.MoPubRewardedVideos;
import com.mopub.mobileads.MoPubView;
import com.tapque.analytics.Analytics;
import com.tapque.mopubads.R;
import com.tapque.tools.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

public class AdsManager {
    private static AdsManager adsManagerInstance;
    public interface AdsStateChangeListenner {
        void onAdsStateChange(String adsState);
    }
    private AdsStateChangeListenner androidAdsListener;

    public  interface  OnGetGoogleIdSucceed{
        void onGetSucceed(String googleID);
    }
    private OnGetGoogleIdSucceed onGetGoogleIdSucceed;
    private Activity activity;
    public String bannerID;
    public String padBannerID;
    private String interstitialID;
    private String otherInterstitialID;
    private String rewardVideoID;
    private boolean showAdsLog;
    private int adsIntervalTime = 0;
    private long lastTime = 0;

    public static AdsManager instance() {
        if (null == adsManagerInstance) {
            adsManagerInstance = new AdsManager();
        }
        return adsManagerInstance;
    }
    /**
     * for unity init
     *
     * @param activity 上下文
     * @param debug    是否为测试模式
     */
    private void init(Activity activity, boolean debug) {
        this.activity = activity;
        Analytics.setContext(activity,debug);
        this.showAdsLog = debug;
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initMopub();
            }
        });
    }
    /**
     * laya 引擎初始化使用
     * @param activity
     */
    protected   void setLayaActivity(Activity activity){
        this.activity=activity;
    }

    /**
     *
     * laya引擎初始化广告使用
     * @param debug
     */
    protected   void initAdsFromLaya(boolean debug){
        init(activity,debug);
    }

    boolean isForceClose=false;

    /**
     *
     *强制关闭log
     * @param close
     */
    public  void forceCloseDebug(boolean close){
        isForceClose=close;
    }
    private   void log(String message){
        if(showAdsLog&&!isForceClose){
            Log.e("Ads", message );
        }
    }
    protected  void getGoogleID(){
        Adjust.getGoogleAdId(activity, new OnDeviceIdsRead() {
            @Override
            public void onGoogleAdIdRead(String googleAdId) {
                if(!TextUtils.isEmpty(googleAdId)){
                    log("获得广告id:"+googleAdId);
                    AdsCallbackCenter.sendGoogleId(googleAdId);
                }else{
                    log("获得设备id:"+Adjust.getAdid());
                    if(!TextUtils.isEmpty(Adjust.getAdid())){
                        AdsCallbackCenter.sendGoogleId(Adjust.getAdid());
                    }
                }
            }
        });
    }
    /***
     *
     * @param onGetGoogleIdSucceed
     */
    public  void getGoogleID(final OnGetGoogleIdSucceed onGetGoogleIdSucceed){
        this.onGetGoogleIdSucceed=onGetGoogleIdSucceed;
        Adjust.getGoogleAdId(activity, new OnDeviceIdsRead() {
            @Override
            public void onGoogleAdIdRead(String googleAdId) {
                onGetGoogleIdSucceed.onGetSucceed(googleAdId);
            }
        });
    }
    /**
     * 初始化mopub平台
     */
    private void initMopub() {
        AudienceNetworkAds.isInAdsProcess(activity);
        boolean isInitApplovin=Utils.initApplovin(activity,"com.applovin.sdk.AppLovinSdk");
        if(isInitApplovin)
            log("初始化Applovin");
        isPad = (activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
        bannerID = Utils.readValueFromManifestToString(activity, "bannerID");
        padBannerID = Utils.readValueFromManifestToString(activity, "padBannerID");
        interstitialID = Utils.readValueFromManifestToString(activity, "interstitialID");
        otherInterstitialID = Utils.readValueFromManifestToString(activity, "otherInterstitialID");
        rewardVideoID = Utils.readValueFromManifestToString(activity, "rewardVideoID");
        SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(rewardVideoID)
                .withLogLevel(showAdsLog ? MoPubLog.LogLevel.DEBUG : MoPubLog.LogLevel.NONE)
                .build();
        MoPub.initializeSdk(activity, sdkConfiguration, initSdkListener());
        MoPub.onCreate(activity);
    }
    /***
     *初始化广告平台完成的回调
     * @return
     */
    private SdkInitializationListener initSdkListener() {
        return new SdkInitializationListener() {
            @Override
            public void onInitializationFinished() {
                log("广告初始化成功");
                if(null!=androidAdsListener){
                    androidAdsListener.onAdsStateChange(AdsState.INIT_SUCCEED);
                }
                AdsCallbackCenter.sendMessageToEngine(AdsState.INIT_SUCCEED);
            }
        };
    }
    /**
     * for android method
     *
     * @param act            context 上下文
     * @param
     * @param listener ads listenner 广告初始化成功回调事件
     */
    public void init(Activity act, boolean debugLog, AdsStateChangeListenner listener) {
        this.activity = act;
        this.showAdsLog = debugLog;
        this.androidAdsListener=listener;
        initMopub();
    }
    /**
     * 通用函数
     * @param cdTime 广告间隔时间
     */
    public void setAdsIntervalTime(int cdTime) {
        this.adsIntervalTime = cdTime;
    }
    private boolean canPlayAds() {
        long currentlyTime = System.currentTimeMillis();
        long d = currentlyTime - lastTime;
        if (d > adsIntervalTime * 1000) {
            lastTime = currentlyTime;
            return true;
        }
        log("CD时间: 还剩" +(adsIntervalTime-(d/1000f))+"秒可播放插屏广告");
        return false;
    }

    private  void resetAdIntervalTime(){
        if(showAdsLog){
            log("重置插屏广告间隔时间");
        }
        lastTime = System.currentTimeMillis();
    }
    //-------------------------------------------------------------------插屏广告-------------------------------------------------------
    private boolean isRequestingInterstitial;
    private boolean isRequestingOtherInterstitial;
    private MoPubInterstitial interstitial;
    private MoPubInterstitial otherInterstitial;
    long lastRequestInterstitialTime;

    /**
     * 请求当前插屏广告
     *
     * @param unitIDIndex 0：默认
     *                    1：另外一个
     */
    public void requestInterstitial(int unitIDIndex) {
        if (unitIDIndex == 0) {
            if (isRequestingInterstitial)
                return;
            isRequestingInterstitial = true;
            log("正在请求插屏广告");
            lastRequestInterstitialTime=System.currentTimeMillis();
            if (TextUtils.isEmpty(AdsManager.instance().interstitialID)) {
                log("当前广告id为空");
                return;
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    interstitial = new MoPubInterstitial(activity, AdsManager.instance().interstitialID);
                    if (null != androidAdsListener)
                        androidAdsListener.onAdsStateChange(AdsState.INTERSTITIAL_REQUEST);
                    AdsCallbackCenter.sendMessageToEngine(AdsState.INTERSTITIAL_REQUEST);
                    interstitial.setInterstitialAdListener(interstitialAdListener);
                    interstitial.load();
                }
            });
            Analytics.setThinkTrackEvent(AdsState.INTERSTITIAL_REQUEST);

        } else if (unitIDIndex == 1) {
            if (isRequestingOtherInterstitial)
                return;
            isRequestingOtherInterstitial = true;
            log("正在请求另外一个插屏广告");
            if (TextUtils.isEmpty(AdsManager.instance().otherInterstitialID)) {
                log("当前广告id为空");
                return;
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    otherInterstitial = new MoPubInterstitial(activity, AdsManager.instance().otherInterstitialID);
                    if (null != androidAdsListener)
                        androidAdsListener.onAdsStateChange(AdsState.OTHER_INTERSTITIAL_REQUEST);
                    AdsCallbackCenter.sendMessageToEngine(AdsState.OTHER_INTERSTITIAL_REQUEST);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            otherInterstitial.setInterstitialAdListener(otherInterstitialAdListener);
                        }
                    });
                    otherInterstitial.load();
                }
            });
            Analytics.setThinkTrackEvent(AdsState.OTHER_INTERSTITIAL_REQUEST);
        }
    }
    /**
     * 判断当前插屏广告是否加载成功
     *
     * @param unitIDIndex 0：默认
     *                    1：另外一个
     * @return
     */
    public boolean hasIntertitial(int unitIDIndex) {
        if (unitIDIndex == 0) {
            if (null!=interstitial&&interstitial.isReady()) {
                if(canPlayAds()){
                    return true;
                }else{
                    return false;
                }
            } else {
                requestInterstitial(unitIDIndex);
                return false;
            }
        }else  if(unitIDIndex==1){
            if(null!=otherInterstitial&&otherInterstitial.isReady()){
                if(canPlayAds()){
                    return true;
                }else{
                    return false;
                }
            }else{
                requestInterstitial(1);
                return false;
            }
        }else{
            return  false;
        }
    }

    private MoPubInterstitial.InterstitialAdListener interstitialAdListener = new MoPubInterstitial.InterstitialAdListener() {
        @Override
        public void onInterstitialLoaded(MoPubInterstitial interstitial) {
            double loadInterstitialTime=(System.currentTimeMillis()-lastRequestInterstitialTime)/1000.0;
            log("插屏加载成功,所需要的时长"+loadInterstitialTime);
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("LOAD_IN_SUCCEED_TIME",loadInterstitialTime);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Analytics.setThinkTrackEvent(AdsState.INTERSTITIAL_LOADED,jsonObject);
            isRequestingInterstitial = false;
            if (null != androidAdsListener)
                androidAdsListener.onAdsStateChange(AdsState.INTERSTITIAL_LOADED);
            AdsCallbackCenter.sendMessageToEngine(AdsState.INTERSTITIAL_LOADED);
        }
        @Override
        public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
            double loadInterstitialTime=(System.currentTimeMillis()-lastRequestInterstitialTime)/1000.0;
            log("插屏加载失败:"+errorCode+"耗时:"+loadInterstitialTime);
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("errorCode",errorCode.toString());
                jsonObject.put("LOAD_IN_FAILED_TIME",loadInterstitialTime);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Analytics.setThinkTrackEvent(AdsState.INTERSTITIAL_FAILED,jsonObject);
            isRequestingInterstitial = false;
            if (null != androidAdsListener)
                androidAdsListener.onAdsStateChange(AdsState.INTERSTITIAL_FAILED);
            AdsCallbackCenter.sendMessageToEngine(AdsState.INTERSTITIAL_FAILED);
        }

        @Override
        public void onInterstitialShown(MoPubInterstitial interstitial) {
            log("打开插屏");
            if (null != androidAdsListener)
                androidAdsListener.onAdsStateChange(AdsState.INTERSTITIAL_OPEN);
            AdsCallbackCenter.sendMessageToEngine(AdsState.INTERSTITIAL_OPEN);
            Analytics.setThinkTrackEvent(AdsState.INTERSTITIAL_OPEN);
        }
        @Override
        public void onInterstitialClicked(MoPubInterstitial interstitial) {
            log("点击插屏");
            if (null != androidAdsListener)
                androidAdsListener.onAdsStateChange(AdsState.INTERSTITIAL_CLICK);
            AdsCallbackCenter.sendMessageToEngine(AdsState.INTERSTITIAL_CLICK);
        }

        @Override
        public void onInterstitialDismissed(MoPubInterstitial interstitial) {
            log("关闭插屏");
            resetAdIntervalTime();
            Analytics.logAdjustEvent(Utils.readValueFromManifestToString(activity,"interstitial_impression"));
            Analytics.setThinkTrackEvent(AdsState.INTERSTITIAL_IMPRESSION);
                AdsCallbackCenter.sendMessageToEngine(AdsState.INTERSTITIAL_CLOSE);
                if (null != androidAdsListener)
                    androidAdsListener.onAdsStateChange(AdsState.INTERSTITIAL_CLOSE);
            requestInterstitial(0);
        }
    };
    private MoPubInterstitial.InterstitialAdListener otherInterstitialAdListener = new MoPubInterstitial.InterstitialAdListener() {
        @Override
        public void onInterstitialLoaded(MoPubInterstitial interstitial) {
            log("另外一个插屏加载成功");
            Analytics.setThinkTrackEvent(AdsState.OTHER_INTERSTITIAL_LOADED);
            isRequestingOtherInterstitial = false;
            if (null != androidAdsListener)
                androidAdsListener.onAdsStateChange(AdsState.OTHER_INTERSTITIAL_LOADED);
            AdsCallbackCenter.sendMessageToEngine(AdsState.OTHER_INTERSTITIAL_LOADED);
        }

        @Override
        public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
            log("另外一个插屏加载失败"+errorCode);
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("errorCode",errorCode.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Analytics.setThinkTrackEvent(AdsState.OTHER_INTERSTITIAL_FAILED,jsonObject);
            isRequestingOtherInterstitial = false;
            if (null != androidAdsListener)
                androidAdsListener.onAdsStateChange(AdsState.OTHER_INTERSTITIAL_FAILED);
            AdsCallbackCenter.sendMessageToEngine(AdsState.OTHER_INTERSTITIAL_FAILED);
        }

        @Override
        public void onInterstitialShown(MoPubInterstitial interstitial) {
            log("另外一个插屏播放");
            Analytics.setThinkTrackEvent(AdsState.INTERSTITIAL_OPEN);
            if (null != androidAdsListener)
                androidAdsListener.onAdsStateChange(AdsState.OTHER_INTERSTITIAL_OPEN);
            AdsCallbackCenter.sendMessageToEngine(AdsState.OTHER_INTERSTITIAL_OPEN);
        }

        @Override
        public void onInterstitialClicked(MoPubInterstitial interstitial) {
            log("另外一个插屏点击");
            if (null != androidAdsListener)
                androidAdsListener.onAdsStateChange(AdsState.OTHER_INTERSTITIAL_CLICK);
            AdsCallbackCenter.sendMessageToEngine(AdsState.OTHER_INTERSTITIAL_CLICK);
        }

        @Override
        public void onInterstitialDismissed(MoPubInterstitial interstitial) {
            log("另外一个插屏关闭");
            resetAdIntervalTime();
            Analytics.logAdjustEvent(Utils.readValueFromManifestToString(activity,"other_interstitial_impression"));
            Analytics.setThinkTrackEvent(AdsState.OTHER_INTERSTITIAL_IMPRESSION);
                AdsCallbackCenter.sendMessageToEngine(AdsState.OTHER_INTERSTITIAL_CLOSE);
                if (null != androidAdsListener)
                    androidAdsListener.onAdsStateChange(AdsState.OTHER_INTERSTITIAL_CLOSE);
            requestInterstitial(1);
        }
    };

    /**
     * for untiy and laya播放激励广告
     *
     * @param unitIDIndex 0：默认
     *                    1：另外一个
     */
    protected void showInterstitial(int unitIDIndex) {
        if (unitIDIndex == 0) {
            if (null != interstitial)
                Analytics.setThinkTrackEvent(AdsState.INTERSTITIAL_SHOW);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        interstitial.show();
                    }
                });
        } else if (unitIDIndex == 1) {
            if (null != otherInterstitial)
                Analytics.setThinkTrackEvent(AdsState.OTHER_INTERSTITIAL_SHOW);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        otherInterstitial.show();
                    }
                });
        }
    }
    /**
     * 播放当前插屏广告带回调事件
     *
     * @param unitID
     *
     */
    public void showInterstitialInAndroid(int unitID) {
        if (unitID == 0&&null!=interstitial) {
            Analytics.setThinkTrackEvent(AdsState.INTERSTITIAL_SHOW);
            interstitial.show();
        } else if (unitID == 1&&null!=otherInterstitial) {
            Analytics.setThinkTrackEvent(AdsState.OTHER_INTERSTITIAL_SHOW);
            otherInterstitial.show();
        }
    }
    //-----------------------------------------------------------------------------

    //-------------------------------------------------------------------激励视频----------
    private boolean isRequestingRewardVideo;
    private boolean isCompleteWatchVideo;
    private boolean autoPlay;
    private  long lastRequestRewardVideoTime;
    /**
     * 请求激励视频
     */
    public void requestRewardVideo() {
        if (isRequestingRewardVideo)
            return;
        isRequestingRewardVideo = true;
        log("正在请求激励视频");
        lastRequestRewardVideoTime=System.currentTimeMillis();
        Analytics.setThinkTrackEvent(AdsState.REWARD_REQUEST);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MoPubRewardedVideos.setRewardedVideoListener(moPubRewardedVideoListener);
                MoPubRewardedVideos.loadRewardedVideo(AdsManager.instance().rewardVideoID);
            }
        });
        if (null != androidAdsListener)
            androidAdsListener.onAdsStateChange(AdsState.REWARD_REQUEST);
        AdsCallbackCenter.sendMessageToEngine(AdsState.REWARD_REQUEST);
    }

    MoPubRewardedVideoListener moPubRewardedVideoListener = new MoPubRewardedVideoListener() {
        @Override
        public void onRewardedVideoLoadSuccess(@NonNull String adUnitId) {
            double loadReardVideoTime=(System.currentTimeMillis()-lastRequestRewardVideoTime)/1000.0;
            log("激励加载成功,加载所需时长"+loadReardVideoTime);
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("LOAD_RV__SUCCEED_TIME",loadReardVideoTime);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Analytics.setThinkTrackEvent(AdsState.REWARD_LOADED,jsonObject);
            isRequestingRewardVideo = false;
            if(null!=androidAdsListener)
                androidAdsListener.onAdsStateChange(AdsState.REWARD_LOADED);

            AdsCallbackCenter.sendMessageToEngine(AdsState.REWARD_LOADED);

            if (autoPlay) {
                showRewardVideo();
            }
        }
        @Override
        public void onRewardedVideoLoadFailure(@NonNull String adUnitId, @NonNull MoPubErrorCode errorCode) {
            double loadReardVideoTime=(System.currentTimeMillis()-lastRequestRewardVideoTime)/1000.0;
            log("激励加载失败"+errorCode+"耗时"+loadReardVideoTime);
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("errorCode",errorCode.toString());
                jsonObject.put("LOAD_RV__FAILED_TIME",loadReardVideoTime);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Analytics.setThinkTrackEvent(AdsState.REWARD_FAILED,jsonObject);
            isRequestingRewardVideo = false;
            if(null!=androidAdsListener)
                androidAdsListener.onAdsStateChange(AdsState.REWARD_FAILED);
            AdsCallbackCenter.sendMessageToEngine(AdsState.REWARD_FAILED);
        }

        @Override
        public void onRewardedVideoStarted(@NonNull String adUnitId) {
            isCompleteWatchVideo = false;
            autoPlay = false;
            log("激励开始播放");
            Analytics.setThinkTrackEvent(AdsState.REWARD_OPEN);
            if(null!=androidAdsListener)
                androidAdsListener.onAdsStateChange(AdsState.REWARD_OPEN);
            AdsCallbackCenter.sendMessageToEngine(AdsState.REWARD_OPEN);
        }

        @Override
        public void onRewardedVideoPlaybackError(@NonNull String adUnitId, @NonNull MoPubErrorCode errorCode) {
            isRequestingRewardVideo = false;
            log("激励播放失败:"+errorCode);
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("ERROR_CODE",errorCode.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Analytics.setThinkTrackEvent(AdsState.REWARD_PLAY_ERROR,jsonObject);
            if(null!=androidAdsListener)
                androidAdsListener.onAdsStateChange(AdsState.REWARD_PLAY_ERROR);
            AdsCallbackCenter.sendMessageToEngine(AdsState.REWARD_PLAY_ERROR);
        }

        @Override
        public void onRewardedVideoClicked(@NonNull String adUnitId) {
            log("激励点击");
            if(null!=androidAdsListener)
                androidAdsListener.onAdsStateChange(AdsState.REWARD_CLICK);
            AdsCallbackCenter.sendMessageToEngine(AdsState.REWARD_CLICK);
        }

        @Override
        public void onRewardedVideoClosed(@NonNull String adUnitId) {
            resetAdIntervalTime();
            autoPlay = false;
            if (isCompleteWatchVideo) {
                log("激励用户");
                if(null!=androidAdsListener)
                    androidAdsListener.onAdsStateChange(AdsState.REWARD_COMPLETE);
                AdsCallbackCenter.sendMessageToEngine(AdsState.REWARD_COMPLETE);
            } else {
                log("激励关闭");
                if(null!=androidAdsListener)
                    androidAdsListener.onAdsStateChange(AdsState.REWARD_CLOSE);
                AdsCallbackCenter.sendMessageToEngine(AdsState.REWARD_CLOSE);
            }
            requestRewardVideo();
        }
        @Override
        public void onRewardedVideoCompleted(@NonNull Set<String> adUnitIds, @NonNull MoPubReward reward) {
            isCompleteWatchVideo = true;
            Analytics.logAdjustEvent(Utils.readValueFromManifestToString(activity,"reward_impression"));
            Analytics.setThinkTrackEvent(AdsState.REWARD_IMPRESSION);
        }
    };
    /*通用
     */
    public boolean hasReawrdVideo() {
        if (MoPubRewardedVideos.hasRewardedVideo(AdsManager.instance().rewardVideoID)) {
            return true;
        } else {
            requestRewardVideo();
            return false;
        }
    }


    protected    void setRewardVideoAutoPlayWhenLoadedStatus(boolean autoPlay){
        log(autoPlay?"打开激励视频加载成功自动播放":"关闭激励视频自动播放");
        this.autoPlay = autoPlay;
    }
    public   void setRewardVideoAutoPlayWhenLoadedStatusInAndroid(boolean autoPlay){
        log(autoPlay?"打开激励视频加载成功自动播放":"关闭激励视频自动播放");
        this.autoPlay = autoPlay;
    }
    /**
     * for unity laya
     */
    protected void showRewardVideo() {
        Analytics.setThinkTrackEvent(AdsState.REWARD_SHOW);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MoPubRewardedVideos.showRewardedVideo(rewardVideoID);
            }
        });
    }
    /**
     * for android
     */
    public void showRewardVideos() {
        Analytics.setThinkTrackEvent(AdsState.REWARD_SHOW);
        MoPubRewardedVideos.showRewardedVideo(rewardVideoID);
    }
    //-----------------------------------------------------------------------------

    //------------------------------------------banner-----------------------------------
    private View bannerContainner;
    private MoPubView bannerView;
    private  boolean autoShowBannerWhenLoaded;
    private boolean isPad;
    private  boolean isShowBannerInAndroid;
    /**
     * 请求banner广告
     *
     * @param MopubBannerViewID mopubBannerView的id
     *
     */
    public void requestBanner(int MopubBannerViewID) {
        isShowBannerInAndroid=true;
        Analytics.setThinkTrackEvent(AdsState.BANNER_REQUEST);
        if (TextUtils.isEmpty(bannerID)) {
            log("请求的banner广告id不能为空");
            return;
        }
        bannerView = activity.findViewById(MopubBannerViewID);
        bannerView.setAdUnitId(bannerID);
        bannerView.setBannerAdListener(bannerAdListener);
        bannerView.loadAd();

    }
    /*
     * 销毁banner
     */
    public void destoryBanner() {
        bannerView.setVisibility(View.INVISIBLE);
        androidAdsListener.onAdsStateChange(AdsState.BANNER_HIDE);
    }
    /**
     * for unity and laya
     * 展示banner广告
     * @param isTop
     */
    boolean isBannerInTop=false;
    protected void showBanner(boolean isTop) {
        isBannerInTop=isTop;
        log("正在请求banner广告");
        Analytics.setThinkTrackEvent(AdsState.BANNER_REQUEST);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                autoShowBannerWhenLoaded=true;
                isShowBannerInAndroid=false;
                if (null == bannerContainner){//首次请求需要初始化layout和add view
                    log("初始化banner容器");
                    bannerContainner = isBannerInTop ? LayoutInflater.from(activity).inflate(R.layout.top_banner, null) : LayoutInflater.from(activity).inflate(R.layout.bottom_banner, null);
                    activity.addContentView(bannerContainner,
                            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT));
                }else{
                    log("显示baner容器");
                    bannerContainner.setVisibility(View.VISIBLE);
                }
                bannerView = bannerContainner.findViewById(R.id.adview);
                if (isPad) {
                    if(TextUtils.isEmpty(padBannerID)){
                        log("Pad的banner广告不能为空");
                        return;
                    }
                    bannerView.setAdUnitId(padBannerID);
                } else {
                    if(TextUtils.isEmpty(bannerID)){
                        log("banner广告不能为空");
                        return;
                    }
                    bannerView.setAdUnitId(bannerID);
                }
                bannerView.setBannerAdListener(bannerAdListener);
                bannerView.loadAd();
            }
        });
    }
    /**
     *
     * for 隐藏banner
     */
    protected void hideBanner() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                log("关闭banner");
                autoShowBannerWhenLoaded=false;
                if(null!=bannerContainner)
                bannerContainner.setVisibility(View.INVISIBLE);
            }
        });
    }
    MoPubView.BannerAdListener bannerAdListener = new MoPubView.BannerAdListener() {
        @Override
        public void onBannerLoaded(MoPubView banner) {
            log("banner加载成功");
            if(null!=androidAdsListener)
                androidAdsListener.onAdsStateChange(AdsState.BANNER_SHOW);
            AdsCallbackCenter.sendMessageToEngine(AdsState.BANNER_SHOW);
            if (isShowBannerInAndroid) {
                bannerView.setVisibility(View.VISIBLE);
                Analytics.logAdjustEvent(Utils.readValueFromManifestToString(activity,"banner_impression"));
                Analytics.setThinkTrackEvent(AdsState.BANNER_IMPRESSION);
            } else {
                if(autoShowBannerWhenLoaded){
                    Analytics.logAdjustEvent(Utils.readValueFromManifestToString(activity,"banner_impression"));
                    Analytics.setThinkTrackEvent(AdsState.BANNER_IMPRESSION);
                }
                bannerContainner.setVisibility(autoShowBannerWhenLoaded?View.VISIBLE:View.INVISIBLE);
            }
        }
        @Override
        public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
            log("banner加载失败"+errorCode);
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("errorCode",errorCode.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Analytics.setThinkTrackEvent(AdsState.BANNER_FAILED,jsonObject);
            if(null!=androidAdsListener)
                androidAdsListener.onAdsStateChange(AdsState.BANNER_FAILED);
            AdsCallbackCenter.sendMessageToEngine(AdsState.BANNER_FAILED);
        }
        @Override
        public void onBannerClicked(MoPubView banner) {
            if(null!=androidAdsListener)
                androidAdsListener.onAdsStateChange(AdsState.BANNER_CLICK);
            AdsCallbackCenter.sendMessageToEngine(AdsState.BANNER_CLICK);
        }
        @Override
        public void onBannerExpanded(MoPubView banner) {

        }
        @Override
        public void onBannerCollapsed(MoPubView banner) {

        }
    };
}