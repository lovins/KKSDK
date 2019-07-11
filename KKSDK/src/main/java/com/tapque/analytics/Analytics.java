package com.tapque.analytics;
import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustAttribution;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;
import com.adjust.sdk.LogLevel;
import com.adjust.sdk.OnAttributionChangedListener;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.tapque.ads.AdsCallbackCenter;
import com.tapque.tools.Utils;
import com.tapque.mopubads.BuildConfig;
import com.thinking.analyselibrary.ThinkingAnalyticsSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Analytics {
    private  static boolean debugInfo;
    private static  Activity mActivity;
    public static String trackInfo;
    public   static boolean isInitThinkData;
    /*
     *
     *  初始化分析类
     * @param activity
     * @param debug
     */
    public  static  void setContext(Activity activity,boolean debug){
        debugInfo=debug;
        mActivity=activity;
    }

    private   static void log(String message){
        if(debugInfo){
            Log.e("Analytics", message );
        }
    }
    /**
     * 初始化Adjust
     *
     * @param application
     */
    public  static  void initAdjust(Application application){
        String ajustAppToken = Utils.readValueFromManifestToString(application, "adjust_app_token");
        if (TextUtils.isEmpty(ajustAppToken)) {
            Log.e("启动项", "initAdjust：请在清单文件中配置adjust id");
        } else {
            boolean adjust_production_enivroment = Utils.readValueFromManifestForBool(application, "adjust_production_enivroment");
            AdjustConfig config = new AdjustConfig(application, ajustAppToken, adjust_production_enivroment ? "production" : "sandbox");
            config.setOnAttributionChangedListener(new OnAttributionChangedListener() {
                public void onAttributionChanged(AdjustAttribution attribution) {
                    if(!adjust_production_enivroment)
                        log("获得的所有adjust归因信息:" + attribution.network);
                    JSONObject jsonObject=new JSONObject();
                    try {
                        jsonObject.put("TRACK_NETWORK",attribution.network);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ThinkingAnalyticsSDK.sharedInstance().user_set(jsonObject);
                    trackInfo = attribution.network;
                }
            });
            if (!adjust_production_enivroment) {
                config.setLogLevel(LogLevel.VERBOSE);
            }
            Adjust.onCreate(config);
            application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                }

                public void onActivityStarted(Activity activity) {
                }

                public void onActivityResumed(Activity activity) {
                    Adjust.onResume();
                }

                public void onActivityPaused(Activity activity) {
                    Adjust.onPause();
                }

                public void onActivityStopped(Activity activity) {
                }

                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                }

                public void onActivityDestroyed(Activity activity) {
                }
            });
        }
    }

    /**
     *
     *
     * 初始化facebook
     * @param application
     */
    public  static  void initFacebook(Application application){
        String fbid=Utils.readValueFromManifestToString(application,"com.facebook.sdk.ApplicationId");
        if(!TextUtils.isEmpty(fbid)){
            FacebookSdk.setApplicationId(fbid.substring(2));
            FacebookSdk.sdkInitialize(application);
        }else{
            Log.e("启动项", "initFacebook: 请在清单文件中配置facebook id" );
        }
    }
    /**
     *
     *
     * 初始化
     * @param application
     */
    public static  void initThinkingData(Application application){
        String thinkApp=Utils.readValueFromManifestToString(application,"thinkingData_app_id");
        String thinkUrl=Utils.readValueFromManifestToString(application,"thinkingData_app_url");
        if(!TextUtils.isEmpty(thinkApp)&&!TextUtils.isEmpty(thinkUrl)){
            Analytics.isInitThinkData=true;
            ThinkingAnalyticsSDK.sharedInstance(application,thinkApp,thinkUrl);
            ThinkingAnalyticsSDK.sharedInstance(application).enableAutoTrack(new ArrayList<ThinkingAnalyticsSDK.AutoTrackEventType>(Arrays.asList(ThinkingAnalyticsSDK.AutoTrackEventType.APP_START,ThinkingAnalyticsSDK.AutoTrackEventType.APP_END)){});
        }else {
            Analytics.isInitThinkData=false;
            Log.e("启动项", "initThink: 请在清单文件中配置thinkingData_app_id和thinkingData_app_url" );
        }
    }
    /**
     * for laya 获得渠道信息
     *
     */
    public static void getMessage() {
        if (TextUtils.isEmpty(trackInfo)) {
            AdsCallbackCenter.sendMessageToEngine("SendTrackerInfo", "no_trackInfo");
        } else {
            AdsCallbackCenter.sendMessageToEngine("SendTrackerInfo", trackInfo);
        }
    }
    /**for unity 获取渠道名称
     *
     * @return
     */
    public static String getTrackName() {
        return trackInfo;
    }

    public static String getVersionName() {
        String versionName = null;
        try {
            versionName = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException var2) {
            var2.printStackTrace();
        }
        return versionName;
    }
    /**通用
     * adjust 打点事件
     * @param token
     */
    public  static void logAdjustEvent(String token){
        if(!TextUtils.isEmpty(token)){
            AdjustEvent event=new AdjustEvent(token);
            com.adjust.sdk.Adjust.trackEvent(event);
        }else{
            Log.e("Analytics", "Adjust token为空");
        }
    }
    /**通用
     * 收入打点事件
     * @param token 口令
     * @param prices 价格
     * @param currency 货币单元
     */
    public  static  void logAdjustRevenue(String token,double prices,String currency){
        if(!TextUtils.isEmpty(token)){
            AdjustEvent event=new AdjustEvent(token);
            event.setRevenue(prices,currency);
            Adjust.trackEvent(event);
        }else{
            Log.e("Analytics", "收入事件token为空");
        }
    }
    /**
     * for android
     * @param eventName
     * @param bundle
     */
    public  static  void logFacebookEvent(String eventName, Bundle bundle){
        AppEventsLogger logger=AppEventsLogger.newLogger(mActivity);
        if(null!=bundle){
            logger.logEvent(eventName,bundle);
        }else{
            logger.logEvent(eventName);
        }
    }
    /**
     * for unity  laya
     * @param eventName
     * @param json
     */
    public    static  void logFacebookEvent(String eventName, String json){
        Bundle bundle=Utils.convertJsontoDict(json);
        logFacebookEvent(eventName,bundle);
    }

    /**
     *
     *
     * for adnroid
     * @param eventName
     * @param bundle
     */
    public  static  void logFirebaseEvent(String eventName,Bundle bundle){
        if(null!=bundle){
            FirebaseAnalytics.getInstance(mActivity).logEvent(eventName,bundle);
        }else{
            FirebaseAnalytics.getInstance(mActivity).logEvent(eventName,null);
        }
    }
    /**
     *
     *
     * for unity laya
     * @param eventName
     * @param json
     */
    public  static  void logFirebaseEvent(String eventName,String json){
        Bundle bundle=Utils.convertJsontoDict(json);
        logFirebaseEvent(eventName,bundle);
    }
    public  static  void logFacebook(String jsonParam) {
        String data = "";
        String eventName = "";
        try {
            JSONObject  outParam = new JSONObject(jsonParam);
            eventName = outParam.getString("eventName");
            data=outParam.getString("data");
            if(null!=jsonParam&&!TextUtils.isEmpty(data)){
                Bundle bundle = new Bundle();
                JSONObject jsonObject = new JSONObject(data);
                Iterator<String> stringList = jsonObject.keys();
                while (stringList.hasNext()) {
                    String key = stringList.next();
                    bundle.putString(key, String.valueOf(jsonObject.get(key)));
                }
                logFacebookEvent(eventName,bundle);
            }else{
                logFacebook(eventName);
            }
        } catch (Exception e) {

        }
    }

    /**for unity and laya
     *
     * @param jsonParam
     */
    public  static  void logFirebase(String jsonParam) {
        String data = "";
        String eventName = "";
        try {
            JSONObject  outParam = new JSONObject(jsonParam);
            eventName = outParam.getString("eventName");
            data=outParam.getString("data");
            if(null!=jsonParam&&!TextUtils.isEmpty(data)){
                Bundle bundle = new Bundle();
                JSONObject jsonObject = new JSONObject(data);
                Iterator<String> stringList = jsonObject.keys();
                while (stringList.hasNext()) {
                    String key = stringList.next();
                    bundle.putString(key, String.valueOf(jsonObject.get(key)));
                }
                logFirebaseEvent(eventName,bundle);
            }else{
                logFirebaseEvent(eventName,"");
            }
        } catch (Exception e) {

        }
    }

    /**
     * for laya
     * @param jsonParam
     */
    public  static  void logThinkingData(String jsonParam){
        String _type = "";
        String _key = "";
        try {
            JSONObject  outParam = new JSONObject(jsonParam);
            _type = outParam.getString("type");
            _key = outParam.getString("key");

            if (_type.equals("track")) {
                JSONObject _data =outParam.getJSONObject("data");
                ThinkingAnalyticsSDK.sharedInstance(mActivity).track(_key,_data);
            }
            else if (_type.equals("setSP")) {
                JSONObject _data =outParam.getJSONObject("data");
                ThinkingAnalyticsSDK.sharedInstance(mActivity).setSuperProperties(_data);
            }
            else if (_type.equals("login")) {
                String _data = outParam.getString("data");
                ThinkingAnalyticsSDK.sharedInstance(mActivity).login(_data);
            }
            else if (_type.equals("user_set")) {
                JSONObject _data =outParam.getJSONObject("data");
                ThinkingAnalyticsSDK.sharedInstance(mActivity).user_set(_data);
            }
            else if (_type.equals("user_setOnce")) {
                JSONObject _data =outParam.getJSONObject("data");
                ThinkingAnalyticsSDK.sharedInstance(mActivity).user_setOnce(_data);
            }
        } catch (Exception e) {

        }
    }

    /**
     *
     * 追踪事件
     * @param eventName 事件的名称，需要以字母开头，可以包括数字、字母及“_”，最大为50个字符
     * @param jsonObject 事件的属性，是一个JSONObject对象，每个元素代表一个属性，key值为属性名，类型为String，value值为属性值，类型为String、Number、Boolean、Date
     */
    public  static  void setThinkTrackEvent(String eventName,JSONObject jsonObject){
        if(!isInitThinkData)
            return;
        if(null!=jsonObject){
            ThinkingAnalyticsSDK.sharedInstance(mActivity).track(eventName,jsonObject);
        }else{
            ThinkingAnalyticsSDK.sharedInstance(mActivity).track(eventName);
        }
    }
    /**
     * 追踪事件
     * @param eventName 事件的名称，需要以字母开头，可以包括数字、字母及“_”，最大为50个字符
     */
    public  static  void setThinkTrackEvent(String eventName){
        if(!isInitThinkData)
            return;
        ThinkingAnalyticsSDK.sharedInstance(mActivity).track(eventName);
    }
    /**for unity
     * 追踪一个事件，不设置任何属性（预置属性和公共属性仍会上传）
     * @param eventName 事件的名称，需要以字母开头，可以包括数字、字母及“_”，最大为50个字符
     * @param json 事件的属性，是一个JSONObject转化的Json字符串
     */
    public  static  void setThinkTrackEvent(String eventName,String json){
        JSONObject jsonObject=Utils.convertJsonStringToJsonObject(json);
        setThinkTrackEvent(eventName,jsonObject);
    }

    /**
     *
     *  for android
     * @param eventName
     * @param bundle
     */
    public  static  void logFacebookFirebaseEvent(String eventName,Bundle bundle){
        logFacebookEvent(eventName,bundle);
        logFirebaseEvent(eventName,bundle);
    }
    /**
     *
     * for unity laya
     * @param eventName
     * @param json
     */
    public  static  void logFacebookFirebaseEvent(String eventName,String json){
        logFacebookEvent(eventName,json);
        logFirebaseEvent(eventName,json);
    }
    /**
     *
     * 通用
     * @param eventName
     * @param json
     */
    public  static  void logFacebookFirebaseThinkEvent(String eventName,String json){
        logFacebookFirebaseEvent(eventName,json);
        setThinkTrackEvent(eventName,json);
    }
    /**
     * 设置用户的访客ID，SDK默认以UUID作为用户的访客ID，每次设置将会覆盖先前的访客ID，设置后的访客ID将会被保存，无需多次调用
     * @param vistorID 您设置的访客ID
     */
    public  static  void setThinkVistorID(String vistorID){
        if(!isInitThinkData)
            return;
        ThinkingAnalyticsSDK.sharedInstance(mActivity).identify(vistorID);
    }
    /**
     * 设置用户的账号ID，设置后用户上传的数据中将带有#account_id这一字段，每次设置将会覆盖先前的账号ID，设置后的账号ID将会被保存，无需多次调用
     * @param loginID
     */
    public  static  void setThinkLoginID(String loginID){
        if(!isInitThinkData)
            return;
        ThinkingAnalyticsSDK.sharedInstance(mActivity).login(loginID);
    }
    /**
     *清除账号ID，设置后用户上传的数据中将没有#account_id这一字段
     */
    public  static  void setThinkLogout(){
        if(!isInitThinkData)
            return;
        ThinkingAnalyticsSDK.sharedInstance(mActivity).logout();
    }
    /**
     *添加事件公共属性，设置后上传的事件都会带有这些事件公共属性。
     * 每次设置会将新的属性添加进公共属性中，之前设置的也将保留，
     * 如果多次设置同一属性，则取最后一次的设置值，事件公共属性的配置将会被保存，
     * 无需多次调用
     *
     * @param propertiesJson 您添加的公共事件属性，是一个JSONObject对象，每个元素代表一个属性，key值为属性名，类型为String，value值为属性值，类型为String、Number、Boolean
     */
    public  static  void setThinkSuperProperties(String propertiesJson){
        if(!isInitThinkData)
            return;
        JSONObject jsonObject=Utils.convertJsonStringToJsonObject(propertiesJson);
        if(null!=jsonObject){
            ThinkingAnalyticsSDK.sharedInstance(mActivity).setSuperProperties(jsonObject);
        }
    }
    /**
     * 删除已设置的事件公共属性
     * @param propetieName 需要删除的事件公共属性的属性名
     */
    public  static  void unsetThinkProperties(String propetieName){
        if(!isInitThinkData)
            return;
        ThinkingAnalyticsSDK.sharedInstance(mActivity).unsetSuperProperty(propetieName);
    }
    /**
     *
     * 开始记录某个事件的时长，直到用户上传该事件为止，该事件中将会带有#duration这一预置属性，用以表示记录的时长，多次开始记录同一事件的时长将会以最后一次调用作为计时起点，
     * @param timeEventName 您需要计时的事件的名称，当使用track上传该事件名的事件时，计时停止，并上传计时数据。
     *
     */
    public  static  void setThinkTimeTrackEvent(String timeEventName){
        if(!isInitThinkData)
            return;
        ThinkingAnalyticsSDK.sharedInstance(mActivity).timeEvent(timeEventName);
    }
    /**
     *设置用户属性，该属性有值则覆盖
     * @param useInfoJson 需要设置的用户属性，是一个JSONObject对象转化的json字符串，每个元素代表一个属性，key值为属性名，类型为String，value值为属性值，类型为String、Number、Boolean
     */
    public   static  void setThinkUser(String useInfoJson){
        if(!isInitThinkData)
            return;
        JSONObject jsonObject=Utils.convertJsonStringToJsonObject(useInfoJson);
        if(null!=jsonObject){
            ThinkingAnalyticsSDK.sharedInstance(mActivity).user_set(jsonObject);
        }
    }
    /**
     *设置用户属性，该属性有值则不写入
     * @param useInfoJson 需要设置的用户属性，是一个JSONObject对象转化的json字符串，每个元素代表一个属性，key值为属性名，类型为String，value值为属性值，类型为String、Number、Boolean
     */
    public  static  void setThinkUserOnce(String useInfoJson){
        if(!isInitThinkData)
            return;
        JSONObject jsonObject=Utils.convertJsonStringToJsonObject(useInfoJson);
        if(null!=jsonObject){
            ThinkingAnalyticsSDK.sharedInstance(mActivity).user_setOnce(jsonObject);
        }
    }
    /**
     *设置用户属性，该属性有值则不写入
     * @param jsonObject 需要设置的用户属性，是一个JSONObject对象，每个元素代表一个属性，key值为属性名，类型为String，value值为属性值，类型为String、Number、Boolean
     */
    public  static  void setThinkUserOnce(JSONObject jsonObject){
        if(!isInitThinkData||null==mActivity)
            return;
        ThinkingAnalyticsSDK.sharedInstance(mActivity).user_setOnce(jsonObject);
    }
    /**
     * 对数值型的用户属性进行累加操作，输入负值相当于减法操作
     * @param useInfoJson 需要进行累加操作的用户属性，是一个JSONObject对象转化的json字符串，每个元素代表一个属性，key值为属性名，类型为String，value值为属性值，类型为Number
     */
    public   static  void setThinkUserAdd(String useInfoJson){
        if(!isInitThinkData)
            return;
        JSONObject jsonObject=Utils.convertJsonStringToJsonObject(useInfoJson);
        if(null!=jsonObject){
            ThinkingAnalyticsSDK.sharedInstance(mActivity).user_add(jsonObject);
        }
    }
    /**
     * 对数值型的用户属性进行累加操作，输入负值相当于减法操作   需要进行累加操作的用户属性，是一个JSONObject对象，每个元素代表一个属性，key值为属性名，类型为String，value值为属性值，类型为Number
     * @param jsonObject
     */
    public  static  void setThinkUserAdd(JSONObject jsonObject){
        if(!isInitThinkData||null==mActivity)
            return;
        ThinkingAnalyticsSDK.sharedInstance(mActivity).user_add(jsonObject);
    }
    /**
     *在数据库中删除该用户，该操作可能产生不可逆的后果，请慎用
     */
    public  static  void deleteThinkUser(){
        if(!isInitThinkData)
            return;
        ThinkingAnalyticsSDK.sharedInstance(mActivity).user_delete();
    }
}
