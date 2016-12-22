package com.wellpass.core.clients;

import com.relayrides.pushy.apns.ApnsClient;
import com.relayrides.pushy.apns.ClientNotConnectedException;
import com.relayrides.pushy.apns.PushNotificationResponse;
import com.relayrides.pushy.apns.util.ApnsPayloadBuilder;
import com.relayrides.pushy.apns.util.SimpleApnsPushNotification;
import com.relayrides.pushy.apns.util.TokenUtil;
import com.wellpass.core.models.auth.Device;
import com.wellpass.core.utils.ModeUtils;
import com.wellpass.core.utils.gson.Jsonifier;
import io.netty.util.concurrent.Future;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PushNotificationClient {
  // note: this service expects the keys in `resources/APNS_keys/`
  // and to use the names `dev`, `stage`, and `prod`
  private static final String APNS_KEYS_PATH = "APNS_keys/%s.p12";
  private static final String APNS_PASSWORD = System.getenv("APNS_PASSWORD");
  private static final String MODE = ModeUtils.getMode();

  public class FcmData {
    public String message;
  }

  public class FcmMessage {
    public String to;
    public FcmData data;
  }

  private ApnsClient<SimpleApnsPushNotification> apnsClient;
  private static final Logger LOGGER = LoggerFactory.getLogger(PushNotificationClient.class);

  public PushNotificationClient() throws IOException, InterruptedException {
    if (ModeUtils.isLocal()) {
      LOGGER.info("NOT starting PushNotificationClient because we are in MODE=LOCAL");
      LOGGER.info("Any calls will result in a NOOP");
      return;
    }

    if (APNS_PASSWORD == null) {
      throw new NullPointerException("env var APNS_PASSWORD cannot be null");
    }

    String keyPath;
    if (ModeUtils.isTesting()) {
      keyPath = String.format(APNS_KEYS_PATH, ModeUtils.ENV_DEV);
    } else {
      keyPath = String.format(APNS_KEYS_PATH, ModeUtils.getMode());
    }

    URL url = getClass().getClassLoader().getResource(keyPath);
    if (url == null) {
      throw new IOException("APNS key file not found");
    }

    InputStream key = getClass().getClassLoader().getResourceAsStream(keyPath);
    apnsClient = new ApnsClient<>(key, APNS_PASSWORD);

    try {
      apnsClient.connect(ApnsClient.PRODUCTION_APNS_HOST).await();
    } catch (InterruptedException e) {
      LOGGER.error("Couldn't connect to APNS: {}", e.getMessage());
      throw e;
    }
  }

  public void notifyDevices(List<Device> devices, String message) {
    // NOOP for local environments
    if (ModeUtils.isLocal()) {
      return;
    }

    devices.forEach(device -> {
      try {
        if (device.apnsToken != null) {
          LOGGER.debug("APNS notification sent for device: {} ", device.toString());
          sendApns(device, message);
        } else if (device.androidId != null) {
          LOGGER.debug("FCM notification sent for device: {}", device.toString());
          sendFcm(device, message);
        } else {
          LOGGER.error("No valid sender id for device: {}", device.toString());
        }
      } catch (InterruptedException | IOException e) {
        LOGGER.error("{}: {}", e.getClass().getSimpleName(), e.getMessage());
      }
    });
  }

  void postFcmMessage(FcmMessage message) throws IOException {
    HttpPost request = new HttpPost("https://fcm.googleapis.com/fcm/send");
    StringEntity se = new StringEntity(Jsonifier.toJson(message));
    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
    request.setEntity(se);
    request.setHeader("Authorization", "key=" + System.getenv("FIREBASE_API_KEY"));

    CloseableHttpResponse response = HttpClients.createDefault().execute(request);
    response.close();

    int statusCode = response.getStatusLine().getStatusCode();
    if (!(statusCode >= 200 && statusCode < 300)) {
      String body = EntityUtils.toString(response.getEntity(), "UTF-8");
      throw new IOException(response.getStatusLine().getStatusCode() + " " + body);
    }
  }

  void sendFcm(Device device, String message) throws IOException {
    FcmMessage fcmMessage = new FcmMessage();
    fcmMessage.to = device.androidId;
    fcmMessage.data = new FcmData();
    fcmMessage.data.message = message;

    postFcmMessage(fcmMessage);
  }

  void sendApns(Device device, String message) throws InterruptedException {
    final ApnsPayloadBuilder payloadBuilder = new ApnsPayloadBuilder();
    payloadBuilder.setAlertBody("You have a new message");

    final String payload = payloadBuilder.buildWithDefaultMaximumLength();
    final String token = TokenUtil.sanitizeTokenString(device.apnsToken);

    String topic = null;
    switch (MODE) {
      case ModeUtils.ENV_TESTING:
      case ModeUtils.ENV_DEV:
        topic = "com.wellpass.dev";
        break;
      case ModeUtils.ENV_STAGE:
        topic = "com.wellpass.staging";
        break;
      case ModeUtils.ENV_PROD:
        topic = "com.wellpass";
        break;
    }

    SimpleApnsPushNotification pushNotification =
      new SimpleApnsPushNotification(token, topic, payload);

    final Future<PushNotificationResponse<SimpleApnsPushNotification>>
      sendNotificationFuture =
      apnsClient.sendNotification(pushNotification);

    try {
      final PushNotificationResponse<SimpleApnsPushNotification>
        pushNotificationResponse =
        sendNotificationFuture.get();

      if (pushNotificationResponse.isAccepted()) {
        LOGGER.debug("Push notitification accepted by APNs gateway.");
      } else {
        LOGGER.error("Notification rejected by the APNs gateway: {}",
          pushNotificationResponse.getRejectionReason());

        // TODO: Delete rejected devices
        if (pushNotificationResponse.getTokenInvalidationTimestamp() != null) {
          LOGGER.error("\t...and the token is invalid as of {}",
            pushNotificationResponse.getTokenInvalidationTimestamp());
        }
      }
    } catch (final ExecutionException e) {
      LOGGER.error("{}: {}", e.getClass().getSimpleName(), e.getMessage());

      if (e.getCause() instanceof ClientNotConnectedException) {
        LOGGER.debug("Waiting for client to reconnect...");
        apnsClient.getReconnectionFuture().await();
        LOGGER.debug("Reconnected.");
      }
    }
  }
}
