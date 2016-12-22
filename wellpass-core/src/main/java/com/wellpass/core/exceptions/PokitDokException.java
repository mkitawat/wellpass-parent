package com.wellpass.core.exceptions;

/**
 * Exception for any PokitDok API violation
 */
public class PokitDokException extends Exception {
  private static final long serialVersionUID = 1L;

  public PokitDokException() {
  }

  public PokitDokException(Throwable t) {
    super(t);
  }

  public PokitDokException(String message) {
    super(message);
  }

  public PokitDokException(String message, Throwable t) {
    super(message, t);
  }

}
