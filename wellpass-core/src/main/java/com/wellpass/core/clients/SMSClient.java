package com.wellpass.core.clients;

import com.twilio.Twilio;
import com.twilio.exception.TwilioException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.wellpass.core.exceptions.SMSClientException;
import com.wellpass.core.utils.ModeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SMSClient {
  private static final Logger LOG = LoggerFactory.getLogger(SMSClient.class);
  private static final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
  private static final String AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");
  private static final String SERVICE_ID = System.getenv("TWILIO_SERVICE_ID");
  private static final String TEST_NUMBER = "+15005550006";

  public SMSClient() {
    Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
  }

  public void sendMessage(String to, String text) throws SMSClientException {
    try {
      if (ModeUtils.isLocal()) {
        Message.creator(new PhoneNumber(to), new PhoneNumber(TEST_NUMBER), text).create();
      } else {
        Message.creator(new PhoneNumber(to), SERVICE_ID, text).create();
      }
    } catch (TwilioException e) {
      LOG.error("sendVerificationCode: failed to deliver verification code: {}", e.getMessage(), e);
      throw new SMSClientException(e.getMessage(), e);
    }
  }
}
