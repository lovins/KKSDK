package com.lovin.ads;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.playmil.blockfit3d.R;
import com.tapque.ads.AdsManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
//import com.tapque.toolscn.CommonUtils;

public class MainActivity extends Activity implements View.OnClickListener, AdsManager.AdsStateChangeListenner {

    String TAG="Facebook";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
//        LoginManager.getInstance().logOut();
//        AdsManager.instance().init(this,true,this);
        initView();
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        FacebookSdk.isDebugEnabled();
//        printHashKey(this);
//        loginFacebook();
    }
//    CallbackManager callbackManager;
//    void loginFacebook(){
//        callbackManager = CallbackManager.Factory.create();
//        LoginManager.getInstance(). registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                Log.e(TAG, "onSuccess: " );
//            }
//
//            @Override
//            public void onCancel() {
//                Log.e(TAG, "onCancel: " );
//            }
//            @Override
//            public void onError(FacebookException error) {
//                Log.e(TAG, "onError: "+error.getLocalizedMessage() );
//            }
//        });
//
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult: "+requestCode);
//        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    void initView(){
        findViewById(R.id.entry_ads).setOnClickListener(this);
        findViewById(R.id.entry_purchase).setOnClickListener(this);
        findViewById(R.id.entry_cn_purchase).setOnClickListener(this);
        findViewById(R.id.star_unity_scene).setOnClickListener(this);
        findViewById(R.id.check_app).setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.star_unity_scene:
                Intent unityActivity=new Intent(this, com.lovin.ads.UnityPlayerActivity.class);
                startActivity(unityActivity);
                break;
            case  R.id.entry_ads:
                Intent adsIntent=new Intent(this,AdsTestActivity.class);
                startActivity(adsIntent);
                break;
            case  R.id.entry_purchase:
//                Intent puchaseIntent=new Intent(this,IAPTestActivity.class);
//                startActivity(puchaseIntent);
//                AdsManager.instance().showInterstitialInAndroid(0);
                break;
            case  R.id.entry_cn_purchase:
//                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));

                break;
            case  R.id.check_app:

//                LoginManager.getInstance().logOut();
                break;
        }
    }

    @Override
    public void onAdsStateChange(String adsState) {
        switch (adsState)
        {
            case "INIT_SUCCEED":
                AdsManager.instance().requestInterstitial(0);
                AdsManager.instance().requestRewardVideo();
                break;
        }
    }
//    private  void printHashKey(Activity activity ){
//        try {
//            PackageInfo info = activity.getPackageManager().getPackageInfo(
//                    "com.playmil.blockfit3d",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.e(TAG, Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            Log.e(TAG, "NameNotFoundException: "+e.getLocalizedMessage() );
//
//        } catch (NoSuchAlgorithmException e) {
//            Log.e(TAG, "NoSuchAlgorithmException: "+e.getLocalizedMessage() );
//        }
//    }
}
