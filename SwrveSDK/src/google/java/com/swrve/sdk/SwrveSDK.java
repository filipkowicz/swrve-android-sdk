package com.swrve.sdk;


import android.content.Context;
import android.content.Intent;

import com.swrve.sdk.config.SwrveConfig;

public class SwrveSDK extends SwrveSDKBase {

    /**
     * Create a single Swrve SDK instance.
     * @param context your activity or application context
     * @param appId   your app id in the Swrve dashboard
     * @param apiKey  your app api_key in the Swrve dashboard
     * @return singleton SDK instance.
     */
    public static synchronized ISwrve createInstance(final Context context, final int appId, final String apiKey) {
        return createInstance(context, appId, apiKey, new SwrveConfig());
    }

    /**
     * Create a single Swrve SDK instance.
     * @param context your activity or application context
     * @param appId   your app id in the Swrve dashboard
     * @param apiKey  your app api_key in the Swrve dashboard
     * @param config  your SwrveConfig options
     * @return singleton SDK instance.
     */
    public static synchronized ISwrve createInstance(final Context context, final int appId, final String apiKey, final SwrveConfig config) {
        if (context == null) {
            SwrveHelper.logAndThrowException("Context not specified");
        }

        if (!SwrveHelper.sdkAvailable()) {
            instance =  new SwrveEmpty(context, apiKey);
        }
        if (instance == null) {
            instance = new Swrve(context, appId, apiKey, config);
        }
        return (ISwrve) instance;
    }

    /**
     * Returns the Swrve configuration that was used to initialize the SDK.
     *
     * @return configuration used to context the SDK
     */
    public static SwrveConfig getConfig() {
        checkInstanceCreated();
        return (SwrveConfig) instance.getConfig();
    }

    /**
     * Set the push notification listener.
     *
     * @param pushNotificationListener
     */
    public static void setPushNotificationListener(ISwrvePushNotificationListener pushNotificationListener) {
        checkInstanceCreated();
        SwrvePushSDK pushSDK = SwrvePushSDK.getInstance();
        if (pushSDK != null) {
            pushSDK.setPushNotificationListener(pushNotificationListener);
        }
    }

    /**
     * Set the silent push listener.
     *
     * @param silentPushListener
     */
    public static void setSilentPushListener(SwrveSilentPushListener silentPushListener) {
        checkInstanceCreated();
        SwrvePushSDK pushSDK = SwrvePushSDK.getInstance();
        if (pushSDK != null) {
            pushSDK.setSilentPushListener(silentPushListener);
        }
    }

    /**
     * Add a Swrve.iap event to the event queue. This event should be added
     * for unvalidated real money transactions in the Google Play Store, where a single item was purchased.
     * (i.e where no in-app currency or bundle was purchased)
     *
     * @param productId     Unique product identifier for the item bought. This should
     *                      match the Swrve resource name.
     *                      Required, cannot be empty.
     * @param productPrice  The price (in real money) of the product that was purchased.
     *                      Note: this is not the price of the total transaction, but the per-product price.
     *                      Must be greater than or equal to zero.
     * @param currency      real world currency used for this transaction. This must be an ISO
     *                      currency code. A typical value would be "USD". Required, cannot be empty.
     * @param dataSignature The purchase data received from Google Play. Required, cannot be empty.
     */
    public static void iapPlay(String productId, double productPrice, String currency, String purchaseData, String dataSignature) {
        checkInstanceCreated();
        ((ISwrve) instance).iapPlay(productId, productPrice, currency, purchaseData, dataSignature);
    }

    /**
     * Add a Swrve.iap event to the event queue. This event should be added for unvalidated real
     * money transactions in the Google Play Store, where in-app currency was purchased
     * or where multiple items and/or currencies were purchased.
     *
     * To create the rewards object, create an instance of SwrveIAPRewards and
     * use addItem() and addCurrency() to add the individual rewards
     *
     * @param productId     Unique product identifier for the item bought. This should match the
     *                      Swrve resource name. Required, cannot be empty.
     * @param productPrice  price of the product in real money. Note that this is the price
     *                      per product, not the total price of the transaction (when quantity greater than 1)
     *                      A typical value would be 0.99. Must be greater or equal to zero.
     * @param currency      real world currency used for this transaction. This must be an
     *                      ISO currency code. A typical value would be "USD".
     *                      Required, cannot be empty.
     * @param rewards       SwrveIAPRewards object containing any in-app currency and/or additional
     *                      items included in this purchase that need to be recorded.
     * @param dataSignature The purchase data received from Google Play. Required, cannot be empty.
     */
    public static void iapPlay(String productId, double productPrice, String currency, SwrveIAPRewards rewards, String purchaseData, String dataSignature) {
        checkInstanceCreated();
        ((ISwrve) instance).iapPlay(productId, productPrice, currency, rewards, purchaseData, dataSignature);
    }

    /**
     * Used internally from the GCM ID listener to get notified that a new token is available.
     */
    public static void onTokenRefreshed() {
        checkInstanceCreated();
        ((ISwrve) instance).onTokenRefreshed();
    }

    /**
     * @param intent The intent that opened the activity
     * @deprecated Swrve engaged events are automatically sent, so this is no longer needed.
     */
    @Deprecated
    public static void processIntent(Intent intent) {
        checkInstanceCreated();
        ((ISwrve) instance).processIntent(intent);
    }

    /**
     * Set the registration Id from external sources.
     *
     * @param registrationId The registration ID obtained from the GCM libs.
     */
    public static void setRegistrationId(String registrationId) {
        checkInstanceCreated();
        ((ISwrve) instance).setRegistrationId(registrationId);
    }

    /**
     * Called to send the push engaged event to Swrve.
     *
     * @param context android context
     * @param pushId  The push id for engagement
     */
    public static void sendPushEngagedEvent(Context context, String pushId) {
        checkInstanceCreated();
        SwrveEngageEventSender.sendPushEngagedEvent(context, pushId);
    }
}
