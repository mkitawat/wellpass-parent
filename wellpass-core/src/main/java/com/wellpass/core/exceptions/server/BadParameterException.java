package com.wellpass.core.exceptions.server;


/**
 * ApiException for invalid inputs (excluding authentication issues and 404s).
 */
public class BadParameterException extends ApiException {

  /**
   * @param parameterName Name of parameter that was the source of the issue
   * @param error What was wrong, e.g. "is required but missing"
   * @param displayMessage Optional message intended for display to userm
   */
  public BadParameterException(String parameterName, String error, String displayMessage) {
    super(
      400,
      String.format("Parameter '%s' %s", parameterName, error),
      displayMessage,
      "bad_param",
      null
    );
  }

  public static BadParameterException requiredMissing(String parameterName) {
    return new BadParameterException(parameterName, "is required but missing or empty", null);
  }

  public static BadParameterException invalid(String parameterName) {
    return new BadParameterException(parameterName, "has an invalid value", null);
  }
}
