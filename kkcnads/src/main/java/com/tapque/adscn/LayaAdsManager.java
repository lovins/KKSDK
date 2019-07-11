package com.tapque.adscn;

import android.app.Activity;
import android.util.Log;

public class LayaAdsManager {
    static  String TAG="Laya";
    public  static  void setLayaActivity(Activity activity){
        AdsManager.instance().setLayaActivity(activity);
    }
    public  static  void initLayaAdsManager(boolean debug){
        AdsManager.instance().initAdsFromLaya(debug);
    }
    public  static  void setAdsInterval(int seconds){

        AdsManager.instance().setAdsIntervalTime(seconds);
    }
    public  static  void showBanner(boolean isTop){
        AdsManager.instance().showBanner(isTop);
    }
    public  static  void hideBanner(){
        AdsManager.instance().hideBanner();
    }
    public  static  void showInterstitial(int unitIDIndex){
        AdsManager.instance().showInterstitial(unitIDIndex);
    }
    public  static  void requestInterstitial(int unitIDIndex){
        AdsManager.instance().requestInterstitial(unitIDIndex);
    }
    public  static  boolean hasInterstitial(int unitIDIndex){
        return  AdsManager.instance().hasIntertitial(unitIDIndex);
    }
    public static  void requestRewardVideo(){

        AdsManager.instance().requestRewardVideo();
    }
    public  static  void setRewardVideoAutoPlay(boolean enable){

        AdsManager.instance().setRewardVideoAutoPlayWhenLoadedStatus(enable);
    }
    public  static  void showRewardVideo(){

        AdsManager.instance().showRewardVideo();
    }
    public static  boolean hasRewardVideo(){
        return  AdsManager.instance().hasReawrdVideo();
    }

    public  static void getIDFA(){
        AdsManager.instance().getGoogleID();
    }
}
