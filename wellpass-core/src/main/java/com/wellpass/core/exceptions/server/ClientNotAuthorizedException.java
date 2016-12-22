package com.wellpass.core.exceptions.server;

/**
 * ApiException for invalid inputs (excluding authentication issues and 404s).
 */
public class ClientNotAuthorizedException extends ApiException {

  public ClientNotAuthorizedException(String error) {
    super(
      403,
      error,
      null,
      "client_not_authorized",
      null
    );
  }
}
