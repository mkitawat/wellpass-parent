package com.wellpass.core.utils;

import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

import com.wellpass.core.annotations.UnitTests;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 *
 */
@Category(UnitTests.class)
public class SecurityUtilsTest {

  @Test
  public void testSecurePassword() {
    byte[] salt = SecurityUtils.getSalt();
    String password = SecurityUtils.nextToken();

    byte[] hash1 = SecurityUtils.hashPassword(password.toCharArray(), salt);

    byte[] hash2 = SecurityUtils.hashPassword(password.toCharArray(), salt);

    assertArrayEquals(hash1, hash2);
  }

  @Test
  public void testFailedPassword() {
    byte[] salt = SecurityUtils.getSalt();
    String password = SecurityUtils.nextToken();
    String invalidPassword = SecurityUtils.nextToken();

    byte[] hash1 = SecurityUtils.hashPassword(password.toCharArray(), salt);

    byte[] hash2 = SecurityUtils.hashPassword(invalidPassword.toCharArray(), salt);

    assertThat(hash1, not(hash2));
  }
}
