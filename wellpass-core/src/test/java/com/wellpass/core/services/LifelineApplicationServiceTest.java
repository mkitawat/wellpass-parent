package com.wellpass.core.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.wellpass.core.annotations.UnitTests;
import com.wellpass.core.daos.LifelineApplicationDAO;
import com.wellpass.core.models.auth.Person;
import com.wellpass.core.models.lifeline.LifelineApplication;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Category(UnitTests.class)
public class LifelineApplicationServiceTest {
  private LifelineApplicationDAO lifelineApplicationDAO;

  @Before
  public void setUp() {
    lifelineApplicationDAO = mock(LifelineApplicationDAO.class);
  }

  @Test
  public void testValidIdentityVerifiedSucccessfully() {
    // Given
    String token = "123456dfghj567hl";

    Person person = new Person();
    person.firstName = "Alex";
    person.lastName = "Smith";
    person.last4Ssn = "9999";
    person.dateOfBirth = LocalDate.now();
    person.id = new ObjectId();

    Calendar c = Calendar.getInstance();
    c.add(Calendar.DATE, 1);

    LifelineApplication application = new LifelineApplication();
    application.token = token;
    application.tokenExpirationDate = c.getTime();
    application.person = person;

    when(lifelineApplicationDAO.findOne("token", token)).thenReturn(application);

    // When
    LifelineApplicationService service = new LifelineApplicationService(lifelineApplicationDAO);
    Map<String, String> errors = new HashMap<>();
    service.verifyIdentity(token, "Alex", "Smith", new Date(person.dateOfBirth.toEpochDay()),
      "9999", errors);

    // Then
    assertTrue(errors.isEmpty());
  }

  @Test
  public void testValidationFailsWhenTokenIsExpired() {
    // Given
    String token = "123456dfghj567hl";
    LifelineApplication application = new LifelineApplication();
    application.token = token;
    application.tokenExpirationDate = new Date();

    when(lifelineApplicationDAO.findOne("token", token)).thenReturn(application);

    // When
    LifelineApplicationService service = new LifelineApplicationService(lifelineApplicationDAO);
    Map<String, String> errors = new HashMap<>();
    service.verifyIdentity(token, "Alex", "Smith", new Date(1234567890), "9999", errors);

    // Then
    assertNotNull(errors);
    assertEquals("Invalid token.", errors.get("token"));
  }

  @Test
  public void testValidationFailsWhenTokenIsInvalid() {
    // Given
    String token = "123456dfghj567hl";
    when(lifelineApplicationDAO.findOne("token", token)).thenReturn(null);
    // When
    LifelineApplicationService service = new LifelineApplicationService(lifelineApplicationDAO);
    Map<String, String> errors = new HashMap<>();
    service.verifyIdentity(token, "Alex", "Smith", new Date(1234567890), "9999", errors);

    // Then
    assertNotNull(errors);
    assertEquals("Invalid token.", errors.get("token"));
  }

  @Test
  public void testValidationFailsWhenPersonDetailsDoNotMatch() {
    // Given
    String token = "123456dfghj567hl";

    Person person = new Person();
    person.firstName = "Alexandra";
    person.lastName = "Smith";
    person.last4Ssn = "9999";
    person.dateOfBirth = LocalDate.now();
    person.id = new ObjectId();

    Calendar c = Calendar.getInstance();
    c.add(Calendar.DATE, 1);

    LifelineApplication application = new LifelineApplication();
    application.token = token;
    application.tokenExpirationDate = c.getTime();
    application.person = person;
    when(lifelineApplicationDAO.findOne("token", token)).thenReturn(application);

    // When first name doesn't match
    LifelineApplicationService service = new LifelineApplicationService(lifelineApplicationDAO);
    Map<String, String> errors = new HashMap<>();
    service.verifyIdentity(token, "Alex", "Smith", new Date(person.dateOfBirth.toEpochDay()),
      "9999", errors);

    // Then
    assertNotNull(errors);
    assertEquals("Invalid identity details.", errors.get("identity"));

    // -----
    // When last name doesn't match
    errors = new HashMap<>();
    service.verifyIdentity(token, "Alexandra", "Smithie", new Date(person.dateOfBirth.toEpochDay()),
      "9999", errors);

    // Then
    assertNotNull(errors);
    assertEquals("Invalid identity details.", errors.get("identity"));

    // -----
    // When last name doesn't match
    errors = new HashMap<>();
    service.verifyIdentity(token, "Alexandra", "Smith", new Date(), "9999", errors);

    // Then
    assertNotNull(errors);
    assertEquals("Invalid identity details.", errors.get("identity"));

    // -----
    // When last name doesn't match
    errors = new HashMap<>();
    service.verifyIdentity(token, "Alexandra", "Smith", new Date(person.dateOfBirth.toEpochDay()),
      "9990", errors);

    // Then
    assertNotNull(errors);
    assertEquals("Invalid identity details.", errors.get("identity"));
  }
}
