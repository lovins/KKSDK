package com.tapque.iap;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.gpower.billing.GPowerBillinglibFactory;
import com.gpower.billing.api.IBillinglibManager;
import com.gpower.billing.entry.IBillingEntry;
import com.gpower.billing.entry.IPaymentOrderEntry;
import com.gpower.billing.provider.google.IabHelper;
import com.gpower.billing.utils.GPowerBillinglibUtils;

import java.util.ArrayList;
import java.util.List;

public class IAPManager {

    private IBillinglibManager billingLibManager;

    /**
     * init billing lib
     *
     * @param context
     * @param callerHandler
     * @param provider
     */
    public void initBillingLib(Context context, Handler callerHandler, IBillingEntry.Provider provider) {

        IBillingEntry billingEntry;
        if (provider.equals(IBillingEntry.Provider.BILLING_PROVIDER_GOOGLE)) {
            billingEntry = GPowerBillinglibUtils.createBillingEntry(SystemConstants.APPLICATION_ID, SystemConstants.GOOGLE_KEY,null, false);
        } else {
            // Default billing provider is Payssion
            // Does user comes from India?
            if (SystemConstants.IAP_LIVE_ENABLED) {
                billingEntry = GPowerBillinglibUtils.createBillingEntry(SystemConstants.PAYSSION_APP_ID_LIVE,  SystemConstants.PAYSSION_SECRETE_KEY_LIVE, null,true);
            } else {
                billingEntry = GPowerBillinglibUtils.createBillingEntry(SystemConstants.PAYSSION_APP_ID_SANDBOX, SystemConstants.PAYSSION_SECRETE_KEY_SANDBOX,null,  false);
            }
        }

        billingLibManager = GPowerBillinglibFactory.getBillinglibManager(context, provider);
        billingLibManager.initBillinglib(callerHandler, billingEntry, null, false);
    }

    public void purchaseItem(IPaymentOrderEntry paymentOrderEntry) {
        billingLibManager.purchaseItem(paymentOrderEntry);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        billingLibManager.onActivityResult(requestCode, resultCode, data);
    }

    public void doRestore(String paymentOrderId, String paymentTransId) {
        IPaymentOrderEntry paymentOrderEntry = new IPaymentOrderEntry();
        paymentOrderEntry.setPaymentOrderId(paymentOrderId);
        paymentOrderEntry.setPaymentTransId(paymentTransId);
        List<IPaymentOrderEntry> list = new ArrayList<>();
        list.add(paymentOrderEntry);

        billingLibManager.doPurchaseRestore(list);
    }

    public void initSubscriptionItemDetail() {
        billingLibManager.querySKUDetails(false,IAPUtils.loadSubscriptionSKUList(),null);
    }

    public void onDestroy() {
        billingLibManager.onDestroy();
    }
}
