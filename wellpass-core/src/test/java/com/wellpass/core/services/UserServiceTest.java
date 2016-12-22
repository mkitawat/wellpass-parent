package com.wellpass.core.services;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wellpass.core.annotations.UnitTests;
import com.wellpass.core.daos.OAuthSystemClientDAO;
import com.wellpass.core.daos.UserDAO;
import com.wellpass.core.exceptions.SMSClientException;
import com.wellpass.core.models.auth.OAuthSystemClient;
import com.wellpass.core.models.auth.Person;
import com.wellpass.core.models.auth.User;
import junit.framework.Assert;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(UnitTests.class)
public class UserServiceTest {
  private com.wellpass.core.clients.SMSClient SMSClient;
  private com.wellpass.core.clients.WPPortalClient WPPortalClient;
  private UserService userService;
  private UserDAO userDAO;
  private OAuthSystemClientDAO oAuthSystemClientDAO;

  @Before
  public void setup() {
    this.SMSClient = mock(com.wellpass.core.clients.SMSClient.class);
    this.WPPortalClient = mock(com.wellpass.core.clients.WPPortalClient.class);
    this.userDAO = mock(UserDAO.class);
    this.oAuthSystemClientDAO = mock(OAuthSystemClientDAO.class);
    this.userService =
      spy(new UserService(SMSClient, WPPortalClient, userDAO, oAuthSystemClientDAO));
  }

  @Test
  public void testSendVerificationCode() throws SMSClientException {
    User user = new User();
    Person person = new Person();
    person.mobile = "12223423443";
    user.person = person;

    String msg = "Your code is: 12345 Enter it in the verification screen!";

    doReturn(12345).when(userService).generateCode();
    doNothing().when(SMSClient).sendMessage(person.mobile, msg);

    userService.sendAndPersistVerificationCode(user);

    verify(SMSClient).sendMessage(person.mobile, msg);
    verify(userDAO).save(user);
    Assert.assertEquals(user.verificationCode, "12345");
  }

  @Test
  public void testNotifyUserVerified() throws SMSClientException {
    User user = new User();
    Person person = new Person();
    person.id = new ObjectId();
    user.person = person;

    OAuthSystemClient client = new OAuthSystemClient();
    when(oAuthSystemClientDAO.getWellpassPortalSystemClient()).thenReturn(client);

    // verified : True
    try {
      doNothing().when(WPPortalClient)
        .notifyUserVerified(any(OAuthSystemClient.class), any(User.class));
      boolean isVerified = userService.notifyUserVerified(user);
      Assert.assertTrue(isVerified);
      verify(WPPortalClient).notifyUserVerified(any(OAuthSystemClient.class), eq(user));
    } catch (IOException ex) {
      fail();
    }

    reset(WPPortalClient);

    // verified : False
    try {
      doThrow(new IOException("api failure")).when(WPPortalClient)
        .notifyUserVerified(any(OAuthSystemClient.class), any(User.class));
      boolean isVerified = userService.notifyUserVerified(user);
      Assert.assertFalse(isVerified);
      verify(WPPortalClient).notifyUserVerified(any(OAuthSystemClient.class), eq(user));
    } catch (IOException ex) {
      fail();
    }
  }
}
