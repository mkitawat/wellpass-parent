package com.wellpass.core.services;

import com.wellpass.core.annotations.IntegrationTests;
import com.wellpass.core.daos.FamilyDAO;
import com.wellpass.core.daos.PersonDAO;
import com.wellpass.core.models.auth.Person;
import com.wellpass.core.models.coverage.Family;
import junit.framework.Assert;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.bson.types.ObjectId;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTests.class)
public class FamilyServiceIT extends BaseIT {

  private static FamilyService familyService;
  private static FamilyDAO familyDAO;
  private static PersonDAO personDAO;

  @BeforeClass
  public static void before() {
    familyDAO = new FamilyDAO(getDatastore());
    personDAO = new PersonDAO(getDatastore());
    familyService = new FamilyService(familyDAO, personDAO);
  }

  @Test
  public void testFindFamilyContact() throws Exception {
    Family family = new Family();
    family.id = new ObjectId();
    family.familyContact = new Family.FamilyContact();
    family.familyContact.personId = new ObjectId();
    family.familyContact.familyContactType = Family.FamilyContactType.OTHER;

    Person person = new Person();
    person.id = new ObjectId();
    person.familyId = family.id;

    familyDAO.save(family);
    personDAO.save(person);

    // Family contact id is known
    ObjectId contactPersonId = familyService.findFamilyContact(String.valueOf(person.id));
    Assert.assertEquals(contactPersonId, family.contactPersonId());
  }

  @Test
  public void testFindFamilyContactSelf() throws Exception {
    Person person = new Person();
    person.id = new ObjectId();

    personDAO.save(person);

    // Family contact id is self, no family
    ObjectId familyContactId = familyService.findFamilyContact(String.valueOf(person.id));
    Assert.assertEquals(familyContactId, person.id);
  }

  @Test
  public void testFamilyFromUser() throws Exception {

    // test not found
    Assert.assertNull(familyService.familyFromUser(String.valueOf(new ObjectId())));

    // tet found
    Family family = new Family();
    family.id = new ObjectId();
    family.familyContact = new Family.FamilyContact();
    family.familyContact.personId = new ObjectId();
    family.familyContact.familyContactType = Family.FamilyContactType.OTHER;

    Person person = new Person();
    person.id = new ObjectId();
    person.familyId = family.id;

    familyDAO.save(family);
    personDAO.save(person);

    Family fetchedFamily = familyService.familyFromUser(String.valueOf(person.id));
    org.junit.Assert.assertTrue(EqualsBuilder.reflectionEquals(fetchedFamily.id, family.id));
    org.junit.Assert.assertTrue(EqualsBuilder.reflectionEquals(fetchedFamily.familyContact, family.familyContact));
  }
}
