package com.tapque.tools;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;


import com.facebook.appevents.AppEventsLogger;
import com.google.android.exoplayer2.util.ParsableNalUnitBitArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Utils {
    public  static  String readValueFromManifestToString(Activity activity, String key){
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
    public  static Bundle convertJsontoDict(String param){
        Bundle bundle = null;
        if (null != param && !TextUtils.isEmpty(param)) {
            try {
                bundle = new Bundle();
                JSONObject jsonObject = new JSONObject(param);
                Iterator<String> stringList = jsonObject.keys();
                while (stringList.hasNext()) {
                    String key = stringList.next();
                    bundle.putString(key, String.valueOf(jsonObject.get(key)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return  bundle;
    }
    public    static  JSONObject convertJsonStringToJsonObject(String json){
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
    private   void checkPermission(Activity activity){
        if (Build.VERSION.SDK_INT >= 23) {
            checkAndRequestPermission(activity);
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    private void checkAndRequestPermission(Activity activity) {
        List<String> lackedPermission = new ArrayList<String>();
        if (!(activity.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!(activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!(activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (lackedPermission.size() != 0) {
            String[] requestPermissions = new String[lackedPermission.size()];
            lackedPermission.toArray(requestPermissions);
            activity.requestPermissions(requestPermissions, 1024);
        }
    }
    public boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }
    public  static boolean checkClassExist(String className){
        boolean isExist=false;
        try {
           Class.forName(className);
            isExist=true;
        }
       catch (ClassNotFoundException e) {
            e.printStackTrace();
           isExist=false;
        }
        return  isExist;
    }

    public  static  boolean initApplovin(Activity activity,String className){
        boolean initApplovin=false;
        try {
            Class applovinClass=Class.forName(className);
            Method initMethod =applovinClass.getDeclaredMethod("initializeSdk",Context.class);
            initMethod.invoke(null,activity);
            initApplovin=true;
        } catch (ClassNotFoundException e) {
            Log.e("Ads", "ClassNotFoundException: "+e.getLocalizedMessage() );
            initApplovin=false;
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            Log.e("Ads", "NoSuchMethodException: "+e.getLocalizedMessage() );
            initApplovin=false;
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.e("Ads", "IllegalAccessException: "+e.getLocalizedMessage() );
            initApplovin=false;
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            Log.e("Ads", "InvocationTargetException: "+e.getLocalizedMessage() );
            initApplovin=false;
            e.printStackTrace();
        }
        return initApplovin;
    }
}
