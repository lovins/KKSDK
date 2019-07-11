package com.tapque.adscn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustAttribution;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;
import com.adjust.sdk.LogLevel;
import com.adjust.sdk.OnAttributionChangedListener;
import com.adjust.sdk.Util;
import com.adjust.sdk.imei.AdjustImei;
import com.tapque.analyticscn.Analytics;
import com.thinking.analyselibrary.ThinkingAnalyticsSDK;

import java.util.ArrayList;
import java.util.Arrays;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        new Handler().postDelayed(this::initAdjust, 5000);
    }
    void initAdjust() {
        boolean adjust_production_enivroment = Utils.readValueFromManifestForBool(this, "adjust_production_enivroment");
        if(!adjust_production_enivroment){
           Log.e("adjust", "initAdjust: " );
        }

        AdjustImei.readImei();
        String ajustAppToken = Utils.readValueFromManifestToString(this, "adjust_app_token");

        if (TextUtils.isEmpty(ajustAppToken)) {
            Log.e("启动项", "initAdjust：请在清单文件中配置adjust id");
            return;
        }
        AdjustConfig config = new AdjustConfig(this, ajustAppToken, adjust_production_enivroment ? AdjustConfig.ENVIRONMENT_PRODUCTION : AdjustConfig.ENVIRONMENT_SANDBOX);
        config.setOnAttributionChangedListener(new OnAttributionChangedListener() {
            public void onAttributionChanged(AdjustAttribution attribution) {
                Log.e("adjust", "获得的所有adjust归因信息:" + attribution.toString());
                Analytics.trackInfo = attribution.network;
            }
        });
        if (!adjust_production_enivroment) {
            config.setLogLevel(LogLevel.VERBOSE);
        }
        config.setSendInBackground(true);
        Adjust.onCreate(config);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }
            @Override
            public void onActivityStarted(Activity activity) {

            }
            @Override
            public void onActivityResumed(Activity activity) {
                Adjust.onResume();
            }
            @Override
            public void onActivityPaused(Activity activity) {
                Adjust.onPause();
            }
            @Override
            public void onActivityStopped(Activity activity) {

            }
            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }
            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
        Adjust.trackEvent(new AdjustEvent(""));
    }
    @SuppressLint("MissingPermission")
    String getDeviceId(){
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }
    void initThink(){
        String thinkApp=Utils.readValueFromManifestToString(this,"thinkingData_app_id")+"";
        String thinkUrl=Utils.readValueFromManifestToString(this,"thinkingData_app_url")+"";
        if(!TextUtils.isEmpty(thinkApp)&&!TextUtils.isEmpty(thinkUrl)){
            Analytics.isInitThinkData=true;
            ThinkingAnalyticsSDK.sharedInstance(this,thinkApp,thinkUrl);
            ThinkingAnalyticsSDK.sharedInstance(this).enableAutoTrack(new ArrayList<ThinkingAnalyticsSDK.AutoTrackEventType>(Arrays.asList(ThinkingAnalyticsSDK.AutoTrackEventType.APP_START,ThinkingAnalyticsSDK.AutoTrackEventType.APP_END)){});
        }else {
            Analytics.isInitThinkData=false;
            Log.e("启动项", "initThink: 请在清单文件中配置thinkingData_app_id和thinkingData_app_url" );
        }
    }
}
