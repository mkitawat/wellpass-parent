package com.wellpass.core.exceptions;

public class UrlSigningException extends RuntimeException {
  public UrlSigningException(String message) {
    super(message);
  }

  public UrlSigningException(String message, Exception ex) {
    super(message, ex);
  }
}
