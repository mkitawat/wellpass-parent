package com.wellpass.core.models.coverage;

import static com.wellpass.core.utils.CollectionUtils.first;
import static com.wellpass.core.utils.CollectionUtils.isEmpty;

import com.wellpass.core.mongo.converters.LocalDateConverter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Converters;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.annotations.Version;
import org.mongodb.morphia.utils.IndexType;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Eligibility is the current representation of people’s healthcare information,
 * so right now we only use it to store and pass some values to SH when creating/updating users
 */
@Entity(value = "eligibility", noClassnameStored = true)
@Converters(LocalDateConverter.class)
@Indexes({
  @Index(fields = {@Field(value = "personId", type = IndexType.ASC)}),
  @Index(
    fields = {
      @Field(value = "enterpriseId", type = IndexType.ASC),
      @Field(value = "memberId", type = IndexType.ASC)},
    options = @IndexOptions(unique = true))
})
public class Eligibility {
  public static final String STATUS_ACTIVE = "ACTIVE";
  public static final String STATUS_INACTIVE = "INACTIVE";

  @Id
  public ObjectId id;

  public ObjectId personId;

  @Version
  public long version;

  public String enterpriseId;

  public String memberId;

  public String medicaidId;

  public String subscriberId;

  public String subscriberRelationship;

  public MemberInfo guardianInfo;

  public MemberInfo memberInfo;

  public Coverage coverage;

  public Boolean consent;

  public List<String> conditions;

  public List<HealthEvent> events;

  public String status;

  public String pokitdokPayload;

  public Date timeToLive;

  /**
   * short-term parking for tracfone data.
   * Eventually it should go away once tracfone contract expires.
   */
  public Tracfone tracfone;

  public boolean hasValidTracfone() {
    return this.tracfone != null && this.tracfone.isValid();
  }

  public boolean isSubscriber() {
    return this.memberId != null && this.memberId.equals(this.subscriberId);
  }

  public boolean isInactive() {
    return STATUS_INACTIVE.equalsIgnoreCase(status);
  }

  public boolean isTracfoneInactive() {
    return this.tracfone != null && this.tracfone.isInactive();
  }

  public boolean effectiveConsent() {
    if (tracfone != null && tracfone.consent != null) {
      return tracfone.consent;
    }
    // mdf consent
    if (consent != null) {
      return consent;
    }

    return false;
  }

  public PhoneNumber winnerPhoneNumber() {
    if (tracfone == null) {
      if (this.memberInfo != null) {
        // any phone number from mdf file
        return first(this.memberInfo.phoneNumbers);
      } else {
        return null;
      }
    }

    // preference to tracfone number
    if (tracfone.isValid()) {
      return new PhoneNumber(tracfone.number, true);
    }

    if (this.memberInfo == null || isEmpty(this.memberInfo.phoneNumbers)) {
      return null;
    }

    // any other phonenumber from mdf which does not match with tracfone
    return this.memberInfo.phoneNumbers.stream()
      // filter out tracfone number (as tracfone says its invalid)
      .filter(phone -> !Objects.equals(phone.number, tracfone.number))
      .findFirst()
      .orElse(null);
  }

  // Temporary parking. Eventually it has to go to some dynamic code set.
  public static enum HealthEventType {
    DUE_DATE,
    PHYSICAL_EXAM,
    VISION_EXAM,
    DENTAL_EXAM,
    MAMMOGRAM,
    PAP_SCREENING,
    COLORECTAL_SCREENING,
    FLUSHOT,
    BMI;
  }

  //Temporary parking. Eventually it has to go to some dynamic code set.
  public static enum ConditionType {
    SMOKER, DIABETES, ASTHMA, PREGNANT, DIABETES1, DIABETES2;
  }

  public static class HealthEvent {
    public String name;
    public LocalDate eventDate;
    public Integer intResult;

    public HealthEvent() {
    }

    public HealthEvent(String name, LocalDate eventDate, Integer intResult) {
      this.name = name;
      this.eventDate = eventDate;
      this.intResult = intResult;
    }

    public HealthEvent(String name, LocalDate eventDate) {
      this(name, eventDate, null);
    }

    public HealthEvent(String name, Integer intResult) {
      this(name, null, intResult);
    }
  }
}
