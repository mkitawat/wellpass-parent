package com.wellpass.core.utils;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Security Related Utility Functions.
 *
 * https://www.cigital.com/blog/proper-use-of-javas-securerandom/
 */
public class SecurityUtils {

  private final static WPSecureRandom WP_SECURE_RANDOM = new WPSecureRandom();

  /**
   * From
   * http://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string
   *
   * Sufficient for now may need to revisit.
   */
  public static String nextToken() {
    return new BigInteger(130, WP_SECURE_RANDOM.getNonBlockingRandom()).toString(32);
  }

  /**
   * Salt uses blocking random
   */
  public static byte[] getSalt() {
    SecureRandom random = WP_SECURE_RANDOM.getNonBlockingRandom();
    byte[] salt = new byte[32];
    random.nextBytes(salt);
    return salt;
  }


  /**
   * @param password - Password to Hash
   * @param salt - Random Salt
   * @return Hashed Password in byte[]
   *
   * From https://www.owasp.org/index.php/Hashing_Java
   * @implNote iterations = 100000, keyLength = 256
   */
  public static byte[] hashPassword(final char[] password, final byte[] salt) {
    try {
      SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
      PBEKeySpec pks = new PBEKeySpec(password, salt, 1, 256);
      SecretKey key = skf.generateSecret(pks);
      byte[] res = key.getEncoded();
      return res;
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new RuntimeException(e);
    }
  }


  public static boolean verifyPassword(final String password, final byte[] salt,
                                       final byte[] storedHash) {
    byte[] passwordHash = hashPassword(password.toCharArray(), salt);
    return Arrays.equals(passwordHash, storedHash);
  }
}


