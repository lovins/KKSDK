package com.tapque.ads;

import com.unity3d.player.UnityPlayer;
import com.tapque.analytics.Analytics;

import org.json.JSONException;
import org.json.JSONObject;

//import layaair.game.browser.ExportJavaFunction;

public class AdsCallbackCenter {
    public  static  void sendMessageToEngine(String message) {
        try {
//            ExportJavaFunction.CallBackToJS(LayaAdsManager.class, "initLayaAdsManager", message);
        }catch (Exception e){

        }
//        UnityPlayer.UnitySendMessage("AdsManager","Callback",message);
    }
    public  static  void sendGoogleId(String googleID) {
        try {
//            ExportJavaFunction.CallBackToJS(LayaAdsManager.class, "getIDFA", googleID);
        }catch (Exception e){

        }
//        UnityPlayer.UnitySendMessage("AdsManager","GetIDCallback",googleID);
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
    public  static  void sendKeyBoardText(String gameObjectName,String callbackMethodName, String text){
        UnityPlayer.UnitySendMessage(gameObjectName,callbackMethodName,text);
    }
}
