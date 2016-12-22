package com.wellpass.core.services;

import com.wellpass.core.daos.FamilyDAO;
import com.wellpass.core.daos.PersonDAO;
import com.wellpass.core.exceptions.WPNotFoundException;
import com.wellpass.core.models.auth.Person;
import com.wellpass.core.models.coverage.Family;
import org.bson.types.ObjectId;

/**
 * Service to handle interaction with Families
 */
public class FamilyService {
  private final FamilyDAO familyDAO;
  private final PersonDAO personDAO;

  public FamilyService(FamilyDAO familyDAO, PersonDAO personDAO) {
    this.familyDAO = familyDAO;
    this.personDAO = personDAO;
  }

  public Family familyFromId(String id) {
    try {
      return familyDAO.findOneById(id);
    } catch (WPNotFoundException ex) {
      return null;
    }
  }

  // TODO: need to filter by org when we handle multiple families
  public Family familyFromUser(String userId) {
    Person person;
    try {
      person = personDAO.findOneById(userId);
    } catch (WPNotFoundException ex) {
      person = null;
    }
    if (person == null || person.familyId == null) {
      return null;
    }
    return familyFromId(person.familyId.toString());
  }

  // TODO: handle orgId
  public ObjectId findFamilyContact(String userId) {
    Family family = familyFromUser(userId);
    if (family != null && family.contactPersonId() != null) {
      return family.contactPersonId(); // Family should always have familyContactId
    }
    return new ObjectId(userId);
  }

}
