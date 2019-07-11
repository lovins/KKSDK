package com.tapque.analyticscn;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;
import com.tapque.adscn.AdsCallbackCenter;
import com.tapque.toolscn.Utils;
import com.thinking.analyselibrary.ThinkingAnalyticsSDK;
import org.json.JSONObject;
import java.util.Iterator;

public class Analytics {
    static  Activity mActivity;
    public static String trackInfo;
    public   static boolean isInitThinkData;
    public  static  void setContext(Activity activity){
        mActivity=activity;
    }
    public static void getMessage() {
        if (TextUtils.isEmpty(trackInfo)) {
            AdsCallbackCenter.sendMessageToEngine("SendTrackerInfo", "no_trackInfo");
        } else {
            AdsCallbackCenter.sendMessageToEngine("SendTrackerInfo", trackInfo);
        }
    }
    /**for unity and laya
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
        JSONObject jsonObject= Utils.convertJsonStringToJsonObject(json);
        setThinkTrackEvent(eventName,jsonObject);
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
