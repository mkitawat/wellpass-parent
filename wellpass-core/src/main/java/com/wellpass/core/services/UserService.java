package com.wellpass.core.services;

import com.wellpass.core.clients.SMSClient;
import com.wellpass.core.clients.WPPortalClient;
import com.wellpass.core.daos.OAuthSystemClientDAO;
import com.wellpass.core.daos.UserDAO;
import com.wellpass.core.exceptions.SMSClientException;
import com.wellpass.core.models.auth.User;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.SecureRandom;

public class UserService {
  private final static Logger LOG = LoggerFactory.getLogger(UserService.class);
  private final static SecureRandom random = new SecureRandom();
  private final SMSClient smsClient;
  private final WPPortalClient wpPortalClient;
  private final UserDAO userDAO;
  private final OAuthSystemClientDAO oAuthSystemClientDAO;


  public UserService(SMSClient smsClient, WPPortalClient wpPortalClient, UserDAO userDAO,
                     OAuthSystemClientDAO oAuthSystemClientDAO) {
    this.smsClient = smsClient;
    this.wpPortalClient = wpPortalClient;
    this.userDAO = userDAO;
    this.oAuthSystemClientDAO = oAuthSystemClientDAO;
  }

  public User findByEmail(String email) {
    String lowercaseEmail = email.toLowerCase();
    return userDAO.findOneByEmail(lowercaseEmail);
  }

  public User findByPerson(ObjectId personId) {
    return userDAO.findOne("person", personId.toHexString());
  }

  public User findById(ObjectId id) {
    return userDAO.findOneById(id.toHexString());
  }

  public String sendAndPersistVerificationCode(User user) {
    String verificationCode = generateCode().toString();
    String msg =
      String.format("Your code is: %s Enter it in the verification screen!", verificationCode);

    try {
      smsClient.sendMessage(user.person.mobile, msg);
      user.verificationCode = verificationCode;
      userDAO.save(user);
    } catch (SMSClientException e) {
      LOG.error("sendVerificationCode: failed to deliver verification code: {}", e.getMessage());
    }

    return verificationCode;
  }

  /**
   * Notify Sensehealth of a newly verified person in Wellpass.
   */
  public boolean notifyUserVerified(User user) {
    try {
      wpPortalClient.notifyUserVerified(oAuthSystemClientDAO.getWellpassPortalSystemClient(), user);
      return true;
    } catch (IOException ex) {
      LOG.error("error notifying user verify in sensehealth: ", ex.getMessage());
      return false;
    }
  }

  protected Integer generateCode() {
    return random.nextInt(90000) + 10000;
  }
}
