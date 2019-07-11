package com.tapque.adscn;

import android.util.Log;

import com.tapque.analyticscn.Analytics;
//import com.unity3d.player.UnityPlayer;

import org.json.JSONException;
import org.json.JSONObject;

//import layaair.game.browser.ExportJavaFunction;

public class AdsCallbackCenter {
    public  static  void sendMessageToEngine(String message) {
        Log.e("Ads", "sendMessageToEngine: " +message);
//        ExportJavaFunction.CallBackToJS(LayaAdsManager.class, "initLayaAdsManager", message);
//        UnityPlayer.UnitySendMessage("AdsManager","Callback",message);
    }
    public  static  void sendGoogleId(String googleID) {
//        ExportJavaFunction.CallBackToJS(LayaAdsManager.class, "getIDFA", googleID);
//        UnityPlayer.UnitySendMessage("AdsManager","GetGoogleIDCallback",googleID);
    }

    public  static  void sendPurchaseState(String purchaseJsonInfo) {
//        ExportJavaFunction.CallBackToJS(LayaAdsManager.class, "getIDFA", googleID);
//        UnityPlayer.UnitySendMessage("IAPManager","Callback",purchaseJsonInfo);
    }
    public static void sendMessageToEngine(String key, String data) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("key", key);
            jsonObject.put("data", data);
//            ExportJavaFunction.CallBackToJS(Analytics.class, "getMessage", jsonObject.toString());
        } catch (JSONException var4) {
            var4.printStackTrace();
        }
    }
    public  static  void sendPurchaseInfo(String purchaseInfo) {
//        UnityPlayer.UnitySendMessage("IAPManager","ReciverPurchaseInfo",purchaseInfo);
    }
    public  static  void sendKeyBoardText(String gameObjectName,String callbackMethodName, String text){
//        UnityPlayer.UnitySendMessage(gameObjectName,callbackMethodName,text);
    }
}
