package com.swrve.sdk;


import android.content.Context;

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
        } else if (SwrveHelper.isNullOrEmpty(apiKey)) {
            SwrveHelper.logAndThrowException("Api key not specified");
        }

        if (!SwrveHelper.sdkAvailable()) {
            return new SwrveEmpty(context, apiKey);
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
