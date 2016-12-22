package com.wellpass.core.exceptions;

public class FileUploadException extends RuntimeException {
  public FileUploadException(String message) {
    super(message);
  }

  public FileUploadException(String message, Exception ex) {
    super(message, ex);
  }
}
