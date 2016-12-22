package com.wellpass.core.exceptions.http;

/**
 * A runtime exception indicating http authorization failure with a default HTTP status code of 401.
 */
public class HttpNotAuthorizedException extends HttpResponseException {
  private static final long serialVersionUID = 1L;

  public HttpNotAuthorizedException() {
    super(null, 401, "Unauthorized", null);
  }

  public HttpNotAuthorizedException(String message) {
    super(message, 401, "Unauthorized", null);
  }

  public HttpNotAuthorizedException(String message, String messageBody) {
    super(message, 401, "Unauthorized", messageBody);
  }

  public HttpNotAuthorizedException(String message, String reasonPhrase, String messageBody) {
    super(message, 401, reasonPhrase != null ? reasonPhrase : "Unauthorized", messageBody);
  }

}
