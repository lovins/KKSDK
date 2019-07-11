package com.tapque.googlesign;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class SignManager {
   private   GoogleSignInClient googleSignInClient;
   private  boolean showLog;
   private GoogleSignInAccount googleSignInAccount;

    public     void init(Activity activity,boolean debug){
        showLog=debug;
        log("初始化登陆管理类");
        GoogleSignInOptions options=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient=GoogleSignIn.getClient(activity,options);

        getLastSignAccount(activity);
    }
    protected  void getLastSignAccount(Activity activity){
        log("获得上次登陆信息");
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(activity);
        if(null!=account){
            googleSignInAccount=account;
            log("用户已经登陆过了 直接更新信息");
        }else{
            log("用户进行首次登陆");
            sign(activity);
        }
    }
    private  void log(Object o){
        if(showLog)
        Log.e("SignManager", o.toString() );
    }
    protected  void sign(Activity activity){
//        Intent signIntent=googleSignInClient.getSignInIntent();
//        activity.startActivityForResult(signIntent,1008);
//        ActivityLauncher.init(activity).startActivityForResult(signIntent, new ActivityLauncher.Callback() {
//            @Override
//            public void onActivityResult(int resultCode, Intent data) {
//                log("登陆返回信息"+resultCode);
//                if(resultCode==1008){
//                    log("用户登陆成功");
//                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//                    handleSignInResult(task);
//                }
//            }
//        });
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            googleSignInAccount=account;
            log("获得用户信息成功："+"用户显示名字"+account.getDisplayName()+"用户邮箱:"+account.getEmail()+"用户头像地址:"+account.getPhotoUrl());
        } catch (ApiException e) {
            Log.e("SignManager", "signInResult:failed code=" + e.getStatusCode());
        }
    }
}
