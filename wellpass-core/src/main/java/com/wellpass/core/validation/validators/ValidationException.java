package com.wellpass.core.validation.validators;

public class ValidationException extends Exception {
  private static final long serialVersionUID = 1L;

  public static final String ERR_CODE_MISSING = "MISSING";
  public static final String ERR_CODE_INVALID_VALUE = "INVALID_VALUE";
  public static final String ERR_CODE_INVALID_FORMAT = "INVALID_FORMAT";
  public static final String ERR_CODE_INVALID_NUMBER = "INVALID_NUMBER";
  public static final String ERR_CODE_PARSING = "PARSING_ERROR";

  private String errorCode;

  public ValidationException(String errorCode) {
    this.errorCode = errorCode;
  }

  public ValidationException(String errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  public ValidationException(String errorCode, String message, Throwable t) {
    super(message, t);
    this.errorCode = errorCode;
  }

  public ValidationException(String errorCode, Throwable t) {
    super(t);
    this.errorCode = errorCode;
  }

  public String getErrorCode() {
    return errorCode;
  }

}
