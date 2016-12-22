package com.wellpass.core.models.pokitdok;

import com.wellpass.core.exceptions.PokitDokException;

/**
 * PokitDok Access Token class.
 */
public class PokitAccessToken {
  private String accessToken;
  private String tokenType;
  private long expires;
  private long expiresIn;

  public PokitAccessToken() {
  }

  public PokitAccessToken(
    String accessToken,
    String tokenType,
    long expires,
    long expiresIn) {

    this.accessToken = accessToken;
    this.tokenType = tokenType;
    this.expires = expires;
    this.expiresIn = expiresIn;
  }

  public static PokitAccessToken fromJson(String jsonResponse) throws PokitDokException {
    try {
      return PokitJsonifier.getInstance().fromJson(jsonResponse, PokitAccessToken.class);
    } catch (Exception ex) {
      throw new PokitDokException("Error parsing accessToken jsonResponse", ex);
    }
  }

  public String getAccessToken() {
    return this.accessToken;
  }

  public String getTokenType() {
    return tokenType;
  }

  public long getExpires() {
    return expires;
  }

  public long getExpiresIn() {
    return expiresIn;
  }
}
