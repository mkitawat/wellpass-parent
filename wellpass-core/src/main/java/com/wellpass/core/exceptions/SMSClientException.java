package com.wellpass.core.exceptions;

public class SMSClientException extends Exception {
  public SMSClientException() {
  }

  public SMSClientException(String message) {
    super(message);
  }

  public SMSClientException(String message, Throwable cause) {
    super(message, cause);
  }

  public SMSClientException(Throwable cause) {
    super(cause);
  }

  public SMSClientException(String message, Throwable cause, boolean enableSuppression,
                            boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
