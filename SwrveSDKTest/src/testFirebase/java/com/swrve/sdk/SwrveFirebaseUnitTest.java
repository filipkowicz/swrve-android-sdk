package com.swrve.sdk;

import android.content.Intent;
import android.os.Bundle;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SwrveFirebaseUnitTest extends SwrveBaseTest {

    private Swrve swrveSpy;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        Swrve swrveReal = (Swrve) SwrveSDK.createInstance(mActivity, 1, "apiKey");
        swrveSpy = Mockito.spy(swrveReal);
        swrveSpy.init(mActivity);
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        swrveSpy.shutdown();
        SwrveTestUtils.removeSwrveSDKSingletonInstance();
    }

    @Test
    public void testSetRegistrationId() throws Exception {
        Mockito.verify(swrveSpy, Mockito.atMost(1)).queueDeviceInfoNow(Mockito.anyBoolean()); // device info queued once upon init

        swrveSpy.setRegistrationId("reg1");
        assertEquals("reg1", swrveSpy.getRegistrationId());
        Mockito.verify(swrveSpy, Mockito.atMost(2)).queueDeviceInfoNow(Mockito.anyBoolean()); // device info queued again so atMost== 2

        swrveSpy.setRegistrationId("reg1");
        assertEquals("reg1", swrveSpy.getRegistrationId());
        Mockito.verify(swrveSpy, Mockito.atMost(2)).queueDeviceInfoNow(Mockito.anyBoolean()); // device info NOT queued again because the regId has NOT changed so remains atMost== 2

        swrveSpy.setRegistrationId("reg2");
        assertEquals("reg2", swrveSpy.getRegistrationId());
        Mockito.verify(swrveSpy, Mockito.atMost(3)).queueDeviceInfoNow(Mockito.anyBoolean()); // device info queued again because the regId has changed so atMost== 3
    }

    @Test
    public void testPushListener() throws Exception {
        TestPushListener pushListener = new TestPushListener();

        Intent intent = new Intent();
        Bundle extras = new Bundle();
        extras.putString("text", "validBundle");
        extras.putString(SwrvePushConstants.SWRVE_TRACKING_KEY, "1234");
        intent.putExtra(SwrvePushConstants.PUSH_BUNDLE, extras);

        SwrvePushEngageReceiver pushEngageReceiver = new SwrvePushEngageReceiver();
        pushEngageReceiver.onReceive(RuntimeEnvironment.application.getApplicationContext(), intent);

        assertTrue(pushListener.pushEngaged == false);
        assertTrue(pushListener.receivedBundle == null);

        //Set listener and generate another message
        SwrveSDK.setPushNotificationListener(pushListener);

        pushEngageReceiver.onReceive(RuntimeEnvironment.application.getApplicationContext(), intent);

        //Expect bundle to have been received
        assertTrue(pushListener.pushEngaged == true);
        assertTrue(pushListener.receivedBundle != null);
    }

    class TestPushListener implements com.swrve.sdk.ISwrvePushNotificationListener {
        public boolean pushEngaged = false;
        public Bundle receivedBundle = null;

        @Override
        public void onPushNotification(Bundle bundle) {
            pushEngaged = true;
            receivedBundle = bundle;
        }
    }

}
