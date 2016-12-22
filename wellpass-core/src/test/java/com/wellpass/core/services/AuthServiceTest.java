package com.wellpass.core.services;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.wellpass.core.annotations.UnitTests;
import com.wellpass.core.daos.DeviceDAO;
import com.wellpass.core.daos.OAuthClientDAO;
import com.wellpass.core.daos.OAuthSystemClientDAO;
import com.wellpass.core.daos.OAuthTokenDAO;
import com.wellpass.core.daos.UserDAO;
import com.wellpass.core.models.auth.OAuthSystemClient;
import com.wellpass.core.utils.SecurityUtils;
import junit.framework.Assert;
import org.bson.types.ObjectId;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mongodb.morphia.Datastore;

import java.io.IOException;

@Category(UnitTests.class)
public class AuthServiceTest {

  private static AuthService authService;

  private static Datastore datastore;
  private static UserService userService;
  private static OAuthSystemClientDAO oAuthSystemClientDAO;
  private static OAuthTokenDAO oAuthTokenDAO;
  private static OAuthClientDAO oAuthClientDAO;
  private static UserDAO userDAO;
  private static DeviceDAO deviceDAO;

  @BeforeClass
  public static void setUp() throws IOException {
    datastore = mock(Datastore.class);
    userService = mock(UserService.class);
    oAuthTokenDAO = mock(OAuthTokenDAO.class);
    oAuthClientDAO = mock(OAuthClientDAO.class);
    userDAO = mock(UserDAO.class);
    deviceDAO = mock(DeviceDAO.class);
    oAuthSystemClientDAO = mock(OAuthSystemClientDAO.class);

    authService =
      new AuthService(userService, oAuthTokenDAO, oAuthClientDAO, userDAO, oAuthSystemClientDAO,
        deviceDAO);
  }

  @Test
  public void testVerifySystemClientAuthSuccess() throws Exception {
    // Test success
    OAuthSystemClient client = new OAuthSystemClient();
    client.id = new ObjectId();
    client.name = OAuthSystemClient.SENSEHEALTH;
    client.clientSecret = SecurityUtils.nextToken();

    // mock req obj
    when(oAuthSystemClientDAO.findOneById(client.id.toHexString())).thenReturn(client);
    Assert.assertTrue(
      authService.verifySystemClientAuth(String.valueOf(client.id), client.clientSecret));
  }

  @Test
  public void testVerifySystemClientAuthFail() throws Exception {
    // Test fail
    OAuthSystemClient client = new OAuthSystemClient();
    client.id = new ObjectId();
    client.name = OAuthSystemClient.SENSEHEALTH;
    client.clientSecret = SecurityUtils.nextToken();

    when(oAuthSystemClientDAO.findOneById(client.id.toHexString())).thenReturn(client);
    Assert.assertFalse(
      authService.verifySystemClientAuth(String.valueOf(client.id), "not the right secret"));

    client.name = "wrong name";
    Assert.assertFalse(
      authService.verifySystemClientAuth(String.valueOf(client.id), client.clientSecret));
  }
}
