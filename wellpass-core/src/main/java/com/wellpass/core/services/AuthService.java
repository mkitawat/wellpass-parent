package com.wellpass.core.services;

import com.wellpass.core.daos.DeviceDAO;
import com.wellpass.core.daos.OAuthClientDAO;
import com.wellpass.core.daos.OAuthSystemClientDAO;
import com.wellpass.core.daos.OAuthTokenDAO;
import com.wellpass.core.daos.UserDAO;
import com.wellpass.core.exceptions.WPNotFoundException;
import com.wellpass.core.models.auth.Device;
import com.wellpass.core.models.auth.OAuthClient;
import com.wellpass.core.models.auth.OAuthSystemClient;
import com.wellpass.core.models.auth.OAuthToken;
import com.wellpass.core.models.auth.User;
import com.wellpass.core.models.wellbox.DisplayClient;
import com.wellpass.core.utils.SecurityUtils;
import com.wellpass.core.utils.gson.Jsonifier;
import com.wellpass.core.validation.validators.PasswordValidator;
import com.wellpass.core.validation.validators.ValidationException;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthService {
  private static final Logger LOG = LoggerFactory.getLogger(AuthService.class);
  private final UserService userService;
  private final OAuthTokenDAO oAuthTokenDAO;
  private final OAuthClientDAO oAuthClientDAO;
  private final UserDAO userDAO;
  private final OAuthSystemClientDAO oAuthSystemClientDAO;
  private final DeviceDAO deviceDAO;

  public AuthService(UserService userService, OAuthTokenDAO oAuthTokenDAO,
                     OAuthClientDAO oAuthClientDAO, UserDAO userDAO,
                     OAuthSystemClientDAO oAuthSystemClientDAO, DeviceDAO deviceDAO) {
    this.userService = userService;
    this.oAuthTokenDAO = oAuthTokenDAO;
    this.oAuthClientDAO = oAuthClientDAO;
    this.userDAO = userDAO;
    this.oAuthSystemClientDAO = oAuthSystemClientDAO;
    this.deviceDAO = deviceDAO;

    ensureConsumers(); // todo justin - added 12/20/2016 as a hack to fix weird bugs
  }

  public void logoutUser(OAuthToken token) {
    if (token != null) {
      token.isExpired = true;
      oAuthTokenDAO.save(token);
    }
  }

  public OAuthClient clientFromId(ObjectId id) {
    return oAuthClientDAO.findOneById(id.toHexString());
  }

  public boolean isClientOfficial(OAuthClient client) {
    System.out.println(Jsonifier.toJson(client));
    return client != null &&
      ((client.id.equals(OAuthClient.IPHONE)) ||
        (client.id.equals(OAuthClient.ANDROID)) ||
        (client.id.equals(OAuthClient.WEB)));
  }

  public OAuthToken getOrCreateToken(User user, OAuthClient client) {
    OAuthToken token = existingToken(user, client);
    if (token == null) {
      token = new OAuthToken();
      token.id = SecurityUtils.nextToken();
      token.clientId = client.id;
      token.userId = user.id;
      oAuthTokenDAO.save(token);
    }
    return token;
  }

  public Map<String, String> changePassword(OAuthToken token, String currentPassword,
                                            String newPassword) {
    Map<String, String> errors = new HashMap<>();
    User user = token.user;
    // User is found, check passwords
    if (SecurityUtils.verifyPassword(currentPassword, user.salt, user.password)) {
      // Current password is valid, check new one
      try {
        PasswordValidator.regexValidate(newPassword);
        // New password checks out, save!
        user.password = SecurityUtils.hashPassword(newPassword.toCharArray(), user.salt);
        userDAO.save(user);

        return null;
      } catch (ValidationException e) {
        errors.put("newPassword", e.getMessage());
      }
    } else {
      errors.put("currentPassword", "Invalid password.");
    }
    return errors;
  }

  // todo justin - is this query right?
  public void updateApnsToken(User user, String apnsToken) {
    Device device = deviceDAO.findOne("apnsToken", apnsToken);
    device.userId = user.id;
    deviceDAO.save(device);
  }

  // todo justin - is this query right?
  public void updateAndroidId(User user, String id) {
    Device device = deviceDAO.findOne("androidId", id);
    device.userId = user.id;
    deviceDAO.save(device);
  }

  public void saveClient(OAuthClient client) {
    oAuthClientDAO.save(client);
  }

  public OAuthToken existingToken(User user, OAuthClient client) {
    return oAuthTokenDAO.getExistingUserToken(user, client);
  }

  // todo justin - this seems to not belong
  public List<DisplayClient> galleryClients() {
    return oAuthClientDAO.getGalleryClientsAsList();
  }

  public OAuthToken tokenForUser(String id, User user) {
    OAuthToken token = oAuthTokenDAO.findOne("id", id);
    if (!user.id.equals(token.userId)) {
      return null;
    }
    return token;
  }

  public OAuthToken tokenFromRequest(String token, String authHeader) {
    // Use of token authorization type is copied from github's API, unclear if this is a standard
    if (token == null && authHeader != null && authHeader.startsWith("token")) {
      token = authHeader.substring("token ".length());
    }
    return this.populatedToken(token);
  }

  // Right now Sensehealth is the only system client
  public boolean verifySystemClientAuth(String clientId, String secret) {
    if (secret != null) {
      OAuthSystemClient oAuthSystemClient = oAuthSystemClientDAO.findOneById(clientId);
      return oAuthSystemClient != null && OAuthSystemClient.SENSEHEALTH
        .equals(oAuthSystemClient.name) && secret.equals(oAuthSystemClient.clientSecret);
    }
    return false;
  }

  public OAuthToken populatedToken(String tokenStr) {
    // todo justin - eventually remove this entire try/catch
    try {
      if (StringUtils.isEmpty(tokenStr)) {
        return null;
      }
      // TODO: Return client for logging and permissioning based on client
      OAuthToken token = oAuthTokenDAO.findOne("id", tokenStr);
      if (token == null || token.isExpired) {
        return null;
      }
      token.user = userService.findById(token.userId);
      if (token.clientId != null) {
        token.client = oAuthClientDAO.findOneById(token.clientId.toHexString());
      }
      return token;
    } catch (WPNotFoundException e) {
      LOG.debug("Couldn't find token with tokenStr: {}", tokenStr);
      return null; // don't rethrow - justin
    }
  }

  public void saveToken(OAuthToken token) {
    oAuthTokenDAO.save(token);
  }

  public void ensureConsumers() {
    makeConsumer(OAuthClient.IPHONE, "Wellpass iOS", true, null);
    makeConsumer(OAuthClient.ANDROID, "Wellpass Android", true, null);
    makeConsumer(OAuthClient.WEB, "Wellpass Web", true, null);
    makeConsumer(OAuthClient.T4B, "Text4Baby", false,
      "https://www.text4baby.org/Images/en/t4b-logo.png");
  }

  private void makeConsumer(ObjectId id, String name, boolean isOfficial, String profilePhoto) {
    OAuthClient client = new OAuthClient();
    client.id = id;
    client.name = name;
    client.isOfficial = isOfficial;
    client.profilePhoto = profilePhoto;
    client.webhookSecret = SecurityUtils.nextToken();
    client.clientSecret = SecurityUtils.nextToken();
    saveClient(client);
  }
}
