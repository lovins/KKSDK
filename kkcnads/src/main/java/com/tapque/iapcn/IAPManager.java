//package com.tapque.iapcn;
//
//import android.app.Activity;
//import android.content.pm.ActivityInfo;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.adjust.sdk.Util;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.tapque.adscn.AdsCallbackCenter;
//import com.tapque.adscn.Utils;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.List;
//
//public class IAPManager {
//
//    private static List<PurchaseBean> purchaseBeanArrayList = new ArrayList<>();
//    static  String TAG="IAP";
//    public  static  String productName;
//    public  static  String app_Id;
//    public  static  String PRIVATE_KEY;
//    public  static  String PUBLIC_KEY;
//    private  static boolean debug;
//
//    public  static PurchaseBean currentPurchasingItem;
//
//    public  static  void initIAPConfig(boolean showlog,Activity activity,String fileName,String name, String appid, String private_key, String public_key){
//        debug=showlog;
//        productName=name;
//        app_Id=appid;
//        PRIVATE_KEY=private_key;
//        PUBLIC_KEY=public_key;
//        if(TextUtils.isEmpty(appid)){
//            Log.e(TAG, "IAP 初始化错误，app id不能为空" );
//        }
////        IAppPay.init(activity,ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,appid);
//        String allProductionInfo=Utils.getAssetJson(activity,fileName);
//        if(TextUtils.isEmpty(allProductionInfo)){
//            Log.e("IAP", "读取商品信息配置文件出错:");
//            return;
//        }
//        purchaseBeanArrayList = new Gson().fromJson(allProductionInfo, new TypeToken<List<PurchaseBean>>() {}.getType());
//        AdsCallbackCenter.sendPurchaseInfo(allProductionInfo);
//    }
//
//    private  static void log(Object object){
//        if(debug){
//            Log.e(TAG, ""+object.toString());
//
//        }
//    }
//
//    private  static void startBuy(Activity activity,int index){
//        if(purchaseBeanArrayList.size()<=0)
//            return;
//        for (PurchaseBean item  :purchaseBeanArrayList) {
//            if(item.getSequence()==index){
//                currentPurchasingItem=item;
//            }
//        }
//        startBuy(activity,currentPurchasingItem.getPurchaseItemName());
//    }
//    private  static void startBuy( Activity activity,String productName){
//        if(purchaseBeanArrayList.size()<=0)
//            return;
//        log("进行购买:"+productName);
//        for (PurchaseBean item  :purchaseBeanArrayList) {
//            if(item.getPurchaseItemName().equals(productName)){
//                currentPurchasingItem=item;
//            }
//        }
//        startPlay(activity,currentPurchasingItem.getPurchaseItemName(),resultCallback);
//    }
//
//
//   static   IPayResultCallback resultCallback=new IPayResultCallback() {
//        @Override
//        public void onPayResult(int resultCode, String signValue, String resultInfo) {
//            switch (resultCode){
//                case IAppPay.PAY_SUCCESS:
//                    boolean payState = IAppPayOrderUtils.checkPayResult(signValue, IAPManager.PUBLIC_KEY);
//                    if(null==IAPManager.currentPurchasingItem){
//                        Log.e("IAP", "系统错误" );
//                        AdsCallbackCenter.sendPurchaseState("Error");
//                        return;
//                    }
//                    AdsCallbackCenter.sendPurchaseState(currentPurchasingItem.getPurchaseItemName());
//                    break;
//                case IAppPay.PAY_CANCEL:
//                    Log.e("IAP", "取消支付"+resultInfo);
//                    AdsCallbackCenter.sendPurchaseState("Cancel");
//                    break;
//                default:
//                    if ("用户已订购该业务，不需要支付(5508)".equalsIgnoreCase(resultInfo)){
//                        if(currentPurchasingItem.getPurchaseType()==1){
//                            Log.e(TAG, "恢复购买 " +currentPurchasingItem.getPurchaseItemName());
//                        }else{
//                            Log.e(TAG, "商品信息配置错误，请检查purchase.json文件中 "+currentPurchasingItem.getPurchaseItemName()+"商品类型配置是否正确");
//                        }
//                        AdsCallbackCenter.sendPurchaseState("Restore:"+currentPurchasingItem.getPurchaseItemName());
//                    }
//            }
//        }
//    };
//
//    public static  void startPlay(Activity activity,String productName,IPayResultCallback iPayResultCallback){
//        String purchaseInfo=getPayInfoDataBySKU(activity, productName);
//        if(TextUtils.isEmpty(purchaseInfo)){
//            Log.e(TAG, "购买失败，没有找到对应的商品信息，请确定商品配置文件与后台保持一致" );
//            return;
//        }
//        IAppPay.startPay(activity, getPayInfoDataBySKU(activity,productName), iPayResultCallback);
//    }
//    private  static  String getPayInfoDataBySKU(Activity activity,String productName){
//        for (PurchaseBean item  :purchaseBeanArrayList) {
//            if(item.getPurchaseItemName().equals(productName)){
//                currentPurchasingItem=item;
//            }
//        }
//        if(null!=currentPurchasingItem){
//            String userID=Utils.getDeviceIdString(activity);
//            if(TextUtils.isEmpty(userID)){
//                userID="defaultUserID";
//            }
//            return getTransdata(userID,currentPurchasingItem.getSequence(), Double.valueOf(currentPurchasingItem.getPurchaseRealPrice()),userID+System.currentTimeMillis());
//        }else{
//            return null;
//        }
//    }
//    private static String getTransdata(String userID, int productIndexID, double price, String orderID) {
//        IAppPayOrderUtils orderUtils = new IAppPayOrderUtils();
//        orderUtils.setAppuserid(userID);
//        orderUtils.setAppid(app_Id);
//        orderUtils.setWaresid(productIndexID);
//        orderUtils.setCporderid(orderID);
//        orderUtils.setPrice(price);
//        return orderUtils.getTransdata(PRIVATE_KEY);
//    }
//}
