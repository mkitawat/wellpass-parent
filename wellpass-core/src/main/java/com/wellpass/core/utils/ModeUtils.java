package com.wellpass.core.utils;

/**
 * Helper for dev vs. prod.
 */
public class ModeUtils {
  public static String CURRENT_ENV = null;
  public static final String ENV_LOCAL = "local";
  public static final String ENV_DEV = "dev";
  public static final String ENV_STAGE = "stage";
  public static final String ENV_PROD = "prod";
  public static final String ENV_TESTING = "testing";
  private static final String MODE_KEY = "MODE";

  public static boolean isLocal() {
    return ENV_LOCAL.equalsIgnoreCase(getMode());
  }

  public static boolean isProd() {
    return ENV_PROD.equalsIgnoreCase(getMode());
  }

  public static String getMode() {
    if (CURRENT_ENV == null) {
      String sysEnv = System.getenv(MODE_KEY);
      CURRENT_ENV = (sysEnv == null || sysEnv.isEmpty()) ? ENV_TESTING : System.getenv(MODE_KEY);
    }
    return CURRENT_ENV;
  }

  public static boolean isTesting() {
    return ENV_TESTING.equalsIgnoreCase(getMode());
  }

  public static boolean isDev() {
    return ENV_DEV.equalsIgnoreCase(getMode());
  }

  public static boolean isStaging() {
    return ENV_STAGE.equalsIgnoreCase(getMode());
  }
}
