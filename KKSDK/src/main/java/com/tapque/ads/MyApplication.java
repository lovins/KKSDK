package com.tapque.ads;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.multidex.MultiDex;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustAttribution;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.LogLevel;
import com.adjust.sdk.OnAttributionChangedListener;
import com.facebook.FacebookSdk;
import com.mopub.mobileads.RewardedMraidActivity;
import com.tapque.analytics.Analytics;
import com.tapque.tools.Utils;
import com.thinking.analyselibrary.ThinkingAnalyticsSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("广告应用层初始化", "");
        MultiDex.install(this);
        Analytics.initAdjust(this);
        Analytics.initThinkingData(this);
        Analytics.initFacebook(this);
    }
//    void initFacebook(){
//        String fbid=Utils.readValueFromManifestToString(this,"com.facebook.sdk.ApplicationId");
//        if(!TextUtils.isEmpty(fbid)){
//            FacebookSdk.setApplicationId(fbid.substring(2));
//            FacebookSdk.sdkInitialize(this);
//        }else{
//            Log.e("启动项", "initFacebook: 请在清单文件中配置facebook id" );
//        }
//    }
//    void initThink(){
//        String thinkApp=Utils.readValueFromManifestToString(this,"thinkingData_app_id")+"";
//        String thinkUrl=Utils.readValueFromManifestToString(this,"thinkingData_app_url")+"";
//        if(!TextUtils.isEmpty(thinkApp)&&!TextUtils.isEmpty(thinkUrl)){
//            Analytics.isInitThinkData=true;
//            ThinkingAnalyticsSDK.sharedInstance(this,thinkApp,thinkUrl);
//            ThinkingAnalyticsSDK.sharedInstance(this).enableAutoTrack(new ArrayList<ThinkingAnalyticsSDK.AutoTrackEventType>(Arrays.asList(ThinkingAnalyticsSDK.AutoTrackEventType.APP_START,ThinkingAnalyticsSDK.AutoTrackEventType.APP_END)){});
//        }else {
//            Analytics.isInitThinkData=false;
//            Log.e("启动项", "initThink: 请在清单文件中配置thinkingData_app_id和thinkingData_app_url" );
//        }
//    }
//    void initAdjust() {
//        String ajustAppToken = Utils.readValueFromManifestToString(this, "adjust_app_token");
//        if (TextUtils.isEmpty(ajustAppToken)) {
//            Log.e("启动项", "initAdjust：请在清单文件中配置adjust id");
//        } else {
//            boolean adjust_production_enivroment = Utils.readValueFromManifestForBool(this, "adjust_production_enivroment");
//            AdjustConfig config = new AdjustConfig(this, ajustAppToken, adjust_production_enivroment ? "production" : "sandbox");
//            config.setOnAttributionChangedListener(new OnAttributionChangedListener() {
//                public void onAttributionChanged(AdjustAttribution attribution) {
//                    if(!adjust_production_enivroment)
//                        Log.e("Adust", "获得的所有adjust归因信息:" + attribution.toString() );
//                    JSONObject jsonObject=new JSONObject();
//                    try {
//                        jsonObject.put("TRACK_NETWORK",attribution.network);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    ThinkingAnalyticsSDK.sharedInstance().user_set(jsonObject);
//                    Analytics.trackInfo = attribution.network;
//                }
//            });
//            if (!adjust_production_enivroment) {
//                config.setLogLevel(LogLevel.VERBOSE);
//            }
//            Adjust.onCreate(config);
//            this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
//                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
//                }
//
//                public void onActivityStarted(Activity activity) {
//                }
//
//                public void onActivityResumed(Activity activity) {
//                    Adjust.onResume();
//                }
//
//                public void onActivityPaused(Activity activity) {
//                    Adjust.onPause();
//                }
//
//                public void onActivityStopped(Activity activity) {
//                }
//
//                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
//                }
//
//                public void onActivityDestroyed(Activity activity) {
//                }
//            });
//        }
//    }


}
