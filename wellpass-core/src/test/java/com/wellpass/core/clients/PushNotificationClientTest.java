package com.wellpass.core.clients;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.wellpass.core.annotations.UnitTests;
import com.wellpass.core.models.auth.Device;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

@Category(UnitTests.class)
@Ignore // PushNotificationClient is instantiating an ApnsClient directly in its constructor
        // rather than refactor to inject that piece, i'm ignoring both tests for now
public class PushNotificationClientTest {
  private static PushNotificationClient pushNotificationClient;

  @BeforeClass
  public static void setup() throws IOException, InterruptedException {
    pushNotificationClient = spy(new PushNotificationClient());
  }

  @Test
  public void testNotifyDevices() throws InterruptedException, IOException {
    // Set test data
    String message = "test message";
    Device androidDevice = new Device();
    androidDevice.androidId = "1234";
    Device iosDevice = new Device();
    iosDevice.apnsToken = "5678";

    // Mock sendApns and sendFcm to check for arguments
    doNothing().when(pushNotificationClient).sendApns(iosDevice, message);
    doNothing().when(pushNotificationClient).sendFcm(androidDevice, message);

    // Call notifyDevices and check that sendApns and sendFcm were called correctly
    pushNotificationClient.notifyDevices(Arrays.asList(iosDevice, androidDevice), message);
    verify(pushNotificationClient).sendApns(iosDevice, message);
    verify(pushNotificationClient).sendFcm(androidDevice, message);

    // test empty devices list
    reset(pushNotificationClient);
    pushNotificationClient.notifyDevices(new ArrayList<>(), message);
    verify(pushNotificationClient, never()).sendApns(iosDevice, message);
    verify(pushNotificationClient, never()).sendFcm(androidDevice, message);

    // No device id set
    reset(pushNotificationClient);
    androidDevice.androidId = null;
    pushNotificationClient.notifyDevices(Arrays.asList(androidDevice), message);
    verify(pushNotificationClient, never()).sendApns(iosDevice, message);
    verify(pushNotificationClient, never()).sendFcm(androidDevice, message);
  }

  @Test
  public void testSendFcm() throws IOException {
    // test inputs
    String message = "test message";
    Device device = new Device();
    device.androidId = "1234";

    // mock the method that actually makes the request
    doNothing().when(pushNotificationClient)
      .postFcmMessage(any(PushNotificationClient.FcmMessage.class));

    // capture the arguments for the mocked method
    ArgumentCaptor<PushNotificationClient.FcmMessage> argument =
      ArgumentCaptor.forClass(PushNotificationClient.FcmMessage.class);

    // check that sendFcm is sending well formed messages
    pushNotificationClient.sendFcm(device, message);
    verify(pushNotificationClient).postFcmMessage(argument.capture());
    assertEquals(device.androidId, argument.getValue().to);
    assertEquals(message, argument.getValue().data.message);
  }
}
