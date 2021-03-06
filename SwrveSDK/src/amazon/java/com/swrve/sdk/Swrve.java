package com.swrve.sdk;

import android.app.Activity;
import android.content.Context;

import com.amazon.device.messaging.ADM;
import com.swrve.sdk.config.SwrveConfig;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Main implementation of the Amazon Swrve SDK.
 */
public class Swrve extends SwrveBase<ISwrve, SwrveConfig> implements ISwrve {
    protected static final String FLAVOUR_NAME = "amazon";
    protected static final String SWRVE_ADM_TOKEN = "swrve.adm_token";

    protected String registrationId;

    protected Swrve(Context context, int appId, String apiKey, SwrveConfig config) {
        super(context, appId, apiKey, config);
        SwrvePushSDK.createInstance(context)
                .setDefaultNotificationChannel(config.getDefaultNotificationChannel());
    }

    @Override
    protected void _onResume(Activity ctx) {
        super._onResume(ctx);

        // Detect if user is influenced by a push notification
        SwrvePushSDK pushSDK = SwrvePushSDK.getInstance();
        if (pushSDK != null) {
            pushSDK.processInfluenceData(this);
        }
    }

    //ADM callbacks
    @Override
    public void onRegistrationIdReceived(String registrationId) {
        if (!SwrveHelper.isNullOrEmpty(registrationId)) {
            setRegistrationId(registrationId);
        }
    }

    @Override
    protected void beforeSendDeviceInfo(final Context context) {
        //Check for existence of ADM messaging class.
        try {
            Class.forName( "com.amazon.device.messaging.ADM" );
        } catch (ClassNotFoundException e) {
            SwrveLogger.e("ADM message class not found.", e);
            return;
        }

        try {
            final ADM adm = new ADM(context);
            String newRegistrationId = adm.getRegistrationId();
            if (SwrveHelper.isNullOrEmpty(newRegistrationId)) {
                SwrveLogger.i("adm.getRegistrationId() returned null. Will call adm.startRegister().");
                adm.startRegister();
            } else {
                SwrveLogger.i("adm.getRegistrationId() returned: " + newRegistrationId);
                registrationId = newRegistrationId;
            }
        } catch (Exception exp) {
            SwrveLogger.e("Exception when trying to obtain the registration key for the device.", exp);
        }
    }

    @Override
    protected void extraDeviceInfo(JSONObject deviceInfo) throws JSONException {
        if (!SwrveHelper.isNullOrEmpty(registrationId)) {
            deviceInfo.put(SWRVE_ADM_TOKEN, registrationId);
        }
    }

    private void setRegistrationId(String regId) {
        try {
            if (registrationId == null || !registrationId.equals(regId)) {
                registrationId = regId;
                if (qaUser != null) {
                    qaUser.logDeviceInfo(getDeviceInfo());
                }

                // Re-send data now
                queueDeviceInfoNow(true);
            }
        } catch (Exception ex) {
            SwrveLogger.e("Couldn't save the ADM registration id for the device", ex);
        }
    }
}
