package com.wellpass.core.exceptions.server;

/**
 * Exception intended to be serialized into an API response.
 */
public class ApiException extends RuntimeException {
  public final int responseCode;
  public final String displayMessage;
  public final String errorType;

  public ApiException(
    int responseCode,
    String message,
    String displayMessage,
    String errorType,
    Exception exception) {
    super(message, exception);
    this.responseCode = responseCode;
    this.displayMessage = displayMessage;
    this.errorType = errorType;
  }

  public ApiException(int responseCode, String detail) {
    this(responseCode, detail, null, null, null);
  }

}
