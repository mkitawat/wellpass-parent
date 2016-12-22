package com.wellpass.core.exceptions.server;

/**
 * ApiException for invalid inputs (excluding authentication issues and 404s).
 */
public class NotFoundException extends ApiException {

  public NotFoundException(String error) {
    super(
      404,
      error,
      null,
      "invalid_path",
      null
    );
  }
}
