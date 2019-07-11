package com.tapque.adscn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStreamReader;

public class Utils {
    @SuppressLint("HardwareIds")
    public static String getDeviceIdString(Context context) {
        String deviceId = "";
        try {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (TextUtils.isEmpty(deviceId)) {
                WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifi != null) {
                    WifiInfo info = wifi.getConnectionInfo();
                    deviceId = info.getMacAddress().replace(":", "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceId;
    }
    public  static   boolean readValueFromManifestForBool(Context activity, String key){
        PackageManager pm = activity.getPackageManager();
        ApplicationInfo appinfo;
        boolean metaDataValue =false;
        try {
            appinfo = pm.getApplicationInfo(activity.getPackageName(),PackageManager.GET_META_DATA);
            Bundle metaData = appinfo.metaData;
            metaDataValue = metaData.getBoolean(key);
            return metaDataValue;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return metaDataValue;
    }
    public  static  String readValueFromManifestToString(Context context, String key){
        PackageManager pm = context.getPackageManager();
        ApplicationInfo appinfo;
        String metaDataValue ="";
        try {
            appinfo = pm.getApplicationInfo(context.getPackageName(),PackageManager.GET_META_DATA);
            Bundle metaData = appinfo.metaData;
            metaDataValue = metaData.getString(key);
            return metaDataValue;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return metaDataValue;
    }
    public  static  String readValueFromManifest(Activity activity, String key){
        PackageManager pm = activity.getPackageManager();
        ApplicationInfo appinfo;
        String metaDataValue ="";
        try {
            appinfo = pm.getApplicationInfo(activity.getPackageName(),PackageManager.GET_META_DATA);
            Bundle metaData = appinfo.metaData;
            metaDataValue = metaData.getString(key);
            return metaDataValue;
        } catch (Exception e) {
            Log.e("===", "readValueFromManifest: " +e);
            e.printStackTrace();
        }
        return metaDataValue;
    }
    public  static  int readIntValueFromManifest(Activity activity, String key){
        PackageManager pm = activity.getPackageManager();
        ApplicationInfo appinfo;
        int metaDataValue=0;
        try {
            appinfo = pm.getApplicationInfo(activity.getPackageName(),PackageManager.GET_META_DATA);
            Bundle metaData = appinfo.metaData;
            metaDataValue = metaData.getInt(key);
            return metaDataValue;
        } catch (Exception e) {
            Log.e("===", "readValueFromManifest: " +e);
            e.printStackTrace();
        }
        return metaDataValue;
    }
    public    static JSONObject convertJsonStringToJsonObject(String json){
        JSONObject jsonObject=null;
        if(null!=json&&!TextUtils.isEmpty(json)){
            try {
                jsonObject = new JSONObject(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  jsonObject;
    }
    public static String getAssetJson(Activity activity, String fileName) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        InputStreamReader inputStreamReader = null;
        BufferedReader bf = null;
        try {
            //获取assets资源管理器
            AssetManager assetManager = activity.getAssets();
            inputStreamReader = new InputStreamReader(assetManager.open(fileName));
            //通过管理器打开文件并读取
            bf = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (Exception e) {


        } finally {
            closeStream(inputStreamReader);
            closeStream(bf);
        }
        return stringBuilder.toString();
    }
    public static void closeStream(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
