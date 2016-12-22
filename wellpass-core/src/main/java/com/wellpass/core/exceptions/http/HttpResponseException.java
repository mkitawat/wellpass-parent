package com.wellpass.core.exceptions.http;

/**
 * Runtime Exception for Http Clients.
 *
 * This exception will typically be thrown on Non 2XX HTTP responses.
 */
public class HttpResponseException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  private int statusCode;
  private String reasonPhrase;
  private String messageBody;

  /**
   * Construct a new instance with the supplied statusCode and reasonPhrase.
   */
  public HttpResponseException(int statusCode, String reasonPhrase) {
    this(null, statusCode, reasonPhrase, null);
  }

  /**
   * Construct a new instance with the supplied statusCode, reasonPhrase and messageBody.
   * @param statusCode statusCode observed on http response.
   * @param reasonPhrase reasonPhrase observed on http response.
   * @param messageBody messageBody observed on http response (only if present and text).
   */
  public HttpResponseException(int statusCode, String reasonPhrase, String messageBody) {
    this(null, statusCode, reasonPhrase, messageBody);
  }

  /**
   * Construct a new instance with the supplied message, statusCode and reasonPhrase.
   * @param message customized message which can be saved or relayed to consumer of http client
   * library.
   * @param statusCode statusCode observed on http response.
   * @param reasonPhrase reasonPhrase observed on http response.
   */
  public HttpResponseException(String message, int statusCode, String reasonPhrase) {
    this(message, statusCode, reasonPhrase, null);
  }

  /**
   * Construct a new instance with the supplied message, statusCode, reasonPhrase and messageBody.
   * @param message customized message which can be saved or relayed to caller of http client. If
   * not provided, will be defaulted to reasonPhrase.
   * @param statusCode statusCode observed on http response.
   * @param reasonPhrase reasonPhrase observed on http response.
   * @param messageBody messageBody observed on http response (only if present and text).
   */
  public HttpResponseException(
    String message, int statusCode, String reasonPhrase, String messageBody) {
    super(message != null ? message : reasonPhrase);
    this.statusCode = statusCode;
    this.reasonPhrase = reasonPhrase;
    this.messageBody = messageBody;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getReasonPhrase() {
    return reasonPhrase;
  }

  public String getMessageBody() {
    return messageBody;
  }

}
