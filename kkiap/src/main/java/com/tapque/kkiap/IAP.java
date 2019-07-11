/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tapque.kkiap;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.BillingResponse;
import com.android.billingclient.api.BillingClient.FeatureType;
import com.android.billingclient.api.BillingClient.SkuType;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.Purchase.PurchasesResult;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * Handles all the interactions with Play Store (via Billing library), maintains connection to
 * it through BillingClient and caches temporary states/data if needed
 */
public class IAP implements PurchasesUpdatedListener {
    public  static  IAP iapInstance;
    // Default value of mBillingClientResponseCode until BillingManager was not yeat initialized
    public static final int BILLING_MANAGER_NOT_INITIALIZED  = -1;

    private static final String TAG = "IAP";
    /** A reference to BillingClient **/
    private BillingClient mBillingClient;
    /**
     * True if billing service is connected now.
     */
    private boolean mIsServiceConnected;

    private  BillingUpdatesListener mBillingUpdatesListener;

    private  Activity mActivity;

    private final List<Purchase> mPurchases = new ArrayList<>();

    private Set<String> mTokensToBeConsumed;

    private int mBillingClientResponseCode = BILLING_MANAGER_NOT_INITIALIZED;

    /* BASE_64_ENCODED_PUBLIC_KEY should be YOUR APPLICATION'S PUBLIC KEY
     * (that you got from the Google Play developer console). This is not your
     * developer public key, it's the *app-specific* public key.
     *
     * Instead of just storing the entire literal string here embedded in the
     * program,  construct the key at runtime from pieces or
     * use bit manipulation (for example, XOR with some other string) to hide
     * the actual key.  The key itself is not secret information, but we don't
     * want to make it easy for an attacker to replace the public key with one
     * of their own and then fake messages from the server.
     */
    private static final String BASE_64_ENCODED_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApgQ3alap/5G9h4IS8VtnpYitiCs3RTyk/rIan1mOCzRfBt+vAxugYPg9BV5MMhrl2J7i2oJDF3zzwZGnYXe5s8wuKHeJoC/R3fhLYxFIFM3UHTO807TU0jSe0LP2F6gYICuFR4qzna+5VZ57o8Ve5leGBS0tdZMHizLFvKCZv1fsSN4cM4rqzfBTdJQl1wZzOEv0aGE2JIgwx+8CFYiH7JOBruET9kgP7X0cOxYvtFWg9ZJqORWaaDqocaPCEkZ4GViEX67d1O46NeUI/ZiANP0YQFopEerOWcFVoV1rUMsi5389hNYM8uH/CLkhUsA8xTpixhdDf8U6ZHqBkJxbtQIDAQAB";

    /**
     * Listener to the updates that happen when purchases list was updated or consumption of the
     * item was finished
     */
    public interface BillingUpdatesListener {
        void onBillingClientSetupFinished();
        void onConsumeFinished(String token, @BillingResponse int result);
        void onPurchasesUpdated(List<Purchase> purchases);
    }
    /**
     * Listener for the Billing client state to become connected
     */
    public interface ServiceConnectedListener {
        void onServiceConnected(@BillingResponse int resultCode);
    }
    protected  void setLayaListener(){

    }
    public static IAP instance() {
        if (null == iapInstance) {
            iapInstance = new IAP();
        }
        return iapInstance;
    }
    public  void initIAPManager(Activity activity,final  BillingUpdatesListener billingUpdatesListener){
        Log.d(TAG, "Creating Billing client.");
        mActivity = activity;
        mBillingUpdatesListener = billingUpdatesListener;
        mBillingClient = BillingClient.newBuilder(mActivity).setListener(this).build();
        Log.d(TAG, "Starting setup.");
        // Start setup. This is asynchronous and the specified listener will be called
        // once setup completes.
        // It also starts to report all the new purchases through onPurchasesUpdated() callback.
        startServiceConnection(new Runnable() {
            @Override
            public void run() {
                // Notifying the listener that billing client is ready
                mBillingUpdatesListener.onBillingClientSetupFinished();
                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "设置成功，设置库存信息.");
                queryPurchases();
            }
        });

    }

    /**
     * Handle a callback that purchases were updated from the Billing library  刷新购买
     */
    @Override
    public void onPurchasesUpdated(int resultCode, List<Purchase> purchases) {
        if (resultCode == BillingResponse.OK) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
            mBillingUpdatesListener.onPurchasesUpdated(mPurchases);
        } else if (resultCode == BillingResponse.USER_CANCELED) {
            Log.i(TAG, "onPurchasesUpdated() - 用户取消购买流程");
        } else {
            Log.w(TAG, "onPurchasesUpdated() 未知原因: " + resultCode);
        }
    }

    /**
     * Start a purchase flow
     */
    public void initiatePurchaseFlow(final String skuId, final @SkuType String billingType) {
        initiatePurchaseFlow(skuId, null, billingType);
    }
    public void initiatePurchaseFlow(final SkuDetails skuDetails){
        Runnable purchaseFlowRequest=new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "开始购买流程");
                BillingFlowParams params=BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build();
                mBillingClient.launchBillingFlow(mActivity,params);
            }
        };
    }
    /**
     * Start a purchase or subscription replace flow
     */
    public void initiatePurchaseFlow(final String skuId, final ArrayList<String> oldSkus,
                                     final @SkuType String billingType) {
        Runnable purchaseFlowRequest = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Launching in-app purchase flow. Replace old SKU? " + (oldSkus != null));
                BillingFlowParams purchaseParams = BillingFlowParams.newBuilder()
                        .setSku(skuId).setType(billingType).setOldSkus(oldSkus).build();
                mBillingClient.launchBillingFlow(mActivity, purchaseParams);
            }
        };

        executeServiceRequest(purchaseFlowRequest);
    }
    public Context getContext() {
        return mActivity;
    }

    /**
     * Clear the resources
     */
    public void destroy() {
        Log.d(TAG, "Destroying the manager.");

        if (mBillingClient != null && mBillingClient.isReady()) {
            mBillingClient.endConnection();
            mBillingClient = null;
        }
    }

    public void querySkuDetailsAsync(@SkuType final String itemType, final List<String> skuList,
                                     final SkuDetailsResponseListener listener) {
        // Creating a runnable from the request to use it inside our connection retry policy below
        Runnable queryRequest = new Runnable() {
            @Override
            public void run() {
                // Query the purchase async
                SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                params.setSkusList(skuList).setType(itemType);
                mBillingClient.querySkuDetailsAsync(params.build(),
                        new SkuDetailsResponseListener() {
                            @Override
                            public void onSkuDetailsResponse(int responseCode,
                                                             List<SkuDetails> skuDetailsList) {
                                listener.onSkuDetailsResponse(responseCode, skuDetailsList);
                            }
                        });
            }
        };

        executeServiceRequest(queryRequest);
    }

    public void consumeAsync(final String purchaseToken) {
        Log.e(TAG, "手动消耗"+purchaseToken );
        // If we've already scheduled to consume this token - no action is needed (this could happen
        // if you received the token when querying purchases inside onReceive() and later from
        // onActivityResult()
        if (mTokensToBeConsumed == null) {
            mTokensToBeConsumed = new HashSet<>();
        } else if (mTokensToBeConsumed.contains(purchaseToken)) {
            Log.i(TAG, "Token was already scheduled to be consumed - skipping...");
            return;
        }
        mTokensToBeConsumed.add(purchaseToken);

        // Generating Consume Response listener
        final ConsumeResponseListener onConsumeListener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(@BillingResponse int responseCode, String purchaseToken) {
                // If billing service was disconnected, we try to reconnect 1 time
                // (feel free to introduce your retry policy here).
                mBillingUpdatesListener.onConsumeFinished(purchaseToken, responseCode);
            }
        };

        // Creating a runnable from the request to use it inside our connection retry policy below
        Runnable consumeRequest = new Runnable() {
            @Override
            public void run() {
                // Consume the purchase async
                mBillingClient.consumeAsync(purchaseToken, onConsumeListener);
            }
        };

        executeServiceRequest(consumeRequest);
    }

    /**
     * Returns the value Billing client response code or BILLING_MANAGER_NOT_INITIALIZED if the
     * clien connection response was not received yet.
     */
    public int getBillingClientResponseCode() {
        return mBillingClientResponseCode;
    }

    /**
     * Handles the purchase
     * <p>Note: Notice that for each purchase, we check if signature is valid on the client.
     * It's recommended to move this check into your backend.
     * See {@link Security#verifyPurchase(String, String, String)}
     * </p>
     * @param purchase Purchase to be handled
     */
    private void handlePurchase(Purchase purchase) {
        if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
            Log.i(TAG, "进行购买: " + purchase + "; 但是签名验证失败");
            return;
        }
        Log.d(TAG, "有效验证: " + purchase);
        mPurchases.add(purchase);
    }

    /**
     * Handle a result from querying of purchases and report an updated list to the listener
     */
    private void onQueryPurchasesFinished(PurchasesResult result) {
        // Have we been disposed of in the meantime? If so, or bad result code, then quit
        if (mBillingClient == null || result.getResponseCode() != BillingResponse.OK) {
            Log.w(TAG, "支付系统为空或者存在错误 (" + result.getResponseCode()
                    + ") was bad - quitting");
            return;
        }

        Log.d(TAG, "查询成功.");

        // Update the UI and purchases inventory with new list of purchases
        mPurchases.clear();
        onPurchasesUpdated(BillingResponse.OK, result.getPurchasesList());
    }

    /**
     * Checks if subscriptions are supported for current client
     * <p>Note: This method does not automatically retry for RESULT_SERVICE_DISCONNECTED.
     * It is only used in unit tests and after queryPurchases execution, which already has
     * a retry-mechanism implemented.
     * </p>
     */
    public boolean areSubscriptionsSupported() {
        int responseCode = mBillingClient.isFeatureSupported(FeatureType.SUBSCRIPTIONS);
        if (responseCode != BillingResponse.OK) {
            Log.w(TAG, "不支持订阅的原因 " + responseCode);
        }
        return responseCode == BillingResponse.OK;
    }

    /**
     * Query purchases across various use cases and deliver the result in a formalized way through
     * a listener
     */
    public void queryPurchases() {
        Runnable queryToExecute = new Runnable() {
            @Override
            public void run() {
                long time = System.currentTimeMillis();
                PurchasesResult purchasesResult = mBillingClient.queryPurchases(SkuType.INAPP);
                Log.i(TAG, "查询普通商品信息运行时间: " + (System.currentTimeMillis() - time)
                        + "ms");
                // If there are subscriptions supported, we add subscription rows as well
                if (areSubscriptionsSupported()) {
                    PurchasesResult subscriptionResult
                            = mBillingClient.queryPurchases(SkuType.SUBS);
                    Log.i(TAG, "查询订阅商品信息运行时间: "
                            + (System.currentTimeMillis() - time) + "ms");
                    Log.i(TAG, "查询订阅结果: "
                            + subscriptionResult.getResponseCode()
                            + " 订阅条目数量: " + subscriptionResult.getPurchasesList().size());

                    if (subscriptionResult.getResponseCode() == BillingResponse.OK) {
                        purchasesResult.getPurchasesList().addAll(
                                subscriptionResult.getPurchasesList());
                    } else {
                        Log.e(TAG, "订阅查询失败");
                    }
                } else if (purchasesResult.getResponseCode() == BillingResponse.OK) {
                    Log.i(TAG, "不支持订阅功能");
                } else {
                    Log.w(TAG, "查询错误代码: "
                            + purchasesResult.getResponseCode());
                }
                onQueryPurchasesFinished(purchasesResult);
            }
        };

        executeServiceRequest(queryToExecute);
    }

    public void startServiceConnection(final Runnable executeOnSuccess) {
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingResponse int billingResponseCode) {
                Log.d(TAG, "Setup finished. Response code: " + billingResponseCode);

                if (billingResponseCode == BillingResponse.OK) {
                    mIsServiceConnected = true;
                    if (executeOnSuccess != null) {
                        executeOnSuccess.run();
                    }
                }
                mBillingClientResponseCode = billingResponseCode;
            }

            @Override
            public void onBillingServiceDisconnected() {
                mIsServiceConnected = false;
            }
        });
    }

    private void executeServiceRequest(Runnable runnable) {
        if (mIsServiceConnected) {
            runnable.run();
        } else {
            // If billing service was disconnected, we try to reconnect 1 time.
            // (feel free to introduce your retry policy here).
            startServiceConnection(runnable);
        }
    }
    /**
     * Verifies that the purchase was signed correctly for this developer's public key.
     * <p>Note: It's strongly recommended to perform such check on your backend since hackers can
     * replace this method with "constant true" if they decompile/rebuild your app.
     * </p>
     */
    private boolean verifyValidSignature(String signedData, String signature) {
        // Some sanity checks to see if the developer (that's you!) really followed the
        // instructions to run this sample (don't put these checks on your app!)
        if (BASE_64_ENCODED_PUBLIC_KEY.contains("CONSTRUCT_YOUR")) {
            throw new RuntimeException("Please update your app's public key at: "
                    + "BASE_64_ENCODED_PUBLIC_KEY");
        }
        try {
            return Security.verifyPurchase(BASE_64_ENCODED_PUBLIC_KEY, signedData, signature);
        } catch (IOException e) {
            Log.e(TAG, "Got an exception trying to validate a purchase: " + e);
            return false;
        }
    }
}

