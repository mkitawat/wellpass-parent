package com.wellpass.core.services;

import com.wellpass.core.daos.LifelineApplicationDAO;
import com.wellpass.core.models.auth.Person;
import com.wellpass.core.models.lifeline.LifelineApplication;

import java.util.Date;
import java.util.Map;

public class LifelineApplicationService {

  private LifelineApplicationDAO lifelineApplicationDAO;

  public LifelineApplicationService(LifelineApplicationDAO lifelineApplicationDAO) {
    this.lifelineApplicationDAO = lifelineApplicationDAO;
  }

  public void verifyIdentity(String token, String firstName, String lastName,
                             Date dateOfBirth, String last4SSN, Map<String, String> errors) {
    LifelineApplication application = getApplicationFromToken(token);

    if (application != null) {
      // Make sure this token hasn't expired
      if (new Date().before(application.tokenExpirationDate)) {
        Person person = application.person;

        if (person != null
          && person.firstName == firstName
          && person.lastName == lastName
          && person.last4Ssn == last4SSN
          && person.dateOfBirth.toEpochDay() == dateOfBirth.getTime()) {
          return;
        } else {
          errors.put("identity", "Invalid identity details.");
          return;
        }
      }
    }
    errors.put("token", "Invalid token.");
  }

  private LifelineApplication getApplicationFromToken(String token) {
    return lifelineApplicationDAO.findOne("token", token);
  }
}
