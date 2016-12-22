package com.wellpass.core.models.pokitdok;

/**
 * PokitDok Base Request. Requests for each PokitDok endpoint should sub-class this.
 */
public abstract class PokitBaseRequest {

  /**
   * Subclass should return the corresponding PokitDok target path for the associated
   * endpoint. Returned path should be relative path corresponding to PokitDok prefix
   * https://platform.pokitdok.com/api/v4/.
   * It should not begin with forward slash character '/'.
   */
  public abstract String getTargetPath();

  /**
   * Subclass can throw IllegalArgumentException on invalid data.
   * This method is called by PokiDok client before dispatching the request.
   */
  public void validate() throws IllegalArgumentException {

  }

  public String toJson() {
    return PokitJsonifier.getInstance().toJson(this);
  }
}
