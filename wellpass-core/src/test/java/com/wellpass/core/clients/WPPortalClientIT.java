package com.wellpass.core.clients;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import com.wellpass.core.annotations.IntegrationTests;
import com.wellpass.core.models.auth.Address;
import com.wellpass.core.models.auth.CustomAttributes;
import com.wellpass.core.models.auth.OAuthSystemClient;
import com.wellpass.core.models.auth.Person;
import com.wellpass.core.models.auth.User;
import com.wellpass.core.models.coverage.BenefitPlan;
import com.wellpass.core.models.coverage.Coverage;
import com.wellpass.core.models.coverage.Eligibility;
import com.wellpass.core.models.coverage.Family;
import com.wellpass.core.models.coverage.MemberInfo;
import com.wellpass.core.models.coverage.Tracfone;
import com.wellpass.core.models.wellbox.MessageThread;
import com.wellpass.core.services.BaseIT;
import junit.framework.Assert;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;

@Category(IntegrationTests.class)
public class WPPortalClientIT extends BaseIT {
    private static WPPortalClient wpPortalClient;
    private static HttpClient http;

    private final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @BeforeClass
    public static void setup() {
        http = mock(HttpClient.class);
        wpPortalClient = new WPPortalClient(http);
    }

    @Before
    public void resetMocks(){
        reset(http);
    }

    @Test
    public void testSend() throws IOException {
        // create client
        OAuthSystemClient client = new OAuthSystemClient();
        client.id = new ObjectId("57c48dda07cda7c8545cf0c4");
        client.webhookSecret = "secret";

        // create message thread
        MessageThread messageThread = new MessageThread();
        messageThread.personId = new ObjectId("57c48dda07cda7c8545cf0c5");
        messageThread.name = "name of thread";

        String eventType = "some_event";
        String url = "something.com";

        doReturn("success").when(http).postJson(
                any(String.class),
                any(String.class)
        );

        wpPortalClient.send(messageThread, client, eventType, url);
        verify(http).postJson(url, "{\"messageThread\":{\"personId\":\"57c48dda07cda7c8545cf0c5\""
            + ",\"name\":\"name of thread\"},\"clientId\":\"57c48dda07cda7c8545cf0c4\""
            + ",\"secret\":\"secret\",\"eventType\":\"some_event\"}");
    }

    @Test
    public void testSendEnroll() throws IOException {
        // create person
        User user = new User();
        Person person = new Person();
        person.id = new ObjectId("57c48dda07cda7c8545cf0c6");
        person.firstName = "Joe";
        person.lastName = "Doe";
        person.existsInSenseHealth = true;
        user.person = person;

        // create client
        OAuthSystemClient client = new OAuthSystemClient();
        client.id = new ObjectId("57c48dda07cda7c8545cf0c4");
        client.webhookSecret = "secret";

        String url = "something.com";
        String token = "token123";

        doReturn("success").when(http).postJson(
                any(String.class),
                any(String.class)
        );

        wpPortalClient.send(user, token, client, url);
        verify(http).postJson(url, "{" +
                "\"clientId\":\"57c48dda07cda7c8545cf0c4\"," +
                "\"secret\":\"secret\"," +
                "\"person\":{" +
                  "\"id\":\"57c48dda07cda7c8545cf0c6\"," +
                  "\"firstName\":\"Joe\"," +
                  "\"lastName\":\"Doe\"," +
                  "\"existsInSenseHealth\":true}," +
                "\"token\":\"token123\"}"
        );
    }

    @Test
    public void testCreateSensehealthUser() throws IOException, ParseException {

        // create person
        Person person = new Person();
        person.id = new ObjectId("57c48dda07cda7c8545cf0c6");
        person.firstName = "Joe";
        person.lastName = "Doe";
        person.existsInSenseHealth = false;
        person.dateOfBirth = LocalDate.parse("1980-04-02", DATE_FORMAT);
        person.gender = "M";

        // create client
        OAuthSystemClient client = new OAuthSystemClient();
        client.id = new ObjectId("57c48dda07cda7c8545cf0c4");
        client.webhookSecret = "secret";

        // create benefit plan
        BenefitPlan benefitPlan = new BenefitPlan();

        final LocalDate dateRedeterm = LocalDate.parse("2016-09-01", DATE_FORMAT);
        final LocalDate dateEnd = LocalDate.parse("2016-09-05", DATE_FORMAT);
        final LocalDate dueDate = LocalDate.parse("2016-11-05", DATE_FORMAT);
        final LocalDate physExam = LocalDate.parse("2016-03-05", DATE_FORMAT);

        benefitPlan.memberId = "member123";
        benefitPlan.planNumber = "plan123";
        benefitPlan.groupNumber = "group123";
        benefitPlan.redeterminationDate = dateRedeterm;
        benefitPlan.planEndDate = dateEnd;

        // create member info
        MemberInfo memberInfo = new MemberInfo();
        memberInfo.addresses = new ArrayList<>();
        Address address = new Address();
        address.zipCode = "10003";
        memberInfo.addresses.add(address);

        // create eligibility
        Eligibility eligibility = new Eligibility();
        eligibility.personId = person.id;
        eligibility.enterpriseId = "org123";
        eligibility.medicaidId = "med123";
        eligibility.memberId = benefitPlan.memberId;
        eligibility.consent = true;
        eligibility.memberInfo = memberInfo;
        eligibility.coverage = new Coverage();
        eligibility.coverage.dentalCovered = true;
        eligibility.coverage.benefitPlan = benefitPlan;
        eligibility.events = new ArrayList<>();
        eligibility.events.add(new Eligibility.HealthEvent(Eligibility.HealthEventType.DUE_DATE.name(), dueDate));
        eligibility.events.add(new Eligibility.HealthEvent(Eligibility.HealthEventType.PHYSICAL_EXAM.name(), physExam));

        // create family members
        Family family = new Family();
        family.familyContact = new Family.FamilyContact();
        family.familyContact.personId = person.id;
        family.familyContact.dateOfBirth = person.dateOfBirth;
        family.familyContact.familyContactType = Family.FamilyContactType.ADULT_ACTIVE;
        family.members = new HashSet<>();
        family.members.add(person.id);
        family.members.add(new ObjectId("57fd466f4f91737a2c5e498f"));

        doReturn("success").when(http).postJson(
                any(String.class),
                any(String.class)
        );

        wpPortalClient.createSensehealthPerson(client, person, eligibility, family);
        verify(http).postJson(wpPortalClient.SENSEHEALTH_CREATE_PERSON_URL,
                "{\"organizationId\":\"org123\","
                  + "\"hasConsented\":true,"
                  + "\"customAttributes\":{\"healthPlanMemberID\":\"member123\","
                  + "\"medicaidID\":\"med123\",\"zipCode\":\"10003\",\"dueDate\":\"2016-11-05\","
                  + "\"dateOfInsuranceRedetermination\":\"2016-09-01\","
                  + "\"dateOfInsuranceTermination\":\"2016-09-05\","
                  + "\"dateOfLastPhysical\":\"2016-03-05\","
                  + "\"dentalCovered\":true,\"isPregnant\":true,\"isInFamily\":true,"
                  + "\"isFamilyContact\":true},\"familyMembers\":[\"57fd466f4f91737a2c5e498f\","
                  + "\"57c48dda07cda7c8545cf0c6\"],\"archive\":false,"
                  + "\"clientId\":\"57c48dda07cda7c8545cf0c4\",\"secret\":\"secret\","
                  + "\"person\":{\"id\":\"57c48dda07cda7c8545cf0c6\",\"firstName\":\"Joe\","
                  + "\"lastName\":\"Doe\",\"dateOfBirth\":\"1980-04-02\",\"gender\":\"M\","
                  + "\"existsInSenseHealth\":false}}"
        );
    }

    @Test
    public void testUpdateSensehealthUser() throws IOException, ParseException {

        // create person
        Person person = new Person();
        person.id = new ObjectId("57c48dda07cda7c8545cf0c6");
        person.firstName = "Joe";
        person.lastName = "Doe";
        person.existsInSenseHealth = false;
        person.dateOfBirth = LocalDate.parse("1980-04-02", DATE_FORMAT);
        person.gender = "F";

        // create client
        OAuthSystemClient client = new OAuthSystemClient();
        client.id = new ObjectId("57c48dda07cda7c8545cf0c4");
        client.webhookSecret = "secret";

        // create benefit plan
        BenefitPlan benefitPlan = new BenefitPlan();

        final LocalDate dateRedeterm = LocalDate.parse("2016-09-01", DATE_FORMAT);
        final LocalDate dateEnd = LocalDate.parse("2016-09-05", DATE_FORMAT);
        final LocalDate dueDate = LocalDate.parse("2016-11-05", DATE_FORMAT);
        final LocalDate physExam = LocalDate.parse("2016-03-05", DATE_FORMAT);

        benefitPlan.memberId = "member123";
        benefitPlan.planNumber = "plan123";
        benefitPlan.groupNumber = "group123";
        benefitPlan.redeterminationDate = dateRedeterm;
        benefitPlan.planEndDate = dateEnd;

        // create member info
        MemberInfo memberInfo = new MemberInfo();
        memberInfo.addresses = new ArrayList<>();
        Address address = new Address();
        address.zipCode = "10003";
        memberInfo.addresses.add(address);

        // create eligibility
        Eligibility eligibility = new Eligibility();
        eligibility.personId = person.id;
        eligibility.enterpriseId = "org123";
        eligibility.medicaidId = "med123";
        eligibility.memberId = benefitPlan.memberId;
        eligibility.consent = true;
        eligibility.memberInfo = memberInfo;
        eligibility.coverage = new Coverage();
        eligibility.coverage.dentalCovered = true;
        eligibility.coverage.benefitPlan = benefitPlan;
        eligibility.events = new ArrayList<>();
        eligibility.events.add(new Eligibility.HealthEvent(Eligibility.HealthEventType.DUE_DATE.name(), dueDate));
        eligibility.events.add(new Eligibility.HealthEvent(Eligibility.HealthEventType.PHYSICAL_EXAM.name(), physExam));

        // create family members
        Family family = new Family();
        family.familyContact = new Family.FamilyContact();
        family.familyContact.personId = person.id;
        family.familyContact.dateOfBirth = person.dateOfBirth;
        family.familyContact.familyContactType = Family.FamilyContactType.ADULT_ACTIVE;
        family.members = new HashSet<>();
        family.members.add(person.id);
        family.members.add(new ObjectId("57fd466f4f91737a2c5e498f"));

        doReturn("success").when(http).postJson(
                any(String.class),
                any(String.class)
        );

        wpPortalClient.updateSensehealthPerson(client, person, eligibility, family);
        verify(http).putJson(
          wpPortalClient.SENSEHEALTH_CREATE_PERSON_URL + String.valueOf(person.id) + "/",
                "{\"organizationId\":\"org123\",\"hasConsented\":true,\"customAttributes\""
                  + ":{\"healthPlanMemberID\":\"member123\",\"medicaidID\":\"med123\","
                  + "\"zipCode\":\"10003\",\"dueDate\":\"2016-11-05\","
                  + "\"dateOfInsuranceRedetermination\":\"2016-09-01\","
                  + "\"dateOfInsuranceTermination\":\"2016-09-05\","
                  + "\"dateOfLastPhysical\":\"2016-03-05\",\"dentalCovered\":true,"
                  + "\"isPregnant\":true,\"isInFamily\":true,\"isFamilyContact\":true},"
                  + "\"familyMembers\":[\"57fd466f4f91737a2c5e498f\",\"57c48dda07cda7c8545cf0c6\"],"
                  + "\"archive\":false,\"clientId\":\"57c48dda07cda7c8545cf0c4\","
                  + "\"secret\":\"secret\",\"person\":{\"id\":\"57c48dda07cda7c8545cf0c6\","
                  + "\"firstName\":\"Joe\",\"lastName\":\"Doe\",\"dateOfBirth\":\"1980-04-02\","
                  + "\"gender\":\"F\",\"existsInSenseHealth\":false}}"
        );
    }

    @Test
    public void testArchiveSensehealthUser_familyContact() throws IOException, ParseException {

        // create person
        Person person = new Person();
        person.id = new ObjectId("57c48dda07cda7c8545cf0c6");
        person.tracFone = false;

        Eligibility eligibility = new Eligibility();
        eligibility.enterpriseId = "org123";
        eligibility.personId = person.id;

        ObjectId newFamilyContact = new ObjectId("57c48dda07cda7c8545cf0c7");

        Family family = new Family();
        family.familyContact = new Family.FamilyContact();
        family.familyContact.personId = newFamilyContact;
        family.familyContact.familyContactType = Family.FamilyContactType.OTHER;
        family.members = new HashSet<>();
        family.members.add(person.id);
        family.members.add(newFamilyContact);

        // create client
        OAuthSystemClient client = new OAuthSystemClient();
        client.id = new ObjectId("57c48dda07cda7c8545cf0c4");
        client.webhookSecret = "secret";

        doReturn("success").when(http).postJson(
          any(String.class),
          any(String.class)
        );

        wpPortalClient
          .archiveSensehealthPerson(client, person, eligibility, family, newFamilyContact, null);
        verify(http).putJson(
          wpPortalClient.SENSEHEALTH_CREATE_PERSON_URL + String.valueOf(person.id) + "/",
          "{\"organizationId\":\"org123\",\"hasConsented\":false,"
            + "\"customAttributes\":{\"dentalCovered\":false,\"isPregnant\":false,"
            + "\"isInFamily\":true,\"isFamilyContact\":false},"
            + "\"familyMembers\":[\"57c48dda07cda7c8545cf0c6\",\"57c48dda07cda7c8545cf0c7\"],"
            + "\"archive\":true,\"newFamilyContact\":\"57c48dda07cda7c8545cf0c7\","
            + "\"clientId\":\"57c48dda07cda7c8545cf0c4\",\"secret\":\"secret\","
            + "\"person\":{\"id\":\"57c48dda07cda7c8545cf0c6\",\"tracFone\":false,"
            + "\"existsInSenseHealth\":false}}"
        );
    }

    @Test
    public void testArchiveSensehealthUser_familyMembers() throws IOException, ParseException {

      // create person
      Person person = new Person();
      person.id = new ObjectId("57c48dda07cda7c8545cf0c6");
      person.tracFone = true;

      Eligibility eligibility = new Eligibility();
      eligibility.personId = person.id;
      eligibility.enterpriseId = "org123";
      eligibility.tracfone = new Tracfone();
      eligibility.tracfone.safelinkActive = false;

      HashSet<ObjectId> members = new HashSet<>();
      members.add(new ObjectId("57c48dda07cda7c8545cf0c7"));
      members.add(new ObjectId("57c48dda07cda7c8545cf0c8"));

      Family family = new Family();
      family.familyContact = new Family.FamilyContact();
      family.familyContact.personId = person.id;
      family.familyContact.dateOfBirth = person.dateOfBirth;
      family.familyContact.familyContactType = Family.FamilyContactType.TRACFONE;
      family.members = members;

      // create client
      OAuthSystemClient client = new OAuthSystemClient();
      client.id = new ObjectId("57c48dda07cda7c8545cf0c4");
      client.webhookSecret = "secret";

      doReturn("success").when(http).postJson(
                any(String.class),
                any(String.class)
        );

      wpPortalClient.archiveSensehealthPerson(client, person, eligibility, family, null, members);
      verify(http).putJson(
          wpPortalClient.SENSEHEALTH_CREATE_PERSON_URL + String.valueOf(person.id) + "/",
          "{\"organizationId\":\"org123\",\"hasConsented\":false,"
            + "\"customAttributes\":{\"dentalCovered\":false,"
            + "\"isPregnant\":false,\"isInFamily\":true,\"isFamilyContact\":true},"
            + "\"familyMembers\":[\"57c48dda07cda7c8545cf0c7\",\"57c48dda07cda7c8545cf0c8\"],"
            + "\"archive\":true,\"membersToArchive\":[\"57c48dda07cda7c8545cf0c7\","
            + "\"57c48dda07cda7c8545cf0c8\"],\"clientId\":\"57c48dda07cda7c8545cf0c4\","
            + "\"secret\":\"secret\",\"person\":{\"id\":\"57c48dda07cda7c8545cf0c6\","
            + "\"tracFone\":true,\"existsInSenseHealth\":false}}"
        );
    }

    @Test
    public void testExtractCustomAttributes() throws IOException, ParseException {

        // Test empty doesn't fail
        CustomAttributes customAttrs = new CustomAttributes();
        CustomAttributes extractedCustomAttrs = wpPortalClient
          .extractCustomAttrs(new Eligibility(), null);
        Assert.assertTrue(EqualsBuilder.reflectionEquals(customAttrs, extractedCustomAttrs));

        // Test full custom attributes
        BenefitPlan benefitPlan = new BenefitPlan();

        final LocalDate dob = LocalDate.parse("1980-04-02", DATE_FORMAT);
        final LocalDate dateRedeterm = LocalDate.parse("2016-09-01", DATE_FORMAT);
        final LocalDate dateEnd = LocalDate.parse("2016-09-05", DATE_FORMAT);
        final LocalDate dueDate = LocalDate.parse("2016-11-05", DATE_FORMAT);
        final LocalDate physExam = LocalDate.parse("2016-03-05", DATE_FORMAT);

        benefitPlan.memberId = "member123";
        benefitPlan.planNumber = "plan123";
        benefitPlan.groupNumber = "group123";
        benefitPlan.redeterminationDate = dateRedeterm;
        benefitPlan.planEndDate = dateEnd;

        // create member info
        MemberInfo memberInfo = new MemberInfo();
        memberInfo.dateOfBirth = dob;
        memberInfo.addresses = new ArrayList<>();
        Address address = new Address();
        address.zipCode = "10003";
        memberInfo.addresses.add(address);

        // create eligibility
        Eligibility eligibility = new Eligibility();
        eligibility.personId = new ObjectId();
        eligibility.enterpriseId = "org123";
        eligibility.medicaidId = "med123";
        eligibility.memberId = benefitPlan.memberId;
        eligibility.consent = true;
        eligibility.memberInfo = memberInfo;
        eligibility.coverage = new Coverage();
        eligibility.coverage.dentalCovered = true;
        eligibility.coverage.benefitPlan = benefitPlan;
        eligibility.events = new ArrayList<>();
        eligibility.events.add(new Eligibility.HealthEvent(Eligibility.HealthEventType.DUE_DATE.name(), dueDate));
        eligibility.events.add(new Eligibility.HealthEvent(Eligibility.HealthEventType.PHYSICAL_EXAM.name(), physExam));

        Family family = new Family();
        family.familyContact = new Family.FamilyContact();
        family.familyContact.personId = eligibility.personId;
        family.familyContact.dateOfBirth = eligibility.memberInfo.dateOfBirth;
        family.familyContact.familyContactType = Family.FamilyContactType.ADULT_ACTIVE;

        CustomAttributes expectedCustomAttrs = wpPortalClient.extractCustomAttrs(eligibility, family);

        Assert.assertEquals(expectedCustomAttrs.healthPlanMemberID, eligibility.memberId);
        Assert.assertEquals(expectedCustomAttrs.medicaidID, eligibility.medicaidId);
        Assert.assertEquals(expectedCustomAttrs.zipCode, address.zipCode);
        Assert.assertEquals(expectedCustomAttrs.dueDate, dueDate);
        Assert.assertEquals(expectedCustomAttrs.dateOfInsuranceRedetermination, dateRedeterm);
        Assert.assertEquals(expectedCustomAttrs.dateOfInsuranceTermination, dateEnd);
        Assert.assertEquals(expectedCustomAttrs.dateOfLastPhysical, physExam);
        Assert.assertEquals(expectedCustomAttrs.dentalCovered, true);
        Assert.assertEquals(expectedCustomAttrs.isPregnant, true);
        Assert.assertEquals(expectedCustomAttrs.isInFamily, true);
        Assert.assertEquals(expectedCustomAttrs.isFamilyContact, true);
    }

    @Test
    public void testLinkSensehealthAccount() throws IOException {
        String mobileNumber = "3474162222";
        ObjectId personId = new ObjectId("5835e345c1d0ad43916fa3c6");
        OAuthSystemClient client = new OAuthSystemClient();
        client.id = new ObjectId("57c48dda07cda7c8545cf0c4");
        client.webhookSecret = "secret";

        String url = wpPortalClient.SENSEHEALTH_LINK_PERSON_URL;

        doReturn("success").when(http).postJson(
                any(String.class),
                any(String.class)
        );

        wpPortalClient.linkPersonInSenseHealth(client, mobileNumber, personId, url);

        verify(http).postJson(url,
                "{\"personId\":\"5835e345c1d0ad43916fa3c6\",\"hasConsented\":false,"
                  + "\"mobile\":\"3474162222\",\"archive\":false,"
                  + "\"clientId\":\"57c48dda07cda7c8545cf0c4\",\"secret\":\"secret\"}");
    }

    @Test
    public void testNotifyUserVerified() throws IOException {
      Person person = new Person();
      person.id = new ObjectId("5835e345c1d0ad43916fa3c6");
      person.existsInSenseHealth = true;
      User user = new User();
      user.person = person;
      OAuthSystemClient client = new OAuthSystemClient();
      client.id = new ObjectId("57c48dda07cda7c8545cf0c4");
      client.webhookSecret = "secret";

      String url = wpPortalClient.SENSEHEALTH_VERIFY_PERSON_URL;

      doReturn("success").when(http).postJson(
        any(String.class),
        any(String.class)
      );

      wpPortalClient.notifyUserVerified(client, user);

      verify(http).postJson(url,
        "{\"clientId\":\"57c48dda07cda7c8545cf0c4\","
          + "\"secret\":\"secret\","
          + "\"person\":{\"id\":\"5835e345c1d0ad43916fa3c6\",\"existsInSenseHealth\":true}}");
    }
}
