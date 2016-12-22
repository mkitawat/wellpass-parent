package com.wellpass.core.clients;

import com.wellpass.core.models.auth.CustomAttributes;
import com.wellpass.core.models.auth.OAuthSystemClient;
import com.wellpass.core.models.auth.Person;
import com.wellpass.core.models.auth.User;
import com.wellpass.core.models.coverage.Eligibility;
import com.wellpass.core.models.coverage.Eligibility.HealthEvent;
import com.wellpass.core.models.coverage.Eligibility.HealthEventType;
import com.wellpass.core.models.coverage.Family;
import com.wellpass.core.models.wellbox.MessageThread;
import com.wellpass.core.utils.gson.Jsonifier;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.Set;

/**
 * Manages webhook calls to subscribing apps.
 */
public class WPPortalClient {

  private final HttpClient http;

  public static final String SENSEHEALTH_BASE_URL = System.getenv("SENSEHEALTH_BASE_URL");
  public static final String SENSEHEALTH_CREATE_PERSON_PATH = "/api/wellpass-server/person/";
  public static final String SENSEHEALTH_LINK_PERSON_PATH = "/api/wellpass-server/link/";
  public static final String SENSEHEALTH_VERIFY_PERSON_PATH = "/api/wellpass-server/verify/";
  public static final String SENSEHEALTH_CREATE_PERSON_URL = SENSEHEALTH_BASE_URL + SENSEHEALTH_CREATE_PERSON_PATH;
  public static final String SENSEHEALTH_LINK_PERSON_URL = SENSEHEALTH_BASE_URL + SENSEHEALTH_LINK_PERSON_PATH;
  public static final String SENSEHEALTH_VERIFY_PERSON_URL = SENSEHEALTH_BASE_URL + SENSEHEALTH_VERIFY_PERSON_PATH;

  private class WebhookMessage {
    public ObjectId clientId;
    public String secret;
    public String eventType;
    public Person person;
    public String token;
  }

  private class MessageThreadMessage extends WebhookMessage {
    public MessageThread messageThread;
  }

  private class PersonWebhookMessage extends WebhookMessage {
    public ObjectId personId;
    public String organizationId;
    public boolean hasConsented;
    public CustomAttributes customAttributes;
    public String mobile;
    public Set<ObjectId> familyMembers;

    // archive attrs
    public boolean archive;
    public ObjectId newFamilyContact;
    public Set<ObjectId> membersToArchive;
  }

  public WPPortalClient(HttpClient http) {
    this.http = http;
  }

  public void send(MessageThread messageThread, OAuthSystemClient client, String eventType, String url) throws IOException {
    MessageThreadMessage message = new MessageThreadMessage();
    message.clientId = client.id;
    message.secret = client.webhookSecret;
    message.eventType = eventType;
    message.messageThread = messageThread;

    http.postJson(url, Jsonifier.toJson(message));
  }

  public void send(User user, String token, OAuthSystemClient client, String url) throws IOException {
    WebhookMessage message = new WebhookMessage();
    message.clientId = client.id;
    message.secret = client.webhookSecret;
    message.token = token;
    message.person = user.person;
    message.person.email = user.email;

    http.postJson(url, Jsonifier.toJson(message));
  }

  public String linkPersonInSenseHealth(OAuthSystemClient client, String mobile, ObjectId personId, String url) throws IOException {
    PersonWebhookMessage message = new PersonWebhookMessage();
    message.clientId = client.id;
    message.secret = client.webhookSecret;
    message.mobile = mobile;
    message.personId = personId;

    return http.postJson(url, Jsonifier.toJson(message));
  }

  public void notifyUserVerified(OAuthSystemClient client, User user) throws IOException {
    WebhookMessage message = new WebhookMessage();
    message.clientId = client.id;
    message.secret = client.webhookSecret;
    message.person = user.person;

    http.postJson(SENSEHEALTH_VERIFY_PERSON_URL, Jsonifier.toJson(message));
  }

  public void createSensehealthPerson(OAuthSystemClient client, Person person, Eligibility eligibility, Family family) throws IOException {
    http.postJson(SENSEHEALTH_CREATE_PERSON_URL, Jsonifier.toJson(createPersonWebhookMessage(client, person, eligibility, family)));
  }

  public void updateSensehealthPerson(OAuthSystemClient client, Person person, Eligibility eligibility, Family family) throws IOException {
    http.putJson(updateSensehealthPersonUrl(person), Jsonifier.toJson(createPersonWebhookMessage(client, person, eligibility, family)));
  }

  public void archiveSensehealthPerson(OAuthSystemClient client, Person person, Eligibility eligibility, Family family, ObjectId newFamilyContact, Set<ObjectId> membersToArchive) throws IOException {
    PersonWebhookMessage message = createPersonWebhookMessage(client, person, eligibility, family);
    message.archive = true;
    if (newFamilyContact != null) {
      message.newFamilyContact = newFamilyContact;
    } else if (membersToArchive != null) {
      message.membersToArchive = membersToArchive;
    }

    http.putJson(updateSensehealthPersonUrl(person), Jsonifier.toJson(message));
  }

  private String updateSensehealthPersonUrl(Person person) {
    return SENSEHEALTH_CREATE_PERSON_URL + String.valueOf(person.id) + "/";
  }

  CustomAttributes extractCustomAttrs(Eligibility eligibility, Family family) {
    CustomAttributes customAttributes = new CustomAttributes();

    // Top level fields
    customAttributes.healthPlanMemberID = eligibility.memberId;
    customAttributes.medicaidID = eligibility.medicaidId;

    // Family
    customAttributes.isInFamily = family != null;
    customAttributes.isFamilyContact = family != null && eligibility.personId.equals(family.contactPersonId());

    // Member info
    if (eligibility.memberInfo != null) {
      // TODO - Need to handle when we have multiple addresses
      if (eligibility.memberInfo.addresses.size() > 0) {
        customAttributes.zipCode = eligibility.memberInfo.addresses.get(0).zipCode;
      }
    }

    // Coverage
    if (eligibility.coverage != null) {
      if (eligibility.coverage.dentalCovered != null) {
        customAttributes.dentalCovered = eligibility.coverage.dentalCovered;
      }
      if (eligibility.coverage.benefitPlan != null) {
        customAttributes.dateOfInsuranceRedetermination = eligibility.coverage.benefitPlan.redeterminationDate;
        customAttributes.dateOfInsuranceTermination = eligibility.coverage.benefitPlan.planEndDate;
      }
    }

    // Events
    if (eligibility.events != null) {
      for (HealthEvent healthEvent : eligibility.events) {
        if (HealthEventType.DUE_DATE.name().equals(healthEvent.name)) {
          customAttributes.dueDate = healthEvent.eventDate;
          customAttributes.isPregnant = true;
        } else if (HealthEventType.PHYSICAL_EXAM.name().equals(healthEvent.name)) {
          customAttributes.dateOfLastPhysical = healthEvent.eventDate;
        }
      }
    }

    return customAttributes;
  }

  private PersonWebhookMessage createPersonWebhookMessage(OAuthSystemClient client, Person person, Eligibility eligibility, Family family) {
    PersonWebhookMessage message = new PersonWebhookMessage();
    message.clientId = client.id;
    message.secret = client.webhookSecret;
    message.person = person;
    message.organizationId = eligibility.enterpriseId;
    message.hasConsented = eligibility.effectiveConsent();
    message.customAttributes = extractCustomAttrs(eligibility, family);
    if (family != null) {
      message.familyMembers = family.members;
    }
    return message;
  }
}
