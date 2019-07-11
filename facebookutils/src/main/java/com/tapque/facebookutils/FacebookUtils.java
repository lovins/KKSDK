package com.tapque.facebookutils;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FacebookUtils {
    private  String TAG="KKFB";
    CallbackManager callbackManager;
    /**
     *
     *
     * @param activity
     */
    public  void login(Activity activity){
        callbackManager=CallbackManager.Factory.create();
        new LoginButton(activity).registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e(TAG, "onSuccess: "+loginResult );
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "onCancel: " );
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "onError: "+error );
            }
        });
    }
    /**
     *
     * 检查是否已经登陆
     * @return
     */
    private  boolean checkLoginState(){
        return AccessToken.getCurrentAccessToken()!=null&&AccessToken.getCurrentAccessToken().isExpired();
    }
    private  void printHashKey(Activity activity ){
        try {
            PackageInfo info = activity.getPackageManager().getPackageInfo(
                    "com.facebook.samples.loginhowto",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("TAG:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
}
