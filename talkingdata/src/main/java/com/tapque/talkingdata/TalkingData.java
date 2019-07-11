package com.tapque.talkingdata;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.tendcloud.tenddata.TDGAAccount;
import com.tendcloud.tenddata.TDGAItem;
import com.tendcloud.tenddata.TDGAMission;
import com.tendcloud.tenddata.TDGAVirtualCurrency;
import com.tendcloud.tenddata.TalkingDataGA;

import java.util.HashMap;
import java.util.Map;

public class TalkingData {
    static  Activity activity;


    static TDGAAccount account;
    public static void initTD(Activity act, String appID,String trackerName) {
        activity=act;
        TalkingDataGA.init(activity, appID, trackerName);
    }
    public static void setAccount(String accountId, int accountType, String accountName, int level, int gender, int age, String gameServer) {

        account = TDGAAccount.setAccount(accountId);
        if (accountType >= 0) {
            if (accountType == 0) {
                account.setAccountType( TDGAAccount.AccountType.ANONYMOUS);
            }
            if (accountType == 1) {
                account.setAccountType( TDGAAccount.AccountType.REGISTERED);
            }
            if (accountType == 2) {
                account.setAccountType( TDGAAccount.AccountType.SINA_WEIBO);
            }
            if (accountType == 3) {
                account.setAccountType( TDGAAccount.AccountType.QQ);
            }
            if (accountType == 4) {
                account.setAccountType( TDGAAccount.AccountType.QQ_WEIBO);
            }
            if (accountType == 5) {
                account.setAccountType( TDGAAccount.AccountType.ND91);
            }
            if (accountType == 6) {
                account.setAccountType( TDGAAccount.AccountType.WEIXIN);
            }
        }

        if (!TextUtils.isEmpty(accountName)) {
            account.setAccountName(accountName);
        }
        if(level>0){
            account.setLevel(level);
        }
        if (gender > 0) {
            if(gender==0){
                account.setGender(TDGAAccount.Gender.FEMALE);
            }else  if(gender==1){
                account.setGender(TDGAAccount.Gender.MALE);
            }
        }
        //age
        if (age > 0) {
            account.setAge(age);
        }
        if (!TextUtils.isEmpty(gameServer)) {
            account.setGameServer(gameServer);
        }
    }
    public  static  void setLevel(int level){
        if(null!=account) account.setLevel(level);
    }

    public static  void onReward(double amount,String resason){
        TDGAVirtualCurrency.onReward(amount,resason);
    }
    public  static  void onPurchase(String item,int number,double price){
        TDGAItem.onPurchase(item,number,price);
    }
    public  static  void onUse(String item,int itemNumber){
        TDGAItem.onUse(item,itemNumber);
    }

    public static  void onMission(int state,String misssonID,String cause){
        if(state==0){
            TDGAMission.onBegin(misssonID);
        }else if(state==1){
            TDGAMission.onCompleted(misssonID);
        }else  if(state==2){
            TDGAMission.onFailed(misssonID,cause);
        }
    }
    public  static  void onCustomEvent(String key,String eventName,String param){
        if(!TextUtils.isEmpty(eventName)&&!TextUtils.isEmpty(param)){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(eventName,param);
            TalkingDataGA.onEvent(key,map);
        }else{
            TalkingDataGA.onEvent(key);
        }
    }
    public static  String getDeviceID(){
        if(null!=activity){
            return  TalkingDataGA.getDeviceId(activity);
        }else {
            return  "";
        }
    }
}
