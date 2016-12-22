package com.wellpass.core.exceptions;

public class WPNotFoundException extends RuntimeException {
  public WPNotFoundException(String message) {
    super(message);
  }

  public WPNotFoundException(String message, Exception ex) {
    super(message, ex);
  }
}
