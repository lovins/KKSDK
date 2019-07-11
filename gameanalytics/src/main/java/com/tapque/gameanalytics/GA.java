package com.tapque.gameanalytics;

import android.app.Activity;
import android.text.TextUtils;

import com.gameanalytics.sdk.GAProgressionStatus;
import com.gameanalytics.sdk.GAResourceFlowType;
import com.gameanalytics.sdk.GameAnalytics;

public class GA {

static GA _instance;
    public  static  GA instance(){
        if(_instance == null)
            _instance = new GA();
        return _instance;
    }
    public  static  void initGA(Activity activity,String buildCode,String key,String secretKey,String userID,boolean debugModel){
        if(!TextUtils.isEmpty(buildCode))
            GameAnalytics.configureBuild(buildCode);
        if(debugModel){
            GameAnalytics.setEnabledInfoLog(true);
            GameAnalytics.setEnabledVerboseLog(true);
        }
        if(!TextUtils.isEmpty(userID))
            GameAnalytics.configureUserId(userID);
        if(!TextUtils.isEmpty(key)&&!TextUtils.isEmpty(secretKey))
            GameAnalytics.initializeWithGameKey(activity,key,secretKey);
    }
    public  static  void configureAvailableResourceCurrencies(String resourceCurrencies){
        String[]resourceCurrenciesNames=resourceCurrencies.split(":");
        GameAnalytics.configureAvailableResourceCurrencies(resourceCurrenciesNames);
    }
    public  static  void configureAvailableResourceItemTypes(String ResourceItemTypes){
        String[]resourceItemTypesNames=ResourceItemTypes.split(":");
        GameAnalytics.configureAvailableResourceItemTypes(resourceItemTypesNames);
    }
    public  static  void configureAvailableCustomDimensions01(String Dimensions01){
        String[]Dimensions01Names=Dimensions01.split(":");
        GameAnalytics.configureAvailableResourceItemTypes(Dimensions01Names);
    }
    public  static  void configureAvailableCustomDimensions02(String Dimensions02){
        String[]Dimensions02Names=Dimensions02.split(":");
        GameAnalytics.configureAvailableResourceItemTypes(Dimensions02Names);
    }
    public  static  void configureAvailableCustomDimensions03(String Dimensions03){
        String[]Dimensions03Names=Dimensions03.split(":");
        GameAnalytics.configureAvailableResourceItemTypes(Dimensions03Names);
    }
    public  static  void addBusinessEvent(int amount,String itemType,String ItemID,String cartType){
        GameAnalytics.addBusinessEventWithCurrency("USD", amount, itemType, ItemID, cartType, null
                , "google_play", null);
    }
    public  static  void addResources(boolean added,String currency,float amout,String itemType,String itemID){
        if(added){
            GameAnalytics.addResourceEventWithFlowType(GAResourceFlowType.Source,currency, amout, itemType, "default");
        }else{
            GameAnalytics.addResourceEventWithFlowType(GAResourceFlowType.Sink, currency, amout, itemType, "default");
        }
    }
    public  static  void logLevelState(int levelState, String customEvent1,String customEvent2,String customEvent3){
        if(levelState==0){
            GameAnalytics.addProgressionEventWithProgressionStatus(GAProgressionStatus.Start, customEvent1,customEvent2,customEvent3);
        }else if(levelState==1) {
            GameAnalytics.addProgressionEventWithProgressionStatus(GAProgressionStatus.Complete, customEvent1,customEvent2,customEvent3);
        }else  if(levelState==2){
            GameAnalytics.addProgressionEventWithProgressionStatus(GAProgressionStatus.Fail, customEvent1,customEvent2,customEvent3);
        }
    }
    public  static  void design(String customEvent){
        GameAnalytics.addDesignEventWithEventId(customEvent);
    }
    public  static  void design(String customEvent,float value){
        GameAnalytics.addDesignEventWithEventId(customEvent,value);
    }
}
