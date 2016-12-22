package com.wellpass.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for SecureUtils to provide SecureRandom instances.
 * Will reset the blocking instances periodically.
 */
public class WPSecureRandom {
  private static final Logger LOGGER = LoggerFactory.getLogger(WPSecureRandom.class);
  private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private SecureRandom nonBlockingRandom = new SecureRandom();
  private SecureRandom blockingRandom;

  /**
   * Use blocking random when available, fall back to nonblocking.
   * Reset every 24 hours, unclear what best practice is for this.
   */
  public SecureRandom getRandom() {
    try {
      if (blockingRandom == null) {
        blockingRandom = SecureRandom.getInstanceStrong();

        scheduler.scheduleWithFixedDelay(() -> {
          try {
            blockingRandom = SecureRandom.getInstanceStrong();
          } catch (NoSuchAlgorithmException e) {
            LOGGER.error("got exception: {}", e.getMessage(), e);
            blockingRandom = nonBlockingRandom;
          }
        }, 0, 24, TimeUnit.HOURS);
      }
    } catch (NoSuchAlgorithmException e) {
      LOGGER.error("got exception: {}", e.getMessage(), e);
      blockingRandom = nonBlockingRandom;
    }
    return Optional.of(blockingRandom).orElse(nonBlockingRandom);
  }

  public SecureRandom getNonBlockingRandom() {
    return nonBlockingRandom;
  }

}
