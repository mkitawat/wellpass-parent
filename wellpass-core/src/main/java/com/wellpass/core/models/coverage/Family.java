package com.wellpass.core.models.coverage;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

import java.time.LocalDate;
import java.util.Set;

/**
 * A Wellpass family.
 */
@Entity(value = "family", noClassnameStored = true)
public class Family {
  @Id
  public ObjectId id;

  @Indexed(unique = true, dropDups = true)
  public String familySubscription; // From provider import file

  public FamilyContact familyContact;

  public Set<ObjectId> members;

  public ObjectId contactPersonId() {
    return familyContact != null ? familyContact.personId : null;
  }

  public static String createFamilySubscription(String organizationId, String subscriberId) {
    if (organizationId == null || subscriberId == null) {
      return null;
    }
    return organizationId + ":" + subscriberId;
  }

  public static class FamilyContact {
    public ObjectId personId;

    public FamilyContactType familyContactType;

    // to avoid relookup. remove if this becomes data integrity issue
    public LocalDate dateOfBirth;

    public int rank() {
      return familyContactType.rank;
    }
  }

  public static enum FamilyContactType {
    TRACFONE(1), 
    SUBSCRIBER(2), 
    ADULT_ACTIVE_MEMBER_SUBSCRIBER(3), 
    ADULT_ACTIVE(4), 
    MINOR_ACTIVE_WITH_GUARDIAN(5), 
    MINOR_ACTIVE_WO_GUARDIAN(6), 
    ADULT_INACTIVE(Integer.MAX_VALUE), 
    OTHER(Integer.MAX_VALUE);

    private int rank;

    private FamilyContactType(int rank) {
      this.rank = rank;
    }

    public int rank() {
      return this.rank;
    }

  }
}


